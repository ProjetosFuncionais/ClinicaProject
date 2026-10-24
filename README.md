# Clínica API: Agendamento de Consultas

Projeto da disciplina Desenvolvimento Web Back-end (BRADWBK), IFSP Bragança Paulista.

API REST em Spring Boot para uma clínica médica, com banco relacional (H2 + JPA/Hibernate) e um
painel de atendimento em tempo real via WebSocket.

## Como rodar

Requisito: Java 21. Não precisa instalar Maven nem banco de dados.

```bash
./mvnw spring-boot:run        # macOS / Linux
mvnw.cmd spring-boot:run      # Windows
```

Acesse:

| O quê | Endereço |
|---|---|
| Página inicial | http://localhost:8080 |
| Painel de atendimento (WebSocket) | http://localhost:8080/painel.html |
| Recepção (WebSocket) | http://localhost:8080/recepcao.html |
| Swagger UI (documentação da API) | http://localhost:8080/swagger-ui.html |
| Console do banco H2 | http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/clinica;AUTO_SERVER=TRUE`, usuário `sa`, sem senha) |

Na primeira execução, a aplicação cadastra dados de exemplo: 4 especialidades, 3 médicos, 4 pacientes e consultas para o dia atual.
Para zerar o banco, pare a aplicação e apague a pasta `data/`.

## Entidades e relacionamentos

- Especialidade N:N Médico (tabela `medico_especialidade`)
- Médico 1:N Consulta
- Paciente 1:N Consulta

## Testes funcionais (Postman)

1. No Postman, use Import e selecione `postman/clinica-api.postman_collection.json`
2. Com a aplicação rodando, clique com o botão direito na coleção, escolha Run collection e depois Run
3. As 46 requisições (82 verificações) devem passar.

Também é possível rodar pelo terminal: `npx newman run postman/clinica-api.postman_collection.json`

## Estrutura

```
src/main/java/br/edu/ifsp/clinica/
├── model/        Entidades JPA (Especialidade, Medico, Paciente, Consulta)
├── repository/   Spring Data JPA
├── dto/          Corpos de requisição e resposta
├── service/      Regras de negócio
├── controller/   Endpoints REST
├── websocket/    Servidor WebSocket do painel (/ws/painel)
├── exception/    Tratamento padronizado de erros
└── config/       Configuração do WebSocket e dados de exemplo
src/main/resources/static/   Páginas cliente (painel.html, recepcao.html)
docs/                        Documento de especificação (.md e .docx)
postman/                     Coleção de testes
```
# ClinicaProject
