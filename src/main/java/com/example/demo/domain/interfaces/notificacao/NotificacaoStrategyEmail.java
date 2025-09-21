package com.example.demo.domain.interfaces.notificacao;

import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class NotificacaoStrategyEmail implements INotificacaoStrategy {

    @Autowired
    private EmailService emailService;

    private String SUBJECT = "Pagamento Realizado!";

    @Override
    public void criarNotificacao(String enderecoEmail, BigDecimal value) {
        String body = "Transferência no valor de " + value.toString() + " foi concluida.";
        emailService.enviarEmail(enderecoEmail, SUBJECT, body);
    }
}
