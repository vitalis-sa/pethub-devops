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

USER root
RUN chmod -R a+r /opt/oracle/pethub \
 && chmod a+rx /container-entrypoint-startdb.d/00-pethub-schema.sh
USER oracle

EXPOSE 1521
