package com.example.demo.service;

import com.example.demo.domain.TransacaoDocument;
import com.example.demo.domain.UserEntity;
import com.example.demo.domain.UsuarioComum;
import com.example.demo.domain.enums.StatusTransacao;
import com.example.demo.infra.dtos.RegistroTransacaoDTO;
import com.example.demo.infra.dtos.transacao.TransacaoDTOResponse;
import com.example.demo.infra.repositorios.TransacaoRepository;
import com.example.demo.infra.repositorios.UsuarioRepository;
import com.example.demo.service.exceptions.UsuarioNaoEncontradoException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrosDeTransacoesServiceTest {


    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RegistrosDeTransacoesService registrosDeTransacoesService;

    private UserEntity usuario1;
    private UserEntity usuario2;
    private final List<RegistroTransacaoDTO> transacoesDTO = new ArrayList<>();
    private final List<TransacaoDocument> transacoesDocument = new ArrayList<>();

    @BeforeEach
    void setUp() {

        usuario1 = mock(UsuarioComum.class);

        usuario2 = mock(UsuarioComum.class);

        TransacaoDTOResponse status1 = new TransacaoDTOResponse(StatusTransacao.COMPLETA, "Transação sucedida, porém sem sucesso de notificar os envolvidos");
        TransacaoDTOResponse status2 = new TransacaoDTOResponse(StatusTransacao.FALHA, "Transação não autorizada");

        transacoesDTO.add(new RegistroTransacaoDTO(
                1L,2L,
                BigDecimal.valueOf(10),
                status1,
                LocalDate.parse("2025-09-24"),
                true,
                false
        ));

        transacoesDTO.add(new RegistroTransacaoDTO(
                1L,2L,
                BigDecimal.valueOf(10),
                status2,
                LocalDate.parse("2025-09-24"),
                false,
                false
        ));

        transacoesDocument.add(new TransacaoDocument(
                1L, 2L,
                BigDecimal.valueOf(10),
                status1,
                LocalDate.parse("2025-09-24"),
                true,
                false

        ));

        transacoesDocument.add(new TransacaoDocument(
                1L, 2L,
                BigDecimal.valueOf(10),
                status2,
                LocalDate.parse("2025-09-24"),
                false,
                false
        ));


    }

    @Test
    void quandoTransacoesExistiremEUsuariosExistirem_DeveRetornarListaDeRegistroTransacaoDTO() {

        //* Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.ofNullable(usuario1));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.ofNullable(usuario2));
        when(transacaoRepository.findByIdPayerOrIdPayee(1L, 1L)).thenReturn(transacoesDocument);

        when(usuario1.getId()).thenReturn(1L);
        when(usuario2.getId()).thenReturn(2L);


        //* Act
        var result = registrosDeTransacoesService.procurarTransacoesId(1L);

        //* Assert
        assertEquals(transacoesDTO, result);

    }

    @Test
    void quandoNaoExistiremTransacoes_DeveRetornarListaVazia() {
        assertEquals(List.of(), registrosDeTransacoesService.procurarTransacoesId(3L));

    }



}