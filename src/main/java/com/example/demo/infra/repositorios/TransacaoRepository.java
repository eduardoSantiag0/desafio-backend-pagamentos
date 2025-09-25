package com.example.demo.infra.repositorios;

import com.example.demo.domain.TransacaoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransacaoRepository extends MongoRepository<TransacaoDocument, String> {
    List<TransacaoDocument> findByIdPayerOrIdPayee(Long idPayer, Long idPayee);
}
