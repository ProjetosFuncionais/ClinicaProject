#!/bin/bash
# Gera os PDFs da entrega a partir dos HTML desta pasta.
# Uso: ./gerar-pdfs.sh            (todos)
#      ./gerar-pdfs.sh cr9        (só um: cr1, cr9, apresentacao ou roteiro)
set -e
cd "$(dirname "$0")"
SAIDA=".."

# Procura um Chrome/Chromium instalado
CHROME=""
for c in \
  "$HOME"/Library/Caches/ms-playwright/chromium_headless_shell-*/chrome-headless-shell-mac-*/chrome-headless-shell \
  "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" \
  "/Applications/Chromium.app/Contents/MacOS/Chromium" \
  "$(command -v google-chrome 2>/dev/null)" "$(command -v chromium 2>/dev/null)"; do
  if [ -x "$c" ]; then CHROME="$c"; break; fi
done
[ -z "$CHROME" ] && { echo "Chrome não encontrado"; exit 1; }

python3 montar_cr9.py

gerar() {  # $1 = html, $2 = pdf
  "$CHROME" --headless --disable-gpu --no-pdf-header-footer --allow-file-access-from-files \
    --run-all-compositor-stages-before-draw --virtual-time-budget=8000 \
    --print-to-pdf="$SAIDA/$2" "file://$PWD/$1" 2>/dev/null
  echo "ok: $2"
}

alvo="${1:-todos}"
[[ $alvo == todos || $alvo == cr1 ]] && gerar cr1.html "CR1 - Documento de Especificação da API.pdf"
[[ $alvo == todos || $alvo == cr9 ]] && gerar cr9.html "CR9 - Evidência dos Testes Funcionais.pdf"
[[ $alvo == todos || $alvo == apresentacao ]] && gerar apresentacao.html "Apresentação - Clínica API.pdf"
[[ $alvo == todos || $alvo == roteiro ]] && gerar roteiro.html "Roteiro da Apresentação em Vídeo.pdf"
exit 0
