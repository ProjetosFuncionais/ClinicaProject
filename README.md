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

## Endpoints

URL base: `http://localhost:8080`. Todas as requisições e respostas usam JSON. Datas seguem o formato ISO: `2026-11-10` (data) e `2026-11-10T14:30:00` (data e hora).

### Resumo

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| **Especialidades** ||||
| GET | `/api/especialidades` | Lista todas as especialidades | 200 |
| GET | `/api/especialidades/{id}` | Busca uma especialidade pelo id | 200 |
| GET | `/api/especialidades/{id}/medicos` | Lista os médicos que têm a especialidade (N:N) | 200 |
| POST | `/api/especialidades` | Cadastra uma especialidade | 201 |
| PUT | `/api/especialidades/{id}` | Atualiza uma especialidade | 200 |
| DELETE | `/api/especialidades/{id}` | Exclui uma especialidade (remove o vínculo com os médicos) | 204 |
| **Médicos** ||||
| GET | `/api/medicos` | Lista todos os médicos com suas especialidades | 200 |
| GET | `/api/medicos/{id}` | Busca um médico pelo id | 200 |
| POST | `/api/medicos` | Cadastra um médico (pode já receber `especialidadeIds`) | 201 |
| PUT | `/api/medicos/{id}` | Atualiza um médico | 200 |
| DELETE | `/api/medicos/{id}` | Exclui um médico (somente se não tiver consultas) | 204 |
| GET | `/api/medicos/{id}/especialidades` | Lista as especialidades do médico (N:N) | 200 |
| POST | `/api/medicos/{id}/especialidades/{especialidadeId}` | Vincula uma especialidade ao médico (N:N) | 200 |
| DELETE | `/api/medicos/{id}/especialidades/{especialidadeId}` | Desvincula uma especialidade do médico (N:N) | 204 |
| GET | `/api/medicos/{id}/consultas` | Lista as consultas do médico (1:N) | 200 |
| **Pacientes** ||||
| GET | `/api/pacientes` | Lista todos os pacientes | 200 |
| GET | `/api/pacientes/{id}` | Busca um paciente pelo id | 200 |
| POST | `/api/pacientes` | Cadastra um paciente | 201 |
| PUT | `/api/pacientes/{id}` | Atualiza um paciente | 200 |
| DELETE | `/api/pacientes/{id}` | Exclui um paciente (somente se não tiver consultas) | 204 |
| GET | `/api/pacientes/{id}/consultas` | Lista o histórico de consultas do paciente (1:N) | 200 |
| **Consultas** ||||
| GET | `/api/consultas` | Lista consultas (filtros opcionais) | 200 |
| GET | `/api/consultas/{id}` | Busca uma consulta pelo id | 200 |
| POST | `/api/consultas` | Agenda uma consulta | 201 |
| PUT | `/api/consultas/{id}` | Reagenda ou atualiza uma consulta | 200 |
| DELETE | `/api/consultas/{id}` | Exclui uma consulta | 204 |
| PATCH | `/api/consultas/{id}/status` | Altera o status da consulta (avisa o painel) | 200 |
| POST | `/api/consultas/{id}/chamar` | Chama o paciente para o consultório (avisa o painel) | 200 |
| **Tempo real** ||||
| WS | `/ws/painel` | WebSocket do painel de atendimento | — |

Os `POST` de criação respondem `201 Created` com o cabeçalho `Location` apontando para o novo recurso.

---

### Especialidades: `/api/especialidades`

**Corpo de POST e PUT**

```json
{
  "nome": "Cardiologia",
  "descricao": "Doenças do coração e do sistema circulatório"
}
```

| Campo | Tipo | Regras |
|---|---|---|
| `nome` | string | obrigatório, até 80 caracteres, único |
| `descricao` | string | opcional, até 255 caracteres |

**Resposta**

```json
{ "id": 1, "nome": "Cardiologia", "descricao": "Doenças do coração e do sistema circulatório" }
```

**Erros específicos**

