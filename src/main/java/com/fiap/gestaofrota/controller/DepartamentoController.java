package com.fiap.gestaofrota.controller;

import com.fiap.gestaofrota.dto.DepartamentoDTO;
import com.fiap.gestaofrota.entity.DepartamentoEntity;
import com.fiap.gestaofrota.mapper.DepartamentoMapper;
import com.fiap.gestaofrota.service.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoDTO>> listar() {
        return ResponseEntity.ok(departamentoService.listarTodos().stream().map(DepartamentoMapper::toDepartamentoDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> buscar(@PathVariable Long id) {
        DepartamentoEntity encontrado = departamentoService.buscarPorId(id);
        return ResponseEntity.ok(DepartamentoMapper.toDepartamentoDTO(encontrado));
    }

    @PostMapping
    public ResponseEntity<DepartamentoDTO> criar(@Valid @RequestBody DepartamentoDTO dto) {
        DepartamentoEntity entity = DepartamentoMapper.toDepartamentoEntity(dto);
        DepartamentoEntity salvo = departamentoService.criar(entity);
        return ResponseEntity.ok(DepartamentoMapper.toDepartamentoDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody DepartamentoDTO dto) {
        DepartamentoEntity atualizado = DepartamentoMapper.toDepartamentoEntity(dto);
        DepartamentoEntity salvo = departamentoService.atualizar(id, atualizado);
        return ResponseEntity.ok(DepartamentoMapper.toDepartamentoDTO(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        departamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
