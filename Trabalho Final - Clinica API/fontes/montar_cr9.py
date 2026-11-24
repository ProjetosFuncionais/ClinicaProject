"""Gera cr9.html (Evidência dos Testes Funcionais) a partir da lista abaixo.

Para trocar um print, substitua o arquivo em prints/ mantendo o mesmo nome
e rode ./gerar-pdfs.sh de novo.
"""
import html
import pathlib

INTEGRANTES = [
    ("Josiely de Oliveira Ferreira", "BP3053431"),
    ("Pedro Henrique De Araújo Soares Rodrigues", "BP3060608"),
    ("Vitor Oliveira Faria", "BP3065324"),
    ("Lygio Ursos de Moraes Sobrinho", "BP306042X"),
    ("Vagner Alves Aquino", "BP3079465"),
    ("Guilherme Espejo Bueno", "BP3053822"),
    ("João Chico Rafael", "BP3044823"),
]

# (tipo, dados). tipo "secao" abre um bloco; "teste" é uma página de evidência.
PAGINAS = [
    ("secao", ("CR2 · Entidade 1", "Especialidade", "CRUD completo de especialidades e a consulta dos médicos de cada especialidade (lado N:N).")),
    ("teste", ("01_especialidades_listar", "Listar Especialidades (GET)", "/api/especialidades", "200 OK", "CR2", "Retorna todas as especialidades cadastradas, ordenadas por nome.")),
    ("teste", ("02_especialidades_buscar", "Buscar Especialidade por id (GET)", "/api/especialidades/{id}", "200 OK", "CR2", "Busca a especialidade de id 2 (Cardiologia).")),
    ("teste", ("03_especialidades_cadastrar", "Cadastrar Especialidade (POST)", "/api/especialidades", "201 Created", "CR2", "Cria a especialidade Oftalmologia. A API devolve o registro com o id gerado (5). Executado no Swagger UI.")),
    ("teste", ("04_especialidades_atualizar", "Alterar Especialidade (PUT)", "/api/especialidades/{id}", "200 OK", "CR2", "Atualiza a descrição da especialidade 5. Executado no Swagger UI.")),
    ("teste", ("05_especialidades_excluir", "Excluir Especialidade (DELETE)", "/api/especialidades/{id}", "204 No Content", "CR2", "Remove a especialidade 5. A resposta 204 não tem corpo. Executado no Swagger UI.")),
    ("teste", ("06_especialidades_medicos_nn", "Médicos da Especialidade (GET)", "/api/especialidades/{id}/medicos", "200 OK", "CR7", "Lista os médicos ligados à Clínica Geral pela tabela medico_especialidade (relacionamento N:N).")),

    ("secao", ("CR3 · Entidade 2", "Médico", "CRUD de médicos e as rotas que vinculam e desvinculam especialidades (N:N), além da agenda do médico (1:N).")),
    ("teste", ("07_medicos_listar", "Listar Médicos (GET)", "/api/medicos", "200 OK", "CR3", "Cada médico vem com a lista das suas especialidades.")),
    ("teste", ("08_medicos_buscar", "Buscar Médico por id (GET)", "/api/medicos/{id}", "200 OK", "CR3", "Dra. Ana Souza, com Cardiologia e Clínica Geral.")),
    ("teste", ("09_medicos_cadastrar", "Cadastrar Médico (POST)", "/api/medicos", "201 Created", "CR3 · CR7", "Cria o Dr. Romario Silva (id 5) já vinculado a duas especialidades pelo campo especialidadeIds.")),
    ("teste", ("10_medicos_atualizar", "Alterar Médico (PUT)", "/api/medicos/{id}", "200 OK", "CR3", "Atualiza o e-mail e a sala do médico 5.")),
    ("teste", ("11_medicos_desvincular_nn", "Desvincular Especialidade (DELETE)", "/api/medicos/{id}/especialidades/{id}", "204 No Content", "CR7", "Remove a Cardiologia do médico 5 na tabela de ligação.")),
    ("teste", ("12_medicos_especialidades_nn", "Especialidades do Médico (GET)", "/api/medicos/{id}/especialidades", "200 OK", "CR7", "Depois da remoção, o médico 5 fica só com Clínica Geral.")),
    ("teste", ("13_medicos_vincular_nn", "Vincular Especialidade (POST)", "/api/medicos/{id}/especialidades/{id}", "200 OK", "CR7", "Vincula a Cardiologia de novo: o médico volta a ter duas especialidades.")),
    ("teste", ("14_medicos_excluir", "Excluir Médico (DELETE)", "/api/medicos/{id}", "204 No Content", "CR3", "Remove o médico 5, que não tinha consultas. Médico com consultas é recusado com 409.")),
    ("teste", ("15_medicos_consultas_1n", "Consultas do Médico (GET)", "/api/medicos/{id}/consultas", "200 OK", "CR6", "Agenda do Dr. Bruno Lima: um médico tem várias consultas (relacionamento 1:N).")),

    ("secao", ("CR4 · Entidade 3", "Paciente", "CRUD de pacientes, com CPF único, e o histórico de consultas do paciente (1:N).")),
    ("teste", ("16_pacientes_listar", "Listar Pacientes (GET)", "/api/pacientes", "200 OK", "CR4", "Retorna os pacientes ordenados por nome, com o CPF formatado.")),
    ("teste", ("17_pacientes_buscar", "Buscar Paciente por id (GET)", "/api/pacientes/{id}", "200 OK", "CR4", "Busca a paciente de id 2 (Maria Oliveira).")),
    ("teste", ("18_pacientes_cadastrar", "Cadastrar Paciente (POST)", "/api/pacientes", "201 Created", "CR4", "Cria o paciente Lygio Sobrinho com o id 5. CPF inválido devolve 400 e CPF repetido devolve 409. Executado no Swagger UI.")),
    ("teste", ("19_pacientes_atualizar", "Alterar Paciente (PUT)", "/api/pacientes/{id}", "200 OK", "CR4", "Atualiza o telefone e a data de nascimento do paciente 5. Executado no Swagger UI.")),
    ("teste", ("20_pacientes_excluir", "Excluir Paciente (DELETE)", "/api/pacientes/{id}", "204 No Content", "CR4", "Remove o paciente 5. Só é permitido quando ele não tem consultas. Executado no Swagger UI.")),
    ("teste", ("21_pacientes_historico_1n", "Histórico do Paciente (GET)", "/api/pacientes/{id}/consultas", "200 OK", "CR6", "Consultas da Maria Oliveira: um paciente tem várias consultas (relacionamento 1:N).")),

    ("secao", ("CR5 · Entidade 4", "Consulta", "CRUD de consultas, regras de horário e de status, e as duas rotas que avisam o painel pelo WebSocket.")),
    ("teste", ("22_consultas_listar", "Listar Consultas (GET)", "/api/consultas", "200 OK", "CR5", "Lista as consultas. Aceita filtros opcionais por data, médico, paciente e status.")),
    ("teste", ("23_consultas_buscar", "Buscar Consulta por id (GET)", "/api/consultas/{id}", "200 OK", "CR5", "Consulta 1, com o resumo do médico e do paciente.")),
    ("teste", ("24_consultas_agendar", "Agendar Consulta (POST)", "/api/consultas", "201 Created", "CR5", "Cria a consulta 38 com status inicial AGENDADA. Mesmo médico no mesmo horário devolve 409.")),
    ("teste", ("25_consultas_atualizar", "Alterar Consulta (PUT)", "/api/consultas/{id}", "200 OK", "CR5", "Reagenda a consulta 3 para 10/05/2027; o status continua AGENDADA. Executado no Swagger UI.")),
    ("teste", ("26_consultas_excluir", "Excluir Consulta (DELETE)", "/api/consultas/{id}", "204 No Content", "CR5", "Remove a consulta 3. Executado no Swagger UI.")),
    ("teste", ("27_consultas_status", "Alterar Status (PATCH)", "/api/consultas/{id}/status", "200 OK", "CR5 · CR8", "Passa a consulta 38 para EM_ATENDIMENTO e envia o evento STATUS ao painel pelo WebSocket.")),
    ("teste", ("28_consultas_chamar", "Chamar Paciente (POST)", "/api/consultas/{id}/chamar", "200 OK", "CR8", "Muda a consulta 1 para CHAMADA e envia o evento CHAMADA ao painel da sala de espera.")),

    ("secao", ("CR8 · WebSocket", "Painel em tempo real", "Clientes web conectados em ws://localhost:8080/ws/painel recebem as chamadas sem recarregar a página.")),
    ("tela", ("img/02_recepcao_inicial.png", "Recepção conectada", "/recepcao.html", "Conectada", "CR8", "Consultas do dia. O indicador \"Tempo real ativo\" mostra que a página está conectada ao WebSocket.")),
    ("tela", ("img/04_painel_chamada.png", "Painel recebe a chamada", "/painel.html", "Evento CHAMADA", "CR8", "Ao clicar em Chamar na recepção, o painel mostra João Pereira, Sala 1 e Dra. Ana Souza na hora.")),
    ("tela", ("img/05_recepcao_apos_chamada.png", "Recepção atualizada", "/recepcao.html", "Evento CHAMADA", "CR8", "A mesma mensagem atualiza a tabela da recepção e aparece no quadro de atividade em tempo real.")),
    ("tela", ("img/06_painel_atendimento.png", "Situação das salas", "/painel.html", "Evento STATUS", "CR8", "Pedro Santos é chamado, João Pereira vai para as chamadas anteriores e a Sala 1 aparece em atendimento.")),

    ("secao", ("CR9 · Execução automatizada", "Coleção completa", "A coleção do repositório (postman/clinica-api.postman_collection.json) foi executada com o Newman, o executor de linha de comando do Postman, cobrindo também os casos de erro.")),
    ("tela", ("img/newman_resumo.png", "Resultado da coleção", "47 requisições", "85 verificações", "CR9", "Todas as 85 verificações passaram, sem falhas. Cada requisição confere o status HTTP e o conteúdo da resposta.")),
    ("tela", ("img/newman_pastas.png", "Organização dos testes", "7 pastas", "0 falhas", "CR9", "CRUD das quatro entidades, relacionamento N:N, fluxo do atendimento (WebSocket) e exclusões.")),
    ("tela", ("img/newman_erro409.png", "Caso de erro: horário ocupado", "/api/consultas", "409 Conflict", "CR9", "Agendar o mesmo médico no mesmo horário é recusado com 409. A coleção também testa 400 (dados inválidos) e 404 (registro inexistente).")),
]

