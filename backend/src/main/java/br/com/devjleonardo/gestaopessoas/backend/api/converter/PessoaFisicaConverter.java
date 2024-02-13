package br.com.devjleonardo.gestaopessoas.backend.api.converter;

import br.com.devjleonardo.gestaopessoas.backend.api.dto.PessoaFisicaCadastroAtualizacaoDTO;
import br.com.devjleonardo.gestaopessoas.backend.api.dto.PessoaFisicaDTO;
import br.com.devjleonardo.gestaopessoas.backend.domain.model.PessoaFisica;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PessoaFisicaConverter {
	
	@Autowired
	private ModelMapper modelMapper;
	
	public PessoaFisicaDTO converterParaDTO(PessoaFisica pessoaFisica) {
		return modelMapper.map(pessoaFisica, PessoaFisicaDTO.class);
	}
	
	public List<PessoaFisicaDTO> converterParaListaDTO(List<PessoaFisica> pessoasFisica) {
		return pessoasFisica.stream()
				.map(this::converterParaDTO)
				.collect(Collectors.toList());
	}
	
	public PessoaFisica converterParaEntidade(PessoaFisicaCadastroAtualizacaoDTO dadosCadastroPessoaFisica) {
		return modelMapper.map(dadosCadastroPessoaFisica, PessoaFisica.class);
	}
	
	public void atualizarEntidadeComDadosDTO(PessoaFisicaCadastroAtualizacaoDTO dadosAtualizacaoPessoaFisica, 
			PessoaFisica pessoa) {
		modelMapper.map(dadosAtualizacaoPessoaFisica, pessoa);
	}

}
