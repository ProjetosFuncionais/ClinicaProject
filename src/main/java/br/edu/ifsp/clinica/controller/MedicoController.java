package br.edu.ifsp.clinica.controller;

import br.edu.ifsp.clinica.dto.ConsultaResponse;
import br.edu.ifsp.clinica.dto.EspecialidadeResponse;
import br.edu.ifsp.clinica.dto.MedicoRequest;
import br.edu.ifsp.clinica.dto.MedicoResponse;
import br.edu.ifsp.clinica.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@Tag(name = "Médicos")
public class MedicoController {

    private final MedicoService service;

    public MedicoController(MedicoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todos os médicos com suas especialidades")
    public List<MedicoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um médico pelo id")
    public MedicoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um médico (opcionalmente já com especialidadeIds)")
    public ResponseEntity<MedicoResponse> criar(@RequestBody @Valid MedicoRequest req) {
        MedicoResponse criado = service.criar(req);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um médico")
    public MedicoResponse atualizar(@PathVariable Long id, @RequestBody @Valid MedicoRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um médico (somente se não tiver consultas)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Muitos-para-Muitos: especialidades do médico ----

    @GetMapping("/{id}/especialidades")
    @Operation(summary = "Lista as especialidades do médico (N:N)")
    public List<EspecialidadeResponse> listarEspecialidades(@PathVariable Long id) {
        return service.listarEspecialidades(id);
    }

    @PostMapping("/{id}/especialidades/{especialidadeId}")
    @Operation(summary = "Vincula uma especialidade ao médico (N:N)")
    public MedicoResponse adicionarEspecialidade(@PathVariable Long id, @PathVariable Long especialidadeId) {
        return service.adicionarEspecialidade(id, especialidadeId);
    }

    @DeleteMapping("/{id}/especialidades/{especialidadeId}")
    @Operation(summary = "Desvincula uma especialidade do médico (N:N)")
    public ResponseEntity<Void> removerEspecialidade(@PathVariable Long id, @PathVariable Long especialidadeId) {
        service.removerEspecialidade(id, especialidadeId);
        return ResponseEntity.noContent().build();
    }

    // ---- Um-para-Muitos: consultas do médico ----

    @GetMapping("/{id}/consultas")
    @Operation(summary = "Lista as consultas do médico (1:N)")
    public List<ConsultaResponse> listarConsultas(@PathVariable Long id) {
        return service.listarConsultas(id);
    }
}
