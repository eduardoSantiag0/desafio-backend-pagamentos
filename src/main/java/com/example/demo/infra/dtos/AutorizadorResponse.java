package com.example.demo.infra.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;

public class AutorizadorResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private Data data;

    public boolean isAuthorized() {
        return data != null && data.getAuthorization();
    }

    public static class Data {
        @JsonProperty("authorization")
        private boolean authorization;

        public boolean getAuthorization() {
            return authorization;
        }
    }

}
