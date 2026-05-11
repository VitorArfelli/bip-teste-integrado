# BenefitOps

Aplicação fullstack para gestão de benefícios, saldos e transferências.

O projeto foi organizado em camadas com banco de dados, EJB, API REST, frontend Angular, testes, documentação e CI. O fluxo crítico é a transferência entre benefícios, implementada com validações, transação, rollback e locking para evitar saldo negativo em cenários concorrentes.

## Escopo implementado

- Banco de dados com `db/schema.sql` e `db/seed.sql`.
- Correção da transferência no EJB com validações, rollback transacional e locking pessimista.
- Backend REST com CRUD de benefícios e endpoint de transferência.
- Integração da API Spring Boot com EJB remoto em WildFly via JNDI.
- Frontend Angular consumindo a API.
- Testes automatizados para EJB, API e frontend.
- Documentação via README, ADRs e Swagger/OpenAPI.
- Observabilidade básica com Actuator e logs com correlation id.

## Arquitetura

```txt
beneficio-contract  -> contratos remotos, DTOs e exceções compartilhadas
beneficio-ejb       -> regra transacional, JPA/Hibernate e persistência
beneficio-api       -> REST API, aplicação e adapter JNDI para o EJB
beneficio-web       -> interface Angular
db                  -> scripts SQL de schema e seed
docs/adr            -> decisões arquiteturais
```

A estrutura original do template foi reorganizada para um projeto Maven multi-module, mantendo o domínio do desafio (`Beneficio` e `Transferencia`) e removendo nomes genéricos de pacote.

Na API foi usada uma abordagem hexagonal pragmática: controllers são adapters de entrada, o cliente JNDI é adapter de saída e a camada de aplicação depende de uma porta, não do detalhe de infraestrutura.

## Banco de dados

O banco é inicializado pelos scripts `db/schema.sql` e `db/seed.sql`.

No Docker Compose, esses arquivos são montados em `/docker-entrypoint-initdb.d`, recurso nativo da imagem oficial do PostgreSQL para inicialização do banco.

A tabela `BENEFICIO` preserva a estrutura inicial do domínio, com a alteração de `VALOR` para `VALOR_CENTAVOS`. A tabela `TRANSFERENCIA` foi adicionada para registrar auditoria das transferências realizadas.

## Decisões técnicas

- Spring Boot e WildFly rodam em processos separados; por isso a integração usa EJB remoto via JNDI.
- A entidade JPA fica no `beneficio-ejb`; a API conhece contratos e DTOs.
- A transferência usa `PESSIMISTIC_WRITE` e aquisição de locks ordenada por ID para evitar lost update e reduzir risco de deadlock.
- `@Version` foi mantido na entidade para controle otimista e aderência ao schema.
- Exceções de negócio são runtime para preservar rollback automático no EJB.
- Valores monetários trafegam como centavos (`valorCentavos`), prática comum em sistemas financeiros para padronizar escala e reduzir erros de conversão entre camadas.

## Como subir com Docker

Pré-requisito: Docker e Docker Compose.

```bash
docker compose up --build
```

Serviços principais:

- Frontend: http://localhost:4200
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health
- Ponte API -> EJB: http://localhost:8080/api/v1/health/ejb

O Compose sobe PostgreSQL, inicializa o banco com `schema.sql` e `seed.sql`, implanta o EJB no WildFly, inicia a API e expõe o Angular.

## Como subir sem Docker

Pré-requisitos:

- Java 17
- Maven 3.9+
- Node.js 20+
- PostgreSQL local
- WildFly 31+

1. Crie um banco PostgreSQL e aplique os scripts:

```bash
psql -U postgres -d beneficios -f db/schema.sql
psql -U postgres -d beneficios -f db/seed.sql
```

2. Compile os módulos Java:

```bash
mvn clean package
```

3. Configure no WildFly um datasource JTA chamado `java:/jdbc/BeneficiosDS` apontando para o PostgreSQL. O arquivo `docker/wildfly/configure.cli` serve como referência da configuração usada no Docker.

4. Faça deploy do EJB:

```bash
cp beneficio-ejb/target/beneficio-ejb-0.0.1-SNAPSHOT.jar $WILDFLY_HOME/standalone/deployments/
```

5. Suba a API apontando para o WildFly:

```bash
cd beneficio-api
mvn spring-boot:run
```

6. Suba o frontend:

```bash
cd beneficio-web
npm install
npm start
```

O `npm start` usa proxy local para encaminhar `/api` para `http://localhost:8080`.

## Testes

Estratégia de cobertura:

- `beneficio-ejb`: testes unitários de entidade e validações de negócio.
- `beneficio-api`: testes de camada REST com `MockMvc` standalone, cobrindo controllers, validação de payload, exception handler, status HTTP e formato de erro.
- `beneficio-web`: testes unitários do frontend Angular.

Java:

```bash
mvn test
```

Frontend:

```bash
cd beneficio-web
npm test -- --watch=false --browsers=ChromeHeadless
npm run build
```

Teste reproduzível de concorrência, com a aplicação rodando:

```bash
API_URL=http://localhost:8080 bash scripts/concurrency-test.sh
```

Esse script cria dois benefícios temporários e dispara duas transferências simultâneas sobre o mesmo saldo. O resultado esperado é que apenas uma transferência seja concluída ou que uma delas falhe por regra de negócio, sem deixar saldo negativo.

Smoke test integrado, com a aplicação rodando:

```bash
API_URL=http://localhost:8080 bash scripts/smoke-test.sh
```

Esse script cria dois benefícios, executa uma transferência, confere os saldos finais e valida se a transferência aparece na listagem de auditoria.

## API

Principais endpoints:

- `GET /api/v1/beneficios`
- `GET /api/v1/beneficios/{id}`
- `POST /api/v1/beneficios`
- `PUT /api/v1/beneficios/{id}`
- `DELETE /api/v1/beneficios/{id}`
- `GET /api/v1/beneficios/transferencias`
- `POST /api/v1/beneficios/transferencias`

Exemplo de transferência:

```json
{
  "origemId": 1,
  "destinoId": 2,
  "valorCentavos": 10000
}
```

## CI

O GitHub Actions executa:

- build e testes Maven dos módulos Java;
- instalação, testes headless e build do Angular.
- smoke test integrado e teste de concorrência contra a stack Docker Compose.
