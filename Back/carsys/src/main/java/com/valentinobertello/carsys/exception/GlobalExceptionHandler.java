package com.valentinobertello.carsys.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  Marca esta clase como un manejador global de excepciones para todos los controladores
 * **/
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones de validación (cuando falla @Valid en un request)
     * **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String additionalInfo = getStackTraceAsString(ex);
        BindingResult result = ex.getBindingResult();
        List<String> errors = result.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        String errorMessage = "Errors: " + String.join(", ", errors);
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorMessage, additionalInfo);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja excepciones cuando se lanza un IllegalArgumentException (por ejemplo, argumentos inválidos manuales)
     * **/
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        String additionalInfo = getStackTraceAsString(ex);
        String mensaje = ex.getMessage();
        String errorMessage = "Errors: " + mensaje;
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorMessage, additionalInfo);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Captura cualquier otra excepción no controlada previamente
     **/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        String additionalInfo = getStackTraceAsString(ex);
        ErrorResponse error = new ErrorResponse
                (HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An unexpected error occurred", additionalInfo);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getStackTraceAsString(Throwable ex) {
        StringBuilder traceBuilder = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            traceBuilder.append(element.toString()).append("\r\n");
        }
        return traceBuilder.toString();
    }
}