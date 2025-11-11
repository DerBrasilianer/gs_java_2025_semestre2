package com.fiap.gestaofrota.controller.funcionario;

import com.fiap.gestaofrota.dto.FuncionarioDTO;
import com.fiap.gestaofrota.entity.DepartamentoEntity;
import com.fiap.gestaofrota.entity.FuncionarioEntity;
import com.fiap.gestaofrota.mapper.FuncionarioMapper;
import com.fiap.gestaofrota.service.DepartamentoService;
import com.fiap.gestaofrota.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;
    private final DepartamentoService departamentoService;

    public FuncionarioController(FuncionarioService funcionarioService, DepartamentoService departamentoService) {
        this.funcionarioService = funcionarioService;
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public ResponseEntity<?> listar(Pageable pageable, @RequestParam(required = false) Long departamentoId) {
        Page<FuncionarioEntity> page = (departamentoId == null) ? funcionarioService.listar(pageable) : funcionarioService.listarPorDepartamento(departamentoId, pageable);
        return ResponseEntity.ok(page.map(FuncionarioMapper::toFuncionarioDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> buscar(@PathVariable Long id) {
        FuncionarioEntity encontrado = funcionarioService.buscarPorId(id);
        return ResponseEntity.ok(FuncionarioMapper.toFuncionarioDTO(encontrado));
    }

    @PostMapping
    public ResponseEntity<FuncionarioDTO> criar(@Valid @RequestBody FuncionarioDTO dto) {
        DepartamentoEntity departamento = departamentoService.buscarPorId(dto.getDepartamentoId());
        FuncionarioEntity entity = FuncionarioMapper.toFuncionarioEntity(dto, departamento);
        FuncionarioEntity salvo = funcionarioService.criar(entity);
        return ResponseEntity.ok(FuncionarioMapper.toFuncionarioDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> atualizar(@PathVariable Long id, @Valid @RequestBody FuncionarioDTO dto) {
        DepartamentoEntity departamento = departamentoService.buscarPorId(dto.getDepartamentoId());
        FuncionarioEntity atualizado = FuncionarioMapper.toFuncionarioEntity(dto, departamento);
        FuncionarioEntity salvo = funcionarioService.atualizar(id, atualizado);
        return ResponseEntity.ok(FuncionarioMapper.toFuncionarioDTO(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
