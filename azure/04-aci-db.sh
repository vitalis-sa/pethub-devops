#!/usr/bin/env bash
# =====================================================================
# ETAPA 4 - Deploy do ACI do BANCO DE DADOS
#
# Renderiza azure/aci-db.yaml (template versionado, sem segredos)
# preenchendo credenciais buscadas no Azure e no .env, grava o
# resultado em azure/.generated/ (ignorado pelo Git) e cria o ACI.
# =====================================================================
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"
source ./00-vars.sh

echo
echo "[1/6] Coletando credenciais do ACR e da Storage Account..."
ACR_SERVER=$(az acr show --name "${ACR_NAME}" --query loginServer -o tsv)
ACR_USERNAME=$(az acr credential show --name "${ACR_NAME}" --query username -o tsv)
ACR_PASSWORD=$(az acr credential show --name "${ACR_NAME}" --query "passwords[0].value" -o tsv)
STORAGE_KEY=$(az storage account keys list \
  --resource-group "${RESOURCE_GROUP}" \
  --account-name "${STORAGE_ACCOUNT}" \
  --query "[0].value" -o tsv)

RENDERED="${GENERATED_DIR}/aci-db.yaml"

echo "[2/6] Renderizando ${RENDERED} a partir do template..."
# Delimitador '|' no sed porque chaves da Azure contem '/' e '+'.
sed \
  -e "s|__LOCATION__|${LOCATION}|g" \
  -e "s|__ACI_DB__|${ACI_DB}|g" \
  -e "s|__ACR_SERVER__|${ACR_SERVER}|g" \
  -e "s|__ACR_USERNAME__|${ACR_USERNAME}|g" \
  -e "s|__ACR_PASSWORD__|${ACR_PASSWORD}|g" \
  -e "s|__DNS_DB__|${DNS_DB}|g" \
  -e "s|__DB_IMAGE__|${DB_IMAGE}|g" \
  -e "s|__IMAGE_TAG__|${IMAGE_TAG}|g" \
  -e "s|__DB_CPU__|${DB_CPU}|g" \
  -e "s|__DB_MEMORY__|${DB_MEMORY}|g" \
  -e "s|__ORACLE_PWD__|${ORACLE_PWD}|g" \
  -e "s|__APP_DB_USER__|${APP_DB_USER}|g" \
  -e "s|__APP_DB_PASSWORD__|${APP_DB_PASSWORD}|g" \
  -e "s|__FILE_SHARE__|${FILE_SHARE}|g" \
  -e "s|__STORAGE_ACCOUNT__|${STORAGE_ACCOUNT}|g" \
  -e "s|__STORAGE_KEY__|${STORAGE_KEY}|g" \
  ./aci-db.yaml > "${RENDERED}"
chmod 600 "${RENDERED}"

# ---------------------------------------------------------------------
# Pre-voo: o File Share precisa estar APTO a receber os datafiles.
#
# O entrypoint do gvenzl decide se extrai o /opt/oracle/XE.7z olhando um
# unico caminho: oradata/dbconfig/${ORACLE_SID}. Se esse diretorio ja
# existir, ele assume "banco ja inicializado", NAO extrai nada e sobe o
# Oracle sem datafile -> ORA-00205 -> container sai com exit code 205.
#
# Uma tentativa anterior que falhou deixa exatamente esse rastro: um
# dbconfig/XE com 5 arquivos de config e NENHUM datafile. Detectamos essa
# combinacao (dbconfig presente + control file ausente) e abortamos, em
# vez de subir um ACI que vai entrar em CrashLoopBackOff.
# ---------------------------------------------------------------------
echo "[3/6] Conferindo o estado do File Share ${FILE_SHARE}..."

# Antes de qualquer coisa: o share precisa EXISTIR. Sem ele o ACI falha
# com NoFileShareFound. O "az ... --query exists -o tsv" responde em
# minusculo ('true'/'false'), e a caixa ja variou entre versoes do CLI,
# entao normalizamos antes de comparar.
SHARE_EXISTE=$(az storage share exists \
  --name "${FILE_SHARE}" \
  --account-name "${STORAGE_ACCOUNT}" --account-key "${STORAGE_KEY}" \
  --query exists -o tsv 2>/dev/null | tr '[:upper:]' '[:lower:]')

