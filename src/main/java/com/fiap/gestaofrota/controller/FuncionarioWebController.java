package com.fiap.gestaofrota.controller;

import com.fiap.gestaofrota.dto.DepartamentoDTO;
import com.fiap.gestaofrota.dto.FuncionarioDTO;
import com.fiap.gestaofrota.entity.FuncionarioEntity;
import com.fiap.gestaofrota.mapper.DepartamentoMapper;
import com.fiap.gestaofrota.mapper.FuncionarioMapper;
import com.fiap.gestaofrota.service.DepartamentoService;
import com.fiap.gestaofrota.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioWebController {

    private final FuncionarioService funcionarioService;
    private final DepartamentoService departamentoService;

    public FuncionarioWebController(FuncionarioService funcionarioService, DepartamentoService departamentoService) {
        this.funcionarioService = funcionarioService;
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("funcionarios", funcionarioService.listar(org.springframework.data.domain.Pageable.unpaged()).stream().map(FuncionarioMapper::toFuncionarioDTO).toList());
        return "funcionarios/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("funcionario", new FuncionarioDTO());
        model.addAttribute("departamentos", departamentoService.listarTodos().stream().map(DepartamentoMapper::toDepartamentoDTO).toList());
        return "funcionarios/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("funcionario") @Valid FuncionarioDTO funcionarioDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("departamentos", departamentoService.listarTodos().stream().map(DepartamentoMapper::toDepartamentoDTO).toList());
            return "funcionarios/form";
        }
        var departamento = departamentoService.buscarPorId(funcionarioDto.getDepartamentoId());
        FuncionarioEntity entity = FuncionarioMapper.toFuncionarioEntity(funcionarioDto, departamento);
        if (funcionarioDto.getId() == null) {
            funcionarioService.criar(entity);
        } else {
            funcionarioService.atualizar(funcionarioDto.getId(), entity);
        }
        return "redirect:/funcionarios";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        FuncionarioEntity entity = funcionarioService.buscarPorId(id);
        FuncionarioDTO dto = FuncionarioMapper.toFuncionarioDTO(entity);
        model.addAttribute("funcionario", dto);
        model.addAttribute("departamentos", departamentoService.listarTodos().stream().map(DepartamentoMapper::toDepartamentoDTO).toList());
        return "funcionarios/form";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id) {
        funcionarioService.deletar(id);
        return "redirect:/funcionarios";
    }

}
