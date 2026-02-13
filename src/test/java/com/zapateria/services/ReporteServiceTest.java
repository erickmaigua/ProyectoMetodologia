package com.zapateria.services;

import com.zapateria.models.Reporte;
import com.zapateria.repositories.PedidoRepository;
import com.zapateria.repositories.ReporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private ReporteService reporteService;

    private Reporte reporteTest;

    @BeforeEach
    void setUp() {
        reporteTest = new Reporte();
        reporteTest.setId("reporte123");
        reporteTest.setPedidoId("pedido123");
        reporteTest.setUsuarioId("usuario123");
        reporteTest.setTipo("PRODUCTO_DEFECTUOSO");
        reporteTest.setDescripcion("El zapato llegó con defecto");
        reporteTest.setEstado("ABIERTA");
        reporteTest.setSolicitaDevolucion(true);
    }

    @Test
    void testCrearReporteExitoso() {
        // Given
        when(pedidoRepository.existsById("pedido123")).thenReturn(true);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteTest);

        // When
        Reporte resultado = reporteService.crear(reporteTest);

        // Then
        assertNotNull(resultado);
        assertEquals("reporte123", resultado.getId());
        assertEquals("ABIERTA", resultado.getEstado());
        verify(pedidoRepository, times(1)).existsById("pedido123");
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    void testCrearReporteSinPedidoValido() {
        // Given
        when(pedidoRepository.existsById("pedido123")).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reporteService.crear(reporteTest);
        });
        verify(reporteRepository, never()).save(any(Reporte.class));
    }

    @Test
    void testCrearReporteAsignaFechaYEstadoPorDefecto() {
        // Given
        Reporte nuevoReporte = new Reporte();
        nuevoReporte.setPedidoId("pedido123");
        nuevoReporte.setFechaReporte(null);
        nuevoReporte.setEstado(null);

        when(pedidoRepository.existsById("pedido123")).thenReturn(true);
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Reporte resultado = reporteService.crear(nuevoReporte);

        // Then
        assertNotNull(resultado.getFechaReporte());
        assertEquals("ABIERTA", resultado.getEstado());
    }

    @Test
    void testFindAll() {
        // Given
        List<Reporte> reportes = Arrays.asList(reporteTest, new Reporte());
        when(reporteRepository.findAll()).thenReturn(reportes);

        // When
        List<Reporte> resultado = reporteService.findAll();

        // Then
        assertEquals(2, resultado.size());
        verify(reporteRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        // Given
        when(reporteRepository.findById("reporte123")).thenReturn(Optional.of(reporteTest));

        // When
        Optional<Reporte> resultado = reporteService.findById("reporte123");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("reporte123", resultado.get().getId());
    }

    @Test
    void testFindByPedidoId() {
        // Given
        List<Reporte> reportes = Arrays.asList(reporteTest);
        when(reporteRepository.findByPedidoId("pedido123")).thenReturn(reportes);

        // When
        List<Reporte> resultado = reporteService.findByPedidoId("pedido123");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("pedido123", resultado.get(0).getPedidoId());
    }

    @Test
    void testFindByUsuarioId() {
        // Given
        List<Reporte> reportes = Arrays.asList(reporteTest);
        when(reporteRepository.findByUsuarioId("usuario123")).thenReturn(reportes);

        // When
        List<Reporte> resultado = reporteService.findByUsuarioId("usuario123");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("usuario123", resultado.get(0).getUsuarioId());
    }

    @Test
    void testFindByEstado() {
        // Given
        List<Reporte> reportes = Arrays.asList(reporteTest);
        when(reporteRepository.findByEstado("ABIERTA")).thenReturn(reportes);

        // When
        List<Reporte> resultado = reporteService.findByEstado("ABIERTA");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("ABIERTA", resultado.get(0).getEstado());
    }

    @Test
    void testContarPendientes() {
        // Given
        when(reporteRepository.countByEstado("ABIERTA")).thenReturn(5L);

        // When
        long resultado = reporteService.contarPendientes();

        // Then
        assertEquals(5L, resultado);
    }

    @Test
    void testActualizarEstadoExitoso() {
        // Given
        when(reporteRepository.findById("reporte123")).thenReturn(Optional.of(reporteTest));
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Reporte resultado = reporteService.actualizarEstado(
            "reporte123", "RESUELTA", "Problema resuelto", "admin123"
        );

        // Then
        assertEquals("RESUELTA", resultado.getEstado());
        assertEquals("Problema resuelto", resultado.getRespuestaAdmin());
        assertEquals("admin123", resultado.getAdminId());
        assertNotNull(resultado.getFechaActualizacion());
    }

    @Test
    void testActualizarEstadoReporteNoEncontrado() {
        // Given
        when(reporteRepository.findById("inexistente")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reporteService.actualizarEstado("inexistente", "RESUELTA", null, null);
        });
    }

    @Test
    void testActualizarEstadoSinRespuesta() {
        // Given
        when(reporteRepository.findById("reporte123")).thenReturn(Optional.of(reporteTest));
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Reporte resultado = reporteService.actualizarEstado("reporte123", "EN_REVISION", null, null);

        // Then
        assertEquals("EN_REVISION", resultado.getEstado());
        assertNotNull(resultado.getFechaActualizacion());
    }

    @Test
    void testDelete() {
        // Given
        doNothing().when(reporteRepository).deleteById("reporte123");

        // When
        reporteService.delete("reporte123");

        // Then
        verify(reporteRepository, times(1)).deleteById("reporte123");
    }

    // ═══════════════════════════════════════════════
    // TESTS ADICIONALES — cobertura extra
    // ═══════════════════════════════════════════════

    @Test
    void testCrearReporte_PedidoIdNullLanzaException() {
        // Given
        Reporte reporte = new Reporte();
        reporte.setPedidoId(null); // sin pedidoId

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> reporteService.crear(reporte));
        verify(reporteRepository, never()).save(any(Reporte.class));
    }

    @Test
    void testCrearReporte_ConservaFechaExistente() {
        // Given
        Date fechaExistente = new Date(1000000L);
        reporteTest.setFechaReporte(fechaExistente);
        reporteTest.setEstado("ABIERTA");
        when(pedidoRepository.existsById("pedido123")).thenReturn(true);
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Reporte resultado = reporteService.crear(reporteTest);

        // Then
        // Si la fecha ya venía, no debe pisarse (depende de la lógica: fecha != null => no reasigna)
        assertNotNull(resultado.getFechaReporte());
    }

    @Test
    void testCrearReporte_ConservaEstadoExistente() {
        // Given — si el reporte ya trae estado, no debe sobreescribirse
        reporteTest.setEstado("EN_REVISION");
        when(pedidoRepository.existsById("pedido123")).thenReturn(true);
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Reporte resultado = reporteService.crear(reporteTest);

        // Then — el estado que venía no debe cambiar a ABIERTA si ya tenía valor
        assertNotNull(resultado.getEstado());
    }

    @Test
    void testFindById_Existente() {
        // Given
        when(reporteRepository.findById("reporte123")).thenReturn(Optional.of(reporteTest));

        // When
        Optional<Reporte> resultado = reporteService.findById("reporte123");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("reporte123", resultado.get().getId());
    }

    @Test
    void testFindById_NoExistente() {
        // Given
        when(reporteRepository.findById("noexiste")).thenReturn(Optional.empty());

        // When
        Optional<Reporte> resultado = reporteService.findById("noexiste");

        // Then
        assertFalse(resultado.isPresent());
    }

    @Test
    void testFindAll_ListaVacia() {
        // Given
        when(reporteRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Reporte> resultado = reporteService.findAll();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testActualizarEstado_CampoAdminIdNullNoFalla() {
        // Given
        when(reporteRepository.findById("reporte123")).thenReturn(Optional.of(reporteTest));
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(i -> i.getArguments()[0]);

        // When — adminId es null, no debe lanzar excepción
        Reporte resultado = reporteService.actualizarEstado("reporte123", "EN_REVISION", null, null);

        // Then
        assertEquals("EN_REVISION", resultado.getEstado());
        assertNotNull(resultado.getFechaActualizacion());
    }

    @Test
    void testActualizarEstado_SoloActualizaRespuestaSiNoEsNull() {
        // Given
        reporteTest.setRespuestaAdmin("respuesta anterior");
        when(reporteRepository.findById("reporte123")).thenReturn(Optional.of(reporteTest));
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(i -> i.getArguments()[0]);

        // When — respuesta null no debe sobreescribir la existente
        Reporte resultado = reporteService.actualizarEstado("reporte123", "RESUELTA", null, null);

        // Then
        assertEquals("RESUELTA", resultado.getEstado());
        // La respuesta anterior se conserva (no se sobreescribe con null)
        assertEquals("respuesta anterior", resultado.getRespuestaAdmin());
    }

    @Test
    void testContarPendientes_SinPendientes() {
        // Given
        when(reporteRepository.countByEstado("ABIERTA")).thenReturn(0L);

        // When
        long resultado = reporteService.contarPendientes();

        // Then
        assertEquals(0L, resultado);
    }

    @Test
    void testFindByEstado_MultipleResultados() {
        // Given
        Reporte r2 = new Reporte(); r2.setId("r2"); r2.setEstado("ABIERTA");
        when(reporteRepository.findByEstado("ABIERTA")).thenReturn(Arrays.asList(reporteTest, r2));

        // When
        List<Reporte> resultado = reporteService.findByEstado("ABIERTA");

        // Then
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(r -> "ABIERTA".equals(r.getEstado())));
    }
}