if [[ "${SHARE_EXISTE}" != "true" ]]; then
  echo
  echo "ERRO: o File Share '${FILE_SHARE}' nao existe na conta '${STORAGE_ACCOUNT}'."
  echo "      O ACI nao sobe sem ele (NoFileShareFound). Rode antes:"
  echo
  echo "        bash ./azure/03-storage.sh"
  echo
  exit 1
fi

# O entrypoint testa "[ -d oradata/dbconfig/XE ]", um DIRETORIO. E preciso
# usar "az storage directory exists" para isso: "az storage file exists" so
# enxerga arquivos e devolve false para qualquer diretorio.
# Os dois respondem 'true'/'false' em minusculo, e a caixa ja variou entre
# versoes do CLI -- normalizamos antes de comparar.
share_tem_dir() {
  az storage directory exists \
    --share-name "${FILE_SHARE}" --name "$1" \
    --account-name "${STORAGE_ACCOUNT}" --account-key "${STORAGE_KEY}" \
    --query exists -o tsv 2>/dev/null | tr '[:upper:]' '[:lower:]'
}

share_tem_arquivo() {
  az storage file exists \
    --share-name "${FILE_SHARE}" --path "$1" \
    --account-name "${STORAGE_ACCOUNT}" --account-key "${STORAGE_KEY}" \
    --query exists -o tsv 2>/dev/null | tr '[:upper:]' '[:lower:]'
}

TEM_DBCONFIG=$(share_tem_dir "dbconfig/XE")
TEM_DATAFILE=$(share_tem_arquivo "XE/control01.ctl")

if [[ "${TEM_DBCONFIG}" == "true" && "${TEM_DATAFILE}" != "true" ]]; then
  echo
  echo "ERRO: o share '${FILE_SHARE}' esta num estado inconsistente."
  echo "      Tem dbconfig/XE (marca de 'banco ja inicializado') mas NAO tem"
  echo "      os datafiles (XE/control01.ctl). Sobrou de uma tentativa que"
  echo "      falhou. Se o ACI subir assim, o Oracle nao acha o control file"
  echo "      e o container morre com ORA-00205 (exit code 205)."
  echo
  echo "      Limpe o share e rode este script de novo:"
  echo
  echo "        bash ./azure/97-limpar-share.sh"
  echo
  exit 1
fi

if [[ "${TEM_DATAFILE}" == "true" ]]; then
  echo "      Share ja tem datafiles do Oracle - subida rapida, sem extracao."
else
  echo "      Share vazio - o entrypoint vai extrair o XE.7z (~2,7 GB) via SMB."
fi

echo "[4/6] Criando o ACI ${ACI_DB}..."
# Grupo de containers do ACI e praticamente imutavel: rodar "create" por
# cima de um que ja existe nao reaproveita nada e pode ficar preso em
# "Creating". Removemos antes -- isso NAO apaga dados, que vivem no share.
if az container show -g "${RESOURCE_GROUP}" -n "${ACI_DB}" -o none 2>/dev/null; then
  echo "      Ja existia um ACI ${ACI_DB}; removendo antes de recriar."
  az container delete -g "${RESOURCE_GROUP}" -n "${ACI_DB}" --yes -o none
fi

az container create \
  --resource-group "${RESOURCE_GROUP}" \
  --file "${RENDERED}" \
  -o none

DB_FQDN=$(az container show \
  --resource-group "${RESOURCE_GROUP}" \
  --name "${ACI_DB}" \
  --query "ipAddress.fqdn" -o tsv)

# ---------------------------------------------------------------------
# "az container create" volta assim que o ACI e ACEITO pela Azure -- isso
# NAO quer dizer que o Oracle subiu. Sem esperar de verdade, o script
# imprimia "banco criado" com o container ja em CrashLoopBackOff.
#
# Aqui acompanhamos o estado real ate o banco anunciar que esta pronto,
# e falhamos alto se o container comecar a reiniciar.
# ---------------------------------------------------------------------
echo
echo "[5/6] Aguardando o Oracle inicializar (extracao de ~2,7 GB via SMB)."
echo "      Isso leva de 10 a 30 minutos na PRIMEIRA subida. Ctrl+C nao"
echo "      derruba o ACI - da para reconectar com o 06-status.sh."
echo

