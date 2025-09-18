package com.example.demo.infra.dtos;

import java.math.BigDecimal;

public record TransacaoDTO (
        BigDecimal value,
        Long payer,
        Long payee
) {
}
