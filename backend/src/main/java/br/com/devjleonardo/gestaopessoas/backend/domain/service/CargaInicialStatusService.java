package br.com.devjleonardo.gestaopessoas.backend.domain.service;

import br.com.devjleonardo.gestaopessoas.backend.domain.exception.ArquivoNaoEncontradoException;
import br.com.devjleonardo.gestaopessoas.backend.domain.model.CargaInicialStatus;
import br.com.devjleonardo.gestaopessoas.backend.domain.model.PessoaFisica;
import br.com.devjleonardo.gestaopessoas.backend.domain.repository.CargaInicialStatusRepository;
import br.com.devjleonardo.gestaopessoas.backend.domain.repository.PessoaFisicaRepository;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class CargaInicialStatusService {

	private static final Logger logger = LoggerFactory.getLogger(CargaInicialStatusService.class);

	private static final String CAMINHO_ARQUIVO = "/pessoas.ods";

	private final CargaInicialStatusRepository cargaInicialStatusRepository;

	private final PessoaFisicaRepository pessoaFisicaRepository;

	public CargaInicialStatusService(CargaInicialStatusRepository cargaInicialStatusRepository,
									 PessoaFisicaRepository pessoaFisicaRepository) {
		this.cargaInicialStatusRepository = cargaInicialStatusRepository;
		this.pessoaFisicaRepository = pessoaFisicaRepository;
	}

	public void realizarCargaInicial() {
		if (!isCargaInicialExecutada()) {
			try {
				InputStream inputStream = getClass().getResourceAsStream(CAMINHO_ARQUIVO);

				if (inputStream == null) {
					throw new ArquivoNaoEncontradoException("Arquivo não encontrado: " + CAMINHO_ARQUIVO);
				}

				OdfSpreadsheetDocument documentoPlanilha = OdfSpreadsheetDocument.loadDocument(inputStream);

				OdfTable primeiraPlanilha = documentoPlanilha.getTableList(true).get(0);

				int quantidadeLinhas = primeiraPlanilha.getRowCount();

				List<PessoaFisica> pessoasFisica = new ArrayList<>();

				for (int i = 1; i < quantidadeLinhas; i++) {
					Long id = Long.parseLong(primeiraPlanilha.getCellByPosition(0, i).getStringValue());
					String firstName = primeiraPlanilha.getCellByPosition(1, i).getStringValue();
					String lastName = primeiraPlanilha.getCellByPosition(2, i).getStringValue();
					String email = primeiraPlanilha.getCellByPosition(3, i).getStringValue();
					String cpf = primeiraPlanilha.getCellByPosition(4, i).getStringValue();

					String nomeCompleto = firstName + " " + lastName;

					PessoaFisica pessoaFisica = new PessoaFisica();
					pessoaFisica.setId(id);
					pessoaFisica.setNome(nomeCompleto);
					pessoaFisica.setCpf(cpf);
					pessoaFisica.setEmail(email);

					pessoasFisica.add(pessoaFisica);
				}

				pessoaFisicaRepository.saveAllAndFlush(pessoasFisica);

				marcarCargaInicialComoExecutada();
			} catch (ArquivoNaoEncontradoException e) {
				logger.error("Erro ao carregar arquivo: {}", e.getMessage());
			} catch (Exception e) {
				logger.error("Stacktrace do erro:", e);
			}
		}
	}

	public boolean isCargaInicialExecutada() {
		return cargaInicialStatusRepository.findById(1L)
				.map(CargaInicialStatus::isExecutada)
				.orElse(false);
	}

	private void marcarCargaInicialComoExecutada() {
		if (cargaInicialStatusRepository.count() == 0) {
		    CargaInicialStatus cargaInicialStatus = new CargaInicialStatus();
		    cargaInicialStatus.setExecutada(true);
		    
		    cargaInicialStatusRepository.save(cargaInicialStatus);
		}
	}
}
