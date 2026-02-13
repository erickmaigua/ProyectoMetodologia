package com.zapateria.controllers;

import com.zapateria.models.Producto;
import com.zapateria.services.InventarioService;
import com.zapateria.services.PedidoService;
import com.zapateria.repositories.PedidoRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.zapateria.utils.Constants.*;

/**
 * Historia PDLS-38: Clasificación de calzado por rangos de edad.
 * Historia PDLS-39: Reporte detallado de ventas.
 */
@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
public class InventarioController {

    private final InventarioService   inventarioService;
    private final PedidoRepository    pedidoRepository;

    public InventarioController(InventarioService inventarioService,
                                PedidoRepository pedidoRepository) {
        this.inventarioService = inventarioService;
        this.pedidoRepository  = pedidoRepository;
    }

    // ========== PDLS-38: CLASIFICACIÓN POR EDAD ==========

    /**
     * GET /api/inventario/por-edad?edad=10
     * Devuelve productos para la categoría de edad indicada.
     */
    @GetMapping("/por-edad")
    public ResponseEntity<List<Producto>> porEdad(@RequestParam int edad) {
        return ResponseEntity.ok(inventarioService.filtrarPorEdad(edad));
    }

    /**
     * GET /api/inventario/categoria/{categoria}
     * Categorías: NINO, ADOLESCENTE, ADULTO
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> porCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(inventarioService.filtrarPorCategoria(categoria.toUpperCase()));
    }

    /**
     * GET /api/inventario/estadisticas/stock
     * Estadísticas de stock por categoría de edad.
     */
    @GetMapping("/estadisticas/stock")
    public ResponseEntity<Map<String, Object>> estadisticasStock() {
        return ResponseEntity.ok(inventarioService.obtenerEstadisticasStock());
    }

    // ========== PDLS-39: REPORTE DE VENTAS ==========

    /**
     * GET /api/inventario/reportes/ventas?inicio=2025-01-01&fin=2025-12-31
     * Reporte detallado de ventas para monitorear el flujo de salida de productos
     * y analizar las tendencias de consumo.
     */
    @GetMapping("/reportes/ventas")
    public ResponseEntity<Map<String, Object>> reporteVentas(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date inicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fin) {

        if (inicio == null) {
            // Por defecto: último mes
            Calendar cal = Calendar.getInstance();
            fin   = cal.getTime();
            cal.add(Calendar.MONTH, -1);
            inicio = cal.getTime();
        }
        if (fin == null) {
            fin = new Date();
        }

        // Filtrar pedidos por rango de fechas en memoria (compatible sin índice)
        final Date inicioFinal = inicio;
        final Date finFinal    = fin;
        List<com.zapateria.models.Pedido> pedidosFiltrados = pedidoRepository.findAll()
                .stream()
                .filter(p -> p.getFecha() != null
                        && !p.getFecha().before(inicioFinal)
                        && !p.getFecha().after(finFinal))
                .toList();

        Map<String, Object> reporte = inventarioService.generarResumenVentas(
                pedidosFiltrados, inicioFinal, finFinal);

        return ResponseEntity.ok(reporte);
    }
}
