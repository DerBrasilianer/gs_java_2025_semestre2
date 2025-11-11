package com.fiap.gestaofrota.service;

import com.fiap.gestaofrota.entity.DepartamentoEntity;
import com.fiap.gestaofrota.repository.DepartamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "departamentos")
public class DepartamentoService {

    private final DepartamentoRepository repository;

    public DepartamentoService(DepartamentoRepository repository) {
        this.repository = repository;
    }

    @Cacheable(key = "'all'")
    public List<DepartamentoEntity> listarTodos() {
        return repository.findAll();
    }

    @Cacheable(key = "#id")
    public DepartamentoEntity buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Departamento com id " + id + " não encontrado"));
    }

    @Caching(evict = {
            @CacheEvict(key = "'all'"),
            @CacheEvict(key = "#departamento.id")
    })
    public DepartamentoEntity criar(DepartamentoEntity departamento) {
        return repository.save(departamento);
    }

    @Caching(evict = {
            @CacheEvict(key = "'all'"),
            @CacheEvict(key = "#id")
    })
    public DepartamentoEntity atualizar(Long id, DepartamentoEntity atualizado) {
        DepartamentoEntity existente = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Departamento com id " + id + " não encontrado"));
        existente.setNome(atualizado.getNome());
        existente.setNumeroHorasMaximas(atualizado.getNumeroHorasMaximas());
        return repository.save(existente);
    }

    @Caching(evict = {
            @CacheEvict(key = "'all'"),
            @CacheEvict(key = "#id")
    })
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    @Cacheable(key = "'search:' + #nome")
    public List<DepartamentoEntity> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    @Cacheable(key = "'horas:' + #numeroHorasMaximas")
    public List<DepartamentoEntity> buscarPorHorasMaximas(Integer numeroHorasMaximas) {
        return repository.findByNumeroHorasMaximas(numeroHorasMaximas);
    }

}
