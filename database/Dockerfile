# =========================================================
# Imagem do BANCO DE DADOS - Oracle Database XE 21c
# Challenge FIAP Sprint 3 - DevOps Tools & Cloud Computing
#
# Base: gvenzl/oracle-xe, variante "slim" (NAO-faststart). Ela traz o
# banco compactado em /opt/oracle/XE.7z e o entrypoint o EXTRAI para
# /opt/oracle/oradata no primeiro start.
#
# Esta imagem carrega o DDL e o seed do PetHub, atendendo ao requisito
# "Criar as imagens do projeto com um Dockerfile (Banco e App)".
#
# O processo do banco roda como o usuario 'oracle' (uid 54321),
# herdado da imagem base -- nao roda como root.
# =========================================================
FROM gvenzl/oracle-xe:21-slim

# Scripts SQL do projeto
COPY init/01-ddl.sql  /opt/oracle/pethub/01-ddl.sql
COPY init/02-seed.sql /opt/oracle/pethub/02-seed.sql

# Preparacao do schema. Vai para startdb.d (e nao initdb.d) porque
# o entrypoint do gvenzl pode pular initdb.d se o banco ja existir.
# O script e idempotente: numa subida com o schema ja criado nao faz nada.
COPY init/00-pethub-schema.sh /container-entrypoint-startdb.d/00-pethub-schema.sh

# Reseta SYS/SYSTEM em todo boot -- ver comentario dentro do script.
# So funciona por causa do patch no entrypoint logo abaixo (orapw local).
COPY init/01-reset-sys-password.sh /container-entrypoint-startdb.d/01-reset-sys-password.sh

USER root
RUN chmod -R a+r /opt/oracle/pethub \
 && chmod a+rx /container-entrypoint-startdb.d/00-pethub-schema.sh \
 && chmod a+rx /container-entrypoint-startdb.d/01-reset-sys-password.sh

# ---------------------------------------------------------------------
# Mantem o orapwXE fora do File Share (Azure Files/SMB).
#
# O entrypoint do gvenzl, no primeiro boot, MOVE o password file local
# (${ORACLE_BASE_CONFIG}/dbs/orapwXE) para dentro de oradata/dbconfig/XE
# (que fica no File Share via volume azureFile) e deixa um symlink no
# lugar. O Oracle REESCREVE esse arquivo toda vez que troca a senha do
# SYS/SYSTEM (inclusive no proprio reset da primeira subida) -- e o SMB
# do Azure Files nao sustenta essa escrita: o processo morre com
# ORA-01990 "error opening password file ''" (exit code 198 = 1990 mod
# 256). Foi exatamente o que travou o primeiro deploy deste projeto.
#
# Datafiles, control files, redo e spfile ficam no mesmo share sem
# problema (evidencia: eles sao escritos com sucesso durante a extracao
# do XE.7z) -- o problema e especifico do orapw.
#
# A correcao: desativar so o mv/symlink do orapw (comentando as 3
# linhas abaixo), deixando-o inteiramente no disco local do container.
# Isso faz o orapw ser efemero (perdido a cada "az container create"),
# o que nao e problema: 01-reset-sys-password.sh reaplica a senha do
# SYS/SYSTEM em TODO boot, entao ela nunca fica desatualizada.
#
# "grep -qF" verifica que a linha exata ainda existe antes de comentar
# -- se a gvenzl mudar o entrypoint numa atualizacao da tag "21-slim",
# o build falha alto aqui em vez de silenciosamente deixar de aplicar
# o patch (o que reintroduziria o ORA-01990 sem aviso nenhum).
# ---------------------------------------------------------------------
RUN ENTRYPOINT_SH=/opt/oracle/container-entrypoint.sh; \
    patch_line() { \
      local alvo="$1" arquivo="$2"; \
      local n; \
      n=$(grep -nF -- "${alvo}" "${arquivo}" | head -1 | cut -d: -f1); \
      if [[ -z "${n}" ]]; then \
        echo "ERRO: linha esperada nao encontrada em ${arquivo}:"; \
        echo "      ${alvo}"; \
        echo "      A imagem base gvenzl/oracle-xe:21-slim mudou de conteudo."; \
        exit 1; \
      fi; \
      sed -i "${n}s|^|: # PETHUB (SMB nao sustenta escrita no orapw, ver Dockerfile): |" "${arquivo}"; \
    }; \
    patch_line 'mv "${ORACLE_BASE_CONFIG}"/dbs/orapw"${ORACLE_SID}"' "${ENTRYPOINT_SH}"; \
    patch_line '    rm "${ORACLE_BASE_CONFIG}"/dbs/orapw"${ORACLE_SID}"' "${ENTRYPOINT_SH}"; \
    patch_line '    ln -s "${ORACLE_BASE}"/oradata/dbconfig/"${ORACLE_SID}"/orapw"${ORACLE_SID}" "${ORACLE_BASE_CONFIG}"/dbs/orapw"${ORACLE_SID}"' "${ENTRYPOINT_SH}"

USER oracle

EXPOSE 1521
