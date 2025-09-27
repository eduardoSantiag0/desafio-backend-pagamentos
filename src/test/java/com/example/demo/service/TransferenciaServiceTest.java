package com.example.demo.service;

import com.example.demo.domain.Lojista;
import com.example.demo.domain.TransacaoDocument;
import com.example.demo.domain.UserEntity;
import com.example.demo.domain.UsuarioComum;
import com.example.demo.infra.dtos.transacao.TransacaoDTOResponse;
import com.example.demo.domain.enums.StatusTransacao;
import com.example.demo.infra.dtos.transacao.TransacaoDTORequest;
import com.example.demo.infra.mensageria.PagamentoProducer;
import com.example.demo.infra.repositorios.TransacaoRepository;
import com.example.demo.infra.repositorios.UsuarioRepository;
import com.example.demo.service.exceptions.LojistaNaoPodeEnviarDinheiroException;
import com.example.demo.service.exceptions.SaldoInsuficienteException;
import com.example.demo.service.exceptions.TransacaoComValorNegativoException;
import com.example.demo.service.exceptions.TransacaoNaoAutorizadaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferenciaServiceTest {

    @Mock
    private AutorizadorService autorizadorService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private RegistrosDeTransacoesService registrosDeTransacoesService;

    @Mock
    private NotificadorService notificadorService;

    @Mock
    private PagamentoProducer pagamentoProducer;

    @InjectMocks
    private TransferenciaService transferenciaService;

    private UserEntity usuario1;

    private UserEntity usuario2;

    private BigDecimal valorInicialDoSaldo;

    @BeforeEach
    void setUp() {
        valorInicialDoSaldo = new BigDecimal("100");

        usuario1 = new UsuarioComum("joao", "123456",
                "joao@email.com", "senha123", valorInicialDoSaldo);

        usuario2 = new UsuarioComum("joao", "123456",
                "joao@email.com", "senha123", valorInicialDoSaldo);

    }

    @Test
    void lojistaFazendoTransferencia_NaoDevePermitir() {

        //* Arrange
        UserEntity lojista = new Lojista("lojista", "222222",
                "lojista@email.com", "lojista123", valorInicialDoSaldo);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(lojista));
        TransacaoDTORequest transacaoDTORequest = new TransacaoDTORequest
                (new BigDecimal(100),3L, 1L);


        TransacaoDTOResponse expected = new TransacaoDTOResponse(StatusTransacao.FALHA,
                "Lojistas não podem enviar dinheiro");

        //* Act

        assertThrows(LojistaNaoPodeEnviarDinheiroException.class,
                () -> transferenciaService.fazerTransacao(transacaoDTORequest));

        //* Assert
        assertEquals(lojista.getSaldo(), valorInicialDoSaldo);
        verify(usuarioRepository, never()).save(any());

    }

    @Test
    void servicoIndisponivel_DeveReverterATransacao() {

        //* Arrange

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario2));

        when(autorizadorService.verificarAutorizacao()).thenReturn(false);

        TransacaoDTORequest transacaoDTORequest = new TransacaoDTORequest
                (new BigDecimal(100),1L, 2L);

        //* Act
        assertThrows(TransacaoNaoAutorizadaException.class,
                () -> transferenciaService.fazerTransacao(transacaoDTORequest));


        assertEquals(usuario1.getSaldo(), valorInicialDoSaldo);
        assertEquals(usuario2.getSaldo(), valorInicialDoSaldo);

        verify(usuarioRepository, never()).save(any());

    }

    @Test
    void servicoIndisponivel_DeveInformarQueATransacaoNaoFoiFeita() {

        //* Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario2));
        when(autorizadorService.verificarAutorizacao()).thenReturn(false);

        TransacaoDTORequest transacaoDTORequest = new TransacaoDTORequest
                (new BigDecimal(100),1L, 2L);

        //* Act
        //* Assert
        assertThrows(TransacaoNaoAutorizadaException.class,
                () -> transferenciaService.fazerTransacao(transacaoDTORequest));

        verify(usuarioRepository, never()).save(any());
    }



    @Test
    void quandoSalvarTransacao_DeveChamarRepositorioExatamenteDuasVezes() {

        //* Arrange
        BigDecimal valorTestado = new BigDecimal("10");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario2));
        when(autorizadorService.verificarAutorizacao()).thenReturn(true);

        TransacaoDTORequest transacaoDTORequest = new TransacaoDTORequest
                (valorTestado,1L, 2L);
        when(notificadorService.notificarUsuario(usuario1, usuario2, transacaoDTORequest)).thenReturn(true);

        //* Act
        transferenciaService.fazerTransacao(transacaoDTORequest);

        //* Assert
        verify(usuarioRepository, times(2)).save((any(UserEntity.class)));
        assertEquals(usuario1.getSaldo(),valorInicialDoSaldo.subtract(valorTestado));
        assertEquals(usuario2.getSaldo(),valorInicialDoSaldo.add(valorTestado));


    }

    @Test
    void quandoSaldoInsuficiente_DeveRetornarExcecao() {
        BigDecimal valorTestado = new BigDecimal("1000");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario2));
        TransacaoDTORequest transacaoDTORequest = new TransacaoDTORequest
                (valorTestado,1L, 2L);

        assertThrows(SaldoInsuficienteException.class,
                () -> transferenciaService.fazerTransacao(transacaoDTORequest));
    }

    @Test
    void quandoValorNegativo_DeveRetornarExcecao() {
        //* Arrange
        BigDecimal valorTestado = new BigDecimal("-1000");
        TransacaoDTORequest transacaoDTORequest = new TransacaoDTORequest
                (valorTestado,1L, 2L);

        //* Act and Assert
        assertThrows(TransacaoComValorNegativoException.class,
                () -> transferenciaService.fazerTransacao(transacaoDTORequest));
    }

    @Test
    void quandoNotificacaoNaoEhEnviada_TransacaoEhFeita() {
        BigDecimal valorTestado = new BigDecimal("10");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario2));
        TransacaoDTORequest transacaoDTORequest = new TransacaoDTORequest
                (valorTestado,1L, 2L);

        when(autorizadorService.verificarAutorizacao()).thenReturn(true);
        when(notificadorService.notificarUsuario(usuario1, usuario2, transacaoDTORequest)).thenReturn(false);


        TransacaoDTOResponse expectedResponse = new TransacaoDTOResponse(StatusTransacao.COMPLETA,
                "Transação  sucedida, porem sem sucesso em notificar os envolvidos");


        TransacaoDTOResponse result = transferenciaService.fazerTransacao(transacaoDTORequest);
        assertEquals(expectedResponse, result);

        BigDecimal saldoEsperadoUsuario1 = valorInicialDoSaldo.subtract(valorTestado);
        BigDecimal saldoEsperadoUsuario2 = valorInicialDoSaldo.add(valorTestado);
        assertEquals(saldoEsperadoUsuario1, usuario1.getSaldo());
        assertEquals(saldoEsperadoUsuario2, usuario2.getSaldo());

    }



}