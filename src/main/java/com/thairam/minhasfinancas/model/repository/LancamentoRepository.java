package com.thairam.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thairam.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
