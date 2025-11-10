package com.fiap.gestaofrota.service;

import com.fiap.gestaofrota.entity.DepartamentoEntity;
import com.fiap.gestaofrota.repository.DepartamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartamentoService {

    private final DepartamentoRepository repository;

    public DepartamentoService(DepartamentoRepository repository) {
        this.repository = repository;
    }

    public List<DepartamentoEntity> listarTodos() {
        return repository.findAll();
    }

    public DepartamentoEntity buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Departamento com id " + id + " não encontrado"));
    }

    public DepartamentoEntity criar(DepartamentoEntity departamento) {
        return repository.save(departamento);
    }

    public DepartamentoEntity atualizar(Long id, DepartamentoEntity atualizado) {
        DepartamentoEntity existente = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Departamento com id " + id + " não encontrado"));
        existente.setNome(atualizado.getNome());
        existente.setNumeroHorasMaximas(atualizado.getNumeroHorasMaximas());
        return repository.save(existente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

}
