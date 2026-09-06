#!/usr/bin/env bash
# =====================================================================
# ETAPA 5 - Deploy do ACI da APLICACAO
#
# Descobre o FQDN do ACI do banco, renderiza azure/aci-app.yaml e cria
# o segundo ACI. Rode apenas depois que o banco estiver pronto
# (log do ACI do banco mostrando "DATABASE IS READY TO USE").
# =====================================================================
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"
source ./00-vars.sh

echo
echo "[1/4] Descobrindo o FQDN do ACI do banco (${ACI_DB})..."
DB_FQDN=$(az container show \
  --resource-group "${RESOURCE_GROUP}" \
  --name "${ACI_DB}" \
  --query "ipAddress.fqdn" -o tsv)

if [[ -z "${DB_FQDN}" ]]; then
  echo "ERRO: nao consegui obter o FQDN do ACI ${ACI_DB}. Ele foi criado?"
  exit 1
fi
echo "      banco em ${DB_FQDN}:1521"

echo
echo "[2/4] Coletando credenciais do ACR..."
ACR_SERVER=$(az acr show --name "${ACR_NAME}" --query loginServer -o tsv)
ACR_USERNAME=$(az acr credential show --name "${ACR_NAME}" --query username -o tsv)
ACR_PASSWORD=$(az acr credential show --name "${ACR_NAME}" --query "passwords[0].value" -o tsv)

RENDERED="${GENERATED_DIR}/aci-app.yaml"

echo "[3/4] Renderizando ${RENDERED}..."
sed \
  -e "s|__LOCATION__|${LOCATION}|g" \
  -e "s|__ACI_APP__|${ACI_APP}|g" \
  -e "s|__ACR_SERVER__|${ACR_SERVER}|g" \
  -e "s|__ACR_USERNAME__|${ACR_USERNAME}|g" \
  -e "s|__ACR_PASSWORD__|${ACR_PASSWORD}|g" \
  -e "s|__DNS_APP__|${DNS_APP}|g" \
  -e "s|__APP_IMAGE__|${APP_IMAGE}|g" \
  -e "s|__IMAGE_TAG__|${IMAGE_TAG}|g" \
  -e "s|__APP_CPU__|${APP_CPU}|g" \
  -e "s|__APP_MEMORY__|${APP_MEMORY}|g" \
  -e "s|__DB_FQDN__|${DB_FQDN}|g" \
  -e "s|__APP_DB_USER__|${APP_DB_USER}|g" \
  -e "s|__APP_DB_PASSWORD__|${APP_DB_PASSWORD}|g" \
  -e "s|__JWT_SECRET__|${JWT_SECRET}|g" \
  ./aci-app.yaml > "${RENDERED}"
chmod 600 "${RENDERED}"

echo "[4/4] Criando o ACI ${ACI_APP}..."
az container create \
  --resource-group "${RESOURCE_GROUP}" \
  --file "${RENDERED}" \
  -o none

APP_FQDN=$(az container show \
  --resource-group "${RESOURCE_GROUP}" \
  --name "${ACI_APP}" \
  --query "ipAddress.fqdn" -o tsv)

echo
echo "====================================================="
echo " API .....: http://${APP_FQDN}:8080"
echo " Swagger .: http://${APP_FQDN}:8080/swagger-ui.html"
echo " Health ..: http://${APP_FQDN}:8080/actuator/health"
echo "====================================================="
echo
echo "Acompanhe a subida do Spring Boot com:"
echo "  az container logs -g ${RESOURCE_GROUP} -n ${ACI_APP} --follow"
echo
echo "Roteiro do CRUD com evidencia no banco: docs/roteiro-video.md"
