package com.zapateria.controllers;

import java.util.ArrayList;
import java.util.Date;
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

import com.zapateria.models.Pedido;
import com.zapateria.models.Producto;
import com.zapateria.models.Usuario;
import com.zapateria.repositories.PedidoRepository;
import com.zapateria.repositories.ProductoRepository;
import com.zapateria.repositories.UsuarioRepository;
import com.zapateria.services.TrackingService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TrackingService trackingService;

    // ===== ENDPOINTS BÁSICOS =====

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Pedido obtenerPedido(@PathVariable String id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Pedido> pedidosPorCliente(@PathVariable String clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    @GetMapping("/estado/{estado}")
    public List<Pedido> pedidosPorEstado(@PathVariable String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    @GetMapping("/procesados")
    public List<Pedido> pedidosProcesados() {
        return pedidoRepository.findByEstado("PROCESANDO");
    }

    @GetMapping("/empleado/{empleadoId}")
    public List<Pedido> pedidosPorEmpleado(@PathVariable String empleadoId) {
        return pedidoRepository.findByEmpleadoAsignadoId(empleadoId);
    }

    @GetMapping("/despacho/{estadoDespacho}")
    public List<Pedido> pedidosPorEstadoDespacho(@PathVariable String estadoDespacho) {
        List<Pedido> todos = pedidoRepository.findAll();
        List<Pedido> filtrados = new ArrayList<>();
        for (Pedido p : todos) {
            if (p.getEstadoDespacho() != null && p.getEstadoDespacho().equalsIgnoreCase(estadoDespacho)) {
                filtrados.add(p);
            }
        }
        return filtrados;
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

        if ("DESPACHO".equals(usuario.getRol())) {
            return pedidoRepository.findByEstado("PROCESANDO");
        }

        return pedidoRepository.findByClienteId(userId);
    }

    // ===== CREAR PEDIDO =====

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
            pedido.setEstadoDespacho("EN_BODEGA");
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

    // ===== ACTUALIZAR ESTADO =====

    @PutMapping("/{id}/estado")
    public Pedido actualizarEstado(@PathVariable String id, @RequestBody Map<String, String> body) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow();

        String nuevoEstado = body.get("estado");
        pedido.setEstado(nuevoEstado);

        if (nuevoEstado != null && nuevoEstado.equalsIgnoreCase("ENTREGADO")) {
            pedido.setEstadoDespacho("ENTREGADO");
            pedido.setUbicacionActual("Entregado al cliente");
            pedido.setFechaEntrega(new Date());
        } else if (nuevoEstado != null && nuevoEstado.equalsIgnoreCase("EN_CAMINO")) {
            pedido.setEstadoDespacho("EN_CAMINO");
            pedido.setFechaDespacho(new Date());
            if (pedido.getUbicacionActual() == null || pedido.getUbicacionActual().trim().isEmpty()) {
                pedido.setUbicacionActual("En ruta");
            }
        } else {
            if (pedido.getEstadoDespacho() == null || pedido.getEstadoDespacho().trim().isEmpty()) {
                pedido.setEstadoDespacho("EN_BODEGA");
            }
            if (pedido.getUbicacionActual() == null || pedido.getUbicacionActual().trim().isEmpty()) {
                pedido.setUbicacionActual("Bodega Principal");
            }
        }

        return pedidoRepository.save(pedido);
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

        if ("EN_RUTA".equalsIgnoreCase(pedido.getEstadoDespacho()) && pedido.getFechaDespacho() == null) {
            pedido.setFechaDespacho(new Date());
        }

        if ("ENTREGADO".equalsIgnoreCase(pedido.getEstadoDespacho())) {
            pedido.setFechaEntrega(new Date());
            pedido.setEstado("ENTREGADO");
        }

        pedidoRepository.save(pedido);

        response.put("success", true);
        response.put("mensaje", "Tracking actualizado");
        response.put("pedido", pedido);
        return response;
    }

    // ===== NUEVOS ENDPOINTS DE TRACKING =====

    /**
     * Iniciar tracking de un pedido
     * POST /api/pedidos/{id}/iniciar-tracking
     * Body: {
     *   "latitudOrigen": -0.1807,
     *   "longitudOrigen": -78.4678,
     *   "latitudDestino": -0.2000,
     *   "longitudDestino": -78.5000,
     *   "direccionEntrega": "Av. Principal 123"
     * }
     */
    @PostMapping("/{id}/iniciar-tracking")
    public Map<String, Object> iniciarTracking(@PathVariable String id, @RequestBody Map<String, Object> data) {
        try {
            Double latOrigen = data.get("latitudOrigen") != null ? 
                Double.parseDouble(data.get("latitudOrigen").toString()) : null;
            Double lonOrigen = data.get("longitudOrigen") != null ? 
                Double.parseDouble(data.get("longitudOrigen").toString()) : null;
            Double latDestino = data.get("latitudDestino") != null ? 
                Double.parseDouble(data.get("latitudDestino").toString()) : null;
            Double lonDestino = data.get("longitudDestino") != null ? 
                Double.parseDouble(data.get("longitudDestino").toString()) : null;
            String direccion = data.get("direccionEntrega") != null ? 
                data.get("direccionEntrega").toString() : "";

            if (latOrigen == null || lonOrigen == null || latDestino == null || lonDestino == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("mensaje", "Coordenadas incompletas");
                return response;
            }

            return trackingService.iniciarTracking(id, latOrigen, lonOrigen, latDestino, lonDestino, direccion);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("mensaje", "Error al iniciar tracking: " + e.getMessage());
            return response;
        }
    }

    /**
     * Actualizar ubicación actual del delivery
     * PUT /api/pedidos/{id}/actualizar-ubicacion
     * Body: { "latitud": -0.1850, "longitud": -78.4700 }
     */
    @PutMapping("/{id}/actualizar-ubicacion")
    public Map<String, Object> actualizarUbicacion(@PathVariable String id, @RequestBody Map<String, Object> data) {
        try {
            Double lat = data.get("latitud") != null ? 
                Double.parseDouble(data.get("latitud").toString()) : null;
            Double lon = data.get("longitud") != null ? 
                Double.parseDouble(data.get("longitud").toString()) : null;

            if (lat == null || lon == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("mensaje", "Coordenadas incompletas");
                return response;
            }

            return trackingService.actualizarUbicacion(id, lat, lon);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("mensaje", "Error al actualizar ubicación: " + e.getMessage());
            return response;
        }
    }

    /**
     * Obtener tracking actual de un pedido
     * GET /api/pedidos/{id}/tracking-actual
     */
    @GetMapping("/{id}/tracking-actual")
    public Map<String, Object> obtenerTrackingActual(@PathVariable String id) {
        return trackingService.obtenerTrackingActual(id);
    }

    /**
     * Simular entrega completa (para testing)
     * POST /api/pedidos/{id}/simular-entrega
     */
    @PostMapping("/{id}/simular-entrega")
    public Map<String, Object> simularEntrega(@PathVariable String id) {
        return trackingService.simularEntrega(id);
    }

    /**
     * Detener simulación en curso
     * POST /api/pedidos/{id}/detener-simulacion
     */
    @PostMapping("/{id}/detener-simulacion")
    public Map<String, Object> detenerSimulacion(@PathVariable String id) {
        return trackingService.detenerSimulacion(id);
    }

    // ===== ELIMINAR PEDIDO =====

    @DeleteMapping("/{id}")
    public Map<String, Object> eliminarPedido(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        pedidoRepository.deleteById(id);
        response.put("success", true);
        response.put("mensaje", "Pedido eliminado");
        return response;
    }
}