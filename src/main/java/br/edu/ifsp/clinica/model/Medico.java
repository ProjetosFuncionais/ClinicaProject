package br.edu.ifsp.clinica.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "medico")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 20)
    private String crm;

    @Column(length = 120)
    private String email;

    @Column(length = 20)
    private String telefone;

    // Sala/consultório onde o médico atende (exibida no painel de chamada)
    @Column(length = 20)
    private String sala;

    // Relacionamento Muitos-para-Muitos: um médico tem várias especialidades
    // e uma especialidade pertence a vários médicos (tabela medico_especialidade)
    @ManyToMany
    @JoinTable(
            name = "medico_especialidade",
            joinColumns = @JoinColumn(name = "medico_id"),
            inverseJoinColumns = @JoinColumn(name = "especialidade_id"))
    private Set<Especialidade> especialidades = new HashSet<>();

    // Relacionamento Um-para-Muitos: um médico atende várias consultas
    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultas = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }

    public Set<Especialidade> getEspecialidades() { return especialidades; }

    public List<Consulta> getConsultas() { return consultas; }
}
