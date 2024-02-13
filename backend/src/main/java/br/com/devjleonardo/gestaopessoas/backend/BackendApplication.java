package br.com.devjleonardo.gestaopessoas.backend;

import br.com.devjleonardo.gestaopessoas.backend.domain.service.CargaInicialStatusService;
import br.com.devjleonardo.gestaopessoas.backend.infrastructure.repository.CustomJpaRepositoryImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl.class)
public class BackendApplication implements CommandLineRunner {

    private final CargaInicialStatusService cargaInicialStatusService;

    public BackendApplication(CargaInicialStatusService cargaInicialStatusService) {
        this.cargaInicialStatusService = cargaInicialStatusService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        cargaInicialStatusService.realizarCargaInicial();
    }

}
