package com.zapateria.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
<<<<<<< HEAD
<<<<<<< HEAD
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
=======
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
<<<<<<< HEAD
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52

import com.zapateria.models.Producto;
import com.zapateria.repositories.ProductoRepository;

<<<<<<< HEAD
<<<<<<< HEAD
import static com.zapateria.utils.Constants.*;

=======
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

<<<<<<< HEAD
<<<<<<< HEAD
    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProducto(@PathVariable String id) {
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ID_INVALIDO);
        }
        Optional<Producto> opt = productoRepository.findById(id);
        return opt.isPresent() ? ResponseEntity.ok(opt.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<Producto>> buscarProductos(@PathVariable String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(productoRepository.findByNombreContainingIgnoreCase(nombre));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> productosPorCategoria(@PathVariable String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(productoRepository.findByCategoria(categoria));
    }

    @GetMapping("/categoriaEdad/{categoriaEdad}")
    public ResponseEntity<List<Producto>> productosPorCategoriaEdad(@PathVariable String categoriaEdad) {
        if (categoriaEdad == null || categoriaEdad.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(productoRepository.findByCategoriaEdad(categoriaEdad));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> productosStockBajo() {
        return ResponseEntity.ok(productoRepository.findByStockLessThan(10));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearProducto(@RequestBody Producto producto) {
        Map<String, Object> response = new HashMap<>();
        Producto guardado = productoRepository.save(producto);
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Producto creado exitosamente");
        response.put("producto", guardado);
        logger.info("Producto creado: {}", guardado.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * FIX BUG IMAGEN: Al actualizar un producto, si el body no trae imagen
     * (campo null o vacío), se conserva la imagen que ya estaba en la BD.
     * Antes: producto.setId(id) y save() pisaba la imagen con null.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable String id, @RequestBody Producto producto) {
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ID_INVALIDO);
        }
        Optional<Producto> existente = productoRepository.findById(id);
        if (!existente.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Producto anterior = existente.get();

        // Preservar imagen si el request no trae una nueva
        if (producto.getImagen() == null || producto.getImagen().trim().isEmpty()
                || producto.getImagen().equals("/images/default.jpg")) {
            if (anterior.getImagen() != null && !anterior.getImagen().trim().isEmpty()) {
                producto.setImagen(anterior.getImagen());
            }
        }

        // Preservar categoriaEdad si el request no la trae (evita que quede null al editar)
        if (producto.getCategoriaEdad() == null || producto.getCategoriaEdad().trim().isEmpty()) {
            producto.setCategoriaEdad(anterior.getCategoriaEdad());
        }

        producto.setId(id);
        Producto actualizado = productoRepository.save(producto);
        logger.info("Producto actualizado: {}", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarProducto(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        if (id == null || id.trim().isEmpty()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, ID_INVALIDO);
            return ResponseEntity.badRequest().body(response);
        }
        productoRepository.deleteById(id);
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Producto eliminado");
        logger.info("Producto eliminado: {}", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> actualizarStock(
            @PathVariable String id,
            @RequestBody Map<String, Integer> data) {
        Map<String, Object> response = new HashMap<>();

        if (id == null || id.trim().isEmpty()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, ID_INVALIDO);
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Producto> opt = productoRepository.findById(id);
        if (!opt.isPresent()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, PRODUCTO_NO_ENCONTRADO);
            return ResponseEntity.notFound().build();
        }

        Integer nuevoStock = data.get("stock");
        if (nuevoStock == null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, DATOS_INCOMPLETOS);
            return ResponseEntity.badRequest().body(response);
        }

        Producto producto = opt.get();
        producto.setStock(nuevoStock);
        productoRepository.save(producto);

        response.put(SUCCESS, true);
        response.put(MENSAJE, "Stock actualizado");
        response.put("producto", producto);
        return ResponseEntity.ok(response);
    }
}
=======
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
    @Autowired
    private ProductoRepository productoRepository;

    // Listar todos los productos
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public Producto obtenerProducto(@PathVariable String id) {
        return productoRepository.findById(id).orElse(null);
    }

    // Buscar productos por nombre
    @GetMapping("/buscar/{nombre}")
    public List<Producto> buscarProductos(@PathVariable String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // Buscar productos por categoría
    @GetMapping("/categoria/{categoria}")
    public List<Producto> productosPorCategoria(@PathVariable String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    // Productos con stock bajo
    @GetMapping("/stock-bajo")
    public List<Producto> productosStockBajo() {
        return productoRepository.findByStockLessThan(10);
    }

    // Crear nuevo producto
    @PostMapping
    public Map<String, Object> crearProducto(@RequestBody Producto producto) {
        Map<String, Object> response = new HashMap<>();
<<<<<<< HEAD

        if (producto.getEstado() == null || producto.getEstado().isEmpty()) {
            producto.setEstado("ACTIVO");
        }

=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
        Producto guardado = productoRepository.save(producto);
        response.put("success", true);
        response.put("mensaje", "Producto creado exitosamente");
        response.put("producto", guardado);
        return response;
    }

<<<<<<< HEAD

=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
    // Actualizar producto
    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable String id, @RequestBody Producto producto) {
        producto.setId(id);
        return productoRepository.save(producto);
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    public Map<String, Object> eliminarProducto(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        productoRepository.deleteById(id);
        response.put("success", true);
        response.put("mensaje", "Producto eliminado");
        return response;
    }

    // Actualizar stock
    @PutMapping("/{id}/stock")
    public Map<String, Object> actualizarStock(@PathVariable String id, @RequestBody Map<String, Integer> data) {
        Map<String, Object> response = new HashMap<>();

        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            producto.setStock(data.get("stock"));
            productoRepository.save(producto);
            response.put("success", true);
            response.put("mensaje", "Stock actualizado");
            response.put("producto", producto);
        } else {
            response.put("success", false);
            response.put("mensaje", "Producto no encontrado");
        }

        return response;
    }
<<<<<<< HEAD
    @PutMapping("/{id}/descontinuar")
    public Map<String, Object> descontinuarProducto(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        Producto producto = productoRepository.findById(id).orElse(null);

        if (producto == null) {
            response.put("success", false);
            response.put("mensaje", "Producto no encontrado");
            return response;
        }

        producto.setEstado("DESCONTINUADO");
        productoRepository.save(producto);

        response.put("success", true);
        response.put("mensaje", "Producto marcado como descontinuado");
        return response;
    }
    @PutMapping("/{id}/activar")
    public Map<String, Object> activarProducto(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        Producto producto = productoRepository.findById(id).orElse(null);

        if (producto == null) {
            response.put("success", false);
            response.put("mensaje", "Producto no encontrado");
            return response;
        }

        producto.setEstado("ACTIVO");
        productoRepository.save(producto);

        response.put("success", true);
        response.put("mensaje", "Producto reactivado");
        return response;
    }

}
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
}
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
