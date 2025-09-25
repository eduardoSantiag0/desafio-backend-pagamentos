package com.example.demo.infra.mensageria;

import com.example.demo.domain.TransacaoDocument;
import com.example.demo.domain.UserEntity;
import com.example.demo.domain.enums.StatusTransacao;
import com.example.demo.infra.dtos.RegistroTransacaoDTO;
import com.example.demo.infra.dtos.transacao.TransacaoDTORequest;
import com.example.demo.infra.dtos.transacao.TransacaoDTOResponse;
import com.example.demo.infra.repositorios.UsuarioRepository;
import com.example.demo.service.NotificadorService;
import com.example.demo.service.RegistrosDeTransacoesService;
import com.example.demo.service.exceptions.UsuarioNaoEncontradoException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Component
public class NotificacaoListener {

    @Autowired
    private NotificadorService notificadorService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RegistrosDeTransacoesService registrosDeTransacoesService;

    @Autowired
    private PagamentoProducer pagamentoProducer;

    @RabbitListener(queues = "notificacoes-nao-enviadas")
    public void listen (@Payload RegistroTransacaoDTO dto) {
        try {

            UserEntity payer = usuarioRepository.findById(dto.idPayer())
                    .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario nao encontrado"));
            UserEntity payee = usuarioRepository.findById(dto.idPayee())
                    .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario nao encontrado"));

            TransacaoDTORequest request = new TransacaoDTORequest(
                    dto.valor(),
                    dto.idPayer(),
                    dto.idPayee()
            );

            boolean sucesso = notificadorService.notificarUsuario(payer, payee, request);

            if (sucesso) {
                registrosDeTransacoesService.salvarDocumento(
                        new TransacaoDocument(
                                dto.idPayer(), dto.idPayee(), dto.valor(),
                                new TransacaoDTOResponse(StatusTransacao.COMPLETA,"Transação  sucedida"),
                                LocalDate.now(), true, true
                        )
                );
            } else {
                pagamentoProducer.publicarFilaDeNotificacoes(
                        new RegistroTransacaoDTO(
                                dto.idPayer(), dto.idPayee(),
                                dto.valor(), dto.status(),
                                LocalDate.now(), dto.autorizado(), dto.notificacaoEnviada()
                        ));
            }
        } catch (Exception e)  {
            System.err.println("Erro ao reprocessar pagamento: " + e.getMessage());

        }


    }

}
