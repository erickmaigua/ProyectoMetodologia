package com.zapateria.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zapateria.models.Pedido;
import com.zapateria.repositories.PedidoRepository;

@Service
public class TrackingService {

    @Autowired
    private PedidoRepository pedidoRepository;

    // ===== CONSTANTES =====
    private static final double VELOCIDAD_PROMEDIO_KMH = 40.0; // 40 km/h promedio en ciudad
    private static final int PUNTOS_RUTA = 12; // Número de puntos intermedios en la ruta simulada
    private static final int DELAY_SIMULACION_MS = 3000; // 3 segundos entre cada actualización

    // ===== CLASE INTERNA: Punto (coordenadas) =====
    public static class Punto {
        public double lat;
        public double lng;
        public String descripcion;

        public Punto(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public Punto(double lat, double lng, String descripcion) {
            this.lat = lat;
            this.lng = lng;
            this.descripcion = descripcion;
        }
    }

    /**
     * Calcular distancia entre dos puntos usando la fórmula de Haversine
     * @return distancia en kilómetros
     */
    public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int RADIO_TIERRA_KM = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA_KM * c;
    }

    /**
     * Calcular tiempo estimado de llegada basado en la distancia
     * @param distanciaKm distancia en kilómetros
     * @return tiempo estimado en minutos
     */
    public int calcularTiempoEstimado(double distanciaKm) {
        // Tiempo = Distancia / Velocidad
        double tiempoHoras = distanciaKm / VELOCIDAD_PROMEDIO_KMH;
        int tiempoMinutos = (int) Math.ceil(tiempoHoras * 60);
        
        // Agregar un margen de 5-10 minutos por tráfico
        tiempoMinutos += (int) (Math.random() * 5) + 5;
        
        return tiempoMinutos;
    }

    /**
     * Generar una ruta simulada con puntos intermedios entre origen y destino
     */
    public List<Punto> generarRutaSimulada(double latOrigen, double lonOrigen, 
                                           double latDestino, double lonDestino) {
        List<Punto> ruta = new ArrayList<>();

        // Agregar punto de origen
        ruta.add(new Punto(latOrigen, lonOrigen, "Bodega - Punto de partida"));

        // Generar puntos intermedios
        for (int i = 1; i <= PUNTOS_RUTA; i++) {
            double progreso = (double) i / (PUNTOS_RUTA + 1);

            // Interpolación lineal básica
            double lat = latOrigen + (latDestino - latOrigen) * progreso;
            double lon = lonOrigen + (lonDestino - lonOrigen) * progreso;

            // Agregar algo de variación aleatoria para simular calles reales
            double variacionLat = (Math.random() - 0.5) * 0.002; // ~200m de variación
            double variacionLon = (Math.random() - 0.5) * 0.002;

            lat += variacionLat;
            lon += variacionLon;

            // Descripción del punto
            String descripcion = "En ruta - " + (int) (progreso * 100) + "% completado";
            if (progreso < 0.3) {
                descripcion = "Saliendo de la zona de bodega";
            } else if (progreso < 0.7) {
                descripcion = "En camino hacia el destino";
            } else {
                descripcion = "Aproximándose al destino";
            }

            ruta.add(new Punto(lat, lon, descripcion));
        }

        // Agregar punto de destino
        ruta.add(new Punto(latDestino, lonDestino, "Destino - Dirección del cliente"));

        return ruta;
    }

    /**
     * Iniciar el tracking de un pedido
     * Configura las coordenadas, calcula la ruta y el tiempo estimado
     */
    public Map<String, Object> iniciarTracking(String pedidoId, Double latOrigen, Double lonOrigen,
                                                Double latDestino, Double lonDestino, String direccionEntrega) {
        Map<String, Object> response = new HashMap<>();

        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        if (pedido == null) {
            response.put("success", false);
            response.put("mensaje", "Pedido no encontrado");
            return response;
        }

        // Guardar coordenadas
        pedido.setLatitudOrigen(latOrigen);
        pedido.setLongitudOrigen(lonOrigen);
        pedido.setLatitudDestino(latDestino);
        pedido.setLongitudDestino(lonDestino);
        pedido.setDireccionEntrega(direccionEntrega);

        // Posición inicial = origen
        pedido.setLatitudActual(latOrigen);
        pedido.setLongitudActual(lonOrigen);

        // Calcular distancia total
        double distanciaTotal = calcularDistancia(latOrigen, lonOrigen, latDestino, lonDestino);
        pedido.setDistanciaRestanteKm(distanciaTotal);

        // Calcular tiempo estimado
        int tiempoEstimado = calcularTiempoEstimado(distanciaTotal);
        pedido.setTiempoEstimadoMinutos(tiempoEstimado);

        // Calcular hora estimada de llegada
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, tiempoEstimado);
        pedido.setHoraEstimadaLlegada(cal.getTime());

