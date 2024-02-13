package br.com.devjleonardo.gestaopessoas.backend.domain.exception;

public class EntidadeEmUsoException extends RegraNegocioException {

	private static final long serialVersionUID = 1L;

	public EntidadeEmUsoException(String mensagem) {
		super(mensagem);
	}

}
