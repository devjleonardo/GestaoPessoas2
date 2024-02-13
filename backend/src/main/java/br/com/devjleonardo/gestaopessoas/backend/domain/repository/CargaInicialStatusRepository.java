package br.com.devjleonardo.gestaopessoas.backend.domain.repository;

import br.com.devjleonardo.gestaopessoas.backend.domain.model.CargaInicialStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargaInicialStatusRepository extends JpaRepository<CargaInicialStatus, Long> {

}
