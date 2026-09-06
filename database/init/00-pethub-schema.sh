#!/usr/bin/env bash
# =====================================================================
# PetHub - criacao do usuario da aplicacao e aplicacao do DDL
#
# POR QUE ESTE SCRIPT VIVE EM startdb.d E NAO EM initdb.d
# -------------------------------------------------------
# O entrypoint do gvenzl so roda /container-entrypoint-initdb.d quando
# considera aquela a PRIMEIRA inicializacao do banco, e essa decisao sai
# de um unico teste: existir ou nao /opt/oracle/oradata/dbconfig/${ORACLE_SID}.
#
# Com a variante "faststart" (banco pre-criado dentro da imagem) esse
# diretorio pode ja estar presente no ponto de montagem dependendo de como
# o volume foi criado -- e quando isso acontece o entrypoint conclui
# DATABASE_ALREADY_EXISTS=true e pula tanto a criacao do APP_USER quanto o
# initdb.d, deixando o banco sem schema e sem usuario da aplicacao.
# Foi exatamente o que aconteceu nos primeiros testes deste projeto.
#
# startdb.d nao tem esse condicional: roda em TODA subida do container.
# Em troca, o script precisa ser idempotente -- cria o usuario apenas se
# faltar e aplica o DDL apenas se as tabelas nao existirem. Numa subida
# com o schema ja pronto ele nao faz nada e apenas informa.
# =====================================================================
set -euo pipefail

if [[ -z "${APP_USER:-}" || -z "${APP_USER_PASSWORD:-}" ]]; then
  echo "PETHUB: APP_USER/APP_USER_PASSWORD nao definidos, nada a fazer."
  exit 0
fi

PDB="${ORACLE_DATABASE:-XEPDB1}"
CONN="${APP_USER}/${APP_USER_PASSWORD}@//localhost:1521/${PDB}"

# Roda um SELECT que devolve um unico numero, sem cabecalho nem rodape.
consulta_escalar() {
  sqlplus -s -l "$1" <<SQL 2>/dev/null | tr -d '[:space:]'
SET HEADING OFF FEEDBACK OFF PAGESIZE 0 VERIFY OFF TERMOUT ON
$2
EXIT;
SQL
}

# ---------------------------------------------------------------------
# 1) Usuario da aplicacao
# ---------------------------------------------------------------------
usuarios=$(consulta_escalar "/ as sysdba" \
  "ALTER SESSION SET CONTAINER=${PDB};
   SELECT COUNT(*) FROM dba_users WHERE username = UPPER('${APP_USER}');")

if [[ "${usuarios}" == "0" ]]; then
  echo "PETHUB: criando o usuario ${APP_USER} em ${PDB}..."
  cd /opt/oracle && ./createAppUser "${APP_USER}" "${APP_USER_PASSWORD}" "${PDB}"
else
  echo "PETHUB: usuario ${APP_USER} ja existe, mantendo."
fi

# ---------------------------------------------------------------------
# 2) Schema (DDL + carga inicial)
# ---------------------------------------------------------------------
tabelas=$(consulta_escalar "${CONN}" \
  "SELECT COUNT(*) FROM user_tables WHERE table_name = 'TB_PET';")

if [[ "${tabelas}" == "0" ]]; then
  echo "PETHUB: aplicando o DDL em ${APP_USER}@${PDB}..."
  sqlplus -s -l "${CONN}" @/opt/oracle/pethub/01-ddl.sql

  echo "PETHUB: aplicando a carga inicial (seed)..."
  sqlplus -s -l "${CONN}" @/opt/oracle/pethub/02-seed.sql

  total=$(consulta_escalar "${CONN}" "SELECT COUNT(*) FROM user_tables;")
  echo "PETHUB: schema pronto - ${total} tabelas criadas."
else
  echo "PETHUB: schema ja existe, nada a aplicar."
fi
