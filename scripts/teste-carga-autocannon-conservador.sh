#!/usr/bin/env bash
set -e

BASE_URL="${BASE_URL:-http://localhost:8080}"
OUT_DIR="docs/evidencias/carga"

mkdir -p "$OUT_DIR"

echo "Teste conservador de carga - EmpowerLearn"
echo "Base URL: $BASE_URL"

curl -s "$BASE_URL/api/planos" > /dev/null
curl -s "$BASE_URL/api/professores" > /dev/null

echo "[1/3] GET /api/planos"
npx autocannon -c 20 -d 20 -p 5 "$BASE_URL/api/planos" | tee "$OUT_DIR/04_get_planos_conservador.txt"

echo "[2/3] GET /api/professores"
npx autocannon -c 20 -d 20 -p 5 "$BASE_URL/api/professores" | tee "$OUT_DIR/05_get_professores_conservador.txt"

echo "[3/3] POST /api/pagamentos/stripe/checkout GRATUITO"
BODY='{"planoCodigo":"GRATUITO","usuarioId":888888,"usuarioTipo":"ALUNO","nome":"Teste Carga Conservador","email":"teste.carga.conservador@empowerlearn.com.br"}'

npx autocannon -c 5 -d 15 -m POST -H "Content-Type: application/json" -b "$BODY" "$BASE_URL/api/pagamentos/stripe/checkout" | tee "$OUT_DIR/06_post_checkout_gratuito_conservador.txt"

echo "Teste conservador finalizado."
