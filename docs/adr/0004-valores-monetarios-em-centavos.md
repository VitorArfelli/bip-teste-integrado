# ADR 0004 - Valores monetários em centavos

## Contexto

O fluxo de transferência trabalha com saldo monetário e executa somas e subtrações, sem divisão ou cálculo proporcional.

A representação monetária foi definida em centavos inteiros (`Long`). Essa é uma prática comum em sistemas financeiros quando o objetivo é manter contratos simples, padronizar escala e reduzir erros de conversão entre banco, contrato remoto, API REST e frontend.

## Decisão

Valores monetários são representados como `valorCentavos` nas entidades, DTOs, API e banco de dados.

Exemplo:

```json
{
  "valorCentavos": 1050
}
```

Representa R$ 10,50.

## Consequências

- O contrato entre camadas fica explícito e independente de escala decimal.
- Evita conversões inconsistentes de casas decimais em evoluções futuras da API e do frontend.
- As operações críticas de crédito e débito usam aritmética inteira.
- `BigDecimal` continua sendo uma opção válida e comum em regras financeiras dentro do domínio; para este contrato, centavos inteiros foram escolhidos para simplificar integração e persistência.
