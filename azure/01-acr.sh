#!/usr/bin/env bash
# =====================================================================
# ETAPA 1 - Resource Group + Azure Container Registry
#
# Cria o RG e o ACR onde as duas imagens (app e banco) serao registradas.
# =====================================================================
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"
source ./00-vars.sh

echo
echo "[1/3] Criando Resource Group ${RESOURCE_GROUP} em ${LOCATION}..."
az group create \
  --name "${RESOURCE_GROUP}" \
  --location "${LOCATION}" \
  --output table

echo
echo "[2/3] Criando Azure Container Registry ${ACR_NAME} (SKU ${ACR_SKU})..."
az acr create \
  --resource-group "${RESOURCE_GROUP}" \
  --name "${ACR_NAME}" \
  --sku "${ACR_SKU}" \
  --location "${LOCATION}" \
  --output table

echo
echo "[3/3] Habilitando o usuario admin do ACR..."
# O admin user e como o ACI autentica para puxar as imagens privadas.
az acr update \
  --name "${ACR_NAME}" \
  --admin-enabled true \
  --output table

ACR_SERVER=$(az acr show --name "${ACR_NAME}" --query loginServer -o tsv)

echo
echo "====================================================="
echo " ACR pronto: ${ACR_SERVER}"
echo " Proximo passo: ./02-build-push.sh"
echo "====================================================="
