package com.example.demo.domain;

import com.example.demo.infra.dtos.transacao.TransacaoDTOResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "transacoes")
public class TransacaoDocument {
    @Id
    private String id;

    private Long idPayer;
    private Long idPayee;
    private BigDecimal valor;
    private TransacaoDTOResponse status;
    private LocalDate timestamp;
    private boolean autorizado;
    private boolean notificacaoEnviada;

    public TransacaoDocument(Long idPayer,
                             Long idPayee, BigDecimal valor,
                             TransacaoDTOResponse status,
                             LocalDate timestamp,
                             boolean autorizado,
                             boolean notificacaoEnviada)
    {
        this.idPayer = idPayer;
        this.idPayee = idPayee;
        this.valor = valor;
        this.status = status;
        this.timestamp = timestamp;
        this.autorizado = autorizado;
        this.notificacaoEnviada = notificacaoEnviada;
    }

    public String getId() {
        return id;
    }

    public Long getIdPayer() {
        return idPayer;
    }

    public Long getIdPayee() {
        return idPayee;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public TransacaoDTOResponse getStatus() {
        return status;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public boolean isAutorizado() {
        return autorizado;
    }

    public boolean isNotificacaoEnviada() {
        return notificacaoEnviada;
    }
}
