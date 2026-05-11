#!/usr/bin/env bash
set -euo pipefail

# Smoke test integrado da API, EJB/WildFly e PostgreSQL.
#
# Pré-requisitos:
# - Stack disponível via Docker Compose ou equivalente.
# - API disponível em http://localhost:8080 ou em API_URL.
# - curl e node instalados localmente.

API_URL="${API_URL:-http://localhost:8080}"
suffix="$(date +%s)"

json_get() {
  node -e "const data = JSON.parse(process.argv[1]); console.log(data[process.argv[2]]);" "$1" "$2"
}

json_pretty() {
  node -e "console.log(JSON.stringify(JSON.parse(process.argv[1]), null, 2));" "$1"
}

request() {
  local method="$1"
  local url="$2"
  local payload="${3:-}"
  local output="$4"

  if [ -n "$payload" ]; then
    curl --max-time 20 -sS -w '\nHTTP %{http_code}\n' -X "$method" "$url" \
      -H 'Content-Type: application/json' \
      -d "$payload" > "$output"
  else
    curl --max-time 20 -sS -w '\nHTTP %{http_code}\n' -X "$method" "$url" > "$output"
  fi
}

body() {
  sed '/^HTTP /d' "$1"
}

status() {
  sed -n 's/^HTTP //p' "$1" | tail -n 1
}

expect_status() {
  local file="$1"
  local expected="$2"
  local actual
  actual="$(status "$file")"
  if [ "$actual" != "$expected" ]; then
    echo "ERROR: expected HTTP $expected, got HTTP $actual." >&2
    cat "$file" >&2
    exit 1
  fi
}

wait_for_api() {
  for _ in $(seq 1 60); do
    if curl --max-time 2 -fsS "$API_URL/actuator/health" >/dev/null 2>&1; then
      return
    fi
    sleep 2
  done
  echo "ERROR: API did not become healthy at $API_URL." >&2
  exit 1
}

wait_for_api

origin_payload="{\"nome\":\"Smoke Origem $suffix\",\"descricao\":\"Conta criada pelo smoke integrado\",\"valorCentavos\":50000,\"ativo\":true}"
target_payload="{\"nome\":\"Smoke Destino $suffix\",\"descricao\":\"Conta criada pelo smoke integrado\",\"valorCentavos\":10000,\"ativo\":true}"

origin_file="$(mktemp)"
target_file="$(mktemp)"
transfer_file="$(mktemp)"
origin_after_file="$(mktemp)"
target_after_file="$(mktemp)"
transfers_file="$(mktemp)"

request POST "$API_URL/api/v1/beneficios" "$origin_payload" "$origin_file"
request POST "$API_URL/api/v1/beneficios" "$target_payload" "$target_file"
expect_status "$origin_file" 201
expect_status "$target_file" 201

origin="$(body "$origin_file")"
target="$(body "$target_file")"
origin_id="$(json_get "$origin" id)"
target_id="$(json_get "$target" id)"

transfer_payload="{\"origemId\":$origin_id,\"destinoId\":$target_id,\"valorCentavos\":15000}"
request POST "$API_URL/api/v1/beneficios/transferencias" "$transfer_payload" "$transfer_file"
expect_status "$transfer_file" 201

request GET "$API_URL/api/v1/beneficios/$origin_id" "" "$origin_after_file"
request GET "$API_URL/api/v1/beneficios/$target_id" "" "$target_after_file"
request GET "$API_URL/api/v1/beneficios/transferencias" "" "$transfers_file"
expect_status "$origin_after_file" 200
expect_status "$target_after_file" 200
expect_status "$transfers_file" 200

origin_after="$(body "$origin_after_file")"
target_after="$(body "$target_after_file")"
transfers="$(body "$transfers_file")"

origin_balance="$(json_get "$origin_after" valorCentavos)"
target_balance="$(json_get "$target_after" valorCentavos)"
transfer_found="$(node -e "const data = JSON.parse(process.argv[1]); const found = data.some((item) => item.origemId === Number(process.argv[2]) && item.destinoId === Number(process.argv[3]) && item.valorCentavos === 15000); console.log(found ? 'true' : 'false');" "$transfers" "$origin_id" "$target_id")"

if [ "$origin_balance" -ne 35000 ] || [ "$target_balance" -ne 25000 ]; then
  echo "ERROR: unexpected balances. Expected origin=35000 and target=25000, got origin=$origin_balance and target=$target_balance." >&2
  echo "Origin after:"
  json_pretty "$origin_after"
  echo "Target after:"
  json_pretty "$target_after"
  exit 1
fi

if [ "$transfer_found" != "true" ]; then
  echo "ERROR: transfer audit entry was not found." >&2
  json_pretty "$transfers"
  exit 1
fi

echo "OK: integrated smoke test passed for origin=$origin_id and target=$target_id."
