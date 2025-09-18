package com.example.demo.domain;

import com.example.demo.domain.enums.Role;
import jakarta.persistence.Column;

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
