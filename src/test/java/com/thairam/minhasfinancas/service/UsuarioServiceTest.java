package com.thairam.minhasfinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.thairam.minhasfinancas.exceptions.ErroAutenticacao;
import com.thairam.minhasfinancas.exceptions.RegraNegocioException;
import com.thairam.minhasfinancas.messages.UsuarioExceptionMessages;
import com.thairam.minhasfinancas.model.entity.Usuario;
import com.thairam.minhasfinancas.model.repository.UsuarioRepository;
import com.thairam.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl usuarioService;
	
	@MockBean
	UsuarioRepository usuarioRepository;
	
	public String nome = "thairam";
	public String email = "thairam@email.com";
	public String senha = "senha";
	    
    @Test
    public void deveSalvarUmUsuario() {
      org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> {
        	Mockito.doNothing().when(usuarioService).validarEmail(Mockito.anyString());
        	Usuario usuario = criarUsuarioComId();
        	Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
        	Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);

        	Assertions.assertThat(usuarioSalvo).isNotNull();
        	Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
        	Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo(nome);
        	Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo(email);
        	Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo(senha);
      });    	
    }
    
    @Test
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
    	Usuario usuario = criarUsuarioComId();
    	Mockito.doThrow(RegraNegocioException.class).when(usuarioService).validarEmail(email);
    	Throwable exception = Assertions.catchThrowable( () -> usuarioService.salvarUsuario(usuario));
    	Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class);
    	Mockito.verify(usuarioRepository, Mockito.never()).save(usuario);
    }
    
    @Test
    public void deveAutenticarUmUsuarioComSucesso() {
    	Usuario usuario = criarUsuarioComId();
    	Mockito.when(usuarioRepository.findByEmail(email) ).thenReturn(Optional.of(usuario));    	
    	Usuario resultado = usuarioService.autenticar(email, senha);
    	Assertions.assertThat(resultado).isNotNull();
    }
    
    @Test
    public void deveLancarErroQuandoNaoEncontradoUsuarioCadastradoComEmailInformado() {
    	Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        Throwable exception = Assertions.catchThrowable( () -> usuarioService.autenticar(email, senha) );        
        Assertions.assertThat(exception)
        	.isInstanceOf(ErroAutenticacao.class)
        		.hasMessage(UsuarioExceptionMessages.USUARIO_NAO_ENCONTRADO_PARA_EMAIL);
     }
    
    @Test
    public void deveLancarErroQuandoSenhaNaoCombinar() {
        Usuario usuario = criarUsuarioComId();
        Mockito.when(usuarioRepository.findByEmail(email) ).thenReturn(Optional.of(usuario));
        Throwable exception = Assertions
        	.catchThrowable( () -> usuarioService.autenticar(email, "senhaNaoCombina") );
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class)
        	.hasMessage(UsuarioExceptionMessages.SENHA_INVALIDA);        
     }    
    
    @Test
    public void deveValidarEmail() {
       org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> {
    	   	Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
   			usuarioService.validarEmail(email);    	   
      });
    }
    
    @Test
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
   	   	Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(true);
       	Throwable exception = Assertions.catchThrowable( () -> usuarioService.validarEmail(email));        	
       	Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class);
    }
    
	
    private Usuario criarUsuarioComId() {
    	return Usuario.builder()
    			.nome(nome)
    			.email(email)
    			.senha(senha)
    			.id(1L)
    			.build();
    }    
    
}