- `409`: já existe uma especialidade com o mesmo nome.

`GET /api/especialidades/{id}/medicos` devolve uma lista de médicos no formato da resposta de médico.

---

### Médicos: `/api/medicos`

**Corpo de POST e PUT**

```json
{
  "nome": "Dra. Ana Souza",
  "crm": "123456-SP",
  "email": "ana.souza@clinica.com",
  "telefone": "(11) 99999-0000",
  "sala": "Sala 3",
  "especialidadeIds": [1, 2]
}
```

| Campo | Tipo | Regras |
|---|---|---|
| `nome` | string | obrigatório, até 120 caracteres |
| `crm` | string | obrigatório, até 20 caracteres, único |
| `email` | string | opcional, e-mail válido |
| `telefone` | string | opcional, até 20 caracteres |
| `sala` | string | opcional, até 20 caracteres (aparece no painel de chamada) |
| `especialidadeIds` | number[] | opcional, ids das especialidades do médico |

**Resposta**

```json
{
  "id": 1,
  "nome": "Dra. Ana Souza",
  "crm": "123456-SP",
  "email": "ana.souza@clinica.com",
  "telefone": "(11) 99999-0000",
  "sala": "Sala 3",
  "especialidades": [
    { "id": 1, "nome": "Cardiologia", "descricao": "..." }
  ]
}
```

As especialidades vêm ordenadas por nome.

**Vínculo com especialidades (N:N)**

- `POST /api/medicos/{id}/especialidades/{especialidadeId}` não tem corpo e devolve o médico atualizado.
- `DELETE /api/medicos/{id}/especialidades/{especialidadeId}` não tem corpo e devolve `204`.

**Erros específicos**

- `409`: já existe um médico com o mesmo CRM.
- `409`: tentativa de excluir um médico que tem consultas vinculadas.
- `409`: tentativa de desvincular uma especialidade que o médico não tem.

---

### Pacientes: `/api/pacientes`

**Corpo de POST e PUT**

```json
{
  "nome": "João da Silva",
  "cpf": "123.456.789-09",
  "dataNascimento": "1990-05-20",
  "telefone": "(11) 98888-7777",
  "email": "joao@email.com"
}
```

| Campo | Tipo | Regras |
|---|---|---|
| `nome` | string | obrigatório, até 120 caracteres |
| `cpf` | string | obrigatório, formato `000.000.000-00` (pontos e traço opcionais), único |
| `dataNascimento` | date | opcional, precisa estar no passado |
| `telefone` | string | opcional, até 20 caracteres |
| `email` | string | opcional, e-mail válido |

**Resposta**

```json
{
  "id": 1,
  "nome": "João da Silva",
  "cpf": "123.456.789-09",
  "dataNascimento": "1990-05-20",
  "telefone": "(11) 98888-7777",
  "email": "joao@email.com"
}
```

O CPF sempre volta formatado.

**Erros específicos**

- `409`: já existe um paciente com o mesmo CPF.
- `409`: tentativa de excluir um paciente que tem consultas vinculadas.

---

### Consultas: `/api/consultas`

**Filtros de `GET /api/consultas`** (query string, todos opcionais e combináveis)

| Parâmetro | Tipo | Exemplo |
|---|---|---|
| `data` | date | `?data=2026-11-10` (consultas daquele dia) |
| `medicoId` | number | `?medicoId=1` |
| `pacienteId` | number | `?pacienteId=2` |
| `status` | enum | `?status=AGENDADA` |

O resultado vem ordenado por data e hora.

**Corpo de POST e PUT**

```json
{
  "dataHora": "2026-11-10T14:30:00",
  "medicoId": 1,
  "pacienteId": 2,
  "observacoes": "Retorno"
}
```

| Campo | Tipo | Regras |
|---|---|---|
| `dataHora` | datetime | obrigatório |
| `medicoId` | number | obrigatório, médico precisa existir |
| `pacienteId` | number | obrigatório, paciente precisa existir |
| `observacoes` | string | opcional, até 500 caracteres |

