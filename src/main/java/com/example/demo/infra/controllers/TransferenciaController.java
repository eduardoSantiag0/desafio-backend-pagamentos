package com.example.demo.infra.controllers;

import com.example.demo.infra.dtos.transacao.TransacaoDTORequest;
import com.example.demo.infra.dtos.transacao.TransacaoDTOResponse;
import com.example.demo.service.TransferenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfer")
public class TransferenciaController {

    @Autowired
    private TransferenciaService transferenciaService;

    @PostMapping
    public ResponseEntity<TransacaoDTOResponse> fazerTransacao (@RequestBody TransacaoDTORequest dto) {
        TransacaoDTOResponse response = transferenciaService.fazerTransacao(dto);

        return ResponseEntity.ok(response);

//        if (response == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new TransacaoDTOResponse(StatusTransacao.FALHA, "Usuário não encontrado"));
//        }

//        switch (response.statusTransacao()) {
//            case FALHA:
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//            case EM_ESPERA:
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//            case COMPLETA:
//                return ResponseEntity.status(HttpStatus.OK).body(response);
//            default:
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }

    }

}
