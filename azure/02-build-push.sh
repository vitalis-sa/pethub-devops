#!/usr/bin/env bash
# =====================================================================
# ETAPA 2 - Build local das duas imagens + push para o ACR
#
# Cobre os itens 3, 4 e 6 do enunciado:
#   3) criar as imagens do projeto com um Dockerfile (banco e app)
#   4) realizar o build das imagens localmente
#   6) registrar as imagens no ACR incluindo o RM como prefixo
#
# As imagens sao construidas LOCALMENTE com "docker build" (nao com
# "az acr build"), porque o enunciado pede as evidencias do build local.
# =====================================================================
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"
source ./00-vars.sh
PROJECT_ROOT="$(cd .. && pwd)"

ACR_SERVER=$(az acr show --name "${ACR_NAME}" --query loginServer -o tsv)

echo
echo "[1/5] Build local da imagem do BANCO (${DB_IMAGE}:${IMAGE_TAG})..."
docker build \
  -t "${DB_IMAGE}:${IMAGE_TAG}" \
  -t "${ACR_SERVER}/${DB_IMAGE}:${IMAGE_TAG}" \
  "${PROJECT_ROOT}/database"

echo
echo "[2/5] Build local da imagem do APP (${APP_IMAGE}:${IMAGE_TAG})..."
docker build \
  -t "${APP_IMAGE}:${IMAGE_TAG}" \
  -t "${ACR_SERVER}/${APP_IMAGE}:${IMAGE_TAG}" \
  "${PROJECT_ROOT}"

echo
echo "[3/5] Conferindo que o container do APP NAO roda como root..."
APP_UID=$(docker run --rm --entrypoint id "${APP_IMAGE}:${IMAGE_TAG}" -u)
APP_ID=$(docker run --rm --entrypoint id "${APP_IMAGE}:${IMAGE_TAG}")
echo "      ${APP_ID}"
if [[ "${APP_UID}" == "0" ]]; then
  echo "ERRO: a imagem do app esta rodando como root (uid 0). Abortado."
  exit 1
fi
echo "      OK - uid ${APP_UID}, sem privilegios administrativos."

echo
echo "[4/5] Autenticando no ACR ${ACR_SERVER}..."
az acr login --name "${ACR_NAME}"

echo
echo "[5/5] Push das duas imagens para o ACR..."
docker push "${ACR_SERVER}/${DB_IMAGE}:${IMAGE_TAG}"
docker push "${ACR_SERVER}/${APP_IMAGE}:${IMAGE_TAG}"

echo
echo "Imagens registradas no ACR:"
az acr repository list --name "${ACR_NAME}" --output table

echo
echo "====================================================="
echo " ${ACR_SERVER}/${DB_IMAGE}:${IMAGE_TAG}"
echo " ${ACR_SERVER}/${APP_IMAGE}:${IMAGE_TAG}"
echo " Proximo passo: ./03-storage.sh"
echo "====================================================="
