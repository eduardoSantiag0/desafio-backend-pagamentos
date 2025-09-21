package com.example.demo.service;

import com.example.demo.domain.UserEntity;
import com.example.demo.domain.interfaces.notificacao.INotificacaoStrategy;
import com.example.demo.domain.interfaces.notificacao.NotificacaoStrategyEmail;
import com.example.demo.infra.dtos.NotificadorResponse;
import com.example.demo.infra.dtos.transacao.TransacaoDTORequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificadorService {

    private static final String URL = "https://util.devi.tools/api/v1/notify";

    private final INotificacaoStrategy notificacaoStrategy;

    public NotificadorService(NotificacaoStrategyEmail notificacaoStrategy) {
        this.notificacaoStrategy = notificacaoStrategy;
    }

    public boolean notificarUsuario (UserEntity payer, UserEntity payee,
                                     TransacaoDTORequest transacaoDTORequest)
    {
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<NotificadorResponse> responseEntity = restTemplate
                    .postForEntity(URL, transacaoDTORequest, NotificadorResponse.class);

            payer.notificar(notificacaoStrategy, transacaoDTORequest.value());
            payee.notificar(notificacaoStrategy, transacaoDTORequest.value());

            return true;

        } catch (HttpServerErrorException e) {
            System.out.println("Falha na requisição " + e.getMessage());
            return false;
        }

    }
}
