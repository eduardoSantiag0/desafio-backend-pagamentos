package com.example.demo.infra.dtos;

import com.example.demo.domain.enums.Role;

import java.math.BigDecimal;

public record CriarUsuarioDTO(
        String nome_completo,
        String cpf,
        String email,
        String senha,
        BigDecimal saldo,
        Role role
) {
}
