package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AMQPConfig {

    // Exchange: transacoes.direct
    // Filas: notificacoes-nao-enviadas
    // Filas: pagamentos-para-processar

    private final String NOME_EXCHANGE = "transacoes.direct";
    private final String NOME_FILA_NOTIFICACAO = "notificacoes-nao-enviadas";
    private final String NOME_FILA_PAGAMENTOS = "pagamentos-para-processar";

    private final String ROUTING_KEY_NOTIFICACAO = "notificacao";
    private final String ROUTING_KEY_PAGAMENTOS = "pagamentos";

    @Bean
    public RabbitAdmin createRabbitAdmin(ConnectionFactory conn) {
        return new RabbitAdmin(conn);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> initializeAdmin(RabbitAdmin rabbitAdmin){
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(NOME_EXCHANGE);
    }

    @Bean
    public Queue filaPagamentos() {
        return new Queue(NOME_FILA_PAGAMENTOS);
    }

    @Bean
    public Queue filaNotificacao() {
        return new Queue(NOME_FILA_NOTIFICACAO);
    }

    @Bean
    public Binding bindingPagamentos(@Qualifier("filaPagamentos") Queue filaPagamentos, DirectExchange directExchange) {
        return BindingBuilder.bind(filaPagamentos)
                .to(directExchange)
                .with(ROUTING_KEY_PAGAMENTOS);
    }

    @Bean
    public Binding bindingNotificacao(@Qualifier("filaNotificacao") Queue filaNotificacao, DirectExchange directExchange) {
        return BindingBuilder.bind(filaNotificacao)
                .to(directExchange)
                .with(ROUTING_KEY_NOTIFICACAO);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  //* LocalDateTime
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter); // ✅ Aqui
        return rabbitTemplate;
    }

}
