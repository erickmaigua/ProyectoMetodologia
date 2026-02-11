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
    }
	 // ==========================================
	    // NUEVO: Registrar Queja o Devolución
	    // PUT /api/pedidos/{id}/queja
	    // Body: { "quejaMotivo": "Producto defectuoso", "solicitaDevolucion": "true", "quejaDetalle": "..." }
	    // ==========================================
    @PutMapping("/{id}/queja")
    public Map<String, Object> registrarQueja(
            @PathVariable String id, 
            @RequestBody Map<String, String> data) {
        
        Map<String, Object> response = new HashMap<>();
        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido == null) {
            response.put("success", false);
            response.put("mensaje", "Pedido no encontrado");
            return response;
        }

        // Se asume que el modelo Pedido tendrá estos campos o se guardarán en un log
        // Si el modelo no tiene los campos, se pueden guardar en variables dinámicas si usas MongoDB
        // o añadir los setters correspondientes en la clase Pedido.java
        
        if (data.get("quejaMotivo") != null) {
            pedido.setEstado("RECLAMO_ABIERTO"); // Cambiamos el estado para que el admin lo vea
            // Suponiendo que añadimos estos campos al modelo Pedido:
            pedido.setQuejaMotivo(data.get("quejaMotivo"));
            pedido.setQuejaDetalle(data.get("quejaDetalle"));
            
            boolean quiereDevolucion = Boolean.parseBoolean(data.get("solicitaDevolucion"));
            pedido.setSolicitaDevolucion(quiereDevolucion);
            pedido.setQuejaEstado("ABIERTA");
            pedido.setQuejaFecha(new Date());
            
            if (quiereDevolucion) {
                pedido.setEstadoDespacho("PENDIENTE_DEVOLUCION");
            }
        }

        pedidoRepository.save(pedido);

        response.put("success", true);
        response.put("mensaje", "Su reporte ha sido enviado exitosamente. Revisaremos su caso.");
        response.put("pedido", pedido);
        return response;
    }


    // ==========================================
    // ✅ NUEVO: Resolver/Cerrar una queja (Admin)
    // PUT /api/pedidos/{id}/queja/resolver
    // Body: { "respuesta": "texto opcional", "resultado": "RESUELTA" }
    // ==========================================
    @PutMapping("/{id}/queja/resolver")
    public Map<String, Object> resolverQueja(
            @PathVariable String id,
            @RequestBody Map<String, String> data) {

        Map<String, Object> response = new HashMap<>();
        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido == null) {
            response.put("success", false);
            response.put("mensaje", "Pedido no encontrado");
            return response;
        }

        // Marcar como resuelta sin alterar la funcionalidad principal del pedido
        pedido.setQuejaEstado("RESUELTA");
        pedido.setQuejaRespuesta(data.get("respuesta"));
        pedido.setQuejaFechaRespuesta(new Date());

        // Mantener un estado explícito para auditoría
        pedido.setEstado("RECLAMO_RESUELTO");

        // Si había devolución pendiente, el admin puede dejarlo tal cual o marcarlo como "DEVOLUCION_EN_PROCESO"
        String resultado = data.get("resultado");
        if (resultado != null && !resultado.trim().isEmpty()) {
            // Resultado libre: por ejemplo "DEVOLUCION_EN_PROCESO", "DEVOLUCION_RECHAZADA", etc.
            if (pedido.isSolicitaDevolucion()) {
                pedido.setEstadoDespacho(resultado);
            }
        }

        pedidoRepository.save(pedido);

        response.put("success", true);
        response.put("mensaje", "Queja resuelta/actualizada correctamente.");
        response.put("pedido", pedido);
        return response;
    }

}
