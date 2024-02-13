package br.com.devjleonardo.gestaopessoas.backend.domain.model;

import br.com.devjleonardo.gestaopessoas.backend.domain.model.enums.Genero;

import javax.persistence.*;
import java.util.Objects;

@Entity
@PrimaryKeyJoinColumn(name = "pessoa_id")
public class PessoaFisica extends Pessoa {

    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    private String rg;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof PessoaFisica)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        PessoaFisica outraPessoaFisica = (PessoaFisica) obj;

        return Objects.equals(this.cpf, outraPessoaFisica.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cpf);
    }

}
