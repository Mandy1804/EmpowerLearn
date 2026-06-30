#!/usr/bin/env bash
set -e

BASE_URL="${BASE_URL:-http://localhost:8080}"
OUT_DIR="docs/evidencias/carga"

mkdir -p "$OUT_DIR"

echo "========================================"
echo "Teste de carga EmpowerLearn - Autocannon"
echo "Base URL: $BASE_URL"
echo "Saida: $OUT_DIR"
echo "========================================"

echo ""
echo "[1/4] Validando disponibilidade da API..."
curl -s "$BASE_URL/api/planos" > /dev/null
curl -s "$BASE_URL/api/professores" > /dev/null
echo "API OK."

echo ""
echo "[2/4] Leitura - GET /api/planos"
npx autocannon -c 50 -d 30 -p 10 "$BASE_URL/api/planos" | tee "$OUT_DIR/01_get_planos.txt"

echo ""
echo "[3/4] Leitura - GET /api/professores"
npx autocannon -c 50 -d 30 -p 10 "$BASE_URL/api/professores" | tee "$OUT_DIR/02_get_professores.txt"

echo ""
echo "[4/4] Escrita controlada - POST /api/pagamentos/stripe/checkout com plano GRATUITO"
BODY='{"planoCodigo":"GRATUITO","usuarioId":999999,"usuarioTipo":"ALUNO","nome":"Teste Carga","email":"teste.carga@empowerlearn.com.br"}'

npx autocannon \
  -c 10 \
  -d 20 \
  -m POST \
  -H "Content-Type: application/json" \
  -b "$BODY" \
  "$BASE_URL/api/pagamentos/stripe/checkout" | tee "$OUT_DIR/03_post_checkout_gratuito.txt"

echo ""
echo "Testes de carga finalizados."
echo "Arquivos gerados em: $OUT_DIR"
