package com.thairam.minhasfinancas.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thairam.minhasfinancas.model.entity.Lancamento;
import com.thairam.minhasfinancas.model.enums.StatusLancamento;
import com.thairam.minhasfinancas.model.enums.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

	@Query( value = " "
			+ "SELECT SUM(l.valor) FROM Lancamento l JOIN l.usuario u "
			+ "WHERE u.id = :idUsuario and l.tipo =:tipo and l.status = :status group by u ")
	BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus( 
			@Param("idUsuario")Long idUsuario, 
			@Param("tipo")TipoLancamento tipo,
			@Param("status")StatusLancamento status);
}
