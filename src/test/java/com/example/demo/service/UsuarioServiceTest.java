package com.example.demo.service;

import com.example.demo.domain.UsuarioComum;
import com.example.demo.domain.enums.Role;
import com.example.demo.infra.dtos.CriarUsuarioDTO;
import com.example.demo.infra.repositorios.UsuarioRepository;
import com.example.demo.service.exceptions.UsuarioInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

//import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioComum usuarioExistente;

    @BeforeEach
    public void setUp() {

        usuarioExistente = new UsuarioComum("Joao", "123",
                "joao@gmail.com", "senha123",
                BigDecimal.valueOf(10000));

    }

    @Test
    void usuariosComMesmoCPF_DeveRetornarExcecao() {

        when(usuarioRepository.existsByCpf("123")).thenReturn(true);

        CriarUsuarioDTO usuarioRepetido = new CriarUsuarioDTO("Joao", "123",
                "gemail@gmail.com", "senha123",
                new BigDecimal("10000"), Role.LOJISTA);

        assertThrows(UsuarioInvalidoException.class,
                () -> usuarioService.criarUsuario(usuarioRepetido));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void usuariosComMesmoEmail_DeveRetornarExcecao() {

        when(usuarioRepository.existsByEmail("joao@gmail.com")).thenReturn(true);

        CriarUsuarioDTO usuarioRepetido = new CriarUsuarioDTO("Joao", "456",
                "joao@gmail.com", "senha123",
                new BigDecimal("10000"), Role.LOJISTA);

        assertThrows(UsuarioInvalidoException.class,
                () -> usuarioService.criarUsuario(usuarioRepetido));

        verify(usuarioRepository, never()).save(any());

    }



}