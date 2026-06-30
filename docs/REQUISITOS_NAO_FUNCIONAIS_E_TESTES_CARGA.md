# Requisitos Não Funcionais e Testes de Carga - EmpowerLearn

## 1. Objetivo

Este documento descreve a arquitetura e os requisitos não funcionais da EmpowerLearn para o cenário de alto volume previsto: 1 milhão de requisições de escrita para cada 2 milhões de requisições de leitura.

## 2. Escopo

Foram considerados os principais fluxos web/backend:
- Consulta de professores.
- Consulta de planos.
- Ativação de plano gratuito.
- Criação de checkout de pagamento.
- Envio de notificações.
- Persistência de assinaturas.
- Integração com Stripe em ambiente de teste.

## 3. Arquitetura

A aplicação possui separação entre frontend web, backend REST e banco de dados relacional.

Frontend:
- HTML5.
- JavaScript.
- CSS.
- Páginas integradas ao visual da EmpowerLearn.
- Consumo da API REST via HTTP.

Backend:
- Spring Boot.
- API REST stateless.
- Spring Security.
- Spring Data JPA/Hibernate.
- Controle transacional nos fluxos de assinatura/pagamento.
- WebSocket para notificações.
- Integração com Stripe em ambiente de teste.

Banco de dados:
- MySQL em Docker no ambiente local.
- Tabelas principais: alunos, professores, instituicoes, feedbacks, planos e assinaturas.

## 4. Requisitos não funcionais

### 4.1 Escalabilidade

O backend foi projetado como stateless, permitindo múltiplas instâncias em ambiente de deploy. Como o estado principal está no banco e não em sessão local de servidor, novas instâncias podem ser adicionadas para suportar aumento de demanda.

### 4.2 Desempenho

Endpoints de maior leitura:
- GET /api/planos.
- GET /api/professores.

Para alto volume, recomenda-se:
- Cache para dados pouco mutáveis, como planos.
- Índices em campos de busca de professores.
- Paginação na listagem de professores.
- Pool de conexões configurado.
- Monitoramento de latência.

### 4.3 Escrita e consistência

Fluxos de escrita relevantes:
- Ativação de plano gratuito.
- Criação de assinatura.
- Atualização de status de pagamento.
- Envio de notificações.
- Cadastros.

Estratégias:
- Uso de transações no serviço de pagamento.
- Persistência relacional.
- Identificação de sessão de pagamento.
- Idempotência futura para evitar duplicidades em chamadas repetidas.

### 4.4 Segurança

Pontos considerados:
- STRIPE_SECRET_KEY fora do código.
- CSRF desabilitado para API REST.
- SessionCreationPolicy.STATELESS.
- CORS configurável.
- Senhas armazenadas com hash.
- Separação entre chave de teste e produção.

### 4.5 Disponibilidade

Para deploy, recomenda-se:
- Backend em plataforma com restart automático.
- Banco gerenciado.
- Variáveis de ambiente configuradas.
- Health check.
- Logs de erro e métricas.

### 4.6 Observabilidade

Recomenda-se monitorar:
- Latência média.
- Taxa de erro.
- Throughput.
- Uso de CPU/memória.
- Conexões com banco.
- Erros em chamadas externas de pagamento.

## 5. Estratégia para 1 milhão de escritas e 2 milhões de leituras

Leituras:
- Escalar horizontalmente o backend.
- Cachear endpoints com dados estáveis.
- Criar índices no banco.
- Separar leitura e escrita em ambientes futuros, se necessário.
- Usar paginação e filtros otimizados.

Escritas:
- Transações no backend.
- Pool de conexões.
- Banco relacional com integridade.
- Eventos de pagamento processados de forma assíncrona no futuro.
- Idempotência em fluxos de assinatura/pagamento.
- Rate limit em endpoints sensíveis.

## 6. Testes automatizados

Comando:

    cd empowerlearn-api
    ./mvnw test

Resultado obtido:

    Tests run: 4
    Failures: 0
    Errors: 0
    Skipped: 0
    BUILD SUCCESS

Cenários automatizados:
- Inicialização da aplicação.
- Listagem de planos.
- Ativação de plano gratuito.
- Envio de notificação.

## 7. Testes de carga

Ferramenta utilizada:
- Autocannon.

Comando:

    ./scripts/teste-carga-autocannon.sh

Evidências geradas:
- docs/evidencias/carga/01_get_planos.txt
- docs/evidencias/carga/02_get_professores.txt
- docs/evidencias/carga/03_post_checkout_gratuito.txt

## 8. Resultados resumidos

GET /api/planos:
- 88k requests em 30.08s.
- 6 errors.
- 0 timeouts.

GET /api/professores:
- 30k requests em 30.08s.
- 2 errors.
- 0 timeouts.

POST /api/pagamentos/stripe/checkout com plano gratuito:
- 8k requests em 20.08s.
- 43 errors.
- 0 timeouts.

## 9. Análise dos erros

Os testes foram executados em ambiente local, utilizando MySQL em Docker e backend local. Os erros observados representam uma parcela pequena do volume total e não houve timeouts.

Para ambiente produtivo, recomenda-se:
- Repetir os testes no deploy.
- Reduzir ou controlar pipelining.
- Adicionar idempotência em escritas.
- Aplicar rate limit.
- Monitorar logs durante a carga.
- Usar banco gerenciado com configuração adequada de conexões.

## 10. Conclusão

A aplicação atende ao MVP exigido na camada web/backend:
- Backend REST stateless.
- Persistência relacional.
- Controle transacional.
- WebSocket.
- Integração de pagamento em ambiente de teste.
- Testes automatizados.
- Testes de carga com evidências.
- Documentação dos requisitos não funcionais.

Os testes locais indicam que a aplicação suporta concorrência para os principais fluxos, mas o dimensionamento definitivo deve ser validado novamente após o deploy.