SETA = ('<svg class="rodape-seta" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2.6" '
        'stroke-linecap="round" stroke-linejoin="round"><path d="M6 6l6 6-6 6M13 6l6 6-6 6"/></svg>')


def moldura(conteudo, n, classe=""):
    return f'''<section class="slide {classe}">
  <header class="topo"><span class="disc">BRADWBK</span><span class="proj">Clínica API</span><span class="inst">IFSP · Bragança Paulista</span><span class="quadrado"></span></header>
{conteudo}
  <div class="rodape-num">Página {n:02d}</div>{SETA}
</section>'''


def capa():
    nomes = "".join(f'<div><b>{html.escape(n)}</b> <span class="suave mono">{p}</span></div>' for n, p in INTEGRANTES)
    return f'''<div class="conteudo" style="display:flex;flex-direction:column;justify-content:center;padding-left:20px">
    <span class="rotulo-cr" style="align-self:flex-start">Critério CR9</span>
    <h1 class="titulo" style="font-size:66px;margin:18px 0 8px">CR9 - Testes Funcionais</h1>
    <p class="titulo t3 suave" style="margin:0 0 34px">Evidência dos testes da API REST e do WebSocket</p>
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px 40px;font-size:17px;max-width:1000px">{nomes}</div>
    <p class="suave" style="margin-top:34px;font-size:15px">Ferramentas: Postman, Swagger UI e Newman · Desenvolvimento Web Back-end (BRADWBK) · Prof. Luiz Gustavo Diniz de Oliveira Véras · 2026</p>
  </div>'''


