package com.zapateria.controllers;

import com.zapateria.models.Reporte;
import com.zapateria.services.ReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    @Mock
    private ReporteService reporteService;

    @InjectMocks
    private ReporteController reporteController;

    private Reporte reporteTest;

    @BeforeEach
    void setUp() {
        reporteTest = new Reporte();
        reporteTest.setId("reporte123");
        reporteTest.setPedidoId("pedido123");
        reporteTest.setUsuarioId("usuario123");
        reporteTest.setTipo("PRODUCTO_DEFECTUOSO");
        reporteTest.setDescripcion("Problema con el producto");
        reporteTest.setEstado("ABIERTA");
    }

    @Test
    void testCrearReporteExitoso() {
        // Given
        when(reporteService.crear(any(Reporte.class))).thenReturn(reporteTest);

        // When
        ResponseEntity<?> response = reporteController.crear(reporteTest);

        // Then
        // FIX: el controller ahora retorna 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reporteService, times(1)).crear(any(Reporte.class));
    }

    @Test
    void testCrearReporteError() {
        // Given
        when(reporteService.crear(any(Reporte.class)))
            .thenThrow(new IllegalArgumentException("Pedido no existe"));

        // When
        ResponseEntity<?> response = reporteController.crear(reporteTest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        // FIX: el controller ahora pone clave "error" en el body
        assertNotNull(body);
        assertTrue(body.get("error").toString().contains("Pedido no existe"));
    }

    @Test
    void testListarTodos() {
        // Given
        List<Reporte> reportes = Arrays.asList(reporteTest, new Reporte());
        when(reporteService.findAll()).thenReturn(reportes);

        // When
        ResponseEntity<?> response = reporteController.listar();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<Reporte> body = (List<Reporte>) response.getBody();
        assertEquals(2, body.size());
    }

    @Test
    void testObtenerPorIdExitoso() {
        // Given
        when(reporteService.findById("reporte123")).thenReturn(Optional.of(reporteTest));

        // When
        ResponseEntity<?> response = reporteController.obtener("reporte123");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Reporte body = (Reporte) response.getBody();
        assertEquals("reporte123", body.getId());
    }

    @Test
    void testObtenerPorIdNoEncontrado() {
        // Given
        when(reporteService.findById("inexistente")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = reporteController.obtener("inexistente");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testListarPorPedido() {
        // Given
        List<Reporte> reportes = Arrays.asList(reporteTest);
        when(reporteService.findByPedidoId("pedido123")).thenReturn(reportes);

        // When
        ResponseEntity<?> response = reporteController.porPedido("pedido123");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<Reporte> body = (List<Reporte>) response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    void testListarPorUsuario() {
        // Given
        List<Reporte> reportes = Arrays.asList(reporteTest);
        when(reporteService.findByUsuarioId("usuario123")).thenReturn(reportes);

        // When
        ResponseEntity<?> response = reporteController.porUsuario("usuario123");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<Reporte> body = (List<Reporte>) response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    void testListarPorEstado() {
        // Given
        List<Reporte> reportes = Arrays.asList(reporteTest);
        when(reporteService.findByEstado("ABIERTA")).thenReturn(reportes);

        // When
        ResponseEntity<?> response = reporteController.porEstado("ABIERTA");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<Reporte> body = (List<Reporte>) response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    void testContarPendientes() {
        // Given
        when(reporteService.contarPendientes()).thenReturn(5L);

        // When
        ResponseEntity<?> response = reporteController.estadisticas();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(5L, body.get("pendientes"));
    }

    @Test
    void testResolverExitoso() {
        // Given
        Map<String, String> datos = Map.of(
            "respuesta", "Problema resuelto",
            "adminId", "admin123"
        );
        when(reporteService.actualizarEstado(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(reporteTest);

        // When
        ResponseEntity<?> response = reporteController.resolver("reporte123", datos);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reporteService, times(1)).actualizarEstado(
            "reporte123", "RESUELTA", "Problema resuelto", "admin123"
        );
    }

    @Test
    void testResolverError() {
        // Given
        Map<String, String> datos = Map.of("respuesta", "Test");
        // FIX: usar anyString() en lugar de strings vacíos para que el stub
        // coincida con los argumentos reales que pasa el controller.
        when(reporteService.actualizarEstado(anyString(), anyString(), anyString(), isNull()))
            .thenThrow(new IllegalArgumentException("Reporte no encontrado"));

        // When
        ResponseEntity<?> response = reporteController.resolver("inexistente", datos);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testEliminarExitoso() {
        // Given
        doNothing().when(reporteService).delete("reporte123");

        // When
        ResponseEntity<?> response = reporteController.eliminar("reporte123");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        // FIX: el controller ahora retorna "Reporte eliminado exitosamente"
        assertEquals("Reporte eliminado exitosamente", body.get("mensaje"));
    }

    @Test
    void testEliminarError() {
        // Given
        doThrow(new RuntimeException("Error al eliminar"))
            .when(reporteService).delete("reporte123");

        // When
        ResponseEntity<?> response = reporteController.eliminar("reporte123");

        // Then
        // FIX: el controller ahora captura RuntimeException y retorna 500
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
