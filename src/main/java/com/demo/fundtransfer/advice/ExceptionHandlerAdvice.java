package com.demo.fundtransfer.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
        List<Map<String, String>> errorMap = e.getBindingResult().getFieldErrors().stream().map(error -> Map.of(
                "field", error.getField(),
                "msg", error.getDefaultMessage()
        )).toList();
        return ResponseEntity.badRequest().body(Map.of("errors", errorMap));
    }

}
