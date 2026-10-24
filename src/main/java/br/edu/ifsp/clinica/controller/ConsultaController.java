package br.edu.ifsp.clinica.controller;

import br.edu.ifsp.clinica.dto.ConsultaRequest;
import br.edu.ifsp.clinica.dto.ConsultaResponse;
import br.edu.ifsp.clinica.dto.StatusRequest;
import br.edu.ifsp.clinica.model.StatusConsulta;
import br.edu.ifsp.clinica.service.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
@Tag(name = "Consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista consultas (filtros opcionais: data, medicoId, pacienteId, status)")
    public List<ConsultaResponse> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) StatusConsulta status) {
        return service.listar(data, medicoId, pacienteId, status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma consulta pelo id")
    public ConsultaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Agenda uma consulta")
    public ResponseEntity<ConsultaResponse> criar(@RequestBody @Valid ConsultaRequest req) {
        ConsultaResponse criada = service.criar(req);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Reagenda/atualiza uma consulta")
    public ConsultaResponse atualizar(@PathVariable Long id, @RequestBody @Valid ConsultaRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma consulta")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Altera o status da consulta (notifica o painel via WebSocket)")
    public ConsultaResponse alterarStatus(@PathVariable Long id, @RequestBody @Valid StatusRequest req) {
        return service.alterarStatus(id, req.status());
    }

    @PostMapping("/{id}/chamar")
    @Operation(summary = "Chama o paciente para o consultório (envia para o painel via WebSocket)")
    public ConsultaResponse chamar(@PathVariable Long id) {
        return service.chamar(id);
    }
}
