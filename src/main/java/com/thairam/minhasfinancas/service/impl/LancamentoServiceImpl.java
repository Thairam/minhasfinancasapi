package com.thairam.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thairam.minhasfinancas.exceptions.RegraNegocioException;
import com.thairam.minhasfinancas.messages.LancamentoExceptionMessages;
import com.thairam.minhasfinancas.model.entity.Lancamento;
import com.thairam.minhasfinancas.model.enums.StatusLancamento;
import com.thairam.minhasfinancas.model.repository.LancamentoRepository;
import com.thairam.minhasfinancas.service.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService{
  
	private LancamentoRepository lancamentoRepository;
	
	public LancamentoServiceImpl(LancamentoRepository repository) {
		this.lancamentoRepository = repository;
	}
	
	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return lancamentoRepository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		validar(lancamento);
		return lancamentoRepository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		lancamentoRepository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		Example example = Example.of(lancamentoFiltro, 
				ExampleMatcher
					.matching().withIgnoreCase()
					.withStringMatcher(StringMatcher.CONTAINING));
		return lancamentoRepository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException(LancamentoExceptionMessages.DESCRICAO_INVALIDA);
		}
		
		if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			throw new RegraNegocioException(LancamentoExceptionMessages.MES_INVALIDO);
		}

		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException(LancamentoExceptionMessages.ANO_INVALIDO);			
		}		
		
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			throw new RegraNegocioException(LancamentoExceptionMessages.USUARIO_INVALIDO);						
		}
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			throw new RegraNegocioException(LancamentoExceptionMessages.VALOR_INVALIDO);									
		}
		
		if(lancamento.getTipo() == null) {
			throw new RegraNegocioException(LancamentoExceptionMessages.TIPO_INVALIDO);												
		}
	}

	@Override
	public Optional<Lancamento> obterLancamentoPorId(Long id) {
		return lancamentoRepository.findById(id);
	}
}
