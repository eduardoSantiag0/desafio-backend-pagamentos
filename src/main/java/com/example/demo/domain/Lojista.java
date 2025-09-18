package com.example.demo.domain;

import com.example.demo.domain.enums.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("LOJISTA")
public class Lojista extends UserEntity {
    public Lojista(String nome_completo, String cpf, String email, String senha, BigDecimal saldo) {
        super(nome_completo, cpf, email, senha, saldo);
    }

    public Lojista() {
    }

    @Override
    public String enviarNotificacao() {
        return "";
    }
}
