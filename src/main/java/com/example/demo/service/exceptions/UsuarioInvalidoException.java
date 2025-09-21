package com.example.demo.service.exceptions;

public class UsuarioInvalidoException extends RuntimeException{

    public UsuarioInvalidoException(String message) {
        super(message);
    }
}
