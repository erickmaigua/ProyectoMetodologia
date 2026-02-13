package com.zapateria.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Historia PDLS-36: Grafo dinámico de rutas de entrega.
 * Cada nodo representa una bodega/ciudad y sus conexiones con tiempos de tránsito.
 */
@Document(collection = "nodos_transito")
public class NodoTransito {
    @Id
    private String id;
    private String nombre;      // "Bodega Quito", "Guayaquil", etc.
    private String ciudad;
    private double latitud;
    private double longitud;
    /** mapa destinoId -> tiempo estimado en horas */
    private Map<String, Double> tiemposEstimados;
    /** lista de causas de retraso registradas */
    private List<String> retrasosCausas;

    public NodoTransito() {
        this.tiemposEstimados = new HashMap<>();
        this.retrasosCausas   = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public Map<String, Double> getTiemposEstimados() { return tiemposEstimados; }
    public void setTiemposEstimados(Map<String, Double> t) { this.tiemposEstimados = t; }
    public List<String> getRetrasosCausas() { return retrasosCausas; }
    public void setRetrasosCausas(List<String> r) { this.retrasosCausas = r; }
}
