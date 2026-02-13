package com.zapateria.services;

import com.zapateria.models.Reporte;
import com.zapateria.repositories.PedidoRepository;
import com.zapateria.repositories.ReporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Historia PDLS-37: Servicio de reportes de problemas con pedidos.
 */
@Service
public class ReporteService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteService.class);

    private final ReporteRepository reporteRepository;
    private final PedidoRepository  pedidoRepository;

    public ReporteService(ReporteRepository reporteRepository, PedidoRepository pedidoRepository) {
        this.reporteRepository = reporteRepository;
        this.pedidoRepository  = pedidoRepository;
    }

    public Reporte crear(Reporte reporte) {
        if (reporte.getPedidoId() == null || !pedidoRepository.existsById(reporte.getPedidoId())) {
            throw new IllegalArgumentException("El pedido indicado no existe");
        }
        if (reporte.getFechaReporte() == null) reporte.setFechaReporte(new Date());
        if (reporte.getEstado() == null)        reporte.setEstado("ABIERTA");
        Reporte guardado = reporteRepository.save(reporte);
        logger.info("Reporte creado: {} para pedido {}", guardado.getId(), guardado.getPedidoId());
        return guardado;
    }

    public List<Reporte> findAll()                          { return reporteRepository.findAll(); }
    public Optional<Reporte> findById(String id)            { return reporteRepository.findById(id); }
    public List<Reporte> findByPedidoId(String pedidoId)    { return reporteRepository.findByPedidoId(pedidoId); }
    public List<Reporte> findByUsuarioId(String usuarioId)  { return reporteRepository.findByUsuarioId(usuarioId); }
    public List<Reporte> findByEstado(String estado)        { return reporteRepository.findByEstado(estado); }
    public long contarPendientes()                          { return reporteRepository.countByEstado("ABIERTA"); }

    public Reporte actualizarEstado(String id, String nuevoEstado, String respuesta, String adminId) {
        Reporte r = reporteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado: " + id));
        r.setEstado(nuevoEstado);
        r.setFechaActualizacion(new Date());
        if (respuesta != null) r.setRespuestaAdmin(respuesta);
        if (adminId   != null) r.setAdminId(adminId);
        return reporteRepository.save(r);
    }

    public void delete(String id) { reporteRepository.deleteById(id); }
}
