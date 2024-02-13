package br.com.devjleonardo.gestaopessoas.backend.domain.model.enums;

public enum Genero {

    MASCULINO("Masculino"),
    FEMININO("Feminino"),
    OUTROS("Outros");

	private final String descricao;

	private Genero(String descricao) {
		this.descricao = descricao;
	}
	
	@Override
	public String toString() {
		return descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
}
