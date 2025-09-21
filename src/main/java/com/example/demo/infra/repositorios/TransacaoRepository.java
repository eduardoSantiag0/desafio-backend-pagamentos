package com.example.demo.infra.repositorios;

import com.example.demo.domain.TransacaoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransacaoRepository extends MongoRepository<TransacaoDocument, String> {

}
