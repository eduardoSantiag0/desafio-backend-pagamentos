package com.example.demo.infra.mensageria;

import com.example.demo.domain.TransacaoDocument;
import com.example.demo.domain.UserEntity;
import com.example.demo.domain.UsuarioComum;
import com.example.demo.domain.enums.StatusTransacao;
import com.example.demo.infra.dtos.RegistroTransacaoDTO;
import com.example.demo.infra.dtos.transacao.TransacaoDTORequest;
import com.example.demo.infra.dtos.transacao.TransacaoDTOResponse;
import com.example.demo.infra.repositorios.TransacaoRepository;
import com.example.demo.infra.repositorios.UsuarioRepository;
import com.example.demo.service.NotificadorService;
import com.example.demo.service.RegistrosDeTransacoesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoListenerTest {
    @InjectMocks
    private NotificacaoListener listener;

    @Mock
    private NotificadorService notificadorService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private RegistrosDeTransacoesService registrosDeTransacoesService;

    @Mock
    private PagamentoProducer pagamentoProducer;

    private UserEntity payer;
    private UserEntity payee;

    @BeforeEach
    void setUp() {

        BigDecimal valorInicialDoSaldo = new BigDecimal("200");

        payer = new UsuarioComum("joao", "123456",
                "joao@email.com", "senha123", valorInicialDoSaldo);

        payee = new UsuarioComum("joao", "123456",
                "joao@email.com", "senha123", valorInicialDoSaldo);


        payer = mock(UsuarioComum.class);
        payee = mock(UsuarioComum.class);
    }

    @Test
    void quandoNotificacaoEnviada_DeveSalvarDocumentoENaoPublicarNaFila() {
        RegistroTransacaoDTO dtoRegistro = new RegistroTransacaoDTO(
                1L, 2L, new BigDecimal("100"), null, LocalDate.now(), true, true
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(payer));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(payee));
        when(payer.getId()).thenReturn(1L);
        when(payee.getId()).thenReturn(2L);

        TransacaoDTORequest dtoTransacao = new TransacaoDTORequest(
                dtoRegistro.valor(),
                payer.getId(),
                payee.getId()
        );

        when(notificadorService.notificarUsuario(payer, payee, dtoTransacao)).thenReturn(true);

        listener.listen(dtoRegistro);

        TransacaoDocument transacaoDocument = new TransacaoDocument(
                            dtoRegistro.idPayer(), dtoRegistro.idPayee(),
                            dtoRegistro.valor(), new TransacaoDTOResponse(StatusTransacao.COMPLETA, "Transação  sucedida, porem sem sucesso em notificar os envolvidos"),
                            LocalDate.now(), true, true);

//        verify(registrosDeTransacoesService, times(1)).salvarDocumento(transacaoDocument);

        ArgumentCaptor<TransacaoDocument> captor = ArgumentCaptor.forClass(TransacaoDocument.class);
        verify(registrosDeTransacoesService, times(1)).salvarDocumento(captor.capture());

        verify(pagamentoProducer, never()).publicarFilaDeNotificacoes(any());
    }

    @Test
    void quandoNotificacaoFalhar_DevePublicarNaFilaENaoSalvarDocumento() {
        RegistroTransacaoDTO dtoRegistro = new RegistroTransacaoDTO(
                1L, 2L, new BigDecimal("100"), null, LocalDate.now(), true, false
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(payer));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(payee));
        when(payer.getId()).thenReturn(1L);
        when(payee.getId()).thenReturn(2L);

        TransacaoDTORequest dtoTransacao = new TransacaoDTORequest(dtoRegistro.valor(), payer.getId(), payee.getId());

        when(notificadorService.notificarUsuario(payer, payee, dtoTransacao)).thenReturn(false);

        listener.listen(dtoRegistro);

        verify(registrosDeTransacoesService, never()).salvarDocumento(any());
        verify(pagamentoProducer, times(1)).publicarFilaDeNotificacoes(any());
    }

    @Test
    void quandoUsuarioNaoEncontrado_RetornarExcecaoENaoSalvarNada() {
        RegistroTransacaoDTO dto = new RegistroTransacaoDTO(
                1L, 2L, new BigDecimal("100"), null, LocalDate.now(), true, false
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        listener.listen(dto);

        verify(registrosDeTransacoesService, never()).salvarDocumento(any());
        verify(pagamentoProducer, never()).publicarFilaDeNotificacoes(any());

    }
}
