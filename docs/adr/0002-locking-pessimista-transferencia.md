# ADR 0002 - Locking pessimista na transferencia

## Contexto

A transferência entre benefícios altera dois saldos em uma operação crítica. O problema central do desafio é evitar saldo negativo, lost update e inconsistência em chamadas concorrentes.

## Decisão

A transferência usa transação container-managed no EJB e leitura dos benefícios com `LockModeType.PESSIMISTIC_WRITE`. Os locks são adquiridos por ID ordenado para reduzir risco de deadlock. A entidade mantém `@Version` para controle otimista e rastreabilidade de versão.

## Consequências

- Duas transferências simultâneas sobre a mesma origem são serializadas pelo banco.
- Se a primeira consumir saldo, a segunda reavalia o saldo atualizado e falha por regra de negócio.
- Falhas de validação lançam exceções runtime e provocam rollback automático.
