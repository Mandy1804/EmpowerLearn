# Critérios de Aceite e Cenários de Teste - EmpowerLearn

## 1. Objetivo

Este documento define critérios de aceite e cenários de teste usados para validar a camada web/backend da EmpowerLearn.

## 2. Cenário: listar professores

Critério de aceite:
- O sistema deve listar professores cadastrados para consulta no dashboard.

Validação:
    curl http://localhost:8080/api/professores

Resultado esperado:
- HTTP 200.
- Lista de professores retornada.
- Professor Teste presente na resposta.

## 3. Cenário: listar planos

Critério de aceite:
- O sistema deve listar os planos do modelo freemium.

Validação:
    curl http://localhost:8080/api/planos

Resultado esperado:
- HTTP 200.
- Planos GRATUITO, BASICO, PRO e PREMIUM retornados.

## 4. Cenário: ativar plano gratuito

Critério de aceite:
- O usuário deve conseguir iniciar o uso gratuito sem passar por pagamento externo.

Endpoint:
- POST /api/pagamentos/stripe/checkout

Resultado esperado:
- HTTP 200.
- status GRATUITO_ATIVO.
- checkoutUrl apontando para pagamento-retorno.html.
- modoDemonstracao true.

## 5. Cenário: iniciar checkout pago

Critério de aceite:
- Usuário ou instituição deve conseguir iniciar checkout para ampliar limites.

Endpoint:
- POST /api/pagamentos/stripe/checkout

Resultado esperado:
- HTTP 200.
- status CHECKOUT_CRIADO.
- sessionId iniciando com cs_test_.
- checkoutUrl da Stripe gerada.
- modoDemonstracao false.

## 6. Cenário: cancelar pagamento

Critério de aceite:
- Ao cancelar o checkout, o usuário deve voltar para a plataforma com mensagem clara.

Tela:
- pagamento-retorno.html?status=cancelado

Resultado esperado:
- Mensagem de pagamento cancelado.
- Botão para voltar aos planos.
- Botão para ir ao dashboard.

## 7. Cenário: pagamento aprovado

Critério de aceite:
- Ao concluir pagamento de teste, o usuário deve voltar para a plataforma com mensagem positiva.

Tela:
- pagamento-retorno.html?status=sucesso

Resultado esperado:
- Mensagem de pagamento aprovado.
- Plano selecionado exibido.
- Botão para dashboard.

## 8. Cenário: notificação em tempo real

Critério de aceite:
- O sistema deve permitir envio de notificação para professor/aluno/instituição.

Validação:
    curl -i -X POST http://localhost:8080/api/notificacoes/enviar -H "Content-Type: application/json" -d "{\"destinoTipo\":\"professor\",\"destinoId\":1,\"titulo\":\"Teste WebSocket\",\"mensagem\":\"Mensagem de teste enviada pela API\"}"

Resultado esperado:
- HTTP 200.
- destino professor-1.
- mensagem retornada pela API.

## 9. Cenário: testes automatizados

Critério de aceite:
- A aplicação deve executar testes automatizados sem falhas.

Comando:
    cd empowerlearn-api
    ./mvnw test

Resultado esperado:
- Tests run: 4.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- BUILD SUCCESS.

## 10. Cenário: teste de carga local

Critério de aceite:
- A aplicação deve suportar requisições concorrentes em ambiente local.

Comando:
    ./scripts/teste-carga-autocannon.sh

Resultado esperado:
- Arquivos gerados em docs/evidencias/carga.
- Métricas de latência, throughput e erros documentadas.
- Sem timeouts.
