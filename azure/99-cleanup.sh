#!/usr/bin/env bash
# =====================================================================
# LIMPEZA - apaga TODO o Resource Group do checkpoint.
#
# Rode isto depois de gravar o video, para nao consumir credito da
# subscription. A operacao e IRREVERSIVEL: leva junto o ACR (com as
# imagens), a Conta de Armazenamento (com os datafiles do Oracle) e
# os dois ACIs.
# =====================================================================
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"
source ./00-vars.sh

echo
echo "Isto vai APAGAR o Resource Group ${RESOURCE_GROUP} e tudo dentro dele:"
az resource list \
  --resource-group "${RESOURCE_GROUP}" \
  --query "[].{Nome:name, Tipo:type}" \
  --output table

echo
read -r -p "Digite o nome do Resource Group para confirmar: " CONFIRMACAO

if [[ "${CONFIRMACAO}" != "${RESOURCE_GROUP}" ]]; then
  echo "Nome nao confere. Nada foi apagado."
  exit 1
fi

echo
echo "Apagando ${RESOURCE_GROUP} (roda em segundo plano na Azure)..."
az group delete --name "${RESOURCE_GROUP}" --yes --no-wait

echo
echo "Solicitacao enviada. Acompanhe com:"
echo "  az group show --name ${RESOURCE_GROUP} --query properties.provisioningState -o tsv"
