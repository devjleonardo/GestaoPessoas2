package br.com.devjleonardo.gestaopessoas.backend.domain.exception;

public class PessoaFisicaNaoEncontradaException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;

	public PessoaFisicaNaoEncontradaException(String mensagem) {
		super(mensagem);
	}
	
	public PessoaFisicaNaoEncontradaException(Long id) {
		this(String.format("Nenhuma pessoa física encontrada com o código %d", id));
	}

}
