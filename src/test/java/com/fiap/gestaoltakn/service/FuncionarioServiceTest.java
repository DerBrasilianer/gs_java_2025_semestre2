package com.fiap.gestaoltakn.service;

import com.fiap.gestaoltakn.entity.DepartamentoEntity;
import com.fiap.gestaoltakn.entity.FuncionarioEntity;
import com.fiap.gestaoltakn.enums.FuncionarioStatus;
import com.fiap.gestaoltakn.repository.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private FuncionarioEntity funcionario;
    private DepartamentoEntity departamento;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        departamento = new DepartamentoEntity();
        departamento.setId(1L);
        departamento.setNome("Recursos Humanos");
        departamento.setNumeroHorasMaximas(200);

        funcionario = new FuncionarioEntity();
        funcionario.setId(1L);
        funcionario.setDepartamento(departamento);
        funcionario.setHorasTrabalhadasUltimoMes(160);
        funcionario.setStatus(FuncionarioStatus.SAUDAVEL);

    }

    @Test
    void deveListarFuncionariosSemFiltro() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<FuncionarioEntity> page = new PageImpl<>(Collections.singletonList(funcionario));
        when(funcionarioRepository.findAll(pageable)).thenReturn(page);

        Page<FuncionarioEntity> resultado = funcionarioService.listar(pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(funcionarioRepository, times(1)).findAll(pageable);

    }

    @Test
    void deveListarFuncionariosPorDepartamento() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<FuncionarioEntity> page = new PageImpl<>(Collections.singletonList(funcionario));
        when(funcionarioRepository.findByDepartamentoId(1L, pageable)).thenReturn(page);

        Page<FuncionarioEntity> resultado = funcionarioService.listarPorDepartamento(1L, pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(funcionarioRepository, times(1)).findByDepartamentoId(1L, pageable);

    }

    @Test
    void deveBuscarFuncionarioPorId() {

        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));

        FuncionarioEntity encontrado = funcionarioService.buscarPorId(1L);

        assertNotNull(encontrado);
        assertEquals(160, encontrado.getHorasTrabalhadasUltimoMes());

    }

    @Test
    void deveLancarExcecaoAoBuscarFuncionarioInexistente() {

        when(funcionarioRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> funcionarioService.buscarPorId(2L));

    }

    @Test
    void deveCriarFuncionario() {

        when(funcionarioRepository.save(any(FuncionarioEntity.class))).thenReturn(funcionario);

        FuncionarioEntity criado = funcionarioService.criar(funcionario);

        assertNotNull(criado);
        assertEquals(FuncionarioStatus.SAUDAVEL, criado.getStatus());
        verify(funcionarioRepository, times(1)).save(funcionario);

    }

    @Test
    void deveAtualizarFuncionario() {

        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(any(FuncionarioEntity.class))).thenReturn(funcionario);

        FuncionarioEntity atualizado = new FuncionarioEntity();
        atualizado.setDepartamento(departamento);
        atualizado.setHorasTrabalhadasUltimoMes(170);
        atualizado.setStatus(FuncionarioStatus.EM_RISCO);

        FuncionarioEntity resultado = funcionarioService.atualizar(1L, atualizado);

        assertEquals(170, resultado.getHorasTrabalhadasUltimoMes());
        assertEquals(FuncionarioStatus.EM_RISCO, resultado.getStatus());
        verify(funcionarioRepository, times(1)).save(funcionario);

    }

    @Test
    void deveDeletarFuncionario() {

        doNothing().when(funcionarioRepository).deleteById(1L);

        funcionarioService.deletar(1L);

        verify(funcionarioRepository, times(1)).deleteById(1L);

    }

}