TIMEOUT_MIN="${DB_TIMEOUT_MIN:-45}"
LIMITE=$(( $(date +%s) + TIMEOUT_MIN * 60 ))
INICIO=$(date +%s)

# Um campo por consulta, de proposito. Pedir os tres de uma vez em TSV
# ("{s:...,d:...,r:...}") devolve os valores na ordem da QUERY (s,d,r) --
# o -o json e que exibe ordenado por chave -- e ainda termina a linha com
# CRLF. Ler isso com "read" troca os campos de lugar e gruda um \r no
# ultimo. Substituicao de comando "$(...)" nao tem nenhum dos dois vicios.
campo_db() {
  az container show -g "${RESOURCE_GROUP}" -n "${ACI_DB}" \
    --query "containers[0].instanceView.$1" -o tsv 2>/dev/null
}

while :; do
  ESTADO=$(campo_db "currentState.state")
  DETALHE=$(campo_db "currentState.detailStatus")
  REINICIOS=$(campo_db "restartCount")
  SAIDA=$(campo_db "currentState.exitCode")
  DECORRIDO=$(( ($(date +%s) - INICIO) / 60 ))

  # O Oracle so anuncia isso quando o banco esta aberto e o schema aplicado.
  if az container logs -g "${RESOURCE_GROUP}" -n "${ACI_DB}" 2>/dev/null \
       | grep -q "DATABASE IS READY TO USE"; then
    echo "      [${DECORRIDO} min] BANCO PRONTO."
    break
  fi

  # Morreu: com restartPolicy Never o container fica em Terminated; se
  # alguem voltar para Always, restartCount passa de zero. Cobrimos os dois.
  # O teste numerico e feito so depois de confirmar que o valor E numerico:
  # dentro de [[ ]] o "-ge" avalia o operando como ARITMETICA, e um texto
  # como "CrashLoopBackOff: ..." vira nome de variavel -> "unbound variable".
  MORREU=""
  [[ "${ESTADO}" == "Terminated" ]] && MORREU="sim"
  [[ "${REINICIOS}" =~ ^[0-9]+$ ]] && (( REINICIOS >= 1 )) && MORREU="sim"

  if [[ -n "${MORREU}" ]]; then
    echo
    echo "ERRO: o container do banco morreu (exit code ${SAIDA:-?}, reinicios ${REINICIOS:-0})."
    echo
    echo "Log completo do container:"
    az container logs -g "${RESOURCE_GROUP}" -n "${ACI_DB}" 2>/dev/null | tail -40 || true
    echo
    echo "Eventos do ACI:"
    az container show -g "${RESOURCE_GROUP}" -n "${ACI_DB}" \
      --query "containers[0].instanceView.events[-4:].{Evento:name, Msg:message}" -o table
    echo
    echo "Exit code 205 = ORA-00205 (sem control file). Se o share tiver"
    echo "dbconfig/XE sem os datafiles, rode ./97-limpar-share.sh e repita."
    exit 1
  fi

  if [[ $(date +%s) -ge ${LIMITE} ]]; then
    echo
    echo "ERRO: passou de ${TIMEOUT_MIN} min sem o banco ficar pronto."
    echo "      Estado atual: ${ESTADO:-?} / ${DETALHE:-?}"
    echo "      Aumente o limite com: DB_TIMEOUT_MIN=90 bash ./azure/04-aci-db.sh"
    exit 1
  fi

  echo "      [${DECORRIDO} min] estado=${ESTADO:-?} (${DETALHE:-inicializando}) reinicios=${REINICIOS:-0}"
  sleep 30
done

echo "[6/6] ACI do banco no ar."
echo
echo "====================================================="
echo " FQDN do banco: ${DB_FQDN}:1521/XEPDB1"
echo "====================================================="
echo
echo "Proximo passo: ./05-aci-app.sh"
