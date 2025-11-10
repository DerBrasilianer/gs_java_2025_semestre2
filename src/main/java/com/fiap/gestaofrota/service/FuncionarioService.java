package com.fiap.gestaofrota.service;

import com.fiap.gestaofrota.entity.FuncionarioEntity;
import com.fiap.gestaofrota.enums.FuncionarioStatus;
import com.fiap.gestaofrota.repository.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public Page<FuncionarioEntity> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<FuncionarioEntity> listarPorDepartamento(Long departamentoId, Pageable pageable) {
        return repository.findByDepartamentoId(departamentoId, pageable);
    }

    public FuncionarioEntity buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Funcionário com id " + id + " não encontrado"));
    }

    public FuncionarioEntity criar(FuncionarioEntity funcionario) {
        funcionario.setStatus(computeStatus(funcionario));
        return repository.save(funcionario);
    }

    public FuncionarioEntity atualizar(Long id, FuncionarioEntity atualizado) {
        FuncionarioEntity existente = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Funcionário com id " + id + " não encontrado"));
        existente.setNome(atualizado.getNome());
        existente.setDepartamento(atualizado.getDepartamento());
        existente.setHorasTrabalhadasUltimoMes(atualizado.getHorasTrabalhadasUltimoMes());
        existente.setStatus(computeStatus(existente));
        return repository.save(existente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
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
