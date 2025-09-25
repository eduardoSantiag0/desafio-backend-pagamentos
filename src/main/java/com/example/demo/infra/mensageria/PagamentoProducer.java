package com.example.demo.infra.mensageria;

import com.example.demo.infra.dtos.RegistroTransacaoDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PagamentoProducer {

    private final String NOME_EXCHANGE = "transacoes.direct";
    private final String ROUTING_KEY_PAGAMENTOS = "pagamentos";
    private final String ROUTING_KEY_NOTIFICACAO = "notificacao";

    private final RabbitTemplate rabbitTemplate;

    public PagamentoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarFilaDePagamentos(RegistroTransacaoDTO dto) {
        rabbitTemplate.convertAndSend(NOME_EXCHANGE, ROUTING_KEY_PAGAMENTOS, dto);
    }

    public void publicarFilaDeNotificacoes(RegistroTransacaoDTO dto) {
        rabbitTemplate.convertAndSend(NOME_EXCHANGE, ROUTING_KEY_NOTIFICACAO, dto);
    }

}
