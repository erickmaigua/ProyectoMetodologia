package com.zapateria.services;

import com.zapateria.models.NodoTransito;
import com.zapateria.models.Pedido;
import com.zapateria.repositories.NodoTransitoRepository;
import com.zapateria.repositories.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Historia PDLS-36: Grafo dinámico de rutas de entrega.
 * Permite visualizar tiempo de tránsito entre nodos y registrar retrasos.
 */
@Service
public class TransitoService {

    private static final Logger logger = LoggerFactory.getLogger(TransitoService.class);

    private final NodoTransitoRepository nodoRepository;
    private final PedidoRepository       pedidoRepository;

    public TransitoService(NodoTransitoRepository nodoRepository, PedidoRepository pedidoRepository) {
        this.nodoRepository   = nodoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<NodoTransito> obtenerGrafoCompleto() {
        return nodoRepository.findAll();
    }

    /** Retorna todos los nodos y sus tiempos para el frontend (grafo D3/vis) */
    public Map<String, Object> obtenerGrafoParaVis() {
        List<NodoTransito> nodos = nodoRepository.findAll();
        List<Map<String, Object>> vertices = new ArrayList<>();
        List<Map<String, Object>> aristas  = new ArrayList<>();

        for (NodoTransito nodo : nodos) {
            Map<String, Object> v = new HashMap<>();
            v.put("id",     nodo.getId());
            v.put("label",  nodo.getNombre());
            v.put("ciudad", nodo.getCiudad());
            v.put("lat",    nodo.getLatitud());
            v.put("lon",    nodo.getLongitud());
            v.put("retrasos", nodo.getRetrasosCausas());
            vertices.add(v);

            if (nodo.getTiemposEstimados() != null) {
                nodo.getTiemposEstimados().forEach((destino, tiempo) -> {
                    Map<String, Object> a = new HashMap<>();
                    a.put("from",   nodo.getId());
                    a.put("to",     destino);
                    a.put("tiempo", tiempo);
                    a.put("label",  tiempo + "h");
                    aristas.add(a);
                });
            }
        }

        Map<String, Object> grafo = new HashMap<>();
        grafo.put("nodos",   vertices);
        grafo.put("aristas", aristas);
        return grafo;
    }

    /** Obtiene info de tránsito para un pedido específico */
    public Map<String, Object> obtenerTransitoPedido(String pedidoId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        Map<String, Object> resultado = new HashMap<>();

        if (!pedidoOpt.isPresent()) {
            resultado.put("error", "Pedido no encontrado");
            return resultado;
        }

        Pedido pedido = pedidoOpt.get();
        resultado.put("pedidoId",          pedido.getId());
        resultado.put("estadoDespacho",    pedido.getEstadoDespacho());
        resultado.put("ubicacionActual",   pedido.getUbicacionActual());
        resultado.put("codigoSeguimiento", pedido.getCodigoSeguimiento());
        resultado.put("fechaDespacho",     pedido.getFechaDespacho());
        resultado.put("fechaEntrega",      pedido.getFechaEntrega());
        resultado.put("tiempoEstimadoHoras", 48); // default — se puede calcular según nodo
        return resultado;
    }

    public NodoTransito crearNodo(NodoTransito nodo)   { return nodoRepository.save(nodo); }
    public NodoTransito actualizarNodo(NodoTransito n) { return nodoRepository.save(n); }

    /** Registra una causa de retraso en un nodo */
    public NodoTransito registrarRetraso(String nodoId, String causa) {
        NodoTransito nodo = nodoRepository.findById(nodoId)
                .orElseThrow(() -> new IllegalArgumentException("Nodo no encontrado: " + nodoId));
        List<String> causas = nodo.getRetrasosCausas();
        if (causas == null) causas = new ArrayList<>();
        causas.add(causa);
        nodo.setRetrasosCausas(causas);
        logger.info("Retraso registrado en nodo {}: {}", nodoId, causa);
        return nodoRepository.save(nodo);
    }

    public void eliminarNodo(String id) { nodoRepository.deleteById(id); }
}
