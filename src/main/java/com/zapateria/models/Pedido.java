package com.zapateria.models;

import java.util.ArrayList;
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
    private double ivaPorcentaje;
    private double ivaValor;
    private double envio;
    private double impuestos;
    private double totalFinal;
    private double total;
    
    private String estado; // "PENDIENTE", "PROCESANDO", "EN_CAMINO", "ENTREGADO", "CANCELADO"
    private Date fecha;

    // --- Seguimiento / Despacho ---
    private String despachoId;
    private String despachoNombre;
    private String codigoSeguimiento;
    private String estadoDespacho; // "PENDIENTE", "EN_BODEGA", "EN_CAMINO", "ENTREGADO", "INCIDENCIA"
    private Date fechaDespacho;
    private Date fechaEntrega;
    private String ubicacionActual;

    // --- Pago ---
    private String estadoPago; // "PENDIENTE", "PAGADO", "RECHAZADO"
    private String metodoPago; // "EFECTIVO", "TARJETA_CREDITO", "TARJETA_DEBITO"
    private String tarjetaUltimos4;

    // --- Empleado Asignado ---
    private String empleadoAsignadoId;
    private String empleadoAsignadoNombre;

    // ===== CAMPOS PARA TRACKING =====
    
    // Dirección completa de entrega
    private String direccionEntrega;
    
    // Coordenadas de origen (bodega)
    private Double latitudOrigen;
    private Double longitudOrigen;
    
    // Coordenadas de destino (cliente)
    private Double latitudDestino;
    private Double longitudDestino;
    
    // Posición actual del delivery
    private Double latitudActual;
    private Double longitudActual;
    
    // Estimaciones de tiempo
    private Date horaEstimadaLlegada;
    private Integer tiempoEstimadoMinutos;
    private Double distanciaRestanteKm;
    
    // Ruta y progreso
    private String rutaActual; // JSON con puntos de la ruta
    private Integer progresoEntrega; // 0-100%
    
    // Historial de ubicaciones para tracking
    private List<UbicacionHistorial> historialUbicaciones;
    
    // Estado de simulación (para testing)
    private Boolean enSimulacion;
    private String estadoSimulacion; // "DETENIDA", "EN_CURSO", "COMPLETADA"

    // ===== CLASE INTERNA: UbicacionHistorial =====
    public static class UbicacionHistorial {
        private Double latitud;
        private Double longitud;
        private Date timestamp;
        private String descripcion;
        private Integer velocidadKmh; // Velocidad aproximada

        public UbicacionHistorial() {}

        public UbicacionHistorial(Double latitud, Double longitud, Date timestamp, String descripcion) {
            this.latitud = latitud;
            this.longitud = longitud;
            this.timestamp = timestamp;
            this.descripcion = descripcion;
        }

        // Getters y Setters
        public Double getLatitud() { return latitud; }
        public void setLatitud(Double latitud) { this.latitud = latitud; }

        public Double getLongitud() { return longitud; }
        public void setLongitud(Double longitud) { this.longitud = longitud; }

        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public Integer getVelocidadKmh() { return velocidadKmh; }
        public void setVelocidadKmh(Integer velocidadKmh) { this.velocidadKmh = velocidadKmh; }
    }

    // ===== CLASE INTERNA: ItemPedido =====
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

    // ===== CONSTRUCTOR =====
    public Pedido() {
        this.fecha = new Date();
        this.estado = "PENDIENTE";
        this.estadoPago = "PENDIENTE";
        this.estadoDespacho = "PENDIENTE";
        this.envio = 5.00;
        this.impuestos = 0.0;
        this.historialUbicaciones = new ArrayList<>();
        this.enSimulacion = false;
        this.estadoSimulacion = "DETENIDA";
        this.progresoEntrega = 0;
    }

    // ===== GETTERS Y SETTERS =====
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

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getIvaPorcentaje() { return ivaPorcentaje; }
    public void setIvaPorcentaje(double ivaPorcentaje) { this.ivaPorcentaje = ivaPorcentaje; }

    public double getIvaValor() { return ivaValor; }
    public void setIvaValor(double ivaValor) { this.ivaValor = ivaValor; }

    public double getEnvio() { return envio; }
    public void setEnvio(double envio) { this.envio = envio; }

    public double getImpuestos() { return impuestos; }
    public void setImpuestos(double impuestos) { this.impuestos = impuestos; }

    public double getTotalFinal() { return totalFinal; }
    public void setTotalFinal(double totalFinal) { this.totalFinal = totalFinal; }

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

    public String getEmpleadoAsignadoId() { return empleadoAsignadoId; }
    public void setEmpleadoAsignadoId(String empleadoAsignadoId) { this.empleadoAsignadoId = empleadoAsignadoId; }

    public String getEmpleadoAsignadoNombre() { return empleadoAsignadoNombre; }
    public void setEmpleadoAsignadoNombre(String empleadoAsignadoNombre) { this.empleadoAsignadoNombre = empleadoAsignadoNombre; }

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

    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }

    public Double getLatitudOrigen() { return latitudOrigen; }
    public void setLatitudOrigen(Double latitudOrigen) { this.latitudOrigen = latitudOrigen; }

    public Double getLongitudOrigen() { return longitudOrigen; }
    public void setLongitudOrigen(Double longitudOrigen) { this.longitudOrigen = longitudOrigen; }

    public Double getLatitudDestino() { return latitudDestino; }
    public void setLatitudDestino(Double latitudDestino) { this.latitudDestino = latitudDestino; }

    public Double getLongitudDestino() { return longitudDestino; }
    public void setLongitudDestino(Double longitudDestino) { this.longitudDestino = longitudDestino; }

    public Double getLatitudActual() { return latitudActual; }
    public void setLatitudActual(Double latitudActual) { this.latitudActual = latitudActual; }

    public Double getLongitudActual() { return longitudActual; }
    public void setLongitudActual(Double longitudActual) { this.longitudActual = longitudActual; }

    public Date getHoraEstimadaLlegada() { return horaEstimadaLlegada; }
    public void setHoraEstimadaLlegada(Date horaEstimadaLlegada) { this.horaEstimadaLlegada = horaEstimadaLlegada; }

    public Integer getTiempoEstimadoMinutos() { return tiempoEstimadoMinutos; }
    public void setTiempoEstimadoMinutos(Integer tiempoEstimadoMinutos) { this.tiempoEstimadoMinutos = tiempoEstimadoMinutos; }

    public Double getDistanciaRestanteKm() { return distanciaRestanteKm; }
    public void setDistanciaRestanteKm(Double distanciaRestanteKm) { this.distanciaRestanteKm = distanciaRestanteKm; }

    public String getRutaActual() { return rutaActual; }
    public void setRutaActual(String rutaActual) { this.rutaActual = rutaActual; }

    public Integer getProgresoEntrega() { return progresoEntrega; }
    public void setProgresoEntrega(Integer progresoEntrega) { this.progresoEntrega = progresoEntrega; }

    public List<UbicacionHistorial> getHistorialUbicaciones() { return historialUbicaciones; }
    public void setHistorialUbicaciones(List<UbicacionHistorial> historialUbicaciones) { 
        this.historialUbicaciones = historialUbicaciones; 
    }

    public Boolean getEnSimulacion() { return enSimulacion; }
    public void setEnSimulacion(Boolean enSimulacion) { this.enSimulacion = enSimulacion; }

    public String getEstadoSimulacion() { return estadoSimulacion; }
    public void setEstadoSimulacion(String estadoSimulacion) { this.estadoSimulacion = estadoSimulacion; }
}