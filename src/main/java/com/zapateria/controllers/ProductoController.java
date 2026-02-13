package com.zapateria.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zapateria.models.Producto;
import com.zapateria.repositories.ProductoRepository;

import static com.zapateria.utils.Constants.*;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

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
