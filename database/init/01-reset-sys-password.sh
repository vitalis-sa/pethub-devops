#!/usr/bin/env bash
# =====================================================================
# PetHub - garante a senha do SYS/SYSTEM em TODO boot do container
#
# POR QUE ESTE SCRIPT EXISTE
# ---------------------------
# A imagem base (gvenzl/oracle-xe) so reseta a senha do SYS/SYSTEM na
# PRIMEIRA inicializacao do banco (via ORACLE_PASSWORD), chamando
# resetPassword uma unica vez. Da segunda subida em diante ela conclui
# "banco ja inicializado" e nunca mais roda esse passo -- SYS/SYSTEM
# ficam com o que quer que tenha sido definido da ultima vez.
#
# No ACI, o disco local do container (tudo FORA do File Share montado
# em /opt/oracle/oradata) e efemero: cada "az container create" comeca
# de novo a partir da imagem, com o orapwXE de BUILD -- uma senha
# ALEATORIA gerada uma unica vez quando a gvenzl compilou a imagem
# (ver install.2130.sh: "date +%s | sha256sum | base64 | head -c 8"),
# nunca publicada em lugar nenhum. Sem este script, toda recriacao do
# ACI deixaria SYS/SYSTEM com uma senha desconhecida e irrecuperavel.
#
# Por isso resetamos a senha do SYS/SYSTEM a cada boot, incondicional-
# mente, para o valor de ORACLE_PASSWORD. "ALTER USER ... IDENTIFIED
# BY" e idempotente: repetir numa subida em que a senha ja esta certa
# nao tem efeito nenhum.
#
# PRE-REQUISITO: o Dockerfile deste projeto desativa o mv/symlink do
# orapwXE para o File Share (ver comentario la). Sem essa desativacao
# o comando abaixo falha com ORA-01990 (exit code 198 = 1990 mod 256):
# o Azure Files via SMB nao sustenta a escrita que o Oracle faz no
# password file a cada troca de senha do SYS.
# =====================================================================
set -euo pipefail

if [[ -z "${ORACLE_PASSWORD:-}" ]]; then
  echo "PETHUB: ORACLE_PASSWORD nao definido, pulando o reset de SYS/SYSTEM."
  exit 0
fi

echo "PETHUB: garantindo a senha do SYS/SYSTEM..."
resetPassword "${ORACLE_PASSWORD}"
echo "PETHUB: senha do SYS/SYSTEM confirmada."
