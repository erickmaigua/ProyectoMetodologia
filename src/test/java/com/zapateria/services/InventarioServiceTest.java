package com.zapateria.services;

import com.zapateria.models.CategoriaEdad;
import com.zapateria.models.Pedido;
import com.zapateria.models.Producto;
import com.zapateria.repositories.ProductoRepository;
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
class InventarioServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Producto productoNino;
    private Producto productoAdulto;
    private List<Pedido> pedidosTest;

    @BeforeEach
    void setUp() {
        // Producto para niños
        productoNino = new Producto();
        productoNino.setId("prod1");
        productoNino.setNombre("Zapato Niño");
        productoNino.setCategoriaEdad("NINO");
        productoNino.setStock(20);
        productoNino.setPrecio(50.0);

        // Producto para adultos
        productoAdulto = new Producto();
        productoAdulto.setId("prod2");
        productoAdulto.setNombre("Zapato Adulto");
        productoAdulto.setCategoriaEdad("ADULTO");
        productoAdulto.setStock(15);
        productoAdulto.setPrecio(100.0);

        // Pedidos de prueba
        pedidosTest = new ArrayList<>();
        Pedido pedido1 = new Pedido();
        pedido1.setId("pedido1");
        pedido1.setTotalFinal(200.0);
        
        List<Pedido.ItemPedido> items1 = new ArrayList<>();
        Pedido.ItemPedido item1 = new Pedido.ItemPedido();
        item1.setProductoId("prod1");
        item1.setNombreProducto("Zapato Niño");
        item1.setCantidad(2);
        item1.setPrecio(50.0);
        items1.add(item1);
        pedido1.setItems(items1);
        
        pedidosTest.add(pedido1);
    }

    @Test
    void testFiltrarPorEdadNino() {
        // Given
        int edad = 8;
        when(productoRepository.findByCategoriaEdad("NINO")).thenReturn(Arrays.asList(productoNino));

        // When
        List<Producto> resultado = inventarioService.filtrarPorEdad(edad);

        // Then
        assertEquals(1, resultado.size());
        assertEquals("NINO", resultado.get(0).getCategoriaEdad());
        verify(productoRepository, times(1)).findByCategoriaEdad("NINO");
    }

    @Test
    void testFiltrarPorEdadAdolescente() {
        // Given
        int edad = 15;
        when(productoRepository.findByCategoriaEdad("ADOLESCENTE")).thenReturn(new ArrayList<>());

        // When
        List<Producto> resultado = inventarioService.filtrarPorEdad(edad);

        // Then
        assertEquals(0, resultado.size());
        verify(productoRepository, times(1)).findByCategoriaEdad("ADOLESCENTE");
    }

    @Test
    void testFiltrarPorEdadAdulto() {
        // Given
        int edad = 25;
        when(productoRepository.findByCategoriaEdad("ADULTO")).thenReturn(Arrays.asList(productoAdulto));

        // When
        List<Producto> resultado = inventarioService.filtrarPorEdad(edad);

        // Then
        assertEquals(1, resultado.size());
        assertEquals("ADULTO", resultado.get(0).getCategoriaEdad());
    }

    @Test
    void testFiltrarPorCategoria() {
        // Given
        when(productoRepository.findByCategoriaEdad("NINO")).thenReturn(Arrays.asList(productoNino));

        // When
        List<Producto> resultado = inventarioService.filtrarPorCategoria("NINO");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("NINO", resultado.get(0).getCategoriaEdad());
    }

    @Test
    void testObtenerEstadisticasStock() {
        // Given
        when(productoRepository.count()).thenReturn(100L);
        when(productoRepository.countByCategoriaEdad("NINO")).thenReturn(30L);
        when(productoRepository.countByCategoriaEdad("ADOLESCENTE")).thenReturn(20L);
        when(productoRepository.countByCategoriaEdad("ADULTO")).thenReturn(50L);

        // When
        Map<String, Object> resultado = inventarioService.obtenerEstadisticasStock();

        // Then
        assertNotNull(resultado);
        assertEquals(100L, resultado.get("totalGeneral"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> ninoStats = (Map<String, Object>) resultado.get("NINO");
        assertEquals(30L, ninoStats.get("cantidad"));
        assertEquals(30L, ninoStats.get("porcentaje"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> adultoStats = (Map<String, Object>) resultado.get("ADULTO");
        assertEquals(50L, adultoStats.get("cantidad"));
        assertEquals(50L, adultoStats.get("porcentaje"));
    }

    @Test
    void testObtenerEstadisticasStockConTotalCero() {
        // Given
        when(productoRepository.count()).thenReturn(0L);
        when(productoRepository.countByCategoriaEdad(anyString())).thenReturn(0L);

        // When
        Map<String, Object> resultado = inventarioService.obtenerEstadisticasStock();

        // Then
        assertNotNull(resultado);
        assertEquals(0L, resultado.get("totalGeneral"));
        
        for (CategoriaEdad cat : CategoriaEdad.values()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> catStats = (Map<String, Object>) resultado.get(cat.name());
            assertEquals(0L, catStats.get("porcentaje"));
        }
    }

    @Test
    void testGenerarResumenVentas() {
        // Given
        Date inicio = new Date(System.currentTimeMillis() - 86400000); // hace 1 día
        Date fin = new Date();

        // When
        Map<String, Object> resultado = inventarioService.generarResumenVentas(pedidosTest, inicio, fin);

        // Then
        assertNotNull(resultado);
        assertEquals(inicio, resultado.get("fechaInicio"));
        assertEquals(fin, resultado.get("fechaFin"));
        assertEquals(1, resultado.get("totalPedidos"));
        assertEquals(200.0, resultado.get("totalVentas"));
        assertEquals(2, resultado.get("totalProductos"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ventasProducto = (List<Map<String, Object>>) resultado.get("ventasPorProducto");
        assertNotNull(ventasProducto);
        assertEquals(1, ventasProducto.size());
    }

    @Test
    void testGenerarResumenVentasConVariosPedidos() {
        // Given
        Pedido pedido2 = new Pedido();
        pedido2.setId("pedido2");
        pedido2.setTotalFinal(300.0);
        
        List<Pedido.ItemPedido> items2 = new ArrayList<>();
        Pedido.ItemPedido item2 = new Pedido.ItemPedido();
        item2.setProductoId("prod2");
        item2.setNombreProducto("Zapato Adulto");
        item2.setCantidad(3);
        item2.setPrecio(100.0);
        items2.add(item2);
        pedido2.setItems(items2);
        
        pedidosTest.add(pedido2);
        
        Date inicio = new Date(System.currentTimeMillis() - 86400000);
        Date fin = new Date();

        // When
        Map<String, Object> resultado = inventarioService.generarResumenVentas(pedidosTest, inicio, fin);

        // Then
        assertEquals(2, resultado.get("totalPedidos"));
        assertEquals(500.0, resultado.get("totalVentas"));
        assertEquals(5, resultado.get("totalProductos"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topProductos = (List<Map<String, Object>>) resultado.get("topProductos");
        assertNotNull(topProductos);
        assertTrue(topProductos.size() <= 5);
    }

    @Test
    void testGenerarResumenVentasSinItems() {
        // Given
        Pedido pedidoVacio = new Pedido();
        pedidoVacio.setId("pedidoVacio");
        pedidoVacio.setItems(null);
        pedidoVacio.setTotalFinal(0.0);
        
        List<Pedido> pedidos = Arrays.asList(pedidoVacio);
        Date inicio = new Date();
        Date fin = new Date();

        // When
        Map<String, Object> resultado = inventarioService.generarResumenVentas(pedidos, inicio, fin);

        // Then
        assertEquals(1, resultado.get("totalPedidos"));
        assertEquals(0.0, resultado.get("totalVentas"));
        assertEquals(0, resultado.get("totalProductos"));
    }

    @Test
    void testGenerarResumenVentasCalculaPorcentajes() {
        // Given
        Date inicio = new Date();
        Date fin = new Date();

        // When
        Map<String, Object> resultado = inventarioService.generarResumenVentas(pedidosTest, inicio, fin);

        // Then
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ventasProducto = (List<Map<String, Object>>) resultado.get("ventasPorProducto");
        for (Map<String, Object> venta : ventasProducto) {
            assertTrue(venta.containsKey("porcentaje"));
            double porcentaje = (Double) venta.get("porcentaje");
            assertTrue(porcentaje >= 0 && porcentaje <= 100);
        }
    }

    @Test
    void testGenerarResumenVentasUsaTotalLegacySiNoHayTotalFinal() {
        // Given
        Pedido pedidoLegacy = new Pedido();
        pedidoLegacy.setId("pedidoLegacy");
        pedidoLegacy.setTotal(150.0);
        pedidoLegacy.setTotalFinal(0.0);
        
        List<Pedido.ItemPedido> items = new ArrayList<>();
        Pedido.ItemPedido item = new Pedido.ItemPedido();
        item.setProductoId("prod1");
        item.setNombreProducto("Producto");
        item.setCantidad(1);
        item.setPrecio(150.0);
        items.add(item);
        pedidoLegacy.setItems(items);
        
        List<Pedido> pedidos = Arrays.asList(pedidoLegacy);
        Date inicio = new Date();
        Date fin = new Date();

        // When
        Map<String, Object> resultado = inventarioService.generarResumenVentas(pedidos, inicio, fin);

        // Then
        assertEquals(150.0, resultado.get("totalVentas"));
    }

    // ═══════════════════════════════════════════════
    // TESTS ADICIONALES — cobertura extra
    // ═══════════════════════════════════════════════

    @Test
    void testFiltrarPorEdadNumerica_Nino() {
        // Given — edad 8 debe mapear a NINO
        when(productoRepository.findByCategoriaEdad("NINO"))
            .thenReturn(Arrays.asList(productoNino));

        // When
        List<Producto> resultado = inventarioService.filtrarPorEdad(8);

        // Then
        assertEquals(1, resultado.size());
        assertEquals("NINO", resultado.get(0).getCategoriaEdad());
        verify(productoRepository).findByCategoriaEdad("NINO");
    }

    @Test
    void testFiltrarPorEdadNumerica_Adolescente() {
        // Given — edad 15 debe mapear a ADOLESCENTE
        Producto prodAdol = new Producto();
        prodAdol.setId("prod3"); prodAdol.setCategoriaEdad("ADOLESCENTE");
        when(productoRepository.findByCategoriaEdad("ADOLESCENTE"))
            .thenReturn(Arrays.asList(prodAdol));

        // When
        List<Producto> resultado = inventarioService.filtrarPorEdad(15);

        // Then
        assertEquals(1, resultado.size());
        assertEquals("ADOLESCENTE", resultado.get(0).getCategoriaEdad());
    }

    @Test
    void testFiltrarPorEdadNumerica_Adulto() {
        // Given — edad 30 debe mapear a ADULTO
        when(productoRepository.findByCategoriaEdad("ADULTO"))
            .thenReturn(Arrays.asList(productoAdulto));

        // When
        List<Producto> resultado = inventarioService.filtrarPorEdad(30);

        // Then
        assertEquals(1, resultado.size());
        assertEquals("ADULTO", resultado.get(0).getCategoriaEdad());
    }

    @Test
    void testFiltrarPorCategoriaString() {
        // Given
        when(productoRepository.findByCategoriaEdad("NINO"))
            .thenReturn(Arrays.asList(productoNino));

        // When
        List<Producto> resultado = inventarioService.filtrarPorCategoria("NINO");

        // Then
        assertEquals(1, resultado.size());
        verify(productoRepository).findByCategoriaEdad("NINO");
    }

    @Test
    void testFiltrarPorCategoriaVacia_RetornaListaVacia() {
        // Given
        when(productoRepository.findByCategoriaEdad("ADULTO"))
            .thenReturn(Arrays.asList());

        // When
        List<Producto> resultado = inventarioService.filtrarPorCategoria("ADULTO");

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testObtenerEstadisticasStock_IncluyelasTresCategorias() {
        // Given
        when(productoRepository.count()).thenReturn(10L);
        when(productoRepository.countByCategoriaEdad("NINO")).thenReturn(4L);
        when(productoRepository.countByCategoriaEdad("ADOLESCENTE")).thenReturn(3L);
        when(productoRepository.countByCategoriaEdad("ADULTO")).thenReturn(3L);

        // When
        Map<String, Object> stats = inventarioService.obtenerEstadisticasStock();

        // Then
        assertEquals(10L, stats.get("totalGeneral"));
        assertNotNull(stats.get("NINO"));
        assertNotNull(stats.get("ADOLESCENTE"));
        assertNotNull(stats.get("ADULTO"));

        @SuppressWarnings("unchecked")
        Map<String, Object> ninoStats = (Map<String, Object>) stats.get("NINO");
        assertEquals(4L, ninoStats.get("cantidad"));
        assertEquals(40L, ninoStats.get("porcentaje")); // 4/10 * 100 = 40
    }

    @Test
    void testObtenerEstadisticasStock_SinProductos_PorcentajeCero() {
        // Given — total 0 para evitar división por cero
        when(productoRepository.count()).thenReturn(0L);
        when(productoRepository.countByCategoriaEdad(any(String.class))).thenReturn(0L);

        // When
        Map<String, Object> stats = inventarioService.obtenerEstadisticasStock();

        // Then
        assertEquals(0L, stats.get("totalGeneral"));
        @SuppressWarnings("unchecked")
        Map<String, Object> ninoStats = (Map<String, Object>) stats.get("NINO");
        assertEquals(0L, ninoStats.get("porcentaje")); // no división por cero
    }

    @Test
    void testGenerarResumenVentas_ListaVacia() {
        // Given
        List<com.zapateria.models.Pedido> vacia = Arrays.asList();
        Date inicio = new Date();
        Date fin    = new Date();

        // When
        Map<String, Object> resultado = inventarioService.generarResumenVentas(vacia, inicio, fin);

        // Then
        assertEquals(0, resultado.get("totalPedidos"));
        assertEquals(0.0, resultado.get("totalVentas"));
        assertEquals(0, resultado.get("totalProductos"));
    }

    @Test
    void testGenerarResumenVentas_ProductoSinIdNoSeCuenta() {
        // Given
        Pedido pedidoConItemSinId = new Pedido();
        pedidoConItemSinId.setId("p1"); pedidoConItemSinId.setTotalFinal(50.0);
        Pedido.ItemPedido itemSinId = new Pedido.ItemPedido();
        itemSinId.setProductoId(null); // sin id
        itemSinId.setCantidad(1); itemSinId.setPrecio(50.0);
        pedidoConItemSinId.setItems(Arrays.asList(itemSinId));

        Date inicio = new Date(); Date fin = new Date();

        // When
        Map<String, Object> resultado = inventarioService.generarResumenVentas(
            Arrays.asList(pedidoConItemSinId), inicio, fin);

        // Then
        assertEquals(1, resultado.get("totalPedidos"));
        // el item con id null no debe contarse en productos
        assertEquals(0, resultado.get("totalProductos"));
    }
}
