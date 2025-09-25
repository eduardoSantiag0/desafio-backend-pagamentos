package com.example.demo.service;

import com.example.demo.domain.TransacaoDocument;
import com.example.demo.domain.UserEntity;
import com.example.demo.infra.dtos.RegistroTransacaoDTO;
import com.example.demo.infra.repositorios.TransacaoRepository;
import com.example.demo.infra.repositorios.UsuarioRepository;
import com.example.demo.service.exceptions.UsuarioNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrosDeTransacoesService {

    private final TransacaoRepository transacaoRepository;

    private final UsuarioRepository usuarioRepository;

    public RegistrosDeTransacoesService(TransacaoRepository transacaoRepository, UsuarioRepository usuarioRepository) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void salvarDocumento(TransacaoDocument documento) {
        transacaoRepository.save(documento);
    }

    public List<RegistroTransacaoDTO> procurarTransacoesId (Long id) {
        List<TransacaoDocument> transacoes = transacaoRepository.findByIdPayerOrIdPayee(id, id);

        if (transacoes.isEmpty()) return List.of();

        return transacoes.stream()
                .map(transacaoDocument -> {
                    UserEntity payer = usuarioRepository.findById(transacaoDocument.getIdPayer())
                            .orElseThrow(() -> new UsuarioNaoEncontradoException("Pagador não encontrado"));

                    UserEntity payee = usuarioRepository.findById(transacaoDocument.getIdPayee())
                            .orElseThrow(() -> new UsuarioNaoEncontradoException("Recebedor não encontrado"));

                    return new RegistroTransacaoDTO(
                            payer.getId(),
                            payee.getId(),
                            transacaoDocument.getValor(),
                            transacaoDocument.getStatus(),
                            transacaoDocument.getTimestamp(),
                            transacaoDocument.isAutorizado(),
                            transacaoDocument.isNotificacaoEnviada()
                    );
                })
                .collect(Collectors.toList());

    }


}
