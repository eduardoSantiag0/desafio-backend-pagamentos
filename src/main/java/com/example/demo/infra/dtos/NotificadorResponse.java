package com.example.demo.infra.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificadorResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
