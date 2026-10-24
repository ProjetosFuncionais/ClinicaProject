# Clínica API: Agendamento de Consultas

Projeto da disciplina Desenvolvimento Web Back-end (BRADWBK), IFSP Bragança Paulista.

API REST em Spring Boot com banco relacional (H2 + JPA/Hibernate) e painel de atendimento em tempo real via WebSocket.

## Como rodar

Requisito: Java 21.

```bash
./mvnw spring-boot:run        # macOS / Linux
mvnw.cmd spring-boot:run      # Windows
```

| Tela | Endereço |
|---|---|
| Página inicial | http://localhost:8080 |
| Recepção | http://localhost:8080/recepcao.html |
| Painel de atendimento | http://localhost:8080/painel.html |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Console H2 | http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/clinica;AUTO_SERVER=TRUE`, usuário `sa`, sem senha) |

Na primeira execução a aplicação cadastra dados de exemplo. Para zerar o banco, apague a pasta `data/`.

## Testes

Importe `postman/clinica-api.postman_collection.json` no Postman e execute a coleção com a aplicação rodando.

## Documentação

Especificação da API, entidades e diagramas: `docs/Especificacao-API-Clinica.docx`.
