package edu.altair.ut5crm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de Recursos No Encontrados (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail manejarNoEncontrado(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problem.setTitle("Recurso No Encontrado");
        problem.setType(URI.create("https://api.crm.altair.edu/errors/not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // Manejo de Conflictos por Claves Candidatas Duplicadas (409)
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail manejarDuplicado(DuplicateResourceException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problem.setTitle("Conflicto de Datos Duplicados");
        problem.setType(URI.create("https://api.crm.altair.edu/errors/conflict"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}