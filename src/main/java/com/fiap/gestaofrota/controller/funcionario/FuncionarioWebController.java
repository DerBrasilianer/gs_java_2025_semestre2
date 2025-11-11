package com.fiap.gestaofrota.controller.funcionario;

import com.fiap.gestaofrota.dto.DepartamentoDTO;
import com.fiap.gestaofrota.dto.FuncionarioDTO;
import com.fiap.gestaofrota.entity.FuncionarioEntity;
import com.fiap.gestaofrota.enums.FuncionarioStatus;
import com.fiap.gestaofrota.mapper.DepartamentoMapper;
import com.fiap.gestaofrota.mapper.FuncionarioMapper;
import com.fiap.gestaofrota.service.DepartamentoService;
import com.fiap.gestaofrota.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
    public String listar(@RequestParam(name = "search", required = false) String search, Model model) {
        List<FuncionarioEntity> todos = funcionarioService.listar(org.springframework.data.domain.Pageable.unpaged()).getContent();

        List<FuncionarioDTO> resultado;
        if (search == null || search.trim().isEmpty()) {
            resultado = todos.stream().map(FuncionarioMapper::toFuncionarioDTO).collect(Collectors.toList());
        } else {
            String q = search.trim();
            String qLower = q.toLowerCase(Locale.ROOT);

            resultado = todos.stream()
                    .filter(func -> {
                        try {
                            Long id = Long.parseLong(q);
                            if (func.getId() != null && func.getId().equals(id)) return true;
                        } catch (NumberFormatException ignored) {}

                        try {
                            Integer horas = Integer.valueOf(q);
                            if (func.getHorasTrabalhadasUltimoMes() != null && func.getHorasTrabalhadasUltimoMes().equals(horas)) return true;
                        } catch (NumberFormatException ignored) {}

                        if (func.getStatus() != null) {
                            String normalized = qLower.replaceAll("[\\s\\-]", "_").toUpperCase(Locale.ROOT);
                            try {
                                FuncionarioStatus s = FuncionarioStatus.valueOf(normalized);
                                if (func.getStatus() == s) return true;
                            } catch (IllegalArgumentException ignored) {}
                            if (func.getStatus().name().toLowerCase(Locale.ROOT).contains(qLower.replaceAll("[_\\-]", ""))) return true;
                        }

                        if (func.getNome() != null && func.getNome().toLowerCase(Locale.ROOT).contains(qLower)) return true;

                        if (func.getDepartamento() != null && func.getDepartamento().getNome() != null
                                && func.getDepartamento().getNome().toLowerCase(Locale.ROOT).contains(qLower)) return true;

                        return false;
                    })
                    .map(FuncionarioMapper::toFuncionarioDTO)
                    .collect(Collectors.toList());
        }

        model.addAttribute("funcionarios", resultado);
        model.addAttribute("search", search);
        return "funcionarios/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("funcionario", new FuncionarioDTO());
        List<DepartamentoDTO> deps = departamentoService.listarTodos().stream().map(DepartamentoMapper::toDepartamentoDTO).collect(Collectors.toList());
        model.addAttribute("departamentos", deps);
        return "funcionarios/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("funcionario") @Valid FuncionarioDTO funcionarioDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("departamentos", departamentoService.listarTodos().stream().map(DepartamentoMapper::toDepartamentoDTO).collect(Collectors.toList()));
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
        model.addAttribute("departamentos", departamentoService.listarTodos().stream().map(DepartamentoMapper::toDepartamentoDTO).collect(Collectors.toList()));
        return "funcionarios/form";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id) {
        funcionarioService.deletar(id);
        return "redirect:/funcionarios";
    }

}
