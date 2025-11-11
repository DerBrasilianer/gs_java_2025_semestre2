package com.fiap.gestaoltakn.service;

import com.fiap.gestaoltakn.entity.FuncionarioEntity;
import com.fiap.gestaoltakn.enums.FuncionarioStatus;
import com.fiap.gestaoltakn.repository.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "funcionarios")
public class FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    @Cacheable(key = "T(org.springframework.data.domain.PageRequest).class.isInstance(#pageable) ? 'page:' + #pageable.pageNumber + ':' + #pageable.pageSize : 'page:all'")
    public Page<FuncionarioEntity> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Cacheable(key = "T(org.springframework.data.domain.PageRequest).class.isInstance(#pageable) ? 'departamento:' + #departamentoId + ':page:' + #pageable.pageNumber + ':' + #pageable.pageSize : 'departamento:' + #departamentoId + ':page:all'")
    public Page<FuncionarioEntity> listarPorDepartamento(Long departamentoId, Pageable pageable) {
        return repository.findByDepartamento_Id(departamentoId, pageable);
    }

    @Cacheable(key = "#id")
    public FuncionarioEntity buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário com id " + id + " não encontrado"));
    }

    @Caching(evict = {
            @CacheEvict(key = "'page:*'"),
            @CacheEvict(key = "'departamento:*'"),
            @CacheEvict(key = "'search:*'"),
            @CacheEvict(key = "'status:*'"),
            @CacheEvict(key = "'horas:*'"),
            @CacheEvict(key = "'departamentoNome:*'"),
            @CacheEvict(key = "#funcionario.id")
    })
    public FuncionarioEntity criar(FuncionarioEntity funcionario) {
        funcionario.setStatus(computeStatus(funcionario));
        return repository.save(funcionario);
    }

    @Caching(evict = {
            @CacheEvict(key = "'page:*'"),
            @CacheEvict(key = "'departamento:*'"),
            @CacheEvict(key = "'search:*'"),
            @CacheEvict(key = "'status:*'"),
            @CacheEvict(key = "'horas:*'"),
            @CacheEvict(key = "'departamentoNome:*'"),
            @CacheEvict(key = "#id")
    })
    public FuncionarioEntity atualizar(Long id, FuncionarioEntity atualizado) {
        FuncionarioEntity existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário com id " + id + " não encontrado"));

        existente.setNome(atualizado.getNome());
        existente.setDepartamento(atualizado.getDepartamento());
        existente.setHorasTrabalhadasUltimoMes(atualizado.getHorasTrabalhadasUltimoMes());
        existente.setStatus(computeStatus(existente));

        return repository.save(existente);
    }

    @Caching(evict = {
            @CacheEvict(key = "'page:*'"),
            @CacheEvict(key = "'departamento:*'"),
            @CacheEvict(key = "'search:*'"),
            @CacheEvict(key = "'status:*'"),
            @CacheEvict(key = "'horas:*'"),
            @CacheEvict(key = "'departamentoNome:*'"),
            @CacheEvict(key = "#id")
    })
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    @Cacheable(key = "'search:' + #nome")
    public List<FuncionarioEntity> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    @Cacheable(key = "'departamentoNome:' + #departamentoNome")
    public List<FuncionarioEntity> buscarPorDepartamentoNome(String departamentoNome) {
        return repository.findByDepartamentoNomeContainingIgnoreCase(departamentoNome);
    }

    @Cacheable(key = "'horas:' + #horas")
    public List<FuncionarioEntity> buscarPorHoras(Integer horas) {
        return repository.findByHorasTrabalhadasUltimoMes(horas);
    }

    @Cacheable(key = "'status:' + #status.name()")
    public List<FuncionarioEntity> buscarPorStatus(FuncionarioStatus status) {
        return repository.findByStatus(status);
    }

    private FuncionarioStatus computeStatus(FuncionarioEntity funcionario) {
        if (funcionario == null) return FuncionarioStatus.SAUDAVEL;
        if (funcionario.getDepartamento() == null) return FuncionarioStatus.SAUDAVEL;

        Integer maxHoras = funcionario.getDepartamento().getNumeroHorasMaximas();
        Integer trabalhadas = funcionario.getHorasTrabalhadasUltimoMes();

        if (maxHoras != null && trabalhadas != null && trabalhadas > maxHoras) {
            return FuncionarioStatus.EM_RISCO;
        }

        return FuncionarioStatus.SAUDAVEL;
    }

}
