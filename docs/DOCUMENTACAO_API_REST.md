# Documentação da API REST - EmpowerLearn

## 1. Visão geral

A EmpowerLearn possui backend REST implementado em Spring Boot, com persistência relacional em MySQL e integração com Stripe em ambiente de teste/homologação.

Base local da API: http://localhost:8080

Tecnologias principais:
- Java 17
- Spring Boot
- Spring Data JPA / Hibernate
- Spring Security
- MySQL
- WebSocket/STOMP
- Stripe Checkout em modo teste

## 2. Configurações necessárias

Variáveis de ambiente utilizadas no backend:

- STRIPE_SECRET_KEY: chave secreta da Stripe, em modo teste ou produção.
- FRONTEND_BASE_URL: URL pública/local do frontend.
- CORS_ALLOWED_ORIGINS: origens liberadas para consumo da API.
- SPRING_DATASOURCE_URL: URL JDBC do banco.
- SPRING_DATASOURCE_USERNAME: usuário do banco.
- SPRING_DATASOURCE_PASSWORD: senha do banco.

## 3. Professores

### GET /api/professores

Lista professores cadastrados.

Exemplo:

    curl http://localhost:8080/api/professores

Status esperado: 200 OK.

Uso no sistema:
- Alimenta o dashboard do contratante/aluno.
- Permite exibição de perfis de professores disponíveis.

## 4. Planos

### GET /api/planos

Lista os planos do modelo freemium.

Exemplo:

    curl http://localhost:8080/api/planos

Resposta esperada:
- GRATUITO
- BASICO
- PRO
- PREMIUM

Status esperado: 200 OK.

Uso no sistema:
- Alimenta a página planos.html.
- Permite que aluno/responsável ou instituição escolha entre acesso gratuito e planos pagos.

## 5. Pagamentos e assinaturas

### POST /api/pagamentos/stripe/checkout

Cria uma sessão de pagamento ou ativa o plano gratuito.

Para planos pagos, o backend cria uma sessão Stripe Checkout em ambiente de teste.
Para o plano gratuito, o backend ativa a assinatura sem chamar a Stripe.

Exemplo de plano gratuito:

    curl -i -X POST http://localhost:8080/api/pagamentos/stripe/checkout -H "Content-Type: application/json" -d "{\"planoCodigo\":\"GRATUITO\",\"usuarioId\":1,\"usuarioTipo\":\"ALUNO\",\"nome\":\"Cliente Teste\",\"email\":\"cliente.teste@empowerlearn.com.br\"}"

Resposta esperada para plano gratuito:
- status: GRATUITO_ATIVO
- sessionId iniciando com gratuito_
- modoDemonstracao: true

Exemplo de plano pago:

    curl -i -X POST http://localhost:8080/api/pagamentos/stripe/checkout -H "Content-Type: application/json" -d "{\"planoCodigo\":\"PRO\",\"usuarioId\":1,\"usuarioTipo\":\"INSTITUICAO\",\"nome\":\"Instituição Teste\",\"email\":\"instituicao.teste@empowerlearn.com.br\"}"

Resposta esperada para plano pago:
- status: CHECKOUT_CRIADO
- sessionId iniciando com cs_test_
- checkoutUrl da Stripe
- modoDemonstracao: false

Status esperado: 200 OK.

### GET /api/pagamentos/stripe/status/{sessionId}

Consulta o status de uma sessão de pagamento.

Exemplo:

    curl http://localhost:8080/api/pagamentos/stripe/status/cs_test_EXEMPLO

Uso no sistema:
- Apoia a página pagamento-retorno.html.

### POST /api/pagamentos/stripe/webhook

Endpoint preparado para receber eventos da Stripe.

Uso no sistema:
- Permite atualização assíncrona de assinaturas.
- Pode ser validado futuramente com Stripe CLI.

## 6. Notificações / WebSocket

### POST /api/notificacoes/enviar

Envia notificação para um destino da plataforma, utilizando o fluxo de notificação em tempo real.

Exemplo:

    curl -i -X POST http://localhost:8080/api/notificacoes/enviar -H "Content-Type: application/json" -d "{\"destinoTipo\":\"professor\",\"destinoId\":1,\"titulo\":\"Teste WebSocket\",\"mensagem\":\"Mensagem de teste enviada pela API\"}"

Status esperado: 200 OK.

Uso no sistema:
- Atende ao requisito de caso de uso com protocolo WebSocket.
- Permite notificação de professores/alunos/instituições.

## 7. Segurança REST

Configurações validadas:
- CSRF desabilitado para API REST.
- SessionCreationPolicy.STATELESS.
- Chave Stripe fora do código-fonte.
- CORS configurado por variável de ambiente.
- Senhas com hash no banco.

## 8. Evidências de validação

Comandos utilizados:
- ./mvnw test
- curl http://localhost:8080/api/planos
- curl http://localhost:8080/api/professores
- curl -i -X POST http://localhost:8080/api/notificacoes/enviar
- ./scripts/teste-carga-autocannon.sh

Resultados esperados:
- BUILD SUCCESS nos testes.
- HTTP 200 nos endpoints principais.
- checkoutUrl gerada para plano pago.
- GRATUITO_ATIVO para plano gratuito.
