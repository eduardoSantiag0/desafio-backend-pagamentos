package com.example.demo.service;

import com.example.demo.domain.CriarUsuarioDTO;
import com.example.demo.domain.Lojista;
import com.example.demo.domain.UsuarioComum;
import com.example.demo.domain.enums.Role;
import com.example.demo.infra.repositorios.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void criarUsuario (CriarUsuarioDTO dto) {

        System.out.println(dto);

        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalStateException("E-mail já cadastrado.");
        }
        if (usuarioRepository.existsByCpf(dto.cpf())) {
            throw new IllegalStateException("CPF já cadastrado.");
        }

        if (dto.role() == Role.LOJISTA) {
            var novoLojista = new Lojista(dto.nome_completo(), dto.cpf(), dto.email(), dto.senha(), dto.saldo());
            usuarioRepository.save(novoLojista);
        }

        if (dto.role() == Role.USUARIO) {
            var novoUsuario = new UsuarioComum(dto.nome_completo(), dto.cpf(), dto.email(), dto.senha(), dto.saldo());
            usuarioRepository.save(novoUsuario);
        }
    }
}
