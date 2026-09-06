#!/usr/bin/env bash
# =====================================================================
# Esvazia o File Share do banco, para uma reinicializacao limpa.
#
# QUANDO USAR
# -----------
# Quando o ACI do banco morre com exit code 205 (ORA-00205, "error in
# identifying control file"). Isso acontece quando o share tem o
# diretorio dbconfig/XE mas NAO tem os datafiles: o entrypoint do
# gvenzl usa a existencia de dbconfig/<SID> como unica prova de que o
# banco ja foi inicializado, entao ele PULA a extracao do XE.7z e sobe
# o Oracle sem control file.
#
# POR QUE ESVAZIAR EM VEZ DE APAGAR O SHARE
# -----------------------------------------
# "az storage share delete" so marca para exclusao: por varios minutos
# o nome fica preso e recriar devolve "ErrorCode:ShareBeingDeleted".
# Apagando so o CONTEUDO, o nome nunca sai do ar e o 03-storage.sh nao
# precisa esperar nada.
#
# ATENCAO: isto apaga os dados do banco na nuvem. O DDL e o seed sao
# reaplicados sozinhos na proxima subida (init/00-pethub-schema.sh).
# =====================================================================
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"
source ./00-vars.sh

CONFIRMA="${1:-}"
if [[ "${CONFIRMA}" != "-y" ]]; then
  echo
  echo "Isto vai APAGAR todo o conteudo do File Share '${FILE_SHARE}'"
  echo "na conta '${STORAGE_ACCOUNT}' (os datafiles do Oracle)."
  echo
  read -r -p "Digite 'sim' para continuar: " resposta
  [[ "${resposta}" == "sim" ]] || { echo "Cancelado."; exit 1; }
fi

echo
echo "[1/3] Removendo o ACI ${ACI_DB} (ele mantem o share montado por SMB)..."
az container delete -g "${RESOURCE_GROUP}" -n "${ACI_DB}" --yes -o none 2>/dev/null \
  && echo "      ACI removido." \
  || echo "      ACI nao existia, seguindo."

STORAGE_KEY=$(az storage account keys list \
  --resource-group "${RESOURCE_GROUP}" \
  --account-name "${STORAGE_ACCOUNT}" \
  --query "[0].value" -o tsv)

echo
echo "[2/3] Apagando os arquivos..."
az storage file delete-batch \
  --source "${FILE_SHARE}" \
  --account-name "${STORAGE_ACCOUNT}" \
  --account-key "${STORAGE_KEY}" \
  --pattern "*" \
  -o none
echo "      Arquivos apagados."

# Os diretorios sobram vazios, e um dbconfig/XE vazio ainda faz o
# entrypoint concluir "banco ja inicializado". Removemos do mais
# profundo para o mais raso, que e a unica ordem que a API aceita.
echo
echo "[3/3] Apagando os diretorios..."

# Os argumentos vao num array: montar a linha com ${base:+--path "$base"}
# passa as ASPAS literalmente para o az, que entao procura um diretorio
# chamado "dbconfig" (com aspas) e nao acha nada.
#
# O "tr -d" existe porque no Git Bash o "az -o tsv" termina as linhas com
# CRLF: "read -r" remove o LF e deixa o CR grudado no nome, e ai a API
# responde InvalidResourceName. Substituicao de comando "$(...)" nao
# sofre disso -- so o "while read".
listar_dirs() {
  local base="$1"
  local nome caminho
  local args=(
    --share-name "${FILE_SHARE}"
    --account-name "${STORAGE_ACCOUNT}" --account-key "${STORAGE_KEY}"
    --query "[?type=='dir'].name" -o tsv
  )
  [[ -n "${base}" ]] && args+=(--path "${base}")

  while IFS= read -r nome; do
    [[ -z "${nome}" ]] && continue
    caminho="${base:+${base}/}${nome}"
    listar_dirs "${caminho}"      # mais profundo primeiro
    echo "${caminho}"
  done < <(az storage file list "${args[@]}" 2>/dev/null | tr -d '\r')
}

while IFS= read -r dir; do
  [[ -z "${dir}" ]] && continue
  az storage directory delete \
    --share-name "${FILE_SHARE}" --name "${dir}" \
    --account-name "${STORAGE_ACCOUNT}" --account-key "${STORAGE_KEY}" \
    -o none && echo "      removido: ${dir}"
done < <(listar_dirs "" | tr -d '\r')

# Conferir de verdade, em vez de anunciar sucesso na fe.
RESTOU=$(az storage file list \
  --share-name "${FILE_SHARE}" \
  --account-name "${STORAGE_ACCOUNT}" --account-key "${STORAGE_KEY}" \
  --query "length(@)" -o tsv)

if [[ "${RESTOU}" != "0" ]]; then
  echo
  echo "ERRO: sobrou conteudo no share '${FILE_SHARE}':"
  az storage file list \
    --share-name "${FILE_SHARE}" \
    --account-name "${STORAGE_ACCOUNT}" --account-key "${STORAGE_KEY}" -o table
  echo
  echo "      Apague o que sobrou pelo portal antes de rodar o 04-aci-db.sh."
  exit 1
fi

echo
echo "====================================================="
echo " Share ${FILE_SHARE} limpo (raiz vazia, conferido)."
echo " Proximo passo: bash ./azure/04-aci-db.sh"
echo "====================================================="
