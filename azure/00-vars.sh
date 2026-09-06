#!/usr/bin/env bash
# =====================================================================
# Variaveis compartilhadas por todos os scripts do provisionamento.
# NAO executa nada sozinho -- e carregado com "source" pelos demais.
#
#   RM do representante do grupo: 566234 (Joao Guilherme Carvalho Novaes)
#   Prefixo obrigatorio de imagens e ACIs: rm566234
# =====================================================================

# ---------------------------------------------------------------------
# Identificacao do grupo
# ---------------------------------------------------------------------
export RM="rm566234"
export GRUPO="vitalis"

# ---------------------------------------------------------------------
# Localizacao e Resource Group
# ---------------------------------------------------------------------
export LOCATION="${LOCATION:-canadacentral}"
export RESOURCE_GROUP="rg-${RM}-challenge-sprint3"

# ---------------------------------------------------------------------
# Azure Container Registry
# ---------------------------------------------------------------------
export ACR_NAME="acr${RM}challenge"
export ACR_SKU="Basic"

# ---------------------------------------------------------------------
# Imagens (o RM do representante e prefixo obrigatorio)
# ---------------------------------------------------------------------
export IMAGE_TAG="${IMAGE_TAG:-v1}"
export APP_IMAGE="${RM}-pethub-app"
export DB_IMAGE="${RM}-pethub-oracle"

# ---------------------------------------------------------------------
# Conta de Armazenamento (persistencia dos dados do banco)
# ---------------------------------------------------------------------
export STORAGE_ACCOUNT="st${RM}challenge"
export FILE_SHARE="${FILE_SHARE:-oradata}"
export FILE_SHARE_QUOTA_GB="50"

# ---------------------------------------------------------------------
# Azure Container Instances (o RM tambem e prefixo dos ACIs)
# ---------------------------------------------------------------------
export ACI_DB="${RM}-aci-db"
export ACI_APP="${RM}-aci-app"

# Rotulos DNS -> viram <label>.<location>.azurecontainer.io
export DNS_DB="${RM}-pethub-db"
export DNS_APP="${RM}-pethub-app"

# Oracle XE precisa de memoria; abaixo de 4GB ele nao inicializa de forma confiavel
export DB_CPU="2"
export DB_MEMORY="4"
export APP_CPU="1"
export APP_MEMORY="1.5"

# ---------------------------------------------------------------------
# Credenciais -- lidas do arquivo .env da raiz do projeto.
# NUNCA sao escritas neste arquivo nem commitadas no Git.
# ---------------------------------------------------------------------
ENV_FILE="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/.env"
if [[ -f "${ENV_FILE}" ]]; then
  set -a
  # shellcheck disable=SC1090
  source "${ENV_FILE}"
  set +a
else
  echo "ERRO: arquivo .env nao encontrado em ${ENV_FILE}"
  echo "      Rode:  cp .env.example .env   e preencha as senhas."
  return 1 2>/dev/null || exit 1
fi

: "${ORACLE_PWD:?ORACLE_PWD nao definido no .env}"
: "${APP_DB_USER:?APP_DB_USER nao definido no .env}"
: "${APP_DB_PASSWORD:?APP_DB_PASSWORD nao definido no .env}"
: "${JWT_SECRET:?JWT_SECRET nao definido no .env}"

# Pasta para artefatos gerados em tempo de execucao (YAMLs com segredo).
# Esta no .gitignore -- nada daqui vai para o repositorio.
export GENERATED_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/.generated"
mkdir -p "${GENERATED_DIR}"

echo "--------------------------------------------------------"
echo " Grupo .............: ${GRUPO} (RM ${RM})"
echo " Resource Group ....: ${RESOURCE_GROUP}"
echo " Regiao ............: ${LOCATION}"
echo " ACR ...............: ${ACR_NAME}"
echo " Storage Account ...: ${STORAGE_ACCOUNT} / share ${FILE_SHARE}"
echo " ACI banco .........: ${ACI_DB}  (${DNS_DB})"
echo " ACI app ...........: ${ACI_APP} (${DNS_APP})"
echo " Imagens ...........: ${APP_IMAGE}:${IMAGE_TAG} | ${DB_IMAGE}:${IMAGE_TAG}"
echo "--------------------------------------------------------"
