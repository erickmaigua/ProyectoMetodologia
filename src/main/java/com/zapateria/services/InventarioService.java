package com.zapateria.services;

import com.zapateria.models.CategoriaEdad;
import com.zapateria.models.Producto;
import com.zapateria.repositories.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Historia PDLS-38: Clasificación de calzado por rango de edad.
 * Historia PDLS-39: Reporte detallado de ventas con tendencias de consumo.
 */
@Service
public class InventarioService {

    private final ProductoRepository productoRepository;

    public InventarioService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // ========== PDLS-38: CLASIFICACIÓN POR EDAD ==========

    public List<Producto> filtrarPorEdad(int edad) {
        String categoria = CategoriaEdad.desdedad(edad).name();
        return productoRepository.findByCategoriaEdad(categoria);
    }

    public List<Producto> filtrarPorCategoria(String categoriaEdad) {
        return productoRepository.findByCategoriaEdad(categoriaEdad);
    }

    public Map<String, Object> obtenerEstadisticasStock() {
        Map<String, Object> stats = new LinkedHashMap<>();
        long totalGeneral = productoRepository.count();
        stats.put("totalGeneral", totalGeneral);

        for (CategoriaEdad cat : CategoriaEdad.values()) {
            Map<String, Object> catStats = new HashMap<>();
            long count = productoRepository.countByCategoriaEdad(cat.name());
            catStats.put("cantidad",    count);
            catStats.put("descripcion", cat.getDescripcion());
            catStats.put("porcentaje",  totalGeneral > 0 ? Math.round((count * 100.0) / totalGeneral) : 0);
            stats.put(cat.name(), catStats);
        }
        return stats;
    }

    // ========== PDLS-39: REPORTE DE VENTAS ==========

    /**
     * Genera un resumen de ventas a partir de una lista de pedidos ya filtrada por fecha.
     * Devuelve la estructura lista para serializar como JSON.
     */
    public Map<String, Object> generarResumenVentas(
            List<com.zapateria.models.Pedido> pedidos, Date inicio, Date fin) {

        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("fechaInicio", inicio);
        reporte.put("fechaFin",    fin);
        reporte.put("totalPedidos", pedidos.size());

        double totalVentas   = 0;
        int    totalProductos = 0;
        Map<String, Map<String, Object>> ventasProducto = new LinkedHashMap<>();

        for (com.zapateria.models.Pedido pedido : pedidos) {
            if (pedido.getItems() == null) continue;
            totalVentas += pedido.getTotalFinal() > 0 ? pedido.getTotalFinal() : pedido.getTotal();

            for (com.zapateria.models.Pedido.ItemPedido item : pedido.getItems()) {
                String pid  = item.getProductoId();
                String pnom = item.getNombreProducto();
                if (pid == null) continue;

                Map<String, Object> entry = ventasProducto.getOrDefault(pid, new LinkedHashMap<>());
                entry.put("nombre",    pnom);
                int cantPrev   = (Integer) entry.getOrDefault("cantidad",     0);
                double totPrev = (Double)  entry.getOrDefault("totalVentas",  0.0);
                entry.put("cantidad",    cantPrev   + item.getCantidad());
                entry.put("totalVentas", totPrev    + item.getPrecio() * item.getCantidad());
                ventasProducto.put(pid, entry);
                totalProductos += item.getCantidad();
            }
        }

        // Calcular porcentajes
        double totalFinal = totalVentas;
        ventasProducto.forEach((pid, entry) -> {
            double tv = (Double) entry.get("totalVentas");
            entry.put("porcentaje", totalFinal > 0 ? Math.round((tv * 100.0) / totalFinal * 10) / 10.0 : 0);
        });

        // Top 5 productos
        List<Map.Entry<String, Map<String, Object>>> top = new ArrayList<>(ventasProducto.entrySet());
        top.sort((a, b) -> Double.compare(
                (Double) b.getValue().get("totalVentas"),
                (Double) a.getValue().get("totalVentas")));

        List<Map<String, Object>> topProductos = new ArrayList<>();
        for (int i = 0; i < Math.min(5, top.size()); i++) {
            Map<String, Object> t = new LinkedHashMap<>(top.get(i).getValue());
            t.put("productoId", top.get(i).getKey());
            topProductos.add(t);
        }

        reporte.put("totalVentas",      totalVentas);
        reporte.put("totalProductos",   totalProductos);
        reporte.put("ventasPorProducto", new ArrayList<>(ventasProducto.values()));
        reporte.put("topProductos",      topProductos);
        return reporte;
    }
}
