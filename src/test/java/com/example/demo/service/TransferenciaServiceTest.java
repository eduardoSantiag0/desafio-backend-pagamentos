package com.example.demo.service;

import com.example.demo.domain.Lojista;
import com.example.demo.domain.UserEntity;
import com.example.demo.domain.UsuarioComum;
import com.example.demo.infra.dtos.transacao.TransacaoDTOResponse;
import com.example.demo.domain.enums.StatusTransacao;
import com.example.demo.infra.dtos.transacao.TransacaoDTORequest;
import com.example.demo.infra.repositorios.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferenciaServiceTest {

    @Mock
    private AutorizadorService autorizadorService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private TransferenciaService transferenciaService;

    private UserEntity usuario1;

    private UserEntity usuario2;

    private BigDecimal valorInicialDoSaldo;

    @BeforeEach
    void setUp() {
        usuario1 = new UsuarioComum("joao", "123456",
                "joao@email.com", "senha123", valorInicialDoSaldo);

        usuario2 = new UsuarioComum("joao", "123456",
                "joao@email.com", "senha123", valorInicialDoSaldo);

        valorInicialDoSaldo = new BigDecimal("100");
    }

    @Test
    void naoDevePermitirLojistaFazerTransferencia() {

        //* Arrange
        UserEntity lojista = new Lojista("lojista", "222222",
                "lojista@email.com", "lojista123", valorInicialDoSaldo);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(lojista));
        TransacaoDTORequest transacaoDTORequest = new TransacaoDTORequest
                (new BigDecimal(100),3L, 1L);


        TransacaoDTOResponse expected =new TransacaoDTOResponse(StatusTransacao.FALHA,
                "Lojistas não podem enviar dinheiro");

        //* Act
        TransacaoDTOResponse result = transferenciaService.fazerTransacao(transacaoDTORequest);


        //* Assert
        assertEquals(lojista.getSaldo(), valorInicialDoSaldo);
        assertEquals(result.mensagem(), expected.mensagem());
        assertEquals(result.statusTransacao(), expected.statusTransacao());
    }

    @Test
    void deveReverterATransacaoSeServicoEstiverIndisponivel() {

        //* Arrange

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario2));

        when(autorizadorService.verificarAutorizacao()).thenReturn(false);

        TransacaoDTORequest transacaoDTORequest = new TransacaoDTORequest
                (new BigDecimal(100),1L, 2L);

        //* Act
        transferenciaService.fazerTransacao(transacaoDTORequest);

        assertEquals(usuario1.getSaldo(), valorInicialDoSaldo);
        assertEquals(usuario2.getSaldo(), valorInicialDoSaldo);

    }

    @Test
    void deveInformarQueATransacaoNaoFoiFeitaCasoServicoEstejaIndisponivel() {

        //* Arrange
        UserEntity usuario1 = new UsuarioComum("joao", "123456",
                "joao@email.com", "senha123", new BigDecimal("1000"));

        UserEntity usuario2 = new UsuarioComum("joao", "123456",
                "joao@email.com", "senha123", new BigDecimal("1000"));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario2));

        when(autorizadorService.verificarAutorizacao()).thenReturn(false);

        TransacaoDTORequest transacaoDTORequest = new TransacaoDTORequest
                (new BigDecimal(100),1L, 2L);

        //* Act
        TransacaoDTOResponse result = transferenciaService.fazerTransacao(transacaoDTORequest);

        TransacaoDTOResponse expected = new TransacaoDTOResponse(StatusTransacao.EM_ESPERA,
                "Transação não autorizada");


        //* Assert
        assertEquals(result.mensagem(), expected.mensagem());
        assertEquals(result.statusTransacao(), expected.statusTransacao());

    }
}