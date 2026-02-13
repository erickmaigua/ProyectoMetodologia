package com.zapateria.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Historia PDLS-37: Casilla de reportes integrada.
 * El cliente puede reportar envíos incorrectos, productos defectuosos
 * o inconvenientes y solicitar devolución.
 */
@Document(collection = "reportes")
public class Reporte {
    @Id
    private String id;
    private String pedidoId;
    private String usuarioId;
    /** "ENVIO_INCORRECTO", "PRODUCTO_DEFECTUOSO", "INCONVENIENTE", "DEVOLUCION" */
    private String tipo;
    private String descripcion;
    private List<String> imagenesUrl;
    private Date fechaReporte;
    private Date fechaActualizacion;
    /** "ABIERTA", "EN_REVISION", "RESUELTA", "RECHAZADA" */
    private String estado;
    private String respuestaAdmin;
    private String adminId;
    private boolean solicitaDevolucion;

    public Reporte() {
        this.fechaReporte  = new Date();
        this.estado        = "ABIERTA";
        this.imagenesUrl   = new ArrayList<>();
        this.solicitaDevolucion = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPedidoId() { return pedidoId; }
    public void setPedidoId(String pedidoId) { this.pedidoId = pedidoId; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<String> getImagenesUrl() { return imagenesUrl; }
    public void setImagenesUrl(List<String> imagenesUrl) { this.imagenesUrl = imagenesUrl; }
    public Date getFechaReporte() { return fechaReporte != null ? new Date(fechaReporte.getTime()) : null; }
    public void setFechaReporte(Date f) { this.fechaReporte = f != null ? new Date(f.getTime()) : null; }
    public Date getFechaActualizacion() { return fechaActualizacion != null ? new Date(fechaActualizacion.getTime()) : null; }
    public void setFechaActualizacion(Date f) { this.fechaActualizacion = f != null ? new Date(f.getTime()) : null; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getRespuestaAdmin() { return respuestaAdmin; }
    public void setRespuestaAdmin(String respuestaAdmin) { this.respuestaAdmin = respuestaAdmin; }
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public boolean isSolicitaDevolucion() { return solicitaDevolucion; }
    public void setSolicitaDevolucion(boolean solicitaDevolucion) { this.solicitaDevolucion = solicitaDevolucion; }
}
