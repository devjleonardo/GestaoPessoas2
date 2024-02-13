package br.com.devjleonardo.gestaopessoas.backend.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ApiExceptionResponse {

    private Integer status;

    private String title;

    private String detail;

    private String userMessage;

    private List<Field> fields;

    public Integer getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public List<Field> getFields() {
        return fields;
    }

    @Builder
    public static class Field {

        private String name;

        private String errorMessage;

        public String getName() {
            return name;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

}
