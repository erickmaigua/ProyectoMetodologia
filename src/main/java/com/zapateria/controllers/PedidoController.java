package com.zapateria.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zapateria.models.Pedido;
import com.zapateria.repositories.PedidoRepository;
import com.zapateria.services.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {
    
    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);
    
    // Constantes para evitar duplicación de literales
    private static final String SUCCESS = "success";
    private static final String MENSAJE = "mensaje";
    private static final String PEDIDO = "pedido";
    private static final String PEDIDO_NO_ENCONTRADO = "Pedido no encontrado";

    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;

    public PedidoController(PedidoRepository pedidoRepository, PedidoService pedidoService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
    }

    // ========== CONSULTAS ==========
    
    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPedido(@PathVariable String id) {
        if (id == null || id.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put(SUCCESS, false);
            response.put(MENSAJE, "ID inválido");
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        
        if (!pedidoOpt.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put(SUCCESS, false);
            response.put(MENSAJE, PEDIDO_NO_ENCONTRADO);
            return ResponseEntity.notFound().build();
        }
        
        // Devolver el pedido directamente para que el frontend lo pueda leer fácilmente
        return ResponseEntity.ok(pedidoOpt.get());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> pedidosPorCliente(@PathVariable String clienteId) {
        if (clienteId == null || clienteId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        return ResponseEntity.ok(pedidos);
    }


    /**
     * GET /api/pedidos/mis-pedidos/{clienteId}
     * Alias de pedidosPorCliente para compatibilidad con el frontend.
     */
    @GetMapping("/mis-pedidos/{clienteId}")
    public ResponseEntity<List<Pedido>> misPedidos(@PathVariable String clienteId) {
        if (clienteId == null || clienteId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        return ResponseEntity.ok(pedidos != null ? pedidos : new java.util.ArrayList<>());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pedido>> pedidosPorEstado(@PathVariable String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Pedido> pedidos = pedidoRepository.findByEstado(estado);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/procesados")
    public ResponseEntity<List<Pedido>> pedidosProcesados() {
        List<Pedido> pedidos = pedidoRepository.findByEstado("PROCESANDO");
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<Pedido>> pedidosPorEmpleado(@PathVariable String empleadoId) {
        if (empleadoId == null || empleadoId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Pedido> pedidos = pedidoRepository.findByEmpleadoAsignadoId(empleadoId);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/despacho/{estadoDespacho}")
    public ResponseEntity<List<Pedido>> pedidosPorEstadoDespacho(@PathVariable String estadoDespacho) {
        if (estadoDespacho == null || estadoDespacho.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Pedido> pedidos = pedidoService.filtrarPorEstadoDespacho(estadoDespacho);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/despacho/usuario/{despachoId}")
    public ResponseEntity<List<Pedido>> pedidosAsignadosADespacho(@PathVariable String despachoId) {
        if (despachoId == null || despachoId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Pedido> todos = pedidoRepository.findAll();
        List<Pedido> asignados = todos.stream()
                .filter(p -> despachoId.equals(p.getDespachoId()))
                .toList();
        
        return ResponseEntity.ok(asignados);
    }

    // ========== CONSULTAS DE QUEJAS ==========
    
    @GetMapping("/quejas")
    public ResponseEntity<List<Pedido>> pedidosConQuejas() {
        List<Pedido> todos = pedidoRepository.findAll();
        List<Pedido> conQuejas = todos.stream()
                .filter(p -> p.getQuejaMotivo() != null && !p.getQuejaMotivo().isEmpty())
                .toList();
        
        return ResponseEntity.ok(conQuejas);
    }

    @GetMapping("/quejas/abiertas")
    public ResponseEntity<List<Pedido>> quejasAbiertas() {
        List<Pedido> todos = pedidoRepository.findAll();
        List<Pedido> abiertas = todos.stream()
                .filter(p -> "ABIERTA".equals(p.getQuejaEstado()))
                .toList();
        
        return ResponseEntity.ok(abiertas);
    }

    // ========== CREACIÓN Y ACTUALIZACIÓN ==========
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPedido(@RequestBody Pedido pedido) {
        Map<String, Object> response = pedidoService.crearPedido(pedido);
        
        Boolean success = (Boolean) response.get(SUCCESS);
        if (Boolean.TRUE.equals(success)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarPedido(
            @PathVariable String id, 
            @RequestBody Pedido pedido) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (id == null || id.trim().isEmpty()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "ID inválido");
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        
        if (!pedidoOpt.isPresent()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, PEDIDO_NO_ENCONTRADO);
            return ResponseEntity.notFound().build();
        }
        
        pedido.setId(id);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Pedido actualizado exitosamente");
        response.put(PEDIDO, pedidoActualizado);
        
        logger.info("Pedido actualizado: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarPedido(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        if (id == null || id.trim().isEmpty()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "ID inválido");
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        
        if (!pedidoOpt.isPresent()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, PEDIDO_NO_ENCONTRADO);
            return ResponseEntity.notFound().build();
        }
        
        pedidoRepository.deleteById(id);
        
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Pedido eliminado exitosamente");
        
        logger.info("Pedido eliminado: {}", id);
        return ResponseEntity.ok(response);
    }

    // ========== CAMBIO DE ESTADOS ==========
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> cambiarEstado(
            @PathVariable String id, 
            @RequestBody Map<String, String> request) {
        
        String nuevoEstado = request.get("estado");
        
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put(SUCCESS, false);
            response.put(MENSAJE, "Estado inválido");
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> response = pedidoService.cambiarEstado(id, nuevoEstado);
        
        Boolean success = (Boolean) response.get(SUCCESS);
        if (Boolean.TRUE.equals(success)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}/asignar-empleado")
    public ResponseEntity<Map<String, Object>> asignarEmpleado(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        
        Map<String, Object> response = new HashMap<>();
        String empleadoId = body.get("empleadoId");
        String empleadoNombre = body.get("empleadoNombre");
        
        if (id == null || empleadoId == null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "Datos incompletos");
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        
        if (!pedidoOpt.isPresent()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, PEDIDO_NO_ENCONTRADO);
            return ResponseEntity.notFound().build();
        }
        
        Pedido pedido = pedidoOpt.get();
        pedido.setEmpleadoAsignadoId(empleadoId);
        pedido.setEmpleadoAsignadoNombre(empleadoNombre);
        
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Empleado asignado exitosamente");
        response.put(PEDIDO, pedidoActualizado);
        
        logger.info("Empleado {} asignado al pedido {}", empleadoId, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/asignar-despacho")
    public ResponseEntity<Map<String, Object>> asignarDespacho(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        
        Map<String, Object> response = new HashMap<>();
        String despachoId = body.get("despachoId");
        String despachoNombre = body.get("despachoNombre");
        
        if (id == null || despachoId == null) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "Datos incompletos");
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        
        if (!pedidoOpt.isPresent()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, PEDIDO_NO_ENCONTRADO);
            return ResponseEntity.notFound().build();
        }
        
        Pedido pedido = pedidoOpt.get();
        pedido.setDespachoId(despachoId);
        pedido.setDespachoNombre(despachoNombre);
        pedido.setEstadoDespacho("ASIGNADO");
        pedido.setFechaDespacho(new Date());
        
        String codigoSeguimiento = generarCodigoSeguimiento();
        pedido.setCodigoSeguimiento(codigoSeguimiento);
        
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Despacho asignado exitosamente");
        response.put(PEDIDO, pedidoActualizado);
        
        logger.info("Despacho {} asignado al pedido {}", despachoId, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/tracking")
    public ResponseEntity<Map<String, Object>> actualizarTracking(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (id == null || id.trim().isEmpty()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "ID inválido");
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        
        if (!pedidoOpt.isPresent()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, PEDIDO_NO_ENCONTRADO);
            return ResponseEntity.notFound().build();
        }
        
        Pedido pedido = pedidoOpt.get();
        
        String estadoDespacho = body.get("estadoDespacho");
        if (estadoDespacho != null && !estadoDespacho.trim().isEmpty()) {
            pedido.setEstadoDespacho(estadoDespacho);
        }
        
        String ubicacion = body.get("ubicacion");
        if (ubicacion != null && !ubicacion.trim().isEmpty()) {
            pedido.setUbicacionActual(ubicacion);
        }
        
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Tracking actualizado exitosamente");
        response.put(PEDIDO, pedidoActualizado);
        
        logger.info("Tracking actualizado para pedido {}", id);
        return ResponseEntity.ok(response);
    }

    // ========== QUEJAS Y DEVOLUCIONES ==========
    
    @PostMapping("/{id}/queja")
    public ResponseEntity<Map<String, Object>> registrarQueja(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        
        String motivo = (String) body.get("motivo");
        String detalle = (String) body.get("detalle");
        Boolean solicitaDevolucion = (Boolean) body.getOrDefault("solicitaDevolucion", false);
        
        Map<String, Object> response = pedidoService.registrarQueja(
                id, motivo, detalle, solicitaDevolucion);
        
        Boolean success = (Boolean) response.get(SUCCESS);
        if (Boolean.TRUE.equals(success)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}/resolver-queja")
    public ResponseEntity<Map<String, Object>> resolverQueja(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (id == null || id.trim().isEmpty()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, "ID inválido");
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        
        if (!pedidoOpt.isPresent()) {
            response.put(SUCCESS, false);
            response.put(MENSAJE, PEDIDO_NO_ENCONTRADO);
            return ResponseEntity.notFound().build();
        }
        
        Pedido pedido = pedidoOpt.get();
        pedido.setQuejaEstado("RESUELTA");
        pedido.setQuejaRespuesta(body.get("respuesta"));
        pedido.setQuejaFechaRespuesta(new Date());
        
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        response.put(SUCCESS, true);
        response.put(MENSAJE, "Queja resuelta exitosamente");
        response.put(PEDIDO, pedidoActualizado);
        
        logger.info("Queja resuelta para pedido {}", id);
        return ResponseEntity.ok(response);
    }

    // ========== MÉTODOS AUXILIARES ==========
    
    private String generarCodigoSeguimiento() {
        return "TRK-" + System.currentTimeMillis();
    }
}
