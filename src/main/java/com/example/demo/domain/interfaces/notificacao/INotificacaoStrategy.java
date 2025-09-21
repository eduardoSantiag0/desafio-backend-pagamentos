package com.example.demo.domain.interfaces.notificacao;

import java.math.BigDecimal;

public interface INotificacaoStrategy {
    void criarNotificacao(String contact, BigDecimal value);
}
