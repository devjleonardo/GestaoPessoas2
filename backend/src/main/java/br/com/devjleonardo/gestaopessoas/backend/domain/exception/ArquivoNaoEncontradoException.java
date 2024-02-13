package br.com.devjleonardo.gestaopessoas.backend.domain.exception;

public class ArquivoNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ArquivoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
    
}
