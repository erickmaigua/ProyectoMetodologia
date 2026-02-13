package com.zapateria.repositories;

import com.zapateria.models.Reporte;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReporteRepository extends MongoRepository<Reporte, String> {
    List<Reporte> findByPedidoId(String pedidoId);
    List<Reporte> findByUsuarioId(String usuarioId);
    List<Reporte> findByEstado(String estado);
    List<Reporte> findByTipo(String tipo);
    long countByEstado(String estado);
}
