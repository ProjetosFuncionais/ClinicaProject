# Especificação da API: Clínica Médica

Disciplina: Desenvolvimento Web Back-end (BRADWBK), IFSP Câmpus Bragança Paulista
Professor: Luiz Gustavo Diniz de Oliveira Véras
Estilo da API: RESTful, com servidor WebSocket integrado

## 1. Integrantes do grupo

| Nome | Prontuário |
|---|---|
| (preencher) | (preencher) |
| (preencher) | (preencher) |

## 2. Domínio da aplicação

O domínio escolhido é o agendamento de consultas em uma clínica médica. A API permite cadastrar especialidades, médicos e pacientes, agendar consultas e acompanhar o atendimento no dia: a recepção chama o paciente, o médico inicia o atendimento e depois o encerra.

A clínica possui um painel de atendimento na sala de espera. Ele recebe as chamadas por WebSocket e mostra o nome do paciente, o consultório e a situação de cada sala.

Tecnologias utilizadas: Java 21, Spring Boot 3.5 (Spring Web, Spring Data JPA, Spring WebSocket e Bean Validation), Hibernate, banco H2, springdoc-openapi (Swagger), Postman e Git.

## 3. Requisitos funcionais da API

- RF01: Cadastrar, listar, consultar, atualizar e excluir especialidades.
- RF02: Cadastrar, listar, consultar, atualizar e excluir médicos, com CRM, contato, sala e especialidades.
- RF03: Vincular e desvincular especialidades de um médico e listar os médicos de uma especialidade.
- RF04: Cadastrar, listar, consultar, atualizar e excluir pacientes. O CPF não pode se repetir.
- RF05: Agendar, listar, consultar, reagendar e excluir consultas entre um médico e um paciente.
- RF06: Filtrar consultas por data, médico, paciente e status.
- RF07: Listar a agenda de um médico e o histórico de consultas de um paciente.
- RF08: Não permitir duas consultas no mesmo horário para o mesmo médico ou para o mesmo paciente.
- RF09: Controlar o status da consulta (AGENDADA, CHAMADA, EM_ATENDIMENTO, CONCLUIDA ou CANCELADA). Consultas concluídas ou canceladas não podem ser alteradas.
- RF10: Não permitir excluir médicos ou pacientes que tenham consultas.
- RF11: Devolver os erros sempre no mesmo formato, com status, mensagem e os campos inválidos.

## 4. Requisito funcional com WebSocket

RF-WS01: Painel de atendimento em tempo real. Quando a recepção chama um paciente pela rota POST /api/consultas/{id}/chamar, o servidor envia uma mensagem no WebSocket ws://localhost:8080/ws/painel para todos os clientes conectados. O painel da sala de espera exibe o nome do paciente, o consultório e o médico, mantém a lista das últimas chamadas e pode emitir um aviso sonoro com o nome do paciente. As demais mudanças de status também são enviadas, e com elas o painel atualiza a situação de cada consultório (disponível, aguardando paciente ou em atendimento) e a tela da recepção se atualiza sem recarregar.

Exemplo de mensagem enviada pelo servidor:

```json
{
  "tipo": "CHAMADA",
  "momento": "2026-11-10T09:31:05",
  "consulta": {
    "id": 2, "dataHora": "2026-11-10T09:30:00", "status": "CHAMADA", "observacoes": "Febre há 2 dias",
    "medico": { "id": 2, "nome": "Dr. Bruno Lima", "sala": "Sala 2" },
    "paciente": { "id": 3, "nome": "Pedro Santos" }
  }
}
```

Páginas cliente usadas na demonstração:

- painel.html: painel da sala de espera, que recebe as chamadas.
- recepcao.html: lista as consultas do dia, com as ações Chamar, Atender, Concluir e Cancelar, e mostra as mensagens recebidas.

## 5. Entidades e relacionamentos

| Entidade | Atributos |
|---|---|
| Especialidade | id, nome (único), descricao |
| Medico | id, nome, crm (único), email, telefone, sala |
| Paciente | id, nome, cpf (único), dataNascimento, telefone, email |
| Consulta | id, dataHora, status, observacoes, medico_id (FK), paciente_id (FK) |

Relacionamentos:

- Médico e Especialidade (muitos-para-muitos): um médico pode ter várias especialidades e uma especialidade pode ter vários médicos. A ligação fica na tabela medico_especialidade (medico_id, especialidade_id). No código foi usado @ManyToMany com @JoinTable na classe Medico e mappedBy na classe Especialidade.
- Médico e Consulta (um-para-muitos): um médico atende várias consultas e cada consulta tem um único médico, pela chave estrangeira consulta.medico_id. No código foi usado @OneToMany(mappedBy = "medico") e @ManyToOne.
- Paciente e Consulta (um-para-muitos): um paciente tem várias consultas e cada consulta pertence a um único paciente, pela chave estrangeira consulta.paciente_id.

```
ESPECIALIDADE (N) ---- medico_especialidade ---- (N) MEDICO (1) ---- (N) CONSULTA (N) ---- (1) PACIENTE
```

## 6. Modelagem da API REST

URL base: http://localhost:8080/api
Documentação interativa (Swagger UI): http://localhost:8080/swagger-ui.html

Formato das respostas de erro:

