# Documentação Técnica

Esta pasta guarda apenas documentação técnica complementar.

## ADRs

- `adr/0001-ejb-remoto-via-jndi.md`: decisão de integrar Spring Boot e WildFly via EJB remoto.
- `adr/0002-locking-pessimista-transferencia.md`: estratégia de consistência da transferência.
- `adr/0003-estrutura-multimodule-e-pacotes.md`: organização Maven multi-module e pacotes.
- `adr/0004-valores-monetarios-em-centavos.md`: representação monetária em centavos inteiros.

## Contexto do desafio

O objetivo foi entregar uma solução em camadas com DB, EJB, backend REST, frontend Angular, testes e documentação, corrigindo o bug de transferência sem validação de saldo e sem locking.
