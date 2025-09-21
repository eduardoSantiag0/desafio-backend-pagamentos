package com.example.demo.domain;

import com.example.demo.domain.interfaces.IPodeEnviarDinheiro;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("USUARIO")
public class UsuarioComum extends UserEntity implements IPodeEnviarDinheiro {
    public UsuarioComum(String nome_completo, String cpf, String email, String senha, BigDecimal saldo) {
        super(nome_completo, cpf, email, senha, saldo);
    }

    public UsuarioComum() {
    }

    @Override
    public void enviarDinheiro(BigDecimal valorEnviado) {
        this.setSaldo(this.getSaldo().subtract(valorEnviado));
    }
}
