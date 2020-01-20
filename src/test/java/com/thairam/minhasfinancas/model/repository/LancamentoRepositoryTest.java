package com.thairam.minhasfinancas.model.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.thairam.minhasfinancas.model.entity.Lancamento;
import com.thairam.minhasfinancas.model.enums.StatusLancamento;
import com.thairam.minhasfinancas.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository lancamentoRepository;
	
	@Autowired
	TestEntityManager entityManager;
	
	private static Integer ano = 2019;
	private static Integer mes = 1;	
	private static String descricao = "descrição lançamento";
	private static BigDecimal valor = BigDecimal.valueOf(10);
	private static TipoLancamento tipo = TipoLancamento.RECEITA;
	private static StatusLancamento status = StatusLancamento.PENDENTE;
	private static LocalDate dataCadastro = LocalDate.now();
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento = lancamentoRepository.save(lancamento);
		assertNotNull(lancamento.getId());
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = criarESalvarLancamento();
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		lancamentoRepository.delete(lancamento);
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		assertEquals(lancamentoInexistente, null);
	}	
	
	@Test
	public void deveAtualizarLancamento() {
		Lancamento lancamento = criarESalvarLancamento();
		lancamento.setAno(2018);
		lancamento.setDescricao("lançamento atualizado");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		
		lancamentoRepository.save(lancamento);
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		
		assertEquals(lancamentoAtualizado.getAno(), 2018);
		assertEquals(lancamentoAtualizado.getDescricao(), "lançamento atualizado");
		assertEquals(lancamentoAtualizado.getStatus(), StatusLancamento.CANCELADO);
	}
	
	@Test
	public void deveBuscarLancamentoPorId() {
		Lancamento lancamento = criarESalvarLancamento();		
		Optional<Lancamento> lancamentoEncontrado = lancamentoRepository.findById(lancamento.getId());
		assertTrue(lancamentoEncontrado.isPresent());
	}
	
	private Lancamento criarESalvarLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;		
	}
	
	public static Lancamento criarLancamento() {
		return Lancamento
				.builder()
				.ano(ano)
				.mes(mes)
				.descricao(descricao)
				.valor(valor)
				.tipo(tipo)
				.status(status)
				.dataCadastro(dataCadastro)
				.build();
	}
	
}
