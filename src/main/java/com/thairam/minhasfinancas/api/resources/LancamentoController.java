package com.thairam.minhasfinancas.api.resources;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thairam.minhasfinancas.api.dto.LancamentoDTO;
import com.thairam.minhasfinancas.exceptions.RegraNegocioException;
import com.thairam.minhasfinancas.messages.LancamentoExceptionMessages;
import com.thairam.minhasfinancas.messages.UsuarioExceptionMessages;
import com.thairam.minhasfinancas.model.entity.Lancamento;
import com.thairam.minhasfinancas.model.entity.Usuario;
import com.thairam.minhasfinancas.model.enums.StatusLancamento;
import com.thairam.minhasfinancas.model.enums.TipoLancamento;
import com.thairam.minhasfinancas.service.LancamentoService;
import com.thairam.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping( "/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

	private final LancamentoService lancamentoService;
	private final UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam("usuario") Long usuarioId
			) {

		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		
		Optional<Usuario> usuario = usuarioService.obterUsuarioPorId(usuarioId);
		if(!usuario.isPresent()) {
			return ResponseEntity.badRequest().body(UsuarioExceptionMessages.USUARIO_NAO_ENCONTRADO_PARA_ID_INFORMADO);
		} else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamento lancamento = converterDtoParaLancamento(dto);
			lancamento = lancamentoService.salvar(lancamento);
			return new ResponseEntity(lancamento, HttpStatus.CREATED);		
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return lancamentoService.obterLancamentoPorId(id).map( entity -> {
			try {
				Lancamento lancamento = converterDtoParaLancamento(dto);
				lancamento.setId(entity.getId());
				lancamentoService.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);				
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () -> 
			new ResponseEntity(LancamentoExceptionMessages
					.LANCAMENTO_NAO_ENCONTRADO, HttpStatus.BAD_REQUEST));
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return lancamentoService.obterLancamentoPorId(id).map( lancamento -> {
			lancamentoService.deletar(lancamento);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet( () -> 
				new ResponseEntity(LancamentoExceptionMessages
						.LANCAMENTO_NAO_ENCONTRADO, HttpStatus.BAD_REQUEST));
	}
	
	private Lancamento converterDtoParaLancamento(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario  = usuarioService.obterUsuarioPorId(dto.getUsuario())
			.orElseThrow( () -> new RegraNegocioException(UsuarioExceptionMessages
					.USUARIO_NAO_ENCONTRADO_PARA_ID_INFORMADO));
		
		lancamento.setUsuario(usuario);
		
		if(dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));			
		}
		
		if(dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));			
		}
		
		return lancamento;
	}
	
}
