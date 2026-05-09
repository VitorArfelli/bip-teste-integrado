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

origin_payload="{\"nome\":\"Concorrencia Origem $suffix\",\"descricao\":\"Conta criada pelo teste de concorrencia\",\"valorCentavos\":100000,\"ativo\":true}"
target_payload="{\"nome\":\"Concorrencia Destino $suffix\",\"descricao\":\"Conta criada pelo teste de concorrencia\",\"valorCentavos\":0,\"ativo\":true}"

origin=$(curl -sS -X POST "$API_URL/api/v1/beneficios" \
  -H 'Content-Type: application/json' \
  -d "$origin_payload")
target=$(curl -sS -X POST "$API_URL/api/v1/beneficios" \
  -H 'Content-Type: application/json' \
  -d "$target_payload")

origin_id=$(json_get "$origin" id)
target_id=$(json_get "$target" id)

transfer_payload="{\"origemId\":$origin_id,\"destinoId\":$target_id,\"valorCentavos\":80000}"

tmp_one=$(mktemp)
tmp_two=$(mktemp)

curl -sS -w '\nHTTP %{http_code}\n' -X POST "$API_URL/api/v1/beneficios/transferencias" \
  -H 'Content-Type: application/json' \
  -d "$transfer_payload" > "$tmp_one" &
pid_one=$!

curl -sS -w '\nHTTP %{http_code}\n' -X POST "$API_URL/api/v1/beneficios/transferencias" \
  -H 'Content-Type: application/json' \
  -d "$transfer_payload" > "$tmp_two" &
pid_two=$!

wait "$pid_one" || true
wait "$pid_two" || true

beneficios=$(curl -sS "$API_URL/api/v1/beneficios")
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
if [ "$origin_balance" -ge 0 ]; then
  echo
  echo "OK: origin balance is not negative."
else
  echo
  echo "ERROR: origin balance is negative." >&2
  exit 1
fi
