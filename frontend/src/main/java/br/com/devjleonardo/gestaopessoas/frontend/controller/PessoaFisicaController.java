package br.com.devjleonardo.gestaopessoas.frontend.controller;

import br.com.devjleonardo.gestaopessoas.frontend.dto.PessoaFisicaDTO;
import br.com.devjleonardo.gestaopessoas.frontend.util.HttpConfigConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
public class PessoaFisicaController {

    private static final Logger logger = LoggerFactory.getLogger(PessoaFisicaController.class);

    private static final String BASE_URL = HttpConfigConstants.BASE_URL + "pessoas-fisicas";

    private final RestTemplate clienteHttp;

    private PessoaFisicaDTO pessoaFisica;

    private List<PessoaFisicaDTO> pessoasFisicas;

    public PessoaFisicaController(RestTemplate clienteHttp) {
        this.clienteHttp = clienteHttp;
    }

    @PostConstruct
    public void inicializar() {
        carregarPessoasFisicas();
    }

    public void iniciarCadastroPessoaFisica() {
        this.pessoaFisica = new PessoaFisicaDTO();
    }

    public void processarOperacaoCadastroOuAtualizacao() {
        if (pessoaFisica.getId() == null) {
            cadastrarPessoaFisica(pessoaFisica);
        } else {
            atualizarPessoaFisica(pessoaFisica);
        }
    }

    public void removerPessoaFisicaSelecionada() {
        try {
            ResponseEntity<Void> response = clienteHttp.getForEntity(
                    BASE_URL + "/" + pessoaFisica.getId(), Void.class
            );

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                handleResponse("Pessoa física removida com sucesso!");
            } else {
                handleErrorResponse(response);
            }
        } catch (RestClientException e) {
            handleException(e);
        }

        pessoasFisicas.remove(pessoaFisica);
        pessoaFisica = null;

        PrimeFaces.current().ajax().update("form:messages", "form:dt-pessoas-fisicas");
    }

    public String formatarCPF(String cpfNaoFormatado) {
        return cpfNaoFormatado.substring(0, 3) + "." +
                cpfNaoFormatado.substring(3, 6) + "." +
                cpfNaoFormatado.substring(6, 9) + "-" +
                cpfNaoFormatado.substring(9);
    }

    private void carregarPessoasFisicas() {
        try {
            ResponseEntity<PessoaFisicaDTO[]> response = clienteHttp.getForEntity(BASE_URL, PessoaFisicaDTO[].class);

            if (response.getStatusCode() == HttpStatus.OK) {
                PessoaFisicaDTO[] pessoasFisicasArray = response.getBody();

                if (pessoasFisicasArray != null) {
                    pessoasFisicas = Arrays.asList(pessoasFisicasArray);
                }
            } else {
                handleErrorResponse(response);
            }
        } catch (RestClientException e) {
            handleException(e);
        }
    }

    private void cadastrarPessoaFisica(PessoaFisicaDTO pessoaFisicaDTO) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(HttpConfigConstants.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(HttpConfigConstants.APPLICATION_JSON));

            HttpEntity<PessoaFisicaDTO> request = new HttpEntity<>(pessoaFisicaDTO, headers);

            ResponseEntity<PessoaFisicaDTO> response = clienteHttp.postForEntity(
                    BASE_URL, request, PessoaFisicaDTO.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                handleResponse("Pessoa física cadastrada com sucesso!");
            } else {
                handleErrorResponse(response);
            }
        } catch (RestClientException e) {
            handleException(e);
        }
    }

    private void atualizarPessoaFisica(PessoaFisicaDTO pessoaFisicaDTO) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(HttpConfigConstants.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(HttpConfigConstants.APPLICATION_JSON));

            HttpEntity<PessoaFisicaDTO> request = new HttpEntity<>(pessoaFisicaDTO, headers);

            ResponseEntity<Void> response = clienteHttp.exchange(
                    BASE_URL + "/" + pessoaFisicaDTO.getId(), HttpMethod.PUT, request, Void.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                handleResponse("Pessoa física atualizada com sucesso!");
            } else {
                handleErrorResponse(response);
            }
        } catch (RestClientException e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        logger.error("Erro ao carregar pessoas físicas: {}", e.getMessage());

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Erro ao carregar pessoas físicas", null));
    }

    private void handleResponse(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(message));

        PrimeFaces.current().executeScript("PF('cadastrarPessoaFisicaDialog').hide()");

        PrimeFaces.current().ajax().update("form:messages", "form:dt-pessoas-fisicas");

        carregarPessoasFisicas();
    }

    private void handleErrorResponse(ResponseEntity<?> response) {
        Object responseBodyObj = response.getBody();

        if (responseBodyObj instanceof JsonNode) {
            JsonNode responseBody = (JsonNode) responseBodyObj;

            String errorMessage = responseBody.has("userMessage") ?
                    responseBody.get("userMessage").asText() : "Erro desconhecido";

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    errorMessage, null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erro desconhecido na resposta do servidor", null));
        }

        PrimeFaces.current().ajax().update("form:messages");
    }

    public PessoaFisicaDTO getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisicaDTO pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public List<PessoaFisicaDTO> getPessoasFisicas() {
        return pessoasFisicas;
    }

    public void setPessoasFisicas(List<PessoaFisicaDTO> pessoasFisicas) {
        this.pessoasFisicas = pessoasFisicas;
    }

}
