package com.zapateria.repositories;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.zapateria.models.Pedido;

@Repository
public interface PedidoRepository extends MongoRepository<Pedido, String> {
    List<Pedido> findByClienteId(String clienteId);
    List<Pedido> findByEstado(String estado);
    Pedido findByNumeroPedido(String numeroPedido);
    
    // Nuevo método: Pedidos asignados a un empleado
    List<Pedido> findByEmpleadoAsignadoId(String empleadoId);
}