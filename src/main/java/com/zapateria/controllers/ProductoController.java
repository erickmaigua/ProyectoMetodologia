package com.zapateria.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.zapateria.models.Producto;
import com.zapateria.repositories.ProductoRepository;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

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
        Producto guardado = productoRepository.save(producto);
        response.put("success", true);
        response.put("mensaje", "Producto creado exitosamente");
        response.put("producto", guardado);
        return response;
    }

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
}