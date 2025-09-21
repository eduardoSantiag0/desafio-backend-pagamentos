package com.example.demo.service;

import com.example.demo.domain.TransacaoDocument;
import com.example.demo.domain.UserEntity;
import com.example.demo.domain.enums.Role;
import com.example.demo.infra.dtos.transacao.TransacaoDTOResponse;
import com.example.demo.domain.enums.StatusTransacao;
import com.example.demo.infra.repositorios.TransacaoRepository;
import com.example.demo.infra.repositorios.UsuarioRepository;
import com.example.demo.infra.dtos.transacao.TransacaoDTORequest;
import com.example.demo.service.exceptions.LojistaNaoPodeEnviarDinheiroException;
import com.example.demo.service.exceptions.SaldoInsuficienteException;
import com.example.demo.service.exceptions.TransacaoNaoAutorizadaException;
import com.example.demo.service.exceptions.UsuarioNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class TransferenciaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AutorizadorService autorizadorService;

    @Autowired
    private NotificadorService notificadorService;

    @Autowired
    private TransacaoRepository transacaoRepository;

    private void validarUsuario (UserEntity payer, UserEntity payee, TransacaoDTORequest dto) {
        // Verificar se o payer é LOJISTA
        if (payer.getDType().equals(Role.LOJISTA)) {

            salvarResultadoDaTransacao(dto, false, false,
                    new TransacaoDTOResponse(StatusTransacao.FALHA,"Lojistas não podem enviar dinheiro"));


            throw new LojistaNaoPodeEnviarDinheiroException("Lojistas não podem enviar dinheiro");
        }

        // Verificar se tem dinheiro na conta
        if (payer.getSaldo().compareTo(dto.value()) < 0) {
            salvarResultadoDaTransacao(dto,false, false,
                    new TransacaoDTOResponse(StatusTransacao.FALHA, "Saldo insificiennte"));

            throw new SaldoInsuficienteException("Saldo insificiennte");
        }

    }

    private void verificarAutorizacao(TransacaoDTORequest dto) {

        if (!autorizadorService.verificarAutorizacao()) {

            salvarResultadoDaTransacao(dto,false, false,
                    new TransacaoDTOResponse(StatusTransacao.EM_ESPERA,"Transação não autorizada"));

            throw new TransacaoNaoAutorizadaException("Transação não autorizada.");
        }

    }

    private boolean enviarNotificacao (UserEntity payer, UserEntity payee, TransacaoDTORequest dto) {
        return notificadorService.notificarUsuario(payer, payee, dto);
    }

    private void salvarResultadoDaTransacao(TransacaoDTORequest dto, boolean autorizado, boolean notificacaoEnviada,
                                            TransacaoDTOResponse respostaTransacaoDTO)
    {
        TransacaoDocument documento = new TransacaoDocument(
                dto.payer(), dto.payee(),
                dto.value(), respostaTransacaoDTO,
                LocalDate.now(), autorizado, notificacaoEnviada);

        transacaoRepository.save(documento);
    }




    public TransacaoDTOResponse fazerTransacao(TransacaoDTORequest dto) {

        UserEntity payer = usuarioRepository.findById(dto.payer())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario nao encontrado"));
        UserEntity payee = usuarioRepository.findById(dto.payee())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario nao encontrado"));

        validarUsuario(payer, payee, dto);
        verificarAutorizacao(dto);

        payer.enviarDinheiro(payee, dto.value());

        usuarioRepository.save(payer);
        usuarioRepository.save(payee);


        if (enviarNotificacao(payer, payee, dto)) {
            TransacaoDTOResponse response =  new TransacaoDTOResponse(StatusTransacao.COMPLETA,
                    "Transação  sucedida");

            salvarResultadoDaTransacao(dto,true, true, response);

            return response;

        } else {
            TransacaoDTOResponse response = new TransacaoDTOResponse(StatusTransacao.COMPLETA,
                    "Transação  sucedida, porém sem sucesso de notificar os envolvidos");

            salvarResultadoDaTransacao(dto,true, false, response);
            return response;
        }


    }
}
