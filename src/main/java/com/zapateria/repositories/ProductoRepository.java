package com.zapateria.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.zapateria.models.Producto;

@Repository
public interface ProductoRepository extends MongoRepository<Producto, String> {
    // Buscar productos por categoría
    List<Producto> findByCategoria(String categoria);

    // Buscar productos por marca
    List<Producto> findByMarca(String marca);

    // Buscar productos por nombre (contiene texto)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar productos con stock bajo
    List<Producto> findByStockLessThan(int stock);
<<<<<<< HEAD
<<<<<<< HEAD
    // PDLS-38
    List<Producto> findByCategoriaEdad(String categoriaEdad);
    long countByCategoriaEdad(String categoriaEdad);
    // Por marca para agrupar tallas
    List<Producto> findByMarcaAndNombre(String marca, String nombre);
}// Agregado para PDLS-38: clasificación por edad
=======
}
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
}
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
