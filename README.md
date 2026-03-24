# Atividade prática API REST - Gustavo Martins PT3031772

1) O primeiro passo para executar o projeto é clonar o repositório em uma máquina que já possua o Docker Desktop e WSL instalados e atualizados
<img width="1468" height="758" alt="image" src="https://github.com/user-attachments/assets/7c1ea9db-dcef-4aa9-9815-7866047bfdd8" />

2) Após isso, entrar na pasta do repositório, abrir o Terminal e executar "docker compose up --build", assim, irá subir as duas APIs REST de acordo com o Dockerfile presente em cada API
<img width="653" height="33" alt="image" src="https://github.com/user-attachments/assets/7713a407-a8c7-4d09-bcc0-d003ae4402bb" />
<img width="1095" height="148" alt="image" src="https://github.com/user-attachments/assets/134b70f6-fc6f-4349-a3b0-6d47e6ac0eb1" />

3) Tendo executado os passos anteriores com sucesso, será disponibilizado duas URLs para chamadas HTTP localmente:
  - http://localhost:8081/clientes/{id}
  - http://localhost:8082/contratos/{id}

4) Dentro das APIs, separei alguns cenários de teste para fins de análise e tratamento de erros, sendo eles:
  - ID 1 -> Cenário feliz, irá executar de maneira rápida e correta.
  - ID 2 -> Cliente não encontrado (HTTP Status Code 404 Not Found), irá retornar erro na API 1 e não terá dados do cliente na API 2.
  - ID 3 -> Erro ao processar solicitação (HTTP Status Code 500 Internal Server Error na API 1), irá retornar erro na API 1 e não terá dados do cliente na API 2.
  - ID 4 -> Tempo de resposta alto (API 1), irá retornar os dados corretamente na API 1, porém irá demorar além do esperado pela API 2, tendo uma exceção de timeout e não retornando os dados do cliente.
  - ID 5+ -> Mesmo cenário do ID 1.

5) Para realizar os testes dos cenários, rodar os seguintes comandos:
  - Garantir que o "docker compose up --build" ainda está em pé.
  - Em outra aba do Terminal, executar os seguintes comandos:
    - "curl http://localhost:8081/clientes/1" -> Chamadas na API de Clientes, podendo alterar o "1" pelos IDs citados anteriormente
    - "curl http://localhost:8082/contratos/1" -> Chamadas na API de Contratos, podendo alterar o "1" pelos IDs citados anteriormente
   
6) Como alguns cenários da API de Clientes lançam erros, não ficam tão legíveis pelo cUrl, portanto, seguem prints das requisições via Postman:
<img width="662" height="220" alt="image" src="https://github.com/user-attachments/assets/a45b2b6e-8c82-4a34-a253-21fc5c57b295" />
<img width="655" height="225" alt="image" src="https://github.com/user-attachments/assets/e142f31b-e442-4842-a124-94620a771538" />
<img width="709" height="279" alt="image" src="https://github.com/user-attachments/assets/70241834-e641-4eb8-a63e-093071cc1521" />


7) Além disso, seguem prints da execução dos cenários da API de Contratos:
<img width="706" height="385" alt="image" src="https://github.com/user-attachments/assets/648d521b-14a2-4198-9c9a-c8db0f5cf8f5" />
<img width="705" height="315" alt="image" src="https://github.com/user-attachments/assets/c9499414-5fd5-42a0-8945-72b3cc128297" />
<img width="705" height="315" alt="image" src="https://github.com/user-attachments/assets/9da11016-eac3-4d2f-8fc9-2cd2b449417a" />
<img width="708" height="317" alt="image" src="https://github.com/user-attachments/assets/693250cb-f696-4066-98af-de13d6e5c31f" />

8) Neste tipo de implementação, quais problemas podem ocorrer?
- A utilização de microsserviços com comunicação síncrona, apesar de ser a abordagem mais comum, pode gerar diversos problemas, principalmente acoplamento entre serviços, latência acumulada e falhas em cascata.
- Acoplamento de Serviços: A API 2 (Contratos) depende diretamente da API 1 (Clientes) para funcionar em sua totalidade. Se a API 1 estiver fora do ar ou indisponível, a API 2 não consegue enriquecer seus dados e passa a retornar respostas incompletas ou erros, mesmo que internamente esteja saudável.
- Latência Acumulada: Como a API 2 precisa aguardar a resposta da API 1 antes de montar seu payload e retornar ao cliente, o tempo de resposta de ambas se soma. Se a API 1 estiver lenta ou degradada, essa lentidão se propaga diretamente para a API 2, aumentando o tempo total de resposta para o usuário final.
- Falhas em Cascata: Caso a API 1 apresente problemas internos,como falha na conexão com o banco de dados ou erros inesperados, a API 2 também será afetada, pois não conseguirá obter as informações de clientes necessárias para complementar o payload de contratos. O timeout de 2 segundos implementado na API 2 mitiga esse cenário, evitando que as threads fiquem bloqueadas indefinidamente, mas não elimina o problema por completo: sob alta carga, múltiplas threads ainda podem ficar presas simultaneamente aguardando o timeout, o que pode degradar a performance do serviço.
