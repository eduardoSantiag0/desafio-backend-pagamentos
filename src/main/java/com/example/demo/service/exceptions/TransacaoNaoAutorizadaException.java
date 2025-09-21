package com.example.demo.service.exceptions;

public class TransacaoNaoAutorizadaException extends RuntimeException {
    public TransacaoNaoAutorizadaException(String message) {
        super(message);
    }
}
