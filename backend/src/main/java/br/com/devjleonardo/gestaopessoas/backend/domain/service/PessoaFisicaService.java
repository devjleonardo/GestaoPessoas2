package br.com.devjleonardo.gestaopessoas.backend.domain.service;

import br.com.devjleonardo.gestaopessoas.backend.domain.exception.EntidadeEmUsoException;
import br.com.devjleonardo.gestaopessoas.backend.domain.exception.PessoaFisicaNaoEncontradaException;
import br.com.devjleonardo.gestaopessoas.backend.domain.exception.RegraNegocioException;
import br.com.devjleonardo.gestaopessoas.backend.domain.model.PessoaFisica;
import br.com.devjleonardo.gestaopessoas.backend.domain.repository.PessoaFisicaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PessoaFisicaService {
	
	private static final String MSG_EXISTENCIA_PESSOA_FISICA = "Já existe uma pessoa física cadastrada com o %s %s";
	
	private static final String MSG_PESSOA_FISICA_EM_USO
			= "Pessoa física de código %d está em uso e não pode ser removida";

    private final PessoaFisicaRepository pessoaFisicaRepository;

    public PessoaFisicaService(PessoaFisicaRepository pessoaFisicaRepository) {
        this.pessoaFisicaRepository = pessoaFisicaRepository;
    }
	
    public List<PessoaFisica> listarTodas() {
        return pessoaFisicaRepository.findAll();
    }

	public PessoaFisica buscarOuFalharPorId(Long id) {
		return pessoaFisicaRepository.findById(id)
				.orElseThrow(() -> new PessoaFisicaNaoEncontradaException(id));
	}
	
	@Transactional
    public PessoaFisica salvar(PessoaFisica pessoaFisica) {
		pessoaFisicaRepository.detach(pessoaFisica);
		
		verificarExistenciaPorEmail(pessoaFisica);
	    verificarExistenciaPorCpf(pessoaFisica);
	    verificarExistenciaPorRg(pessoaFisica);

		return pessoaFisicaRepository.save(pessoaFisica);
    }
    
	@Transactional
	public void excluirPorId(Long id) {
		try {
			pessoaFisicaRepository.deleteById(id);
			pessoaFisicaRepository.flush();
		} catch (EmptyResultDataAccessException e) {
			throw new PessoaFisicaNaoEncontradaException(id);
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
			    String.format(MSG_PESSOA_FISICA_EM_USO, id)
			);
		}
	}
    
    private void verificarExistenciaPorEmail(PessoaFisica pessoaFisica) {
        String email = pessoaFisica.getEmail();
        
        pessoaFisicaRepository.findByEmail(email)
                .ifPresent(pessoFisicaExistente -> {
                    if (!pessoFisicaExistente.equals(pessoaFisica)) {
                        throw new RegraNegocioException(String.format(MSG_EXISTENCIA_PESSOA_FISICA, "E-mail", email));
                    }
                });
    }

    private void verificarExistenciaPorCpf(PessoaFisica pessoaFisica) {
        String cpf = pessoaFisica.getCpf();

        pessoaFisicaRepository.findByCpf(cpf)
			    .ifPresent(pessoFisicaExistente -> {
			        if (!pessoFisicaExistente.equals(pessoaFisica)) {
			            throw new RegraNegocioException(String.format(MSG_EXISTENCIA_PESSOA_FISICA, "CPF", cpf));
			        }
			    });
    }

    private void verificarExistenciaPorRg(PessoaFisica pessoaFisica) {
    	String rg = pessoaFisica.getRg();
    	
        if (rg != null && !rg.isEmpty()) {
        	pessoaFisicaRepository.findByRg(rg)
			        .ifPresent(pessoFisicaExistente -> {
		                if (!pessoFisicaExistente.equals(pessoaFisica)) {
		                    throw new RegraNegocioException(String.format(MSG_EXISTENCIA_PESSOA_FISICA, "RG", rg));
		                }
			        });
        }
    }
    
}
