package com.thairam.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.thairam.minhasfinancas.exceptions.RegraNegocioException;
import com.thairam.minhasfinancas.messages.LancamentoExceptionMessages;
import com.thairam.minhasfinancas.model.entity.Lancamento;
import com.thairam.minhasfinancas.model.entity.Usuario;
import com.thairam.minhasfinancas.model.enums.StatusLancamento;
import com.thairam.minhasfinancas.model.enums.TipoLancamento;
import com.thairam.minhasfinancas.model.repository.LancamentoRepository;
import com.thairam.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.thairam.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl lancamentoService;
	
	@MockBean
	LancamentoRepository lancamentoRepository;
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(lancamentoService).validar(lancamentoASalvar);		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(lancamentoRepository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);		
		Lancamento lancamento = lancamentoService.salvar(lancamentoASalvar);
		assertEquals(lancamento.getId(), lancamentoSalvo.getId());
		assertEquals(lancamento.getStatus(), StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(lancamentoService).validar(lancamentoASalvar);
    	Assertions.catchThrowableOfType(() -> lancamentoService.salvar(lancamentoASalvar), RegraNegocioException.class);
    	Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.doNothing().when(lancamentoService).validar(lancamentoSalvo);		
		Mockito.when(lancamentoRepository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);		
		lancamentoService.atualizar(lancamentoSalvo);
		Mockito.verify(lancamentoRepository, Mockito.times(1)).save(lancamentoSalvo);
	}	
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
    	Assertions.catchThrowableOfType(() -> lancamentoService.atualizar(lancamento), NullPointerException.class);
    	Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamento);
	}	
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		lancamentoService.deletar(lancamento);
		Mockito.verify(lancamentoRepository).delete(lancamento);
	}

	@Test
	public void naoDeveDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		Assertions.catchThrowableOfType(() -> lancamentoService.deletar(lancamento), NullPointerException.class);
		Mockito.verify(lancamentoRepository, Mockito.never()).delete(lancamento);		
	}
	
	@Test
	public void deveFiltarLancamentos() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		List<Lancamento> lista = java.util.Arrays.asList(lancamento);
		when(lancamentoRepository.findAll(Mockito.any(Example.class)) ).thenReturn(lista);
		List<Lancamento> resultado = lancamentoService.buscar(lancamento);
		Assertions
			.assertThat(resultado)
			.isNotEmpty()
			.hasSize(1)
			.contains(lancamento);
	}
	
	@Test
	public void deveAtualizarStatusDeUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);		
		lancamento.setStatus(StatusLancamento.PENDENTE);
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(lancamentoService).atualizar(lancamento);		
		lancamentoService.atualizarStatus(lancamento, novoStatus);
		assertEquals(lancamento.getStatus(), novoStatus);
		Mockito.verify(lancamentoService).atualizar(lancamento);
	}
	
	@Test
	public void deveObterUmLancamentoPorId() {
		Long id = 1L;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.of(lancamento));
		Optional<Lancamento> resultado = lancamentoService.obterLancamentoPorId(id);
		assertEquals(resultado.isPresent(), true);
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		Long id = 1L;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.empty());
		Optional<Lancamento> resultado = lancamentoService.obterLancamentoPorId(id);
		assertEquals(resultado.isPresent(), false);
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(LancamentoExceptionMessages.DESCRICAO_INVALIDA);
		
		lancamento.setDescricao("");
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(LancamentoExceptionMessages.DESCRICAO_INVALIDA);
		
		lancamento.setDescricao("Salario");
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(LancamentoExceptionMessages.MES_INVALIDO);
		
		lancamento.setAno(0);
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(LancamentoExceptionMessages.MES_INVALIDO);
		
		lancamento.setAno(13);
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(LancamentoExceptionMessages.MES_INVALIDO);
		
		lancamento.setMes(1);
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(LancamentoExceptionMessages.ANO_INVALIDO);
		
		lancamento.setAno(202);
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(LancamentoExceptionMessages.ANO_INVALIDO);
		
		lancamento.setAno(2020);
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(LancamentoExceptionMessages.USUARIO_INVALIDO);
		
		lancamento.setUsuario(new Usuario());
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(LancamentoExceptionMessages.USUARIO_INVALIDO);
		
		lancamento.getUsuario().setId(1l);
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(LancamentoExceptionMessages.VALOR_INVALIDO);
		
		lancamento.setValor(BigDecimal.ZERO);
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(LancamentoExceptionMessages.VALOR_INVALIDO);
		
		lancamento.setValor(BigDecimal.valueOf(1));
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(LancamentoExceptionMessages.TIPO_INVALIDO);		
	}		
}