        // Cambiar estado
        pedido.setEstado("EN_CAMINO");
        pedido.setEstadoDespacho("EN_CAMINO");
        pedido.setFechaDespacho(new Date());
        pedido.setUbicacionActual("En ruta hacia el cliente");

        // Limpiar historial previo y agregar punto inicial
        pedido.getHistorialUbicaciones().clear();
        pedido.getHistorialUbicaciones().add(
            new Pedido.UbicacionHistorial(latOrigen, lonOrigen, new Date(), "Salió de bodega")
        );

        // Progreso inicial
        pedido.setProgresoEntrega(0);

        pedidoRepository.save(pedido);

        response.put("success", true);
        response.put("mensaje", "Tracking iniciado correctamente");
        response.put("distanciaKm", Math.round(distanciaTotal * 100.0) / 100.0);
        response.put("tiempoEstimadoMin", tiempoEstimado);
        response.put("horaEstimadaLlegada", pedido.getHoraEstimadaLlegada());

        return response;
    }

    /**
     * Actualizar la ubicación actual del delivery
     * Recalcula tiempo estimado basado en la nueva posición
     */
    public Map<String, Object> actualizarUbicacion(String pedidoId, Double nuevaLat, Double nuevaLon) {
        Map<String, Object> response = new HashMap<>();

        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        if (pedido == null) {
            response.put("success", false);
            response.put("mensaje", "Pedido no encontrado");
            return response;
        }

        // Actualizar posición actual
        pedido.setLatitudActual(nuevaLat);
        pedido.setLongitudActual(nuevaLon);

        // Recalcular distancia restante
        double distanciaRestante = calcularDistancia(
            nuevaLat, nuevaLon,
            pedido.getLatitudDestino(), pedido.getLongitudDestino()
        );
        pedido.setDistanciaRestanteKm(distanciaRestante);

        // Recalcular tiempo estimado
        int nuevoTiempoEstimado = calcularTiempoEstimado(distanciaRestante);
        pedido.setTiempoEstimadoMinutos(nuevoTiempoEstimado);

        // Recalcular hora estimada de llegada
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, nuevoTiempoEstimado);
        pedido.setHoraEstimadaLlegada(cal.getTime());

        // Calcular progreso (basado en distancia recorrida)
        double distanciaTotal = calcularDistancia(
            pedido.getLatitudOrigen(), pedido.getLongitudOrigen(),
            pedido.getLatitudDestino(), pedido.getLongitudDestino()
        );
        double distanciaRecorrida = distanciaTotal - distanciaRestante;
        int progreso = (int) ((distanciaRecorrida / distanciaTotal) * 100);
        pedido.setProgresoEntrega(Math.min(progreso, 100));

        // Agregar al historial
        String descripcion = "En ruta - " + progreso + "% completado";
        pedido.getHistorialUbicaciones().add(
            new Pedido.UbicacionHistorial(nuevaLat, nuevaLon, new Date(), descripcion)
        );

        // Si llegó al destino (menos de 100 metros)
        if (distanciaRestante < 0.1) {
            pedido.setEstado("ENTREGADO");
            pedido.setEstadoDespacho("ENTREGADO");
            pedido.setFechaEntrega(new Date());
            pedido.setUbicacionActual("Entregado al cliente");
            pedido.setProgresoEntrega(100);
        }

        pedidoRepository.save(pedido);

        response.put("success", true);
        response.put("distanciaRestanteKm", Math.round(distanciaRestante * 100.0) / 100.0);
        response.put("tiempoEstimadoMin", nuevoTiempoEstimado);
        response.put("progreso", pedido.getProgresoEntrega());
        response.put("horaEstimadaLlegada", pedido.getHoraEstimadaLlegada());

        return response;
    }

    /**
     * Obtener el tracking actual de un pedido
     */
    public Map<String, Object> obtenerTrackingActual(String pedidoId) {
        Map<String, Object> response = new HashMap<>();

        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        if (pedido == null) {
            response.put("success", false);
            response.put("mensaje", "Pedido no encontrado");
            return response;
        }

        response.put("success", true);
        response.put("estado", pedido.getEstado());
        response.put("estadoDespacho", pedido.getEstadoDespacho());
        response.put("latitudActual", pedido.getLatitudActual());
        response.put("longitudActual", pedido.getLongitudActual());
        response.put("latitudOrigen", pedido.getLatitudOrigen());
        response.put("longitudOrigen", pedido.getLongitudOrigen());
        response.put("latitudDestino", pedido.getLatitudDestino());
        response.put("longitudDestino", pedido.getLongitudDestino());
        response.put("distanciaRestanteKm", pedido.getDistanciaRestanteKm());
        response.put("tiempoEstimadoMin", pedido.getTiempoEstimadoMinutos());
        response.put("horaEstimadaLlegada", pedido.getHoraEstimadaLlegada());
        response.put("progreso", pedido.getProgresoEntrega());
        response.put("ubicacionActual", pedido.getUbicacionActual());
        response.put("historial", pedido.getHistorialUbicaciones());
        response.put("enSimulacion", pedido.getEnSimulacion());
        response.put("estadoSimulacion", pedido.getEstadoSimulacion());

        return response;
    }

    /**
     * Simular la entrega completa de un pedido (para testing)
     * Esta función corre en un thread separado y actualiza la posición cada X segundos
     */
    public Map<String, Object> simularEntrega(String pedidoId) {
        Map<String, Object> response = new HashMap<>();

        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        if (pedido == null) {
            response.put("success", false);
            response.put("mensaje", "Pedido no encontrado");
            return response;
        }

        // Validar que tenga coordenadas configuradas
        if (pedido.getLatitudOrigen() == null || pedido.getLatitudDestino() == null) {
            response.put("success", false);
            response.put("mensaje", "El pedido no tiene coordenadas configuradas. Inicie el tracking primero.");
            return response;
        }

        // Validar que no esté ya en simulación
        if (Boolean.TRUE.equals(pedido.getEnSimulacion())) {
            response.put("success", false);
            response.put("mensaje", "Ya hay una simulación en curso para este pedido");
            return response;
        }

        // Marcar como en simulación
        pedido.setEnSimulacion(true);
        pedido.setEstadoSimulacion("EN_CURSO");
        pedidoRepository.save(pedido);

        // Generar ruta simulada
        List<Punto> ruta = generarRutaSimulada(
            pedido.getLatitudOrigen(), pedido.getLongitudOrigen(),
            pedido.getLatitudDestino(), pedido.getLongitudDestino()
        );

        // Ejecutar simulación en un thread separado
        new Thread(() -> {
            try {
                for (int i = 0; i < ruta.size(); i++) {
                    Thread.sleep(DELAY_SIMULACION_MS);

                    Punto puntoActual = ruta.get(i);

                    // Recargar pedido por si cambió
                    Pedido pedidoActual = pedidoRepository.findById(pedidoId).orElse(null);
                    if (pedidoActual == null) break;

                    // Actualizar ubicación
                    actualizarUbicacion(pedidoId, puntoActual.lat, puntoActual.lng);

                    System.out.println("Simulación [" + pedidoId + "] - Punto " + (i + 1) + "/" + ruta.size() 
                        + " - Progreso: " + pedidoActual.getProgresoEntrega() + "%");
                }

                // Marcar simulación como completada
                Pedido pedidoFinal = pedidoRepository.findById(pedidoId).orElse(null);
                if (pedidoFinal != null) {
                    pedidoFinal.setEnSimulacion(false);
                    pedidoFinal.setEstadoSimulacion("COMPLETADA");
                    pedidoRepository.save(pedidoFinal);
                }

                System.out.println("Simulación completada para pedido: " + pedidoId);

            } catch (InterruptedException e) {
                System.err.println("Simulación interrumpida para pedido: " + pedidoId);
                
                // Marcar como detenida
                Pedido pedidoError = pedidoRepository.findById(pedidoId).orElse(null);
                if (pedidoError != null) {
                    pedidoError.setEnSimulacion(false);
                    pedidoError.setEstadoSimulacion("DETENIDA");
                    pedidoRepository.save(pedidoError);
                }
            }
        }).start();

        response.put("success", true);
        response.put("mensaje", "Simulación iniciada. El pedido se actualizará automáticamente.");
        response.put("puntosRuta", ruta.size());
        response.put("tiempoEstimadoSimulacion", (ruta.size() * DELAY_SIMULACION_MS) / 1000 + " segundos");

        return response;
    }

    /**
     * Detener una simulación en curso
     */
    public Map<String, Object> detenerSimulacion(String pedidoId) {
        Map<String, Object> response = new HashMap<>();

        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        if (pedido == null) {
            response.put("success", false);
            response.put("mensaje", "Pedido no encontrado");
            return response;
        }

        pedido.setEnSimulacion(false);
        pedido.setEstadoSimulacion("DETENIDA");
        pedidoRepository.save(pedido);

        response.put("success", true);
        response.put("mensaje", "Simulación detenida");
        return response;
    }
}