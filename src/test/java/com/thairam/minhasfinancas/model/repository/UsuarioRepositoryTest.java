package com.thairam.minhasfinancas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.thairam.minhasfinancas.model.entity.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	TestEntityManager entityManager;

	private String nome = "thairam";
	private String email = "thairam@email.com";
	private String senha = "senha";

	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		boolean resultado = usuarioRepository.existsByEmail(email);
		Assertions.assertThat(resultado).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		boolean resultado = usuarioRepository.existsByEmail(email);
		Assertions.assertThat(resultado).isFalse();
	}
	
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		Usuario usuario = criarUsuario();
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		Optional<Usuario> resultado =usuarioRepository.findByEmail(email);
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {
		Optional<Usuario> resultado =usuarioRepository.findByEmail(email);
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}	

	public Usuario criarUsuario() {
		return Usuario.builder()
				.nome(nome)
				.senha(senha)
				.email(email)
				.build();
	}
}
