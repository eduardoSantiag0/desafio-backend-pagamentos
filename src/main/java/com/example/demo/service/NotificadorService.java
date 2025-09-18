package com.example.demo.service;

import com.example.demo.domain.UserEntity;
import com.example.demo.infra.dtos.NotificadorResponse;
import com.example.demo.infra.dtos.TransacaoDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificadorService {

    private static final String URL = "https://util.devi.tools/api/v1/notify";

    public boolean notificarUsuario (UserEntity payer, UserEntity payee, TransacaoDTO transacaoDTO) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<NotificadorResponse> responseEntity = restTemplate
                    .postForEntity(URL, transacaoDTO, NotificadorResponse.class);

//            HttpStatusCode response = responseEntity.getStatusCode();
//
//            if (response.is5xxServerError()) {
//                return false;
//            }

            payer.enviarNotificacao();
            payee.enviarNotificacao();
            return true;

        } catch (HttpServerErrorException e) {
            System.out.println("Falha na requisição " + e.getMessage());
            return false;
        }


    }
}
