package br.com.devjleonardo.gestaopessoas.backend.api.exception;

import br.com.devjleonardo.gestaopessoas.backend.domain.exception.EntidadeEmUsoException;
import br.com.devjleonardo.gestaopessoas.backend.domain.exception.EntidadeNaoEncontradaException;
import br.com.devjleonardo.gestaopessoas.backend.domain.exception.RegraNegocioException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    public ApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Método relacionado a exceções não mapeadas

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
        String detail = "Ocorreu um erro interno inesperado no sistema. "
                + "Tente novamente e se o problema persistir, entre em contato "
                + "com o administrador do sistema.";

        ApiExceptionResponse apiExceptionResponse = createApiExceptionResponseBuilder(
                HttpStatus.INTERNAL_SERVER_ERROR, "Erro de Sistema", detail
        )
        .build();

        return handleExceptionInternal(
                ex, apiExceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request
        );
    }

    // Método relacionado a exceções de campos estão inválidos

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        String detail = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";

        BindingResult bindingResult = ex.getBindingResult();

        List<ApiExceptionResponse.Field> fieldsWithErrors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> {
                    String message = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());

                    return ApiExceptionResponse.Field.builder()
                            .name(fieldError.getField())
                            .errorMessage(message)
                            .build();
                })
                .collect(Collectors.toList());

        ApiExceptionResponse apiExceptionResponse = createApiExceptionResponseBuilder(
                status, "Dados inválidos", detail
        )
        .userMessage(detail)
        .fields(fieldsWithErrors)
        .build();

        return handleExceptionInternal(ex, apiExceptionResponse, headers, status, request);
    }

    // Métodos relacionados a exceções de mensagem HTTP não legível

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex);

        if (rootCause instanceof InvalidFormatException) {
            return handleInvalidFormatException(
                    (InvalidFormatException) rootCause, headers, status, request
            );
        }

        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    private ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex,
                                                                HttpHeaders headers, HttpStatus status,
                                                                WebRequest request) {
        String path = getPathFromException(ex);

        String detail = String.format(
                "O campo '%s' foi atribuído com o valor '%s', que não é válido para o "
                        + "tipo esperado (%s). Por favor, corrija e forneça um valor compatível.",
                path, ex.getValue(), ex.getTargetType().getSimpleName()
        );

        ApiExceptionResponse apiExceptionResponse = createApiExceptionResponseBuilder(
                status, "Mensagem de Requisição Inválida", detail
        )
        .build();

        return handleExceptionInternal(ex, apiExceptionResponse, headers, status, request);
    }

    private String getPathFromException(InvalidFormatException ex) {
        return ex.getPath().stream()
                .map(Reference::getFieldName)
                .reduce((first, second) -> first + "." + second)
                .orElse("");
    }

    // Métodos relacionados a exceções de tipo de parâmetro na URL

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                        HttpStatus status, WebRequest request) {
        if (ex instanceof MethodArgumentTypeMismatchException) {
            return handleMethodArgumentTypeMismatch(
                    (MethodArgumentTypeMismatchException) ex, headers, status, request
            );
        }

        return super.handleTypeMismatch(ex, headers, status, request);
    }

    private ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                    HttpHeaders headers, HttpStatus status,
                                                                    WebRequest request) {
        String detail = String.format(
                "O parâmetro '%s' na URL foi atribuído com o valor '%s', que não é válido "
                        + "para o tipo esperado (%s). Por favor, corrija e forneça um valor compatível.",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()
        );

        ApiExceptionResponse apiExceptionResponse = createApiExceptionResponseBuilder(
                status, "Tipo de Parâmetro Inválido na URL", detail
        )
        .build();

        return handleExceptionInternal(ex, apiExceptionResponse, headers, status, request);
    }

    // Métodos relacionados a exceções de recurso não encontrado

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatus status, WebRequest request) {
        String detail = String.format(
                "O recurso '%s' que você tentou acessar, é inexistente.",
                ex.getRequestURL()
        );

        ApiExceptionResponse apiExceptionResponse = createApiExceptionResponseBuilder(
                status, "Recurso não Encontrado", detail
        )
        .build();

        return handleExceptionInternal(ex, apiExceptionResponse, headers, status, request);
    }

    // Métodos relacionados a exceções específicas da aplicação

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<Object> handleEntidadeNaoEncontradaException(EntidadeNaoEncontradaException ex,
                                                                       WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String detail = ex.getMessage();

        ApiExceptionResponse apiExceptionResponse = createApiExceptionResponseBuilder(
                status, "Entidade não encontrada", detail
        )
        .userMessage(detail)
        .build();

        return handleExceptionInternal(ex, apiExceptionResponse, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(EntidadeEmUsoException.class)
    public ResponseEntity<Object> handleEntidadeEmUsoException(EntidadeEmUsoException ex,
                                                               WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String detail = ex.getMessage();

        ApiExceptionResponse apiExceptionResponse = createApiExceptionResponseBuilder(
                status, "Entidade em uso", detail
        )
        .userMessage(detail)
        .build();

        return handleExceptionInternal(ex, apiExceptionResponse, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Object> handleRegraNegocioException(
            RegraNegocioException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String detail = ex.getMessage();

        ApiExceptionResponse apiExceptionResponse = createApiExceptionResponseBuilder(
                status, "Violação de regra de negócio", detail
        )
        .userMessage(detail)
        .build();

        return handleExceptionInternal(ex, apiExceptionResponse, new HttpHeaders(), status, request);
    }

    // Método padrão para lidar com exceções internas

    private ResponseEntity<Object> handleExceptionInternal(Exception ex, ApiExceptionResponse body,
                                                           HttpHeaders headers, HttpStatus status,
                                                           WebRequest request) {
        if (body == null) {
            body = createApiExceptionResponseBuilder(
                    status, status.getReasonPhrase(), "Detalhe não disponível"
            ).build();
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    // Método auxiliar para criar uma resposta padronizada de exceção

    private ApiExceptionResponse.ApiExceptionResponseBuilder createApiExceptionResponseBuilder(HttpStatus status,
                                                                                               String title,
                                                                                               String detail) {
        return ApiExceptionResponse.builder()
                .status(status.value())
                .title(title)
                .detail(detail);
    }

}