Toda consulta nova começa com status `AGENDADA`.

**Resposta**

```json
{
  "id": 10,
  "dataHora": "2026-11-10T14:30:00",
  "status": "AGENDADA",
  "observacoes": "Retorno",
  "medico": { "id": 1, "nome": "Dra. Ana Souza", "sala": "Sala 3" },
  "paciente": { "id": 2, "nome": "João da Silva" }
}
```

**Alterar status: `PATCH /api/consultas/{id}/status`**

```json
{ "status": "EM_ATENDIMENTO" }
```

Valores de `status`: `AGENDADA`, `CHAMADA`, `EM_ATENDIMENTO`, `CONCLUIDA`, `CANCELADA`.

Transições permitidas:

| De | Para |
|---|---|
| `AGENDADA` | `CHAMADA`, `EM_ATENDIMENTO`, `CANCELADA` |
| `CHAMADA` | `CHAMADA` (chamar de novo), `AGENDADA` (paciente não compareceu e volta para a fila), `EM_ATENDIMENTO`, `CANCELADA` |
| `EM_ATENDIMENTO` | `CONCLUIDA` |
| `CONCLUIDA`, `CANCELADA` | nenhuma (status final) |

Cada mudança de status é enviada ao painel pelo WebSocket.

**Chamar paciente: `POST /api/consultas/{id}/chamar`**

Não tem corpo. Muda o status para `CHAMADA` e envia o evento `CHAMADA` ao painel. Só funciona para consultas `AGENDADA` ou já `CHAMADA` (chamar de novo).

**Erros específicos**

- `404`: médico, paciente ou consulta não encontrado.
- `409`: o médico já tem uma consulta nesse horário (consultas canceladas não contam).
- `409`: o paciente já tem uma consulta nesse horário (consultas canceladas não contam).
- `409`: tentativa de alterar (`PUT`) uma consulta `CONCLUIDA` ou `CANCELADA`.
- `409`: transição de status inválida. A mensagem lista as transições permitidas.
- `409`: tentativa de chamar um paciente de consulta que não está `AGENDADA` nem `CHAMADA`.

---

### WebSocket do painel: `ws://localhost:8080/ws/painel`

O servidor envia uma mensagem a todos os painéis conectados sempre que o status de uma consulta muda. O envio só acontece depois que a alteração é salva no banco.

```json
{
  "tipo": "CHAMADA",
  "momento": "2026-11-10T14:31:05",
  "consulta": { "id": 10, "dataHora": "2026-11-10T14:30:00", "status": "CHAMADA", "observacoes": "Retorno",
                "medico": { "id": 1, "nome": "Dra. Ana Souza", "sala": "Sala 3" },
                "paciente": { "id": 2, "nome": "João da Silva" } }
}
```

- `tipo` é `CHAMADA` quando o paciente foi chamado e `STATUS` para qualquer outra mudança.
- Se o cliente enviar o texto `ping`, o servidor responde `pong` (serve para checar a conexão).

---

### Formato de erro

Todas as respostas de erro usam o mesmo corpo:

```json
{
  "timestamp": "2026-11-10T14:31:05",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Dados inválidos na requisição",
  "path": "/api/pacientes",
  "campos": {
    "cpf": "deve estar no formato 000.000.000-00",
    "nome": "é obrigatório"
  }
}
```

O campo `campos` só aparece em erros de validação.

| Status | Quando |
|---|---|
| `400` | Validação dos campos falhou, JSON ausente ou malformado, ou parâmetro com tipo errado (ex.: `?status=XYZ`) |
| `404` | Recurso com o id informado não existe |
| `409` | Regra de negócio violada ou restrição do banco (ex.: valor único repetido) |

## Testes

Importe `postman/clinica-api.postman_collection.json` no Postman e execute a coleção com a aplicação rodando.

## Documentação

Especificação da API, entidades e diagramas: `docs/Especificacao-API-Clinica.docx`.
