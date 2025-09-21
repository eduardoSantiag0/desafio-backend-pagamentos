package com.example.demo.infra.controllers;

import com.example.demo.infra.dtos.CriarUsuarioDTO;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/signup")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    ResponseEntity<String> criarUsuario (@Validated @RequestBody CriarUsuarioDTO dto) {
        usuarioService.criarUsuario(dto);
        return ResponseEntity.ok("Conta criada: " + dto.email());
    }



}
