#!/usr/bin/env bash
set -euo pipefail

# Teste manual de concorrência da transferência.
#
# Objetivo:
# - Criar dois benefícios temporários.
# - Disparar duas transferências simultâneas de 80000 centavos sobre a mesma
#   origem, que possui saldo inicial de 100000 centavos.
# - Confirmar que o saldo da origem nunca fica negativo.
#
# Resultado esperado:
# - Apenas uma transferência deve ser concluída com sucesso, ou uma delas deve
#   falhar por regra de negócio após a primeira consumir saldo suficiente.
# - O saldo final da origem deve permanecer maior ou igual a zero.
#
# Pré-requisitos:
# - API disponível em http://localhost:8080 ou em API_URL.
# - EJB/WildFly e PostgreSQL operacionais.
# - curl e node instalados localmente.
#
# Uso:
#   bash scripts/concurrency-test.sh
#   API_URL=http://localhost:8080 bash scripts/concurrency-test.sh

API_URL="${API_URL:-http://localhost:8080}"
suffix="$(date +%s)"

json_get() {
  node -e "const data = JSON.parse(process.argv[1]); console.log(data[process.argv[2]]);" "$1" "$2"
}

json_select_id() {
  node -e "const data = JSON.parse(process.argv[1]); const item = data.find((entry) => entry.id === Number(process.argv[2])); console.log(JSON.stringify(item));" "$1" "$2"
}

json_pretty() {
  node -e "console.log(JSON.stringify(JSON.parse(process.argv[1]), null, 2));" "$1"
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

http_status() {
  sed -n 's/^HTTP //p' "$1" | tail -n 1
}

wait_for_api

origin_payload="{\"nome\":\"Concorrencia Origem $suffix\",\"descricao\":\"Conta criada pelo teste de concorrencia\",\"valorCentavos\":100000,\"ativo\":true}"
target_payload="{\"nome\":\"Concorrencia Destino $suffix\",\"descricao\":\"Conta criada pelo teste de concorrencia\",\"valorCentavos\":0,\"ativo\":true}"

origin=$(curl --max-time 20 -sS -X POST "$API_URL/api/v1/beneficios" \
  -H 'Content-Type: application/json' \
  -d "$origin_payload")
target=$(curl --max-time 20 -sS -X POST "$API_URL/api/v1/beneficios" \
  -H 'Content-Type: application/json' \
  -d "$target_payload")

origin_id=$(json_get "$origin" id)
target_id=$(json_get "$target" id)

transfer_payload="{\"origemId\":$origin_id,\"destinoId\":$target_id,\"valorCentavos\":80000}"

tmp_one=$(mktemp)
tmp_two=$(mktemp)

curl --max-time 20 -sS -w '\nHTTP %{http_code}\n' -X POST "$API_URL/api/v1/beneficios/transferencias" \
  -H 'Content-Type: application/json' \
  -d "$transfer_payload" > "$tmp_one" &
pid_one=$!

curl --max-time 20 -sS -w '\nHTTP %{http_code}\n' -X POST "$API_URL/api/v1/beneficios/transferencias" \
  -H 'Content-Type: application/json' \
  -d "$transfer_payload" > "$tmp_two" &
pid_two=$!

wait "$pid_one" || true
wait "$pid_two" || true

beneficios=$(curl --max-time 20 -sS "$API_URL/api/v1/beneficios")
origin_after=$(json_select_id "$beneficios" "$origin_id")
target_after=$(json_select_id "$beneficios" "$target_id")

echo "Origin id: $origin_id"
echo "Target id: $target_id"
echo
echo "Transfer response 1:"
cat "$tmp_one"
echo
echo "Transfer response 2:"
cat "$tmp_two"
echo
echo "Origin after:"
json_pretty "$origin_after"
echo
echo "Target after:"
json_pretty "$target_after"

origin_balance=$(json_get "$origin_after" valorCentavos)
target_balance=$(json_get "$target_after" valorCentavos)
status_one=$(http_status "$tmp_one")
status_two=$(http_status "$tmp_two")
success_count=0
business_failure_count=0

for status in "$status_one" "$status_two"; do
  if [ "$status" = "201" ]; then
    success_count=$((success_count + 1))
  elif [ "$status" = "400" ]; then
    business_failure_count=$((business_failure_count + 1))
  fi
done

if [ "$success_count" -ne 1 ] || [ "$business_failure_count" -ne 1 ]; then
  echo
  echo "ERROR: expected exactly one 201 response and one 400 response. Got $status_one and $status_two." >&2
  exit 1
fi

if [ "$origin_balance" -lt 0 ]; then
  echo
  echo "ERROR: origin balance is negative." >&2
  exit 1
fi

if [ "$origin_balance" -ne 20000 ] || [ "$target_balance" -ne 80000 ]; then
  echo
  echo "ERROR: unexpected balances. Expected origin=20000 and target=80000, got origin=$origin_balance and target=$target_balance." >&2
  exit 1
fi

echo
echo "OK: exactly one transfer succeeded, one failed by business rule, and balances are consistent."
