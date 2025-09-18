package com.example.demo.domain;

import com.example.demo.domain.enums.Role;
import com.example.demo.domain.interfaces.INotificavel;
import com.example.demo.domain.interfaces.IPodeReceberDinheiro;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
public abstract class UserEntity implements IPodeReceberDinheiro, INotificavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome_completo;

    @Column(unique = true)
    private String cpf;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private BigDecimal saldo;

    public UserEntity(String nome_completo, String cpf, String email, String senha, BigDecimal saldo) {
        this.nome_completo = nome_completo;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.saldo = saldo;
    }

    public UserEntity() {
    }

    @Override
    public void receberDinheiro(BigDecimal valorRecebido) {
        this.setSaldo(this.getSaldo().add(valorRecebido));
    }

//    public void sacarDinheiro (BigDecimal valorSacado) {
//        this.setSaldo(this.saldo.subtract(valorSacado));
//    }

    public boolean enviarDinheiro (UserEntity payee, BigDecimal value) {

        // Verificar se tem dinheiro na conta
        if (this.getSaldo().compareTo(value) < 0) {
            return false;
        }

        payee.receberDinheiro(value);
        this.setSaldo(this.saldo.subtract(value));
        return true;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getNome_completo() {
        return nome_completo;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public Long getId() {
        return id;
    }

    public Role getDType() {
        return Role.valueOf(this.getClass().getAnnotation(DiscriminatorValue.class).value());
    }
}
