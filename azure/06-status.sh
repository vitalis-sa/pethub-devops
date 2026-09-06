#!/usr/bin/env bash
# =====================================================================
# Panorama dos recursos criados na Azure.
#
# Rode este script no INICIO DO VIDEO: ele lista, numa tela so, o
# Resource Group, o ACR com as duas imagens, a Conta de Armazenamento
# com o File Share e os dois ACIs com seus enderecos publicos.
# =====================================================================
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"
source ./00-vars.sh

echo
echo "#####################################################"
echo "# 1) TODOS OS RECURSOS DO RESOURCE GROUP"
echo "#####################################################"
az resource list \
  --resource-group "${RESOURCE_GROUP}" \
  --query "[].{Nome:name, Tipo:type, Regiao:location}" \
  --output table

echo
echo "#####################################################"
echo "# 2) ACR - IMAGENS REGISTRADAS (prefixo ${RM})"
echo "#####################################################"
az acr repository list --name "${ACR_NAME}" --output table
for repo in "${DB_IMAGE}" "${APP_IMAGE}"; do
  echo
  echo "-- tags de ${repo}:"
  az acr repository show-tags --name "${ACR_NAME}" --repository "${repo}" --output table
done

echo
echo "#####################################################"
echo "# 3) CONTA DE ARMAZENAMENTO E FILE SHARE"
echo "#####################################################"
az storage account show \
  --resource-group "${RESOURCE_GROUP}" \
  --name "${STORAGE_ACCOUNT}" \
  --query "{Nome:name, Sku:sku.name, Regiao:primaryLocation}" \
  --output table

STORAGE_KEY=$(az storage account keys list \
  --resource-group "${RESOURCE_GROUP}" \
  --account-name "${STORAGE_ACCOUNT}" \
  --query "[0].value" -o tsv)

az storage share list \
  --account-name "${STORAGE_ACCOUNT}" \
  --account-key "${STORAGE_KEY}" \
  --output table

echo
echo "-- arquivos do Oracle gravados no File Share (prova da persistencia):"
az storage file list \
  --share-name "${FILE_SHARE}" \
  --account-name "${STORAGE_ACCOUNT}" \
  --account-key "${STORAGE_KEY}" \
  --output table

echo
echo "#####################################################"
echo "# 4) OS DOIS ACIs (prefixo ${RM})"
echo "#####################################################"
# "az container list" NAO devolve instanceView (vem null), entao a coluna
# Estado sairia sempre vazia. Consultamos cada ACI com "show".
# A projecao e um DICIONARIO: em "-o tsv" uma query em lista ([a,b,c])
# imprime um item POR LINHA, enquanto o dicionario sai numa linha so,
# separada por TAB, na ordem escrita na query.
printf '%-18s  %-10s  %-52s  %-15s  %-4s  %s\n' Nome Estado FQDN IP CPU MemGB
printf '%-18s  %-10s  %-52s  %-15s  %-4s  %s\n' \
  '------------------' '----------' \
  '----------------------------------------------------' '---------------' '----' '-----'
for aci in "${ACI_DB}" "${ACI_APP}"; do
  az container show -g "${RESOURCE_GROUP}" -n "${aci}" \
    --query "{a:name, b:instanceView.state, c:ipAddress.fqdn, d:ipAddress.ip, e:containers[0].resources.requests.cpu, f:containers[0].resources.requests.memoryInGb}" \
    -o tsv 2>/dev/null | tr -d '\r' \
    | awk -F'\t' '{printf "%-18s  %-10s  %-52s  %-15s  %-4s  %s\n", $1,$2,$3,$4,$5,$6}'
done

DB_FQDN=$(az container show -g "${RESOURCE_GROUP}" -n "${ACI_DB}"  --query "ipAddress.fqdn" -o tsv 2>/dev/null || echo "-")
APP_FQDN=$(az container show -g "${RESOURCE_GROUP}" -n "${ACI_APP}" --query "ipAddress.fqdn" -o tsv 2>/dev/null || echo "-")

echo
echo "====================================================="
echo " Banco ....: ${DB_FQDN}:1521/XEPDB1"
echo " API ......: http://${APP_FQDN}:8080"
echo " Swagger ..: http://${APP_FQDN}:8080/swagger-ui.html"
echo "====================================================="
