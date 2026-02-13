package com.zapateria.controllers;

import com.zapateria.models.Reporte;
import com.zapateria.services.ReporteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.zapateria.utils.Constants.*;

/**
 * Historia PDLS-37: Casilla de reportes integrada.
 */
@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * POST /api/reportes — Crear reporte.
     * FIX: retorna 200 OK (el test espera OK, no CREATED).
     * FIX: error body usa clave "error" (el test hace body.get("error")).
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Reporte reporte) {
        Map<String, Object> response = new HashMap<>();
        try {
            Reporte nuevo = reporteService.crear(reporte);
            response.put(SUCCESS, true);
            response.put(MENSAJE, "Reporte enviado exitosamente");
            response.put("reporte", nuevo);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put(SUCCESS, false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<Reporte>> listar() {
        return ResponseEntity.ok(reporteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable String id) {
        return reporteService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<Reporte>> porPedido(@PathVariable String pedidoId) {
        return ResponseEntity.ok(reporteService.findByPedidoId(pedidoId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Reporte>> porUsuario(@PathVariable String usuarioId) {
        return ResponseEntity.ok(reporteService.findByUsuarioId(usuarioId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Reporte>> porEstado(@PathVariable String estado) {
        return ResponseEntity.ok(reporteService.findByEstado(estado));
    }

    /** PUT /api/reportes/{id}/resolver — Admin responde */
    @PutMapping("/{id}/resolver")
    public ResponseEntity<Map<String, Object>> resolver(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            Reporte actualizado = reporteService.actualizarEstado(
                    id,
                    body.getOrDefault("estado", "RESUELTA"),
                    body.get("respuesta"),
                    body.get("adminId"));
            response.put(SUCCESS, true);
            response.put(MENSAJE, "Reporte actualizado");
            response.put("reporte", actualizado);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> cambiarEstado(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            String nuevoEstado = body.get("estado");
            Reporte actualizado = reporteService.actualizarEstado(id, nuevoEstado, null, null);
            response.put(SUCCESS, true);
            response.put(MENSAJE, "Estado actualizado");
            response.put("reporte", actualizado);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> estadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendientes",  reporteService.contarPendientes());
        stats.put("total",       reporteService.findAll().size());
        stats.put("abiertos",    reporteService.findByEstado("ABIERTA").size());
        stats.put("enRevision",  reporteService.findByEstado("EN_REVISION").size());
        stats.put("resueltos",   reporteService.findByEstado("RESUELTA").size());
        return ResponseEntity.ok(stats);
    }

    /**
     * DELETE /api/reportes/{id}
     * FIX testEliminarExitoso: mensaje correcto "Reporte eliminado exitosamente".
     * FIX testEliminarError: captura RuntimeException y retorna 500.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            reporteService.delete(id);
            response.put(SUCCESS, true);
            response.put("mensaje", "Reporte eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put(SUCCESS, false);
            response.put("mensaje", "Error al eliminar el reporte: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
