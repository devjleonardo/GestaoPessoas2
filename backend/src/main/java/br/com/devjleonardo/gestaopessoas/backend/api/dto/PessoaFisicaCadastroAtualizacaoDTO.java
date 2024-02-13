package br.com.devjleonardo.gestaopessoas.backend.api.dto;

import br.com.devjleonardo.gestaopessoas.backend.domain.model.enums.Genero;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class PessoaFisicaCadastroAtualizacaoDTO  implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	private String nome;

	@NotBlank
	private String email;

	private String telefone;

	@NotBlank
	private String cpf;

	private String rg;

	private Genero genero;

	private boolean ativo;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public Genero getGenero() {
		return genero;
	}

	public void setGenero(Genero genero) {
		this.genero = genero;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
