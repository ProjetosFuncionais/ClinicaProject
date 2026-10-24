package br.edu.ifsp.clinica.controller;

import br.edu.ifsp.clinica.dto.ConsultaResponse;
import br.edu.ifsp.clinica.dto.PacienteRequest;
import br.edu.ifsp.clinica.dto.PacienteResponse;
import br.edu.ifsp.clinica.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todos os pacientes")
    public List<PacienteResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um paciente pelo id")
    public PacienteResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um paciente")
    public ResponseEntity<PacienteResponse> criar(@RequestBody @Valid PacienteRequest req) {
        PacienteResponse criado = service.criar(req);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um paciente")
    public PacienteResponse atualizar(@PathVariable Long id, @RequestBody @Valid PacienteRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um paciente (somente se não tiver consultas)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/consultas")
    @Operation(summary = "Lista o histórico de consultas do paciente (1:N)")
    public List<ConsultaResponse> listarConsultas(@PathVariable Long id) {
        return service.listarConsultas(id);
    }
}
