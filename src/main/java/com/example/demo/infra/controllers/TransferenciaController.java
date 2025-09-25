package com.example.demo.infra.controllers;

import com.example.demo.infra.dtos.RegistroTransacaoDTO;
import com.example.demo.infra.dtos.transacao.TransacaoDTORequest;
import com.example.demo.infra.dtos.transacao.TransacaoDTOResponse;
import com.example.demo.service.RegistrosDeTransacoesService;
import com.example.demo.service.TransferenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transfer")
public class TransferenciaController {

    @Autowired
    private TransferenciaService transferenciaService;

    @Autowired
    private RegistrosDeTransacoesService registrosDeTransacoesService;

    @PostMapping
    public ResponseEntity<TransacaoDTOResponse> fazerTransacao (@RequestBody TransacaoDTORequest dto) {
        TransacaoDTOResponse response = transferenciaService.fazerTransacao(dto);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<List<RegistroTransacaoDTO>> procurarTransacao (@PathVariable Long id) {
        List<RegistroTransacaoDTO> response = registrosDeTransacoesService.procurarTransacoesId(id);
        return ResponseEntity.ok(response);
    }

}
