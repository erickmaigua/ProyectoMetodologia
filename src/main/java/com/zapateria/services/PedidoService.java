package com.zapateria.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zapateria.exceptions.PedidoNotFoundException;
import com.zapateria.models.Pedido;
import com.zapateria.models.Pedido.ItemPedido;
import com.zapateria.models.Producto;
import com.zapateria.models.Usuario;
import com.zapateria.repositories.PedidoRepository;
import com.zapateria.repositories.ProductoRepository;
import com.zapateria.repositories.UsuarioRepository;

<<<<<<< HEAD
import static com.zapateria.utils.Constants.*;

=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
@Service
public class PedidoService {
    
    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);
    
<<<<<<< HEAD
    // Constantes de ubicación (locales, no están en Constants)
    private static final String UBICACION_BODEGA_PRINCIPAL = UBICACION_BODEGA;
    private static final String UBICACION_EN_RUTA_LOCAL    = UBICACION_EN_RUTA;
    private static final String UBICACION_ENTREGADO_LOCAL  = UBICACION_ENTREGADO;
=======
    // Constantes
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_PROCESANDO = "PROCESANDO";
    private static final String ESTADO_ENVIADO = "ENVIADO";
    private static final String ESTADO_ENTREGADO = "ENTREGADO";
    private static final String ESTADO_DESPACHO_EN_RUTA = "EN_RUTA";
    private static final String ESTADO_DESPACHO_ENTREGADO = "ENTREGADO";
    private static final String UBICACION_BODEGA_PRINCIPAL = "Bodega Principal";
    private static final String UBICACION_EN_RUTA = "En ruta";
    private static final String UBICACION_ENTREGADO = "Entregado al cliente";
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
    
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    
    public PedidoService(PedidoRepository pedidoRepository, 
                        ProductoRepository productoRepository,
                        UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }
    
    /**
     * Crea un nuevo pedido validando stock y actualizándolo
     */
    public Map<String, Object> crearPedido(Pedido pedido) {
        Map<String, Object> response = new HashMap<>();
        
        if (!validarPedido(pedido, response)) {
            return response;
        }
        
        if (!validarYActualizarStock(pedido, response)) {
            return response;
        }
        
        Pedido pedidoCreado = guardarPedido(pedido);
        
        response.put(SUCCESS, true);
        response.put("mensaje", "Pedido creado exitosamente");
        response.put("pedido", pedidoCreado);
        
        logger.info("Pedido creado exitosamente: {}", pedidoCreado.getId());
        return response;
    }
    
    /**
     * Valida que el pedido tenga datos básicos
     */
    private boolean validarPedido(Pedido pedido, Map<String, Object> response) {
        if (pedido == null) {
            response.put(SUCCESS, false);
            response.put("mensaje", "Pedido inválido");
            return false;
        }
        
        if (pedido.getItems() == null || pedido.getItems().isEmpty()) {
            response.put(SUCCESS, false);
            response.put("mensaje", "El pedido debe tener al menos un producto");
            return false;
        }
        
        if (pedido.getClienteId() == null || pedido.getClienteId().trim().isEmpty()) {
            response.put(SUCCESS, false);
            response.put("mensaje", "El pedido debe tener un cliente asociado");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida stock disponible y lo actualiza
     */
    private boolean validarYActualizarStock(Pedido pedido, Map<String, Object> response) {
        List<ItemPedido> items = pedido.getItems();
        
        for (ItemPedido item : items) {
            if (item == null || item.getProductoId() == null) {
                response.put(SUCCESS, false);
                response.put("mensaje", "Item de pedido inválido");
                return false;
            }
            
            Optional<Producto> productoOpt = productoRepository.findById(item.getProductoId());
            
            if (!productoOpt.isPresent()) {
                response.put(SUCCESS, false);
                response.put("mensaje", "Producto no encontrado: " + item.getProductoId());
                return false;
            }
            
            Producto producto = productoOpt.get();
            
<<<<<<< HEAD
            // Verificar stock: soporte tallas múltiples
            int stockDisponible = producto.getStockTotal();
            
            // Si el item tiene talla específica, verificar esa talla
            if (item.getTalla() != null && !item.getTalla().trim().isEmpty()
                    && producto.getTallas() != null && !producto.getTallas().isEmpty()) {
                String tallaItem = item.getTalla().trim();
                stockDisponible = producto.getTallas().stream()
                    .filter(t -> tallaItem.equals(t.getTalla()))
                    .mapToInt(Producto.TallaStock::getStock)
                    .findFirst()
                    .orElse(0);
            }
            
            if (stockDisponible < item.getCantidad()) {
=======
            if (producto.getStock() < item.getCantidad()) {
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
                response.put(SUCCESS, false);
                response.put("mensaje", "Stock insuficiente para: " + producto.getNombre());
                return false;
            }
            
<<<<<<< HEAD
            // Actualizar stock (soporte tallas)
            if (item.getTalla() != null && !item.getTalla().trim().isEmpty()
                    && producto.getTallas() != null && !producto.getTallas().isEmpty()) {
                String tallaItem = item.getTalla().trim();
                producto.getTallas().stream()
                    .filter(t -> tallaItem.equals(t.getTalla()))
                    .findFirst()
                    .ifPresent(t -> t.setStock(Math.max(0, t.getStock() - item.getCantidad())));
            }
            // También actualizar campo legacy
            producto.setStock(Math.max(0, producto.getStock() - item.getCantidad()));
=======
            // Actualizar stock
            producto.setStock(producto.getStock() - item.getCantidad());
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
            productoRepository.save(producto);
        }
        
        return true;
    }
    
    /**
     * Guarda el pedido con todos sus datos calculados
     */
    private Pedido guardarPedido(Pedido pedido) {
        calcularTotales(pedido);
        inicializarEstados(pedido);
        generarNumeroPedido(pedido);
        
        return pedidoRepository.save(pedido);
    }
    
    /**
     * Calcula totales del pedido
     */
    private void calcularTotales(Pedido pedido) {
        double subtotal = pedido.getItems().stream()
                .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                .sum();
        
        pedido.setSubtotal(subtotal);
        
        double ivaPorcentaje = pedido.getIvaPorcentaje() > 0 ? pedido.getIvaPorcentaje() : 0.15;
        pedido.setIvaPorcentaje(ivaPorcentaje);
        
        double ivaValor = subtotal * ivaPorcentaje;
        pedido.setIvaValor(ivaValor);
        
<<<<<<< HEAD
        // Envío: $5 domicilio, $0 retiro en tienda
        double envio;
        if ("RETIRO_TIENDA".equals(pedido.getTipoEntrega())) {
            envio = 0.0;
        } else {
            envio = pedido.getEnvio() > 0 ? pedido.getEnvio() : 5.00;
        }
=======
        double envio = pedido.getEnvio() > 0 ? pedido.getEnvio() : 5.00;
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
        pedido.setEnvio(envio);
        
        double totalFinal = subtotal + ivaValor + envio;
        pedido.setTotalFinal(totalFinal);
        pedido.setTotal(totalFinal); // Backward compatibility
    }
    
    /**
     * Inicializa estados por defecto del pedido
     */
    private void inicializarEstados(Pedido pedido) {
        if (pedido.getEstado() == null || pedido.getEstado().isEmpty()) {
            pedido.setEstado(ESTADO_PENDIENTE);
        }
        
        if (pedido.getFecha() == null) {
            pedido.setFecha(new Date());
        }
        
        if (pedido.getEstadoDespacho() == null || pedido.getEstadoDespacho().isEmpty()) {
            pedido.setEstadoDespacho(ESTADO_PENDIENTE);
        }
        
        if (pedido.getUbicacionActual() == null || pedido.getUbicacionActual().isEmpty()) {
            pedido.setUbicacionActual(UBICACION_BODEGA_PRINCIPAL);
        }
    }
    
    /**
     * Genera número único de pedido
     */
    private void generarNumeroPedido(Pedido pedido) {
        if (pedido.getNumeroPedido() == null || pedido.getNumeroPedido().isEmpty()) {
            String numeroPedido = "PED-" + System.currentTimeMillis();
            pedido.setNumeroPedido(numeroPedido);
        }
    }
    
    /**
     * Cambia el estado de un pedido
     */
    public Map<String, Object> cambiarEstado(String pedidoId, String nuevoEstado) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        
        if (!pedidoOpt.isPresent()) {
            response.put(SUCCESS, false);
            response.put("mensaje", "Pedido no encontrado");
            return response;
        }
        
        Pedido pedido = pedidoOpt.get();
        pedido.setEstado(nuevoEstado);
        
        actualizarUbicacionSegunEstado(pedido, nuevoEstado);
        
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        response.put(SUCCESS, true);
        response.put("mensaje", "Estado actualizado correctamente");
        response.put("pedido", pedidoActualizado);
        
        logger.info("Estado del pedido {} actualizado a: {}", pedidoId, nuevoEstado);
        return response;
    }
    
    /**
     * Actualiza la ubicación según el nuevo estado
     */
    private void actualizarUbicacionSegunEstado(Pedido pedido, String nuevoEstado) {
        switch (nuevoEstado) {
            case ESTADO_PROCESANDO:
                pedido.setUbicacionActual(UBICACION_BODEGA_PRINCIPAL);
                break;
            case ESTADO_ENVIADO:
<<<<<<< HEAD
                pedido.setUbicacionActual(UBICACION_EN_RUTA_LOCAL);
                pedido.setEstadoDespacho(DESPACHO_EN_RUTA);
=======
                pedido.setUbicacionActual(UBICACION_EN_RUTA);
                pedido.setEstadoDespacho(ESTADO_DESPACHO_EN_RUTA);
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
                if (pedido.getFechaDespacho() == null) {
                    pedido.setFechaDespacho(new Date());
                }
                break;
            case ESTADO_ENTREGADO:
<<<<<<< HEAD
                pedido.setUbicacionActual(UBICACION_ENTREGADO_LOCAL);
                pedido.setEstadoDespacho(DESPACHO_ENTREGADO);
=======
                pedido.setUbicacionActual(UBICACION_ENTREGADO);
                pedido.setEstadoDespacho(ESTADO_DESPACHO_ENTREGADO);
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
                if (pedido.getFechaEntrega() == null) {
                    pedido.setFechaEntrega(new Date());
                }
                break;
            default:
<<<<<<< HEAD
=======
                // No cambiar ubicación para otros estados
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
                break;
        }
    }
    
    /**
     * Filtra pedidos por estado de despacho
     */
    public List<Pedido> filtrarPorEstadoDespacho(String estadoDespacho) {
        return pedidoRepository.findAll().stream()
                .filter(p -> estadoDespacho.equals(p.getEstadoDespacho()))
                .collect(Collectors.toList());
    }
    
    /**
     * Registra una queja en un pedido
     */
    public Map<String, Object> registrarQueja(String pedidoId, String motivo, String detalle, boolean solicitaDevolucion) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        
        if (!pedidoOpt.isPresent()) {
            response.put(SUCCESS, false);
            response.put("mensaje", "Pedido no encontrado");
            return response;
        }
        
        Pedido pedido = pedidoOpt.get();
        pedido.setQuejaMotivo(motivo);
        pedido.setQuejaDetalle(detalle);
        pedido.setSolicitaDevolucion(solicitaDevolucion);
        pedido.setQuejaEstado("ABIERTA");
        pedido.setQuejaFecha(new Date());
        
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        response.put(SUCCESS, true);
        response.put("mensaje", "Queja registrada exitosamente");
        response.put("pedido", pedidoActualizado);
        
        logger.info("Queja registrada en pedido {}: {}", pedidoId, motivo);
        return response;
    }
}
