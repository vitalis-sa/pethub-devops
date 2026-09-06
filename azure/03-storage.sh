#!/usr/bin/env bash
# =====================================================================
# ETAPA 3 - Conta de Armazenamento + File Share
#
# Atende ao item "Persistir os dados do banco em uma Conta de
# Armazenamento": o File Share criado aqui e montado em
# /opt/oracle/oradata dentro do ACI do banco, de modo que os datafiles
# do Oracle sobrevivem a destruicao e recriacao do container.
# =====================================================================
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"
source ./00-vars.sh

echo
echo "[1/3] Criando Conta de Armazenamento ${STORAGE_ACCOUNT}..."
az storage account create \
  --resource-group "${RESOURCE_GROUP}" \
  --name "${STORAGE_ACCOUNT}" \
  --location "${LOCATION}" \
  --sku Standard_LRS \
  --kind StorageV2 \
  --output table

echo
echo "[2/3] Recuperando a chave de acesso (nao e gravada em disco)..."
STORAGE_KEY=$(az storage account keys list \
  --resource-group "${RESOURCE_GROUP}" \
  --account-name "${STORAGE_ACCOUNT}" \
  --query "[0].value" -o tsv)

echo
echo "[3/3] Criando File Share ${FILE_SHARE} (${FILE_SHARE_QUOTA_GB} GB)..."
# "az storage share delete" nao apaga na hora: por alguns minutos o nome
# fica preso em exclusao e recriar devolve ErrorCode:ShareBeingDeleted.
# Em vez de quebrar o script (e obrigar a inventar um 'oradata2'), esperamos
# o nome ser liberado. Para limpar sem esperar nada use 97-limpar-share.sh,
# que apaga o CONTEUDO e preserva o nome do share.
TENTATIVAS=20
for (( i=1; i<=TENTATIVAS; i++ )); do
  if SAIDA=$(az storage share create \
      --name "${FILE_SHARE}" \
      --account-name "${STORAGE_ACCOUNT}" \
      --account-key "${STORAGE_KEY}" \
      --quota "${FILE_SHARE_QUOTA_GB}" \
      --output table 2>&1); then
    echo "${SAIDA}"
    break
  fi

  if [[ "${SAIDA}" != *"ShareBeingDeleted"* ]]; then
    echo "${SAIDA}" >&2
    exit 1
  fi

  if (( i == TENTATIVAS )); then
    echo "ERRO: o share '${FILE_SHARE}' segue em exclusao apos ${TENTATIVAS} tentativas." >&2
    exit 1
  fi

  echo "      Share ainda em exclusao na Azure, nova tentativa em 15s (${i}/${TENTATIVAS})..."
  sleep 15
done

echo
echo "Compartilhamentos na conta:"
az storage share list \
  --account-name "${STORAGE_ACCOUNT}" \
  --account-key "${STORAGE_KEY}" \
  --output table

echo
echo "====================================================="
echo " Storage Account: ${STORAGE_ACCOUNT}"
echo " File Share ....: ${FILE_SHARE}"
echo " Proximo passo: ./04-aci-db.sh"
echo "====================================================="
