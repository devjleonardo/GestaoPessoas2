package br.com.devjleonardo.gestaopessoas.backend.api.controller;

import br.com.devjleonardo.gestaopessoas.backend.api.converter.PessoaFisicaConverter;
import br.com.devjleonardo.gestaopessoas.backend.api.dto.PessoaFisicaCadastroAtualizacaoDTO;
import br.com.devjleonardo.gestaopessoas.backend.api.dto.PessoaFisicaDTO;
import br.com.devjleonardo.gestaopessoas.backend.domain.model.PessoaFisica;
import br.com.devjleonardo.gestaopessoas.backend.domain.service.PessoaFisicaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/pessoas-fisicas")
public class PessoaFisicaController {

    private final PessoaFisicaService pessoaFisicaService;
    
    private final PessoaFisicaConverter pessoaFisicaConverter;

    public PessoaFisicaController(PessoaFisicaService pessoaFisicaService,
                                  PessoaFisicaConverter pessoaFisicaConverter) {
        this.pessoaFisicaService = pessoaFisicaService;
        this.pessoaFisicaConverter = pessoaFisicaConverter;
    }

    @GetMapping
    public List<PessoaFisicaDTO> listarTodas() {
        List<PessoaFisica> todasPessoasFisicas = pessoaFisicaService.listarTodas();
        
        return pessoaFisicaConverter.converterParaListaDTO(todasPessoasFisicas);
    }

    @GetMapping("/{id}")
    public PessoaFisicaDTO buscarPorId(@PathVariable Long id) {
        PessoaFisica pessoaFisica = pessoaFisicaService.buscarOuFalharPorId(id);
        
        return pessoaFisicaConverter.converterParaDTO(pessoaFisica);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PessoaFisicaDTO criar(@RequestBody @Valid PessoaFisicaCadastroAtualizacaoDTO dadosCadastroPessoaFisica) {
        PessoaFisica pessoaFisica = pessoaFisicaConverter.converterParaEntidade(dadosCadastroPessoaFisica);
        
        PessoaFisica pessoaFisicaSalva = pessoaFisicaService.salvar(pessoaFisica);
        
        return pessoaFisicaConverter.converterParaDTO(pessoaFisicaSalva);
    }

    @PutMapping("/{id}")
    public PessoaFisicaDTO atualizar(@PathVariable Long id, 
    		@RequestBody @Valid PessoaFisicaCadastroAtualizacaoDTO dadosAtualizacaoPessoaFisica) {
        PessoaFisica pessoaExistente = pessoaFisicaService.buscarOuFalharPorId(id);
        
        pessoaFisicaConverter.atualizarEntidadeComDadosDTO(dadosAtualizacaoPessoaFisica, pessoaExistente);
        
        PessoaFisica pessoaFisicaAtualizada = pessoaFisicaService.salvar(pessoaExistente);
        
        return pessoaFisicaConverter.converterParaDTO(pessoaFisicaAtualizada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        pessoaFisicaService.excluirPorId(id);
    } 
    
}
