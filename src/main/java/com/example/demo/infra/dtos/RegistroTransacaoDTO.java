package com.example.demo.infra.dtos;

import com.example.demo.infra.dtos.transacao.TransacaoDTOResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroTransacaoDTO(
        Long idPayer,
        Long idPayee,
        BigDecimal valor,
        TransacaoDTOResponse status,
        LocalDate timestamp,
        boolean autorizado,
        boolean notificacaoEnviada
) {
}
