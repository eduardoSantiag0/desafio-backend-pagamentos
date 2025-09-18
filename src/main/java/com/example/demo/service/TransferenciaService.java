package com.example.demo.service;

import com.example.demo.domain.UserEntity;
import com.example.demo.domain.enums.Role;
import com.example.demo.infra.dtos.RespostaTransacaoDTO;
import com.example.demo.infra.dtos.StatusTransacao;
import com.example.demo.infra.repositorios.UsuarioRepository;
import com.example.demo.infra.dtos.TransacaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransferenciaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AutorizadorService autorizadorService;

    @Autowired
    private NotificadorService notificadorService;

    public RespostaTransacaoDTO fazerTransacao(TransacaoDTO dto) {
        Optional<UserEntity> payerOptional = usuarioRepository.findById(dto.payer());
        Optional<UserEntity> payeeOptional = usuarioRepository.findById(dto.payee());

        if (payerOptional.isEmpty()  || payeeOptional.isEmpty()) {
            return null;
        }

        UserEntity payer = payerOptional.get();
        UserEntity payee = payeeOptional.get();

        // Verificar se o payer é LOJISTA
        if (payer.getDType().equals(Role.LOJISTA)) {
            return new RespostaTransacaoDTO(StatusTransacao.FALHA,
                    "Lojistas não podem enviar dinheiro");
        }


        // Verificar serviço autorizador
        if (!autorizadorService.verificarAutorizacao()) {
            return new RespostaTransacaoDTO(StatusTransacao.EM_ESPERA,
                    "Transação não autorizada");
        }

        // Debitar da Conta
        if (!payer.enviarDinheiro(payee, dto.value())) {
            return new RespostaTransacaoDTO(StatusTransacao.FALHA,
                    "Saldo insificiennte");
        }

        usuarioRepository.save(payer);
        usuarioRepository.save(payee);

        if (!notificadorService.notificarUsuario(payer, payee, dto)) {
            return new RespostaTransacaoDTO(StatusTransacao.COMPLETA,
                    "Transaçao sucedida, porém sem sucesso de notificar os envolvidos");

        }

        return new RespostaTransacaoDTO(StatusTransacao.COMPLETA,
                "Transaçao sucedida");


    }
}
