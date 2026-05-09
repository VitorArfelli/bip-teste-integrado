# ADR 0003 - Estrutura multimodule e pacotes internos

## Contexto

Módulos Maven irmãos na raiz são comuns em projetos multi-module, mas pacotes internos genéricos reduzem a clareza arquitetural.

## Decisão

Os módulos foram mantidos na raiz e os pacotes internos foram reorganizados por responsabilidade:

- `beneficio-contract`: `dto`, `service`, `exception`;
- `beneficio-ejb`: `entity`, `service`, `mapper`, `validation`;
- `beneficio-api`: `adapter.in.rest.dto`, `adapter.in.rest.mapper`, `adapter.in.rest.exception`, `adapter.out.ejb`, `application`, `application.port`, `config`;
- `beneficio-web`: feature `beneficios` com `components`, `models` e `services`.

## Consequências

- A raiz continua simples para build, Docker Compose e navegação do projeto.
- O fluxo REST -> aplicação -> porta -> adapter EJB fica explícito.
- O domínio persistente permanece no EJB e não vaza para a API REST.
