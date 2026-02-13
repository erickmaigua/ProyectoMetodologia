package com.zapateria.controllers;

import com.zapateria.models.Pedido;
import com.zapateria.repositories.PedidoRepository;
import com.zapateria.services.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private Pedido pedidoTest;

    @BeforeEach
    void setUp() {
        pedidoTest = new Pedido();
        pedidoTest.setId("pedido123");
        pedidoTest.setNumeroPedido("PED-123");
        pedidoTest.setClienteId("cliente123");
        pedidoTest.setClienteNombre("Test Cliente");
    }

    @Test
    void testListarPedidos() {
        // Given
        List<Pedido> pedidos = Arrays.asList(pedidoTest);
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        // When
        ResponseEntity<List<Pedido>> response = pedidoController.listarPedidos();

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPedidoExistente() {
        // Given
        when(pedidoRepository.findById("pedido123")).thenReturn(Optional.of(pedidoTest));

        // When
        ResponseEntity<?> response = pedidoController.obtenerPedido("pedido123");

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(pedidoRepository, times(1)).findById("pedido123");
    }

    @Test
    void testObtenerPedidoNoExistente() {
        // Given
        when(pedidoRepository.findById("pedido999")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = pedidoController.obtenerPedido("pedido999");

        // Then
        assertEquals(404, response.getStatusCodeValue());
        verify(pedidoRepository, times(1)).findById("pedido999");
    }

    @Test
    void testRegistrarQueja() {
        // Given
        Map<String, Object> quejaData = new HashMap<>();
        quejaData.put("motivo", "Producto defectuoso");
        quejaData.put("detalle", "Descripción del problema");
        quejaData.put("solicitaDevolucion", true);

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("success", true);
        serviceResponse.put("mensaje", "Queja registrada");

        when(pedidoService.registrarQueja(anyString(), anyString(), anyString(), anyBoolean()))
                .thenReturn(serviceResponse);

        // When
        ResponseEntity<Map<String, Object>> response = pedidoController.registrarQueja("pedido123", quejaData);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue((Boolean) response.getBody().get("success"));
    }

    @Test
    void testResolverQueja() {
        // Given
        when(pedidoRepository.findById("pedido123")).thenReturn(Optional.of(pedidoTest));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoTest);

        Map<String, String> body = new HashMap<>();
        body.put("respuesta", "Problema resuelto");

        // When
        ResponseEntity<Map<String, Object>> response = pedidoController.resolverQueja("pedido123", body);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue((Boolean) response.getBody().get("success"));
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void testPedidosPorCliente() {
        // Given
        List<Pedido> pedidos = Arrays.asList(pedidoTest);
        when(pedidoRepository.findByClienteId("cliente123")).thenReturn(pedidos);

        // When
        ResponseEntity<List<Pedido>> response = pedidoController.pedidosPorCliente("cliente123");

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testQuejasAbiertas() {
        // Given
        pedidoTest.setQuejaMotivo("Producto defectuoso");
        pedidoTest.setQuejaEstado("ABIERTA");
        List<Pedido> pedidos = Arrays.asList(pedidoTest);
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        // When
        ResponseEntity<List<Pedido>> response = pedidoController.quejasAbiertas();

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }
}
