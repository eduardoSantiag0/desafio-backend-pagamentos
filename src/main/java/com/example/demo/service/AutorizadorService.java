package com.example.demo.service;

import com.example.demo.infra.dtos.AutorizadorResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AutorizadorService {

    private static final String URL =  "https://util.devi.tools/api/v2/authorize";

    public boolean verificarAutorizacao () {

        RestTemplate restTemplate = new RestTemplate();

        try {
            AutorizadorResponse response = restTemplate.getForObject(URL, AutorizadorResponse.class);
            return response.isAuthorized();
        } catch (HttpClientErrorException.Forbidden e) {
            return false;
        }
    }


}
