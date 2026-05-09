# ADR 0001 - EJB remoto via JNDI

## Contexto

O desafio propõe uma arquitetura em camadas com banco de dados, EJB, backend REST e frontend. Como o backend Spring Boot roda em processo separado do WildFly, a integração não pode usar EJB local.

## Decisão

O módulo `beneficio-ejb` é empacotado como JAR implantável no WildFly e expõe `BeneficioRemoteService` como interface remota. O módulo `beneficio-api` consome esse contrato via lookup JNDI encapsulado no adapter `adapter.out.ejb`.

## Consequências

- O backend REST não conhece detalhes de JPA, datasource ou WildFly além do adapter de saída.
- O contrato remoto fica isolado em `beneficio-contract`.
- O JNDI é detalhe de infraestrutura e pode ser substituído sem alterar controllers ou serviço de aplicação.
