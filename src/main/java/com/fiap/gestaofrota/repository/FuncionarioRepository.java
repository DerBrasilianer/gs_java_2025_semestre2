package com.fiap.gestaofrota.repository;

import com.fiap.gestaofrota.entity.FuncionarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<FuncionarioEntity, Long> {

    Page<FuncionarioEntity> findByDepartamentoId(Long departamentoId, Pageable pageable);

}
