package com.cursosrecomendados.telegram.exception;

import java.io.IOException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler({ CoinNotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "Book not found", 
          new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    
    @ExceptionHandler({ IOException.class, InterruptedException.class })
    protected ResponseEntity<Object> handleSendErrors(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "Course could not be sent", 
          new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({ CoinIdMismatchException.class, 
      ConstraintViolationException.class, 
      DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleBadRequest(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getLocalizedMessage(), 
          new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