```json
{ "timestamp": "2026-11-10T10:00:00", "status": 400, "erro": "Bad Request",
  "mensagem": "Dados inválidos na requisição", "path": "/api/pacientes",
  "campos": { "cpf": "deve estar no formato 000.000.000-00" } }
```

### 6.1 Especialidades

| Verbo HTTP | Path | Body de requisição | Body de retorno | Status de sucesso | Status de erro |
|---|---|---|---|---|---|
| GET | /especialidades | - | Lista de Especialidade | 200 | - |
| GET | /especialidades/{id} | - | Especialidade | 200 | 404 |
| GET | /especialidades/{id}/medicos | - | Lista de Medico | 200 | 404 |
| POST | /especialidades | { nome, descricao } | Especialidade | 201 | 400, 409 (nome repetido) |
| PUT | /especialidades/{id} | { nome, descricao } | Especialidade | 200 | 400, 404, 409 |
| DELETE | /especialidades/{id} | - | - | 204 | 404 |

Especialidade = { id, nome, descricao }

### 6.2 Médicos

| Verbo HTTP | Path | Body de requisição | Body de retorno | Status de sucesso | Status de erro |
|---|---|---|---|---|---|
| GET | /medicos | - | Lista de Medico | 200 | - |
| GET | /medicos/{id} | - | Medico | 200 | 404 |
| POST | /medicos | { nome, crm, email, telefone, sala, especialidadeIds[] } | Medico | 201 | 400, 404 (especialidade), 409 (CRM repetido) |
| PUT | /medicos/{id} | { nome, crm, email, telefone, sala, especialidadeIds[] } | Medico | 200 | 400, 404, 409 |
| DELETE | /medicos/{id} | - | - | 204 | 404, 409 (possui consultas) |
| GET | /medicos/{id}/especialidades | - | Lista de Especialidade | 200 | 404 |
| POST | /medicos/{id}/especialidades/{especialidadeId} | - | Medico | 200 | 404 |
| DELETE | /medicos/{id}/especialidades/{especialidadeId} | - | - | 204 | 404, 409 (não vinculada) |
| GET | /medicos/{id}/consultas | - | Lista de Consulta | 200 | 404 |

Medico = { id, nome, crm, email, telefone, sala, especialidades: [Especialidade] }

### 6.3 Pacientes

| Verbo HTTP | Path | Body de requisição | Body de retorno | Status de sucesso | Status de erro |
|---|---|---|---|---|---|
| GET | /pacientes | - | Lista de Paciente | 200 | - |
| GET | /pacientes/{id} | - | Paciente | 200 | 404 |
| POST | /pacientes | { nome, cpf, dataNascimento, telefone, email } | Paciente | 201 | 400, 409 (CPF repetido) |
| PUT | /pacientes/{id} | { nome, cpf, dataNascimento, telefone, email } | Paciente | 200 | 400, 404, 409 |
| DELETE | /pacientes/{id} | - | - | 204 | 404, 409 (possui consultas) |
| GET | /pacientes/{id}/consultas | - | Lista de Consulta | 200 | 404 |

Paciente = { id, nome, cpf, dataNascimento, telefone, email }

### 6.4 Consultas

| Verbo HTTP | Path | Body de requisição | Body de retorno | Status de sucesso | Status de erro |
|---|---|---|---|---|---|
| GET | /consultas?data=&medicoId=&pacienteId=&status= | - | Lista de Consulta | 200 | 400 (filtro inválido) |
| GET | /consultas/{id} | - | Consulta | 200 | 404 |
| POST | /consultas | { dataHora, medicoId, pacienteId, observacoes } | Consulta | 201 | 400, 404 (médico ou paciente), 409 (horário ocupado) |
| PUT | /consultas/{id} | { dataHora, medicoId, pacienteId, observacoes } | Consulta | 200 | 400, 404, 409 |
| DELETE | /consultas/{id} | - | - | 204 | 404 |
| PATCH | /consultas/{id}/status | { status } | Consulta | 200 | 400, 404, 409 (consulta finalizada) |
| POST | /consultas/{id}/chamar | - | Consulta | 200 | 404, 409 (status não permite) |

Consulta = { id, dataHora, status, observacoes, medico: { id, nome, sala }, paciente: { id, nome } }

As rotas PATCH /consultas/{id}/status e POST /consultas/{id}/chamar também enviam o evento para o WebSocket /ws/painel.

### 6.5 WebSocket

| Endpoint | Direção | Mensagem | Quando é enviada |
|---|---|---|---|
| ws://localhost:8080/ws/painel | Servidor para clientes | { tipo: "CHAMADA", momento, consulta } | Paciente chamado |
| ws://localhost:8080/ws/painel | Servidor para clientes | { tipo: "STATUS", momento, consulta } | Outra mudança de status |
| ws://localhost:8080/ws/painel | Cliente para servidor | ping (resposta: pong) | Teste de conexão |

## 7. Testes funcionais

Os testes foram feitos no Postman com a coleção postman/clinica-api.postman_collection.json. São 46 requisições com 82 verificações, cobrindo o CRUD das quatro entidades, os relacionamentos, o fluxo de atendimento e os casos de erro (400, 404 e 409).

(Inserir aqui os prints do Postman: execução da coleção no Collection Runner e exemplos de requisições.)

Demonstração do WebSocket: ao clicar em Chamar na tela da recepção, o painel exibe a chamada no mesmo instante.

![Tela da recepção](img/recepcao.png)

![Painel de atendimento](img/painel.png)
