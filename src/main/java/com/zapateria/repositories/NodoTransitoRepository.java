package com.zapateria.repositories;

import com.zapateria.models.NodoTransito;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NodoTransitoRepository extends MongoRepository<NodoTransito, String> {
    List<NodoTransito> findByCiudad(String ciudad);
}
