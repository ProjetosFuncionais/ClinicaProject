# Roteiro sugerido para o vídeo (até 15 min)

Regra do professor: todos os integrantes precisam falar e se apresentar ("Sou Fulano, prontuário BP...")
quando começarem a falar. Quem não falar perde 35% da nota. Não é preciso aparecer na câmera.

Divida os blocos entre os integrantes conforme o tamanho do grupo.

| # | Tempo | Conteúdo | O que mostrar na tela |
|---|---|---|---|
| 1 | 1 min | Apresentação do tema e do grupo | Documento de especificação (domínio e requisitos) |
| 2 | 2 min | Entidades e relacionamentos (1:N e N:N) | Classes em `model/` e o console do H2 com as tabelas, incluindo `MEDICO_ESPECIALIDADE` |
| 3 | 2 min | CRUD de Especialidades e Médicos | Postman: POST, GET, PUT e DELETE, mais um erro 400 ou 409 |
| 4 | 1,5 min | Relacionamento N:N | Postman: vincular e desvincular especialidade, e `GET /especialidades/{id}/medicos` |
| 5 | 2 min | CRUD de Pacientes e Consultas, relacionamento 1:N | Postman: agendar, reagendar, conflito de horário (409), `GET /medicos/{id}/consultas` |
| 6 | 3 min | WebSocket | Recepção e painel lado a lado: clicar em Chamar e o painel atualiza na hora, incluindo a situação das salas. Ligar o som. Mostrar as classes `PainelWebSocketHandler` e `PainelNotifier` |
| 7 | 1,5 min | Testes funcionais | Collection Runner do Postman com os 46 testes aprovados |
| 8 | 1 min | Swagger e encerramento | http://localhost:8080/swagger-ui.html |

Dica: para a demonstração, apague a pasta `data/` antes de gravar. Assim a aplicação recria os dados de exemplo com consultas para o dia da gravação.
