package com.zapateria.controllers;

import com.zapateria.models.NodoTransito;
import com.zapateria.services.TransitoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.zapateria.utils.Constants.*;

/**
 * Historia PDLS-36: Grafo dinámico de ruta de entrega.
 * Como encargado de despacho, visualiza tiempos de tránsito entre nodos
 * y puede registrar retrasos causados por tráfico o clima.
 */
@RestController
@RequestMapping("/api/transito")
@CrossOrigin(origins = "*")
public class TransitoController {

    private final TransitoService transitoService;

    public TransitoController(TransitoService transitoService) {
        this.transitoService = transitoService;
    }

    /** GET /api/transito/grafo — Datos completos para renderizar el grafo */
    @GetMapping("/grafo")
    public ResponseEntity<Map<String, Object>> obtenerGrafo() {
        return ResponseEntity.ok(transitoService.obtenerGrafoParaVis());
    }

    /** GET /api/transito/nodos — Lista todos los nodos */
    @GetMapping("/nodos")
    public ResponseEntity<List<NodoTransito>> listarNodos() {
        return ResponseEntity.ok(transitoService.obtenerGrafoCompleto());
    }

    /** GET /api/transito/pedido/{pedidoId} — Info de tránsito de un pedido */
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Map<String, Object>> transitoPedido(@PathVariable String pedidoId) {
        return ResponseEntity.ok(transitoService.obtenerTransitoPedido(pedidoId));
    }

    /** POST /api/transito/nodos — Crear nodo */
    @PostMapping("/nodos")
    public ResponseEntity<Map<String, Object>> crearNodo(@RequestBody NodoTransito nodo) {
        Map<String, Object> response = new HashMap<>();
        NodoTransito creado = transitoService.crearNodo(nodo);
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Nodo creado exitosamente");
        response.put("nodo", creado);
        return ResponseEntity.ok(response);
    }

    /** PUT /api/transito/nodos/{id} — Actualizar nodo / tiempos */
    @PutMapping("/nodos/{id}")
    public ResponseEntity<Map<String, Object>> actualizarNodo(
            @PathVariable String id,
            @RequestBody NodoTransito nodo) {
        Map<String, Object> response = new HashMap<>();
        nodo.setId(id);
        NodoTransito actualizado = transitoService.actualizarNodo(nodo);
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Nodo actualizado");
        response.put("nodo", actualizado);
        return ResponseEntity.ok(response);
    }

    /** POST /api/transito/nodos/{id}/retraso — Registrar retraso */
    @PostMapping("/nodos/{id}/retraso")
    public ResponseEntity<Map<String, Object>> registrarRetraso(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            String causa = body.get("causa");
            if (causa == null || causa.trim().isEmpty()) {
                response.put(SUCCESS, false);
                response.put(MENSAJE, "Debe indicar la causa del retraso");
                return ResponseEntity.badRequest().body(response);
            }
            NodoTransito actualizado = transitoService.registrarRetraso(id, causa);
            response.put(SUCCESS, true);
            response.put(MENSAJE, "Retraso registrado");
            response.put("nodo", actualizado);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    /** GET /api/transito/ciudades — Lista ciudades disponibles para entrega (para el cliente) */
    @GetMapping("/ciudades")
    public ResponseEntity<List<Map<String,Object>>> listarCiudades() {
        List<NodoTransito> nodos = transitoService.obtenerGrafoCompleto();
        List<Map<String,Object>> ciudades = new java.util.ArrayList<>();
        for (NodoTransito n : nodos) {
            Map<String,Object> m = new java.util.HashMap<>();
            m.put("id", n.getId());
            m.put("nombre", n.getNombre());
            m.put("ciudad", n.getCiudad());
            boolean conRetraso = n.getRetrasosCausas() != null && !n.getRetrasosCausas().isEmpty();
            m.put("estadoRuta", conRetraso ? "CON_RETRASOS" : "NORMAL");
            m.put("tiempoEstimado", conRetraso ? "3-6 días hábiles" : "2-4 días hábiles");
            ciudades.add(m);
        }
        return ResponseEntity.ok(ciudades);
    }

    /** DELETE /api/transito/nodos/{id} */
    @DeleteMapping("/nodos/{id}")
    public ResponseEntity<Map<String, Object>> eliminarNodo(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        transitoService.eliminarNodo(id);
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Nodo eliminado");
        return ResponseEntity.ok(response);
    }
}
