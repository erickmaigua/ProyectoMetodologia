package com.zapateria.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.zapateria.models.Usuario;
import java.util.List;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Usuario findByUsername(String username);
    Usuario findByUsernameAndPassword(String username, String password);
    Usuario findByEmail(String email);
    
    // Nuevos métodos
    List<Usuario> findByRol(String rol);
    List<Usuario> findByEmpleadoAsignadoId(String empleadoId);
}