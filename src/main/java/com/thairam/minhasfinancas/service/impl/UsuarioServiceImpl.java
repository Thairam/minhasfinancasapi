package com.thairam.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thairam.minhasfinancas.exceptions.ErroAutenticacao;
import com.thairam.minhasfinancas.exceptions.RegraNegocioException;
import com.thairam.minhasfinancas.messages.UsuarioExceptionMessages;
import com.thairam.minhasfinancas.model.entity.Usuario;
import com.thairam.minhasfinancas.model.repository.UsuarioRepository;
import com.thairam.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	private UsuarioRepository usuarioRepository;
	
	public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
		super();
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao(UsuarioExceptionMessages
					.USUARIO_NAO_ENCONTRADO_PARA_EMAIL);
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao(UsuarioExceptionMessages.SENHA_INVALIDA);			
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return usuarioRepository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = usuarioRepository.existsByEmail(email);
		if(existe) {
			throw new RegraNegocioException(UsuarioExceptionMessages
					.EMAIL_JA_CADASTRADO);
		}
	}

	@Override
	public Optional<Usuario> obterUsuarioPorId(Long id) {
		return usuarioRepository.findById(id);
	}

}
