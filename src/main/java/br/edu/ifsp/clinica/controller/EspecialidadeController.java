package br.edu.ifsp.clinica.controller;

import br.edu.ifsp.clinica.dto.EspecialidadeRequest;
import br.edu.ifsp.clinica.dto.EspecialidadeResponse;
import br.edu.ifsp.clinica.dto.MedicoResponse;
import br.edu.ifsp.clinica.service.EspecialidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@Tag(name = "Especialidades")
public class EspecialidadeController {

    private final EspecialidadeService service;

    public EspecialidadeController(EspecialidadeService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todas as especialidades")
    public List<EspecialidadeResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma especialidade pelo id")
    public EspecialidadeResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/{id}/medicos")
    @Operation(summary = "Lista os médicos que possuem a especialidade (N:N)")
    public List<MedicoResponse> listarMedicos(@PathVariable Long id) {
        return service.listarMedicos(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra uma especialidade")
    public ResponseEntity<EspecialidadeResponse> criar(@RequestBody @Valid EspecialidadeRequest req) {
        EspecialidadeResponse criada = service.criar(req);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma especialidade")
    public EspecialidadeResponse atualizar(@PathVariable Long id, @RequestBody @Valid EspecialidadeRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma especialidade (remove o vínculo com os médicos)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
