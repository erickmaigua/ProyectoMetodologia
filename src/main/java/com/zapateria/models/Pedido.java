package com.zapateria.models;

import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pedidos")
public class Pedido {
    @Id
    private String id;
    private String numeroPedido;
    private String clienteId;
    private String clienteNombre;
    private List<ItemPedido> items;

    private double subtotal;
    private double ivaPorcentaje;   // ejemplo 0.15
    private double ivaValor;

    // ===== NUEVO (ENVIO / IMPUESTOS) =====
    private double envio;           // ejemplo 5.00
    private double impuestos;       // opcional: si luego quieres sumar otros impuestos (por ahora = ivaValor)

    private double totalFinal;      // subtotal + iva (+ envio si quieres)

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getIvaPorcentaje() { return ivaPorcentaje; }
    public void setIvaPorcentaje(double ivaPorcentaje) { this.ivaPorcentaje = ivaPorcentaje; }

    public double getIvaValor() { return ivaValor; }
    public void setIvaValor(double ivaValor) { this.ivaValor = ivaValor; }

    // ===== NUEVO getters/setters =====
    public double getEnvio() { return envio; }
    public void setEnvio(double envio) { this.envio = envio; }

    public double getImpuestos() { return impuestos; }
    public void setImpuestos(double impuestos) { this.impuestos = impuestos; }

    public double getTotalFinal() { return totalFinal; }
    public void setTotalFinal(double totalFinal) { this.totalFinal = totalFinal; }

    private double total;
    private String estado; // "PENDIENTE", "PROCESANDO", "ENVIADO", "ENTREGADO", "CANCELADO"
    private Date fecha;

    // --- Seguimiento / Despacho ---
    private String despachoId;
    private String despachoNombre;

    // Tracking
    private String codigoSeguimiento;         // ejemplo: TRK-1700000000
    private String estadoDespacho;            // "PENDIENTE", "EN_RUTA", "ENTREGADO", "INCIDENCIA"
    private Date fechaDespacho;
    private Date fechaEntrega;

    // Ubicación simple (opcional)
    private String ubicacionActual;           // "Bodega Quito", "En ruta", etc.

    // Estado del pago: "PENDIENTE", "PAGADO", "RECHAZADO"
    private String estadoPago;

    // Método de pago: "EFECTIVO", "TARJETA_CREDITO", "TARJETA_DEBITO"
    private String metodoPago;

    // Últimos 4 dígitos (solo referencia, no guardes la tarjeta real)
    private String tarjetaUltimos4;

    // Nuevo campo: ID del empleado asignado para gestionar este pedido
    private String empleadoAsignadoId;
    private String empleadoAsignadoNombre;
    
    // ===== NUEVOS CAMPOS: QUEJAS Y DEVOLUCIONES =====
    private String quejaMotivo;      // "Envío incorrecto", "Producto defectuoso", etc.
    private String quejaDetalle;     // Descripción del cliente
    private boolean solicitaDevolucion;
    // Estado interno de la queja (para gestión administrativa)
    private String quejaEstado;      // "ABIERTA", "RESUELTA"
    private Date quejaFecha;
    private String quejaRespuesta;   // Respuesta del admin (opcional)
    private Date quejaFechaRespuesta;

    
    // Getters/Setters para Quejas
    public String getQuejaMotivo() { return quejaMotivo; }
    public void setQuejaMotivo(String quejaMotivo) { this.quejaMotivo = quejaMotivo; }
    public String getQuejaDetalle() { return quejaDetalle; }
    public void setQuejaDetalle(String quejaDetalle) { this.quejaDetalle = quejaDetalle; }
    public boolean isSolicitaDevolucion() { return solicitaDevolucion; }

    public String getQuejaEstado() { return quejaEstado; }
    public void setQuejaEstado(String quejaEstado) { this.quejaEstado = quejaEstado; }

    public Date getQuejaFecha() { return quejaFecha; }
    public void setQuejaFecha(Date quejaFecha) { this.quejaFecha = quejaFecha; }

    public String getQuejaRespuesta() { return quejaRespuesta; }
    public void setQuejaRespuesta(String quejaRespuesta) { this.quejaRespuesta = quejaRespuesta; }

    public Date getQuejaFechaRespuesta() { return quejaFechaRespuesta; }
    public void setQuejaFechaRespuesta(Date quejaFechaRespuesta) { this.quejaFechaRespuesta = quejaFechaRespuesta; }

    public void setSolicitaDevolucion(boolean solicitaDevolucion) { this.solicitaDevolucion = solicitaDevolucion; }

    public Pedido() {
        this.fecha = new Date();
        this.estado = "PENDIENTE";
        this.estadoPago = "PENDIENTE";
        this.estadoDespacho = "PENDIENTE";

        // ===== NUEVO defaults =====
        this.envio = 5.00;     // si tu tienda usa $5 fijo
        this.impuestos = 0.0;  // por ahora
    }

    // Getters y Setters existentes...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public List<ItemPedido> getItems() { return items; }
    public void setItems(List<ItemPedido> items) { this.items = items; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getTarjetaUltimos4() { return tarjetaUltimos4; }
    public void setTarjetaUltimos4(String tarjetaUltimos4) { this.tarjetaUltimos4 = tarjetaUltimos4; }

    // Nuevos getters y setters
    public String getEmpleadoAsignadoId() { return empleadoAsignadoId; }
    public void setEmpleadoAsignadoId(String empleadoAsignadoId) {
        this.empleadoAsignadoId = empleadoAsignadoId;
    }

    public String getEmpleadoAsignadoNombre() { return empleadoAsignadoNombre; }
    public void setEmpleadoAsignadoNombre(String empleadoAsignadoNombre) {
        this.empleadoAsignadoNombre = empleadoAsignadoNombre;
    }

    public String getDespachoId() { return despachoId; }
    public void setDespachoId(String despachoId) { this.despachoId = despachoId; }

    public String getDespachoNombre() { return despachoNombre; }
    public void setDespachoNombre(String despachoNombre) { this.despachoNombre = despachoNombre; }

    public String getCodigoSeguimiento() { return codigoSeguimiento; }
    public void setCodigoSeguimiento(String codigoSeguimiento) { this.codigoSeguimiento = codigoSeguimiento; }

    public String getEstadoDespacho() { return estadoDespacho; }
    public void setEstadoDespacho(String estadoDespacho) { this.estadoDespacho = estadoDespacho; }

    public Date getFechaDespacho() { return fechaDespacho; }
    public void setFechaDespacho(Date fechaDespacho) { this.fechaDespacho = fechaDespacho; }

    public Date getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(Date fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getUbicacionActual() { return ubicacionActual; }
    public void setUbicacionActual(String ubicacionActual) { this.ubicacionActual = ubicacionActual; }

    // Clase interna ItemPedido (sin cambios)
    public static class ItemPedido {
        private String productoId;
        private String nombreProducto;
        private int cantidad;
        private double precio;

        public ItemPedido() {}

        public String getProductoId() { return productoId; }
        public void setProductoId(String productoId) { this.productoId = productoId; }

        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }

        public double getPrecio() { return precio; }
        public void setPrecio(double precio) { this.precio = precio; }
    }
}
