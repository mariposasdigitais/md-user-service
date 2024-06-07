package mariposas.exception;

import io.micronaut.http.HttpStatus;
import io.micronaut.logging.LogLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class BaseException extends RuntimeException {
    public static final String PROBLEM_JSON = "application/problem+json";
    private final HttpStatus httpStatus;
    private final LogLevel logLevel;
    private final String mensagem;

    public BaseException(HttpStatus httpStatus, String mensagem) {
        super(mensagem);
        this.httpStatus = httpStatus;
        this.logLevel = LogLevel.ERROR;
        this.mensagem = mensagem;
    }
}