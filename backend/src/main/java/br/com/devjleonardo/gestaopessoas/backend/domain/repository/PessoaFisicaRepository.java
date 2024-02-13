package br.com.devjleonardo.gestaopessoas.backend.domain.repository;

import br.com.devjleonardo.gestaopessoas.backend.domain.model.PessoaFisica;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaFisicaRepository extends CustomJpaRepository<PessoaFisica, Long> {
	
	Optional<PessoaFisica> findByEmail(String email);
	
	Optional<PessoaFisica> findByCpf(String cpf);
	
	Optional<PessoaFisica> findByRg(String rg);
	
}
