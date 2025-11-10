package com.fiap.gestaofrota.controller;

import com.fiap.gestaofrota.dto.DepartamentoDTO;
import com.fiap.gestaofrota.entity.DepartamentoEntity;
import com.fiap.gestaofrota.mapper.DepartamentoMapper;
import com.fiap.gestaofrota.service.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/departamentos")
public class DepartamentoWebController {

    private final DepartamentoService departamentoService;

    public DepartamentoWebController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("departamentos", departamentoService.listarTodos().stream().map(DepartamentoMapper::toDepartamentoDTO).toList());
        return "departamentos/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("departamento", new DepartamentoDTO());
        return "departamentos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("departamento") @Valid DepartamentoDTO departamentoDto, BindingResult result) {
        if (result.hasErrors()) {
            return "departamentos/form";
        }
        DepartamentoEntity entity = DepartamentoMapper.toDepartamentoEntity(departamentoDto);
        if (departamentoDto.getId() == null) {
            departamentoService.criar(entity);
        } else {
            departamentoService.atualizar(departamentoDto.getId(), entity);
        }
        return "redirect:/departamentos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        DepartamentoEntity entity = departamentoService.buscarPorId(id);
        model.addAttribute("departamento", DepartamentoMapper.toDepartamentoDTO(entity));
        return "departamentos/form";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id) {
        departamentoService.deletar(id);
        return "redirect:/departamentos";
    }

}
