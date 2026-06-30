# Relatório de Testes Web/Backend - EmpowerLearn

## 1. Objetivo

Registrar os testes executados na camada web/backend da EmpowerLearn antes do merge para main.

## 2. Testes automatizados Maven/JUnit

Comando executado:

    cd ~/EmpowerLearn/empowerlearn-api
    ./mvnw test

Resultado obtido:

    Tests run: 4
    Failures: 0
    Errors: 0
    Skipped: 0
    BUILD SUCCESS

Cenários cobertos:
- Inicialização do contexto Spring Boot.
- Listagem de planos freemium.
- Ativação de plano gratuito sem chamada à Stripe.
- Envio de notificação via endpoint de WebSocket/notificações.

Conclusão:
- Os testes automatizados foram executados com sucesso, sem falhas e sem erros.

## 3. Testes manuais de API

GET /api/professores:
- HTTP 200.
- Lista de professores retornada.

GET /api/planos:
- HTTP 200.
- Planos retornados: GRATUITO, BASICO, PRO e PREMIUM.

POST /api/pagamentos/stripe/checkout - plano pago:
- HTTP 200.
- checkoutUrl da Stripe gerada.
- status CHECKOUT_CRIADO.
- modoDemonstracao false.

POST /api/pagamentos/stripe/checkout - plano gratuito:
- HTTP 200.
- status GRATUITO_ATIVO.
- modoDemonstracao true.

POST /api/notificacoes/enviar:
- HTTP 200.
- Notificação enviada para professor-1.

## 4. Testes de carga com Autocannon

Comando executado:

    ./scripts/teste-carga-autocannon.sh

Ambiente:
- Backend: Spring Boot local em http://localhost:8080.
- Banco: MySQL em Docker.
- Ferramenta: Autocannon.
- Sistema operacional: Windows/Git Bash.

## 5. Resultado: GET /api/planos

Configuração:
- 50 conexões.
- 30 segundos.
- pipelining 10.

Resultado observado:
- 88k requests em 30.08s.
- 164 MB read.
- 6 errors.
- 0 timeouts.
- Latência média aproximada: 1473.07 ms.
- Requisições por segundo média aproximada: 2671.97.

Interpretação:
- Endpoint suportou alta concorrência local com grande volume de leitura.
- Foram observados poucos erros em relação ao total de requisições e nenhum timeout.

## 6. Resultado: GET /api/professores

Configuração:
- 50 conexões.
- 30 segundos.
- pipelining 10.

Resultado observado:
- 30k requests em 30.08s.
- 24.6 MB read.
- 2 errors.
- 0 timeouts.
- Latência média aproximada: 1561.72 ms.
- Requisições por segundo média aproximada: 905.8.

Interpretação:
- Endpoint suportou leitura concorrente de professores em ambiente local.
- A latência foi maior que em planos por envolver mais dados e estrutura de resposta.

## 7. Resultado: POST /api/pagamentos/stripe/checkout com plano gratuito

Configuração:
- 10 conexões.
- 20 segundos.
- POST com plano GRATUITO.

Resultado observado:
- 8k requests em 20.08s.
- 5.2 MB read.
- 43 errors.
- 0 timeouts.
- Latência média aproximada: 107.26 ms.
- Requisições por segundo média aproximada: 407.4.

Interpretação:
- O teste validou escrita concorrente controlada no fluxo de ativação gratuita.
- As falhas observadas representam pequena parte do total de requisições.
- Não houve timeout.
- Em produção, recomenda-se aplicar validações adicionais de duplicidade, rate limit e idempotência.

## 8. Conclusão geral

A aplicação web/backend apresentou funcionamento adequado para o MVP:

- Testes automatizados executados com sucesso.
- Endpoints principais responderam corretamente.
- WebSocket/notificações validado por API.
- Integração Stripe em ambiente de teste validada.
- Plano gratuito validado sem pagamento externo.
- Testes de carga locais executados e documentados.

Os resultados de carga são evidências locais e não substituem testes em ambiente de deploy. Para produção, recomenda-se repetir a carga na infraestrutura publicada e configurar monitoramento.