def secao(rotulo, titulo, texto):
    return f'''<div class="centro"><div class="num">{rotulo}</div><h1 class="titulo t1">{html.escape(titulo)}</h1><p class="texto suave">{html.escape(texto)}</p></div>'''


def evidencia(img, titulo, caminho, status, criterio, prova, rotulo="CR9 - Testes Funcionais"):
    erro = " erro" if status.startswith(("4", "5")) else ""
    return f'''<div class="esq">
    <h1 class="titulo t1">{rotulo}</h1>
    <h2 class="titulo t2">{html.escape(titulo)}</h2>
    <div class="caminho">{html.escape(caminho)}</div>
    <div class="info">
      <span class="status">Resultado <b class="{erro.strip()}">{html.escape(status)}</b></span>
      <span class="rotulo-cr" style="align-self:flex-start">{criterio}</span>
      <span class="prova">{html.escape(prova)}</span>
    </div>
  </div>
  <div class="print"><img src="{img}" alt="{html.escape(titulo)}"></div>'''


paginas = [moldura(capa(), 1)]
n = 2
for tipo, d in PAGINAS:
    if tipo == "secao":
        paginas.append(moldura(secao(*d), n, "secao"))
    elif tipo == "teste":
        arq, *resto = d
        paginas.append(moldura(evidencia(f"prints/{arq}.jpeg", *resto), n, "evid"))
    else:
        img, *resto = d
        rotulo = "CR8 - WebSocket" if resto[3] == "CR8" else "CR9 - Testes Funcionais"
        paginas.append(moldura(evidencia(img, *resto, rotulo=rotulo), n, "evid"))
    n += 1

doc = f'''<!doctype html>
<html lang="pt-BR"><head><meta charset="utf-8"><title>CR9 - Evidência dos Testes Funcionais</title>
<link rel="stylesheet" href="slides.css"></head>
<body>
{chr(10).join(paginas)}
</body></html>'''
pathlib.Path(__file__).with_name("cr9.html").write_text(doc, encoding="utf-8")
print(f"cr9.html gerado com {len(paginas)} páginas")
