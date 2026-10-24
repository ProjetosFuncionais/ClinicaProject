package br.edu.ifsp.clinica.config;

import br.edu.ifsp.clinica.model.*;
import br.edu.ifsp.clinica.repository.ConsultaRepository;
import br.edu.ifsp.clinica.repository.EspecialidadeRepository;
import br.edu.ifsp.clinica.repository.MedicoRepository;
import br.edu.ifsp.clinica.repository.PacienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

/** Cadastra dados de exemplo na primeira execução (quando o banco está vazio). */
@Component
public class DataSeeder implements CommandLineRunner {

    private final EspecialidadeRepository especialidades;
    private final MedicoRepository medicos;
    private final PacienteRepository pacientes;
    private final ConsultaRepository consultas;

    public DataSeeder(EspecialidadeRepository especialidades, MedicoRepository medicos,
                      PacienteRepository pacientes, ConsultaRepository consultas) {
        this.especialidades = especialidades;
        this.medicos = medicos;
        this.pacientes = pacientes;
        this.consultas = consultas;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (especialidades.count() > 0) return;

        Especialidade clinico = esp("Clínica Geral", "Atendimento clínico geral e check-ups");
        Especialidade cardio = esp("Cardiologia", "Doenças do coração e sistema circulatório");
        Especialidade pediatria = esp("Pediatria", "Saúde de crianças e adolescentes");
        Especialidade dermato = esp("Dermatologia", "Doenças da pele, cabelo e unhas");

        Medico ana = medico("Dra. Ana Souza", "CRM-SP 123456", "ana@clinica.com", "Sala 1", clinico, cardio);
        Medico bruno = medico("Dr. Bruno Lima", "CRM-SP 234567", "bruno@clinica.com", "Sala 2", pediatria, clinico);
        Medico carla = medico("Dra. Carla Mendes", "CRM-SP 345678", "carla@clinica.com", "Sala 3", dermato);

        Paciente joao = paciente("João Pereira", "11122233344", LocalDate.of(1990, 5, 12));
        Paciente maria = paciente("Maria Oliveira", "22233344455", LocalDate.of(1985, 9, 3));
        Paciente pedro = paciente("Pedro Santos", "33344455566", LocalDate.of(2015, 1, 20));
        Paciente lucia = paciente("Lúcia Ferreira", "44455566677", LocalDate.of(1972, 11, 30));

        LocalDate hoje = LocalDate.now();
        consulta(ana, joao, hoje, 9, 0, "Check-up anual");
        consulta(bruno, pedro, hoje, 9, 30, "Febre há 2 dias");
        consulta(carla, maria, hoje, 10, 0, null);
        consulta(ana, lucia, hoje, 10, 30, "Retorno - exames cardiológicos");
        consulta(bruno, maria, hoje.plusDays(1), 14, 0, null);
    }

    private Especialidade esp(String nome, String descricao) {
        Especialidade e = new Especialidade();
        e.setNome(nome);
        e.setDescricao(descricao);
        return especialidades.save(e);
    }

    private Medico medico(String nome, String crm, String email, String sala, Especialidade... esps) {
        Medico m = new Medico();
        m.setNome(nome);
        m.setCrm(crm);
        m.setEmail(email);
        m.setSala(sala);
        m.setTelefone("(11) 4000-0000");
        for (Especialidade e : esps) m.getEspecialidades().add(e);
        return medicos.save(m);
    }

    private Paciente paciente(String nome, String cpf, LocalDate nascimento) {
        Paciente p = new Paciente();
        p.setNome(nome);
        p.setCpf(cpf);
        p.setDataNascimento(nascimento);
        p.setTelefone("(11) 98888-0000");
        return pacientes.save(p);
    }

    private void consulta(Medico m, Paciente p, LocalDate dia, int hora, int minuto, String obs) {
        Consulta c = new Consulta();
        c.setMedico(m);
        c.setPaciente(p);
        c.setDataHora(dia.atTime(LocalTime.of(hora, minuto)));
        c.setObservacoes(obs);
        c.setStatus(StatusConsulta.AGENDADA);
        consultas.save(c);
    }
}
