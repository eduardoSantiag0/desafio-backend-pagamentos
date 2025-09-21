package com.example.demo.infra.dtos.transacao;

import com.example.demo.domain.enums.StatusTransacao;

public record TransacaoDTOResponse(
        StatusTransacao statusTransacao,
        String mensagem
)
{
}
