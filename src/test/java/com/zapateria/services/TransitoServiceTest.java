package com.zapateria.services;

import com.zapateria.models.NodoTransito;
import com.zapateria.models.Pedido;
import com.zapateria.repositories.NodoTransitoRepository;
import com.zapateria.repositories.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransitoServiceTest {

    @Mock
    private NodoTransitoRepository nodoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private TransitoService transitoService;

    private NodoTransito nodoOrigen;
    private NodoTransito nodoDestino;
    private Pedido pedidoTest;

    @BeforeEach
    void setUp() {
        // Nodo origen
        nodoOrigen = new NodoTransito();
        nodoOrigen.setId("nodo1");
        nodoOrigen.setNombre("Centro Distribución Norte");
        nodoOrigen.setCiudad("Quito");
        nodoOrigen.setLatitud(-0.1807);
        nodoOrigen.setLongitud(-78.4678);
        
        Map<String, Double> tiempos = new HashMap<>();
        tiempos.put("nodo2", 3.5);
        nodoOrigen.setTiemposEstimados(tiempos);
        
        List<String> retrasos = new ArrayList<>();
        retrasos.add("Tráfico pesado");
        nodoOrigen.setRetrasosCausas(retrasos);

        // Nodo destino
        nodoDestino = new NodoTransito();
        nodoDestino.setId("nodo2");
        nodoDestino.setNombre("Centro Distribución Sur");
        nodoDestino.setCiudad("Guayaquil");
        nodoDestino.setLatitud(-2.1894);
        nodoDestino.setLongitud(-79.8884);

        // Pedido
        pedidoTest = new Pedido();
        pedidoTest.setId("pedido123");
        pedidoTest.setEstadoDespacho("EN_TRANSITO");
        pedidoTest.setUbicacionActual("nodo1");
        pedidoTest.setCodigoSeguimiento("TRACK-123");
        pedidoTest.setFechaDespacho(new Date());
    }

    @Test
    void testObtenerGrafoCompleto() {
        // Given
        List<NodoTransito> nodos = Arrays.asList(nodoOrigen, nodoDestino);
        when(nodoRepository.findAll()).thenReturn(nodos);

        // When
        List<NodoTransito> resultado = transitoService.obtenerGrafoCompleto();

        // Then
        assertEquals(2, resultado.size());
        verify(nodoRepository, times(1)).findAll();
    }

    @Test
    void testObtenerGrafoParaVis() {
        // Given
        List<NodoTransito> nodos = Arrays.asList(nodoOrigen, nodoDestino);
        when(nodoRepository.findAll()).thenReturn(nodos);

        // When
        Map<String, Object> resultado = transitoService.obtenerGrafoParaVis();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("nodos"));
        assertTrue(resultado.containsKey("aristas"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> vertices = (List<Map<String, Object>>) resultado.get("nodos");
        assertEquals(2, vertices.size());
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> aristas = (List<Map<String, Object>>) resultado.get("aristas");
        assertEquals(1, aristas.size()); // Solo nodo1 tiene conexión a nodo2
    }

    @Test
    void testObtenerGrafoParaVisConNodosSinTiempos() {
        // Given
        nodoDestino.setTiemposEstimados(null);
        List<NodoTransito> nodos = Arrays.asList(nodoDestino);
        when(nodoRepository.findAll()).thenReturn(nodos);

        // When
        Map<String, Object> resultado = transitoService.obtenerGrafoParaVis();

        // Then
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> aristas = (List<Map<String, Object>>) resultado.get("aristas");
        assertEquals(0, aristas.size());
    }

    @Test
    void testObtenerGrafoParaVisVerificaEstructura() {
        // Given
        when(nodoRepository.findAll()).thenReturn(Arrays.asList(nodoOrigen));

        // When
        Map<String, Object> resultado = transitoService.obtenerGrafoParaVis();

        // Then
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> vertices = (List<Map<String, Object>>) resultado.get("nodos");
        Map<String, Object> nodo = vertices.get(0);
        
        assertEquals("nodo1", nodo.get("id"));
        assertEquals("Centro Distribución Norte", nodo.get("label"));
        assertEquals("Quito", nodo.get("ciudad"));
        assertEquals(-0.1807, nodo.get("lat"));
        assertEquals(-78.4678, nodo.get("lon"));
        assertNotNull(nodo.get("retrasos"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> aristas = (List<Map<String, Object>>) resultado.get("aristas");
        Map<String, Object> arista = aristas.get(0);
        
        assertEquals("nodo1", arista.get("from"));
        assertEquals("nodo2", arista.get("to"));
        assertEquals(3.5, arista.get("tiempo"));
        assertEquals("3.5h", arista.get("label"));
    }

    @Test
    void testObtenerTransitoPedidoExitoso() {
        // Given
        when(pedidoRepository.findById("pedido123")).thenReturn(Optional.of(pedidoTest));

        // When
        Map<String, Object> resultado = transitoService.obtenerTransitoPedido("pedido123");

        // Then
        assertNotNull(resultado);
        assertFalse(resultado.containsKey("error"));
        assertEquals("pedido123", resultado.get("pedidoId"));
        assertEquals("EN_TRANSITO", resultado.get("estadoDespacho"));
        assertEquals("nodo1", resultado.get("ubicacionActual"));
        assertEquals("TRACK-123", resultado.get("codigoSeguimiento"));
        assertEquals(48, resultado.get("tiempoEstimadoHoras"));
        assertNotNull(resultado.get("fechaDespacho"));
    }

    @Test
    void testObtenerTransitoPedidoNoEncontrado() {
        // Given
        when(pedidoRepository.findById("inexistente")).thenReturn(Optional.empty());

        // When
        Map<String, Object> resultado = transitoService.obtenerTransitoPedido("inexistente");

        // Then
        assertTrue(resultado.containsKey("error"));
        assertEquals("Pedido no encontrado", resultado.get("error"));
    }

    @Test
    void testCrearNodo() {
        // Given
        NodoTransito nuevoNodo = new NodoTransito();
        nuevoNodo.setNombre("Nuevo Nodo");
        when(nodoRepository.save(any(NodoTransito.class))).thenReturn(nuevoNodo);

        // When
        NodoTransito resultado = transitoService.crearNodo(nuevoNodo);

        // Then
        assertNotNull(resultado);
        assertEquals("Nuevo Nodo", resultado.getNombre());
        verify(nodoRepository, times(1)).save(nuevoNodo);
    }

    @Test
    void testActualizarNodo() {
        // Given
        nodoOrigen.setNombre("Nombre Actualizado");
        when(nodoRepository.save(any(NodoTransito.class))).thenReturn(nodoOrigen);

        // When
        NodoTransito resultado = transitoService.actualizarNodo(nodoOrigen);

        // Then
        assertEquals("Nombre Actualizado", resultado.getNombre());
        verify(nodoRepository, times(1)).save(nodoOrigen);
    }

    @Test
    void testRegistrarRetrasoExitoso() {
        // Given
        when(nodoRepository.findById("nodo1")).thenReturn(Optional.of(nodoOrigen));
        when(nodoRepository.save(any(NodoTransito.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        NodoTransito resultado = transitoService.registrarRetraso("nodo1", "Condiciones climáticas");

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.getRetrasosCausas().contains("Condiciones climáticas"));
        assertEquals(2, resultado.getRetrasosCausas().size());
        verify(nodoRepository, times(1)).save(any(NodoTransito.class));
    }

    @Test
    void testRegistrarRetrasoEnNodoSinRetrasosPrevios() {
        // Given
        nodoDestino.setRetrasosCausas(null);
        when(nodoRepository.findById("nodo2")).thenReturn(Optional.of(nodoDestino));
        when(nodoRepository.save(any(NodoTransito.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        NodoTransito resultado = transitoService.registrarRetraso("nodo2", "Primer retraso");

        // Then
        assertNotNull(resultado.getRetrasosCausas());
        assertEquals(1, resultado.getRetrasosCausas().size());
        assertTrue(resultado.getRetrasosCausas().contains("Primer retraso"));
    }

    @Test
    void testRegistrarRetrasoNodoNoEncontrado() {
        // Given
        when(nodoRepository.findById("inexistente")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            transitoService.registrarRetraso("inexistente", "Retraso");
        });
        verify(nodoRepository, never()).save(any(NodoTransito.class));
    }

    @Test
    void testEliminarNodo() {
        // Given
        doNothing().when(nodoRepository).deleteById("nodo1");

        // When
        transitoService.eliminarNodo("nodo1");

        // Then
        verify(nodoRepository, times(1)).deleteById("nodo1");
    }

    @Test
    void testObtenerGrafoParaVisConMultiplesConexiones() {
        // Given
        Map<String, Double> tiemposMultiples = new HashMap<>();
        tiemposMultiples.put("nodo2", 3.5);
        tiemposMultiples.put("nodo3", 5.0);
        nodoOrigen.setTiemposEstimados(tiemposMultiples);
        
        when(nodoRepository.findAll()).thenReturn(Arrays.asList(nodoOrigen));

        // When
        Map<String, Object> resultado = transitoService.obtenerGrafoParaVis();

        // Then
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> aristas = (List<Map<String, Object>>) resultado.get("aristas");
        assertEquals(2, aristas.size());
    }

    // ═══════════════════════════════════════════════
    // TESTS ADICIONALES — cobertura extra
    // ═══════════════════════════════════════════════

    @Test
    void testObtenerGrafoCompleto_ListaVacia() {
        // Given
        when(nodoRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<NodoTransito> resultado = transitoService.obtenerGrafoCompleto();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testObtenerGrafoParaVis_NodoSinTiempos() {
        // Given — nodo sin tiempos estimados no debe generar aristas ni NPE
        NodoTransito nodoSinTiempos = new NodoTransito();
        nodoSinTiempos.setId("nodo3"); nodoSinTiempos.setNombre("Nodo sin tiempos");
        nodoSinTiempos.setTiemposEstimados(null);

        when(nodoRepository.findAll()).thenReturn(Arrays.asList(nodoSinTiempos));

        // When
        Map<String, Object> resultado = transitoService.obtenerGrafoParaVis();

        // Then
        assertNotNull(resultado);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> vertices = (List<Map<String, Object>>) resultado.get("nodos");
        assertEquals(1, vertices.size());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> aristas = (List<Map<String, Object>>) resultado.get("aristas");
        assertTrue(aristas.isEmpty()); // sin tiempos = sin aristas
    }

    @Test
    void testObtenerGrafoParaVis_IncluyeCiudadYCoordenadas() {
        // Given
        when(nodoRepository.findAll()).thenReturn(Arrays.asList(nodoOrigen));

        // When
        Map<String, Object> resultado = transitoService.obtenerGrafoParaVis();

        // Then
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> vertices = (List<Map<String, Object>>) resultado.get("nodos");
        Map<String, Object> v = vertices.get(0);
        assertEquals("nodo1",                 v.get("id"));
        assertEquals("Centro Distribución Norte", v.get("label"));
        assertEquals("Quito",                 v.get("ciudad"));
        assertEquals(-0.1807,  (Double) v.get("lat"),  0.0001);
        assertEquals(-78.4678, (Double) v.get("lon"),  0.0001);
    }

    @Test
    void testRegistrarRetraso_ListaRetrasosLlenaCorrectamente() {
        // Given — nodo ya tiene 1 retraso
        when(nodoRepository.findById("nodo1")).thenReturn(Optional.of(nodoOrigen));
        when(nodoRepository.save(any(NodoTransito.class))).thenAnswer(i -> i.getArguments()[0]);

        // When — se agrega un segundo retraso
        NodoTransito resultado = transitoService.registrarRetraso("nodo1", "Huelga de transportistas");

        // Then — debe tener el original + el nuevo
        assertEquals(2, resultado.getRetrasosCausas().size());
        assertTrue(resultado.getRetrasosCausas().contains("Tráfico pesado"));
        assertTrue(resultado.getRetrasosCausas().contains("Huelga de transportistas"));
    }

    @Test
    void testCrearNodo_InicializaCamposBase() {
        // Given
        NodoTransito nodo = new NodoTransito();
        nodo.setNombre("Nodo Test");
        nodo.setCiudad("Cuenca");
        nodo.setLatitud(-2.9001);
        nodo.setLongitud(-79.0059);
        when(nodoRepository.save(any(NodoTransito.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        NodoTransito resultado = transitoService.crearNodo(nodo);

        // Then
        assertEquals("Nodo Test", resultado.getNombre());
        assertEquals("Cuenca",    resultado.getCiudad());
        verify(nodoRepository, times(1)).save(nodo);
    }

    @Test
    void testEliminarNodo_LlamadaCorrecta() {
        // Given
        doNothing().when(nodoRepository).deleteById("nodo1");

        // When
        transitoService.eliminarNodo("nodo1");

        // Then
        verify(nodoRepository, times(1)).deleteById("nodo1");
        verifyNoMoreInteractions(nodoRepository);
    }

    @Test
    void testObtenerTransitoPedido_ConSeguimiento() {
        // Given
        when(pedidoRepository.findById("pedido123")).thenReturn(Optional.of(pedidoTest));

        // When
        Map<String, Object> resultado = transitoService.obtenerTransitoPedido("pedido123");

        // Then
        assertFalse(resultado.containsKey("error"));
        assertEquals("pedido123", resultado.get("pedidoId"));
        assertEquals("EN_TRANSITO", resultado.get("estadoDespacho"));
    }
}
