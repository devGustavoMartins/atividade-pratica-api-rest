# Atividade prática API REST - Gustavo Martins PT3031772

## 1. Clonar o repositório

O primeiro passo para executar o projeto é clonar o repositório em uma máquina que já possua o Docker Desktop e WSL instalados e atualizados.

<img width="1468" height="758" alt="image" src="https://github.com/user-attachments/assets/7c1ea9db-dcef-4aa9-9815-7866047bfdd8" />

## 2. Subir os serviços com Docker Compose

Após isso, entrar na pasta do repositório, abrir o Terminal e executar `docker compose up --build`, assim, irá subir as duas APIs REST de acordo com o Dockerfile presente em cada API.

<img width="653" height="33" alt="image" src="https://github.com/user-attachments/assets/7713a407-a8c7-4d09-bcc0-d003ae4402bb" />

> Comando executado no terminal dentro da pasta do repositório.

<img width="1095" height="148" alt="image" src="https://github.com/user-attachments/assets/134b70f6-fc6f-4349-a3b0-6d47e6ac0eb1" />

> Logs confirmando que ambos os serviços (cliente-service na porta 8081 e contrato-service na porta 8082) foram compilados e iniciados com sucesso.

## 3. Endpoints disponíveis

Tendo executado os passos anteriores com sucesso, serão disponibilizadas duas URLs para chamadas HTTP localmente:

- http://localhost:8081/clientes/{id}
- http://localhost:8082/contratos/{id}

## 4. Cenários de teste

Dentro das APIs, separei alguns cenários de teste para fins de análise e tratamento de erros, sendo eles:

| ID | Cenário | Comportamento API 1 (Clientes) | Comportamento API 2 (Contratos) |
|----|---------|-------------------------------|--------------------------------|
| 1 | Cenário feliz | Retorna dados do cliente com HTTP 200 | Retorna contrato com dados do cliente |
| 2 | Cliente não encontrado | Retorna HTTP 404 Not Found | Retorna contrato sem dados do cliente + mensagem de erro |
| 3 | Erro interno | Retorna HTTP 500 Internal Server Error | Retorna contrato sem dados do cliente + mensagem de erro |
| 4 | Timeout | Demora 5s para responder | Timeout após 2s, retorna contrato sem dados do cliente |
| 5+ | Cenário feliz | Mesmo comportamento do ID 1 | Mesmo comportamento do ID 1 |

## 5. Como executar os testes

- Garantir que o `docker compose up --build` ainda está em pé.
- Em outra aba do Terminal, executar os seguintes comandos:
  - `curl http://localhost:8081/clientes/1` → Chamadas na API de Clientes, podendo alterar o "1" pelos IDs citados anteriormente
  - `curl http://localhost:8082/contratos/1` → Chamadas na API de Contratos, podendo alterar o "1" pelos IDs citados anteriormente

## 6. Resultados — API de Clientes

Como alguns cenários da API de Clientes lançam erros, não ficam tão legíveis pelo cUrl, portanto, seguem prints das requisições via Postman:

<img width="662" height="220" alt="image" src="https://github.com/user-attachments/assets/a45b2b6e-8c82-4a34-a253-21fc5c57b295" />

> **Cenário ID 1 (Sucesso):** A API de Clientes retornou os dados mockados corretamente com HTTP 200.

<img width="655" height="225" alt="image" src="https://github.com/user-attachments/assets/e142f31b-e442-4842-a124-94620a771538" />

> **Cenário ID 2 (Not Found):** A API de Clientes retornou HTTP 404, simulando um cliente inexistente no sistema.

<img width="709" height="279" alt="image" src="https://github.com/user-attachments/assets/70241834-e641-4eb8-a63e-093071cc1521" />

> **Cenário ID 3 (Erro Interno):** A API de Clientes retornou HTTP 500, simulando uma falha interna no serviço.

## 7. Resultados — API de Contratos

<img width="706" height="385" alt="image" src="https://github.com/user-attachments/assets/648d521b-14a2-4198-9c9a-c8db0f5cf8f5" />

> **Cenário ID 1 (Sucesso):** A API de Contratos consumiu a API de Clientes via HTTP com sucesso e retornou o contrato com os dados do cliente enriquecidos no payload.

<img width="705" height="315" alt="image" src="https://github.com/user-attachments/assets/c9499414-5fd5-42a0-8945-72b3cc128297" />

> **Cenário ID 2 (Cliente não encontrado):** A API de Clientes retornou 404. A API de Contratos tratou o erro e retornou o contrato com `cliente: null` e a mensagem de erro correspondente.

<img width="705" height="315" alt="image" src="https://github.com/user-attachments/assets/9da11016-eac3-4d2f-8fc9-2cd2b449417a" />

> **Cenário ID 3 (Erro interno):** A API de Clientes retornou 500. A API de Contratos tratou a exceção e retornou o contrato sem os dados do cliente, informando o erro ocorrido.

<img width="708" height="317" alt="image" src="https://github.com/user-attachments/assets/693250cb-f696-4066-98af-de13d6e5c31f" />

> **Cenário ID 4 (Timeout):** A API de Clientes demorou 5 segundos para responder, porém a API de Contratos possui um timeout configurado de 2 segundos. A requisição foi interrompida e o contrato foi retornado sem os dados do cliente, com a mensagem de timeout.

## 8. Neste tipo de implementação, quais problemas podem ocorrer?

A utilização de microsserviços com comunicação síncrona, apesar de ser a abordagem mais comum, pode gerar diversos problemas, principalmente acoplamento entre serviços, latência acumulada e falhas em cascata.

**Acoplamento de Serviços:** A API 2 (Contratos) depende diretamente da API 1 (Clientes) para funcionar em sua totalidade. Se a API 1 estiver fora do ar ou indisponível, a API 2 não consegue enriquecer seus dados e passa a retornar respostas incompletas ou erros, mesmo que internamente esteja saudável.

**Latência Acumulada:** Como a API 2 precisa aguardar a resposta da API 1 antes de montar seu payload e retornar ao cliente, o tempo de resposta de ambas se soma. Se a API 1 estiver lenta ou degradada, essa lentidão se propaga diretamente para a API 2, aumentando o tempo total de resposta para o usuário final.

**Falhas em Cascata:** Caso a API 1 apresente problemas internos, como falha na conexão com o banco de dados ou erros inesperados, a API 2 também será afetada, pois não conseguirá obter as informações de clientes necessárias para complementar o payload de contratos. O timeout de 2 segundos implementado na API 2 mitiga esse cenário, evitando que as threads fiquem bloqueadas indefinidamente, mas não elimina o problema por completo: sob alta carga, múltiplas threads ainda podem ficar presas simultaneamente aguardando o timeout, o que pode degradar a performance do serviço.
