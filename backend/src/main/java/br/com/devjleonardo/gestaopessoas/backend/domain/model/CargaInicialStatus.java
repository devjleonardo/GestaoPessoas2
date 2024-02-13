package br.com.devjleonardo.gestaopessoas.backend.domain.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class CargaInicialStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String registroUnico = "unico";

    @Column(nullable = false)
    private boolean executada;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistroUnico() {
        return registroUnico;
    }

    public void setRegistroUnico(String registroUnico) {
        this.registroUnico = registroUnico;
    }

    public boolean isExecutada() {
        return executada;
    }

    public void setExecutada(boolean executada) {
        this.executada = executada;
    }

    @Override
    public boolean equals(Object outraCargaInicialStatus) {
        if (this == outraCargaInicialStatus) {
            return true;
        }

        if (outraCargaInicialStatus == null || getClass() != outraCargaInicialStatus.getClass()) {
            return false;
        }

        CargaInicialStatus cargaInicialStatus = (CargaInicialStatus) outraCargaInicialStatus;

        return Objects.equals(id, cargaInicialStatus.id) &&
                Objects.equals(registroUnico, cargaInicialStatus.registroUnico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, registroUnico);
    }

}
