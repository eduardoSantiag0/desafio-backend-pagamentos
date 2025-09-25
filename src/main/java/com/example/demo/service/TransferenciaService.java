package com.example.demo.service;

import com.example.demo.domain.TransacaoDocument;
import com.example.demo.domain.UserEntity;
import com.example.demo.domain.enums.Role;
import com.example.demo.domain.enums.StatusTransacao;
import com.example.demo.infra.dtos.RegistroTransacaoDTO;
import com.example.demo.infra.dtos.transacao.TransacaoDTORequest;
import com.example.demo.infra.dtos.transacao.TransacaoDTOResponse;
import com.example.demo.infra.mensageria.PagamentoProducer;
import com.example.demo.infra.repositorios.TransacaoRepository;
import com.example.demo.infra.repositorios.UsuarioRepository;
import com.example.demo.service.exceptions.LojistaNaoPodeEnviarDinheiroException;
import com.example.demo.service.exceptions.SaldoInsuficienteException;
import com.example.demo.service.exceptions.TransacaoNaoAutorizadaException;
import com.example.demo.service.exceptions.UsuarioNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class TransferenciaService {

    private final UsuarioRepository usuarioRepository;

    private final AutorizadorService autorizadorService;

    private final NotificadorService notificadorService;

    private final RegistrosDeTransacoesService registrosDeTransacoesService;

    private final PagamentoProducer pagamentoProducer;

    public TransferenciaService(UsuarioRepository usuarioRepository,
                                AutorizadorService autorizadorService,
                                NotificadorService notificadorService,
                                RegistrosDeTransacoesService registrosDeTransacoesService,
                                PagamentoProducer pagamentoProducer
    ) {
        this.usuarioRepository = usuarioRepository;
        this.autorizadorService = autorizadorService;
        this.notificadorService = notificadorService;
        this.registrosDeTransacoesService = registrosDeTransacoesService;
        this.pagamentoProducer = pagamentoProducer;
    }

    private void validarUsuario (UserEntity payer, UserEntity payee, TransacaoDTORequest dto) {
        // Verificar se o payer eh LOJISTA
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

            TransacaoDTOResponse response = new TransacaoDTOResponse(StatusTransacao.EM_ESPERA,"Transação não autorizada");

            salvarResultadoDaTransacao(dto,false, false,
                    response);

            pagamentoProducer.publicarFilaDePagamentos(new RegistroTransacaoDTO(
                    dto.payer(), dto.payee(), dto.value(),
                    response, LocalDate.now(), true,false));


            throw new TransacaoNaoAutorizadaException("Transação não autorizada.");
        }

    }

    private boolean enviarNotificacao (UserEntity payer, UserEntity payee, TransacaoDTORequest dto) {
        return notificadorService.notificarUsuario(payer, payee, dto);
    }

    private void salvarResultadoDaTransacao(TransacaoDTORequest dto, boolean autorizado, boolean notificacaoEnviada,
                                            TransacaoDTOResponse respostaTransacaoDTO) {
        TransacaoDocument documento = new TransacaoDocument(
                dto.payer(), dto.payee(),
                dto.value(), respostaTransacaoDTO,
                LocalDate.now(), autorizado, notificacaoEnviada);

        registrosDeTransacoesService.salvarDocumento(documento);

    }


    @Transactional
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
                    "Transação  sucedida, porem sem sucesso em notificar os envolvidos");

            salvarResultadoDaTransacao(dto,true, false, response);

            pagamentoProducer.publicarFilaDeNotificacoes(new RegistroTransacaoDTO(
                    dto.payer(), dto.payee(), dto.value(), response,
                    LocalDate.now(), true,false));

            return response;
        }


    }
}
