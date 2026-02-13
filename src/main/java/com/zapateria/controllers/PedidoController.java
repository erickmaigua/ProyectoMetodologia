package com.zapateria.controllers;

<<<<<<< HEAD
<<<<<<< HEAD
=======
import java.util.ArrayList;
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
=======

import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
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
<<<<<<< HEAD
<<<<<<< HEAD
import com.zapateria.repositories.PedidoRepository;
import com.zapateria.services.PedidoService;
=======
import com.zapateria.models.Producto;
import com.zapateria.models.Usuario;
import com.zapateria.repositories.PedidoRepository;
import com.zapateria.repositories.ProductoRepository;
import com.zapateria.repositories.UsuarioRepository;
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
import com.zapateria.repositories.PedidoRepository;
import com.zapateria.services.PedidoService;
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
    
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
<<<<<<< HEAD
    public ResponseEntity<?> obtenerPedido(@PathVariable String id) {
        if (id == null || id.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
=======
    public ResponseEntity<Map<String, Object>> obtenerPedido(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        if (id == null || id.trim().isEmpty()) {
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
            response.put(SUCCESS, false);
            response.put(MENSAJE, "ID inválido");
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        
        if (!pedidoOpt.isPresent()) {
<<<<<<< HEAD
            Map<String, Object> response = new HashMap<>();
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
            response.put(SUCCESS, false);
            response.put(MENSAJE, PEDIDO_NO_ENCONTRADO);
            return ResponseEntity.notFound().build();
        }
        
<<<<<<< HEAD
        // Devolver el pedido directamente para que el frontend lo pueda leer fácilmente
        return ResponseEntity.ok(pedidoOpt.get());
=======
        response.put(SUCCESS, true);
        response.put(PEDIDO, pedidoOpt.get());
        return ResponseEntity.ok(response);
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> pedidosPorCliente(@PathVariable String clienteId) {
        if (clienteId == null || clienteId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        return ResponseEntity.ok(pedidos);
    }

<<<<<<< HEAD

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

=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
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
<<<<<<< HEAD
=======

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Listar todos los pedidos
    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    // Obtener pedido por ID
    @GetMapping("/{id}")
    public Pedido obtenerPedido(@PathVariable String id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    // Pedidos por cliente
    @GetMapping("/cliente/{clienteId}")
    public List<Pedido> pedidosPorCliente(@PathVariable String clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    // Pedidos por estado
    @GetMapping("/estado/{estado}")
    public List<Pedido> pedidosPorEstado(@PathVariable String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    // Pedidos procesados (para despacho)
    @GetMapping("/procesados")
    public List<Pedido> pedidosProcesados() {
        return pedidoRepository.findByEstado("PROCESANDO");
    }

    // Pedidos asignados a un empleado
    @GetMapping("/empleado/{empleadoId}")
    public List<Pedido> pedidosPorEmpleado(@PathVariable String empleadoId) {
        return pedidoRepository.findByEmpleadoAsignadoId(empleadoId);
    }

    // ==========================
    // ✅ NUEVO: Pedidos en ruta / listos para despacho (por estadoDespacho)
    // ==========================
    @GetMapping("/despacho/{estadoDespacho}")
    public List<Pedido> pedidosPorEstadoDespacho(@PathVariable String estadoDespacho) {
        // Requiere que tengas en Pedido: estadoDespacho
        // Si no tienes repository method, filtramos en memoria para NO tocar repository
        List<Pedido> todos = pedidoRepository.findAll();
        List<Pedido> filtrados = new ArrayList<>();
        for (Pedido p : todos) {
            if (p.getEstadoDespacho() != null && p.getEstadoDespacho().equalsIgnoreCase(estadoDespacho)) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }
    @PutMapping("/{id}/seguimiento")
    public Map<String, Object> actualizarSeguimiento(
            @PathVariable String id,
            @RequestBody Map<String, String> data) {

        Map<String, Object> response = new HashMap<>();
        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido == null) {
            response.put("success", false);
            response.put("mensaje", "Pedido no encontrado");
            return response;
        }

        if (data.get("despachoNombre") != null)
            pedido.setDespachoNombre(data.get("despachoNombre"));

        if (data.get("codigoSeguimiento") != null)
            pedido.setCodigoSeguimiento(data.get("codigoSeguimiento"));

        if (data.get("estadoDespacho") != null)
            pedido.setEstadoDespacho(data.get("estadoDespacho"));

        if (data.get("ubicacionActual") != null)
            pedido.setUbicacionActual(data.get("ubicacionActual"));

        pedidoRepository.save(pedido);

        response.put("success", true);
        response.put("mensaje", "Seguimiento actualizado");
        response.put("pedido", pedido);
        return response;
    }




    // Crear nuevo pedido
    @PostMapping
    public Map<String, Object> crearPedido(@RequestBody Pedido pedido) {
        Map<String, Object> response = new HashMap<>();

        pedido.setNumeroPedido("PED-" + System.currentTimeMillis());

        if (pedido.getFecha() == null) {
            pedido.setFecha(new Date());
        }

        if (pedido.getEstado() == null || pedido.getEstado().trim().isEmpty()) {
            pedido.setEstado("PENDIENTE");
        }

        if (pedido.getCodigoSeguimiento() == null || pedido.getCodigoSeguimiento().trim().isEmpty()) {
            pedido.setCodigoSeguimiento("ZAP-TRK-" + System.currentTimeMillis());
        }

        double total = 0;
        for (Pedido.ItemPedido item : pedido.getItems()) {
            total += item.getPrecio() * item.getCantidad();

            Producto producto = productoRepository.findById(item.getProductoId()).orElse(null);
            if (producto != null) {
                producto.setStock(producto.getStock() - item.getCantidad());
                productoRepository.save(producto);
            }
        }

        double subtotal = total;
        double iva = subtotal * 0.15;
        double envio = 5.00;
        double totalFinal = subtotal + iva + envio;

        pedido.setSubtotal(subtotal);
        pedido.setIvaValor(iva);
        pedido.setEnvio(envio);
        pedido.setTotalFinal(totalFinal);
        pedido.setTotal(totalFinal);

        if ("ENTREGADO".equalsIgnoreCase(pedido.getEstado())) {
            pedido.setEstadoDespacho("ENTREGADO");
            pedido.setUbicacionActual("Entregado al cliente");
        } else {
            pedido.setEstadoDespacho("EN BODEGA");
            if (pedido.getUbicacionActual() == null || pedido.getUbicacionActual().trim().isEmpty()) {
                pedido.setUbicacionActual("Bodega Principal");
            }
        }

        Usuario cliente = usuarioRepository.findById(pedido.getClienteId()).orElse(null);
        if (cliente != null && cliente.getEmpleadoAsignadoId() != null) {
            Usuario empleado = usuarioRepository.findById(cliente.getEmpleadoAsignadoId()).orElse(null);
            if (empleado != null) {
                pedido.setEmpleadoAsignadoId(empleado.getId());
                pedido.setEmpleadoAsignadoNombre(empleado.getNombre() + " " + empleado.getApellido());
            }
        }

        Pedido guardado = pedidoRepository.save(pedido);
        response.put("success", true);
        response.put("mensaje", "Pedido creado exitosamente");
        response.put("pedido", guardado);
        return response;
    }


    // Actualizar estado del pedido
    @PutMapping("/{id}/estado")
    public Pedido actualizarEstado(@PathVariable String id, @RequestBody Map<String, String> body) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow();

        String nuevoEstado = body.get("estado");
        pedido.setEstado(nuevoEstado);

        if (nuevoEstado != null && nuevoEstado.equalsIgnoreCase("ENTREGADO")) {
            pedido.setEstadoDespacho("ENTREGADO");
            pedido.setUbicacionActual("Entregado al cliente");
        } else if (nuevoEstado != null && nuevoEstado.equalsIgnoreCase("EN CAMINO")) {
            pedido.setEstadoDespacho("EN CAMINO");
            if (pedido.getUbicacionActual() == null || pedido.getUbicacionActual().trim().isEmpty()) {
                pedido.setUbicacionActual("En ruta");
            }
        } else {
            if (pedido.getEstadoDespacho() == null || pedido.getEstadoDespacho().trim().isEmpty()) {
                pedido.setEstadoDespacho("EN BODEGA");
            }
            if (pedido.getUbicacionActual() == null || pedido.getUbicacionActual().trim().isEmpty()) {
                pedido.setUbicacionActual("Bodega Principal");
            }
        }

        return pedidoRepository.save(pedido);
    }


    // ==========================
    // ✅ NUEVO: Endpoint de tracking para DESPACHO
    // PUT /api/pedidos/{id}/tracking
    // Body: { "estadoDespacho":"EN_RUTA", "ubicacionActual":"Centro de distribución" }
    // ==========================
    @PutMapping("/{id}/tracking")
    public Map<String, Object> actualizarTracking(@PathVariable String id, @RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();

        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            response.put("success", false);
            response.put("mensaje", "Pedido no encontrado");
            return response;
        }

        if (data.containsKey("estadoDespacho")) {
            pedido.setEstadoDespacho(data.get("estadoDespacho"));
        }

        if (data.containsKey("ubicacionActual")) {
            pedido.setUbicacionActual(data.get("ubicacionActual"));
        }

        // Si cambia a EN_RUTA, registrar fecha despacho (si no existe)
        if ("EN_RUTA".equalsIgnoreCase(pedido.getEstadoDespacho()) && pedido.getFechaDespacho() == null) {
            pedido.setFechaDespacho(new Date());
        }

        // Si cambia a ENTREGADO, registrar fecha entrega y también estado del pedido
        if ("ENTREGADO".equalsIgnoreCase(pedido.getEstadoDespacho())) {
            pedido.setFechaEntrega(new Date());
            pedido.setEstado("ENTREGADO"); // para que tu botón de factura funcione como ya lo tienes
        }

        pedidoRepository.save(pedido);

        response.put("success", true);
        response.put("mensaje", "Tracking actualizado");
        response.put("pedido", pedido);
        return response;
    }

    // Eliminar pedido
    @DeleteMapping("/{id}")
    public Map<String, Object> eliminarPedido(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        pedidoRepository.deleteById(id);
        response.put("success", true);
        response.put("mensaje", "Pedido eliminado");
        return response;
    }

    @GetMapping("/mis-pedidos/{userId}")
    public List<Pedido> misPedidos(@PathVariable String userId) {
        Usuario usuario = usuarioRepository.findById(userId).orElse(null);

        if (usuario == null) {
            return new ArrayList<>();
        }

        if ("ADMINISTRADOR".equals(usuario.getRol())) {
            return pedidoRepository.findAll();
        }

        if ("EMPLEADO".equals(usuario.getRol())) {
            return pedidoRepository.findByEmpleadoAsignadoId(userId);
        }

        // ==========================
        // ✅ NUEVO: DESPACHO solo ve los PROCESANDO (igual como tu front ya usa /procesados)
        // ==========================
        if ("DESPACHO".equals(usuario.getRol())) {
            return pedidoRepository.findByEstado("PROCESANDO");
        }

        return pedidoRepository.findByClienteId(userId);
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
    }
}
