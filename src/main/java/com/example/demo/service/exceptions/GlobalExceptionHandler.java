package com.example.demo.service.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler (UsuarioInvalidoException.class)
    public ResponseEntity<String> handleUsuarioRepetido
            (UsuarioInvalidoException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(LojistaNaoPodeEnviarDinheiroException.class)
    public ResponseEntity<String> handleLojistaNaoPodeEnviarDinheiro
            (LojistaNaoPodeEnviarDinheiroException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TransacaoNaoAutorizadaException.class)
    public ResponseEntity<String> handleTransacaoNaoAutorizadaException
            (TransacaoNaoAutorizadaException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<String> handleSaldoInsuficiente
            (SaldoInsuficienteException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<String> handleUsuarioNaoEncontradoException
            (UsuarioNaoEncontradoException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TransacaoComValorNegativoException.class)
    public ResponseEntity<String> handleTransacaoComValorNegativo
            (SaldoInsuficienteException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
