package com.example.demo.service.exceptions;

public class TransacaoComValorNegativoException extends RuntimeException{
    public TransacaoComValorNegativoException(String message) {
        super(message);
    }
}
