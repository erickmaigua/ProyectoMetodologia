package com.zapateria.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
=======
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a

@Document(collection = "productos")
public class Producto {
    @Id
    private String id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String marca;
    private String categoria;
<<<<<<< HEAD
    private String color;
    private double precio;
    private String imagen;
    // PDLS-38: Clasificación por rango de edad
    private String categoriaEdad; // "NINO", "ADOLESCENTE", "ADULTO"

    /**
     * Lista de tallas disponibles con su propio stock.
     * Permite que una misma marca/modelo tenga varias tallas.
     */
    private List<TallaStock> tallas;

    /** Campo legacy — se mantiene para compatibilidad */
    private String talla;
    private int stock;

    public Producto() {
        this.tallas = new ArrayList<>();
    }

    /** Calcula el stock total sumando el stock de todas las tallas */
    public int getStockTotal() {
        if (tallas == null || tallas.isEmpty()) return stock;
        return tallas.stream().mapToInt(TallaStock::getStock).sum();
    }

    // ===== Clase interna TallaStock =====
    public static class TallaStock {
        private String talla;
        private int stock;

        public TallaStock() {}
        public TallaStock(String talla, int stock) {
            this.talla = talla;
            this.stock = stock;
        }

        public String getTalla() { return talla; }
        public void setTalla(String talla) { this.talla = talla; }
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
    }

    // ===== Getters y Setters =====
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public String getCategoriaEdad() { return categoriaEdad; }
    public void setCategoriaEdad(String categoriaEdad) { this.categoriaEdad = categoriaEdad; }
    public List<TallaStock> getTallas() { return tallas; }
    public void setTallas(List<TallaStock> tallas) { this.tallas = tallas; }
    /** @deprecated Usar tallas en su lugar */
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }
    /** @deprecated Usar getStockTotal() */
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
=======
    private String talla;
    private String color;
    private double precio;
    private int stock;
    private String imagen;
    private String estado; // ACTIVO | DESCONTINUADO


    // Constructor vacío
    public Producto() {
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getEstado() {
		return estado;
	}
    
    public void setEstado(String estado) {
		this.estado = estado;
	}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
}
