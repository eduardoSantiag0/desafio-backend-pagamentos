package com.example.demo.infra.dtos.transacao;

import java.math.BigDecimal;

public record TransacaoDTORequest(
        BigDecimal value,
        Long payer,
        Long payee
) {
}
