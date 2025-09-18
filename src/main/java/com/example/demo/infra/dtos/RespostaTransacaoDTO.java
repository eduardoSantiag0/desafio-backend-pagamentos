package com.example.demo.infra.dtos;

public record RespostaTransacaoDTO (
        StatusTransacao statusTransacao,
        String mensagem
)
{
}
