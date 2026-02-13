package com.zapateria.controllers;

import com.zapateria.models.Producto;
import com.zapateria.repositories.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoController productoController;

    private Producto productoTest;

    @BeforeEach
    void setUp() {
        productoTest = new Producto();
        productoTest.setId("prod001");
        productoTest.setNombre("Nike Air Max");
        productoTest.setMarca("Nike");
        productoTest.setCategoria("Deportivo");
        productoTest.setCategoriaEdad("ADULTO");
        productoTest.setColor("Blanco");
        productoTest.setPrecio(120.0);
        productoTest.setStock(10);
        productoTest.setImagen("http://img.test/nike.jpg");
    }

    // ── GET /api/productos ──────────────────────────────────────

    @Test
    void testListarProductos_RetornaListaCompleta() {
        // Given
        List<Producto> lista = Arrays.asList(productoTest, new Producto());
        when(productoRepository.findAll()).thenReturn(lista);

        // When
        ResponseEntity<List<Producto>> response = productoController.listarProductos();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testListarProductos_ListaVacia() {
        // Given
        when(productoRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<Producto>> response = productoController.listarProductos();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    // ── GET /api/productos/{id} ──────────────────────────────────

    @Test
    void testObtenerProducto_Existente() {
        // Given
        when(productoRepository.findById("prod001")).thenReturn(Optional.of(productoTest));

        // When
        ResponseEntity<?> response = productoController.obtenerProducto("prod001");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Producto body = (Producto) response.getBody();
        assertEquals("prod001", body.getId());
        assertEquals("Nike Air Max", body.getNombre());
    }

    @Test
    void testObtenerProducto_NoExistente_Retorna404() {
        // Given
        when(productoRepository.findById("noexiste")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = productoController.obtenerProducto("noexiste");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testObtenerProducto_IdVacio_Retorna400() {
        // When
        ResponseEntity<?> response = productoController.obtenerProducto("   ");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(productoRepository, never()).findById(any());
    }

    // ── GET /api/productos/buscar/{nombre} ───────────────────────

    @Test
    void testBuscarProductos_EncuentraResultados() {
        // Given
        when(productoRepository.findByNombreContainingIgnoreCase("nike"))
                .thenReturn(Arrays.asList(productoTest));

        // When
        ResponseEntity<List<Producto>> response = productoController.buscarProductos("nike");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Nike Air Max", response.getBody().get(0).getNombre());
    }

    @Test
    void testBuscarProductos_NombreVacio_Retorna400() {
        // When
        ResponseEntity<List<Producto>> response = productoController.buscarProductos("");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(productoRepository, never()).findByNombreContainingIgnoreCase(any());
    }

    // ── GET /api/productos/categoriaEdad/{categoria} ─────────────

    @Test
    void testProductosPorCategoriaEdad_Adulto() {
        // Given
        when(productoRepository.findByCategoriaEdad("ADULTO"))
                .thenReturn(Arrays.asList(productoTest));

        // When
        ResponseEntity<List<Producto>> response =
                productoController.productosPorCategoriaEdad("ADULTO");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("ADULTO", response.getBody().get(0).getCategoriaEdad());
    }

    @Test
    void testProductosPorCategoriaEdad_CategoriaVacia_Retorna400() {
        // When
        ResponseEntity<List<Producto>> response = productoController.productosPorCategoriaEdad("");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ── POST /api/productos ──────────────────────────────────────

    @Test
    void testCrearProducto_Exitoso() {
        // Given
        when(productoRepository.save(any(Producto.class))).thenReturn(productoTest);

        // When
        ResponseEntity<Map<String, Object>> response = productoController.crearProducto(productoTest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));
        assertNotNull(response.getBody().get("producto"));
        verify(productoRepository, times(1)).save(productoTest);
    }

    @Test
    void testCrearProducto_ConservaCategoriaEdad() {
        // Given
        Producto nuevo = new Producto();
        nuevo.setCategoriaEdad("NINO");
        nuevo.setNombre("Zapato Niño");
        nuevo.setPrecio(40.0);
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        ResponseEntity<Map<String, Object>> response = productoController.crearProducto(nuevo);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Producto guardado = (Producto) response.getBody().get("producto");
        assertEquals("NINO", guardado.getCategoriaEdad());
    }

    // ── PUT /api/productos/{id} ──────────────────────────────────

    @Test
    void testActualizarProducto_Exitoso() {
        // Given
        Producto actualizado = new Producto();
        actualizado.setNombre("Nike Air Max 2024");
        actualizado.setCategoriaEdad("ADULTO");
        actualizado.setImagen("http://img.test/new.jpg");

        when(productoRepository.findById("prod001")).thenReturn(Optional.of(productoTest));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        ResponseEntity<?> response = productoController.actualizarProducto("prod001", actualizado);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testActualizarProducto_PreservaImagenSiNoVieneNueva() {
        // Given — el body no trae imagen nueva
        Producto body = new Producto();
        body.setNombre("Nike actualizado");
        body.setCategoriaEdad("ADULTO");
        body.setImagen(null); // sin imagen nueva

        when(productoRepository.findById("prod001")).thenReturn(Optional.of(productoTest));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        ResponseEntity<?> response = productoController.actualizarProducto("prod001", body);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Producto resultado = (Producto) response.getBody();
        // Imagen de la BD original debe conservarse
        assertEquals("http://img.test/nike.jpg", resultado.getImagen());
    }

    @Test
    void testActualizarProducto_ConservaCategoriaEdad() {
        // Given — actualizamos nombre pero conservamos categoriaEdad
        Producto body = new Producto();
        body.setNombre("Nuevo nombre");
        body.setCategoriaEdad("ADULTO");
        body.setImagen("http://img.test/updated.jpg");

        when(productoRepository.findById("prod001")).thenReturn(Optional.of(productoTest));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        ResponseEntity<?> response = productoController.actualizarProducto("prod001", body);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Producto resultado = (Producto) response.getBody();
        assertEquals("ADULTO", resultado.getCategoriaEdad());
    }

    @Test
    void testActualizarProducto_IdVacio_Retorna400() {
        // When
        ResponseEntity<?> response = productoController.actualizarProducto("", new Producto());

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testActualizarProducto_NoExistente_Retorna404() {
        // Given
        when(productoRepository.findById("noexiste")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = productoController.actualizarProducto("noexiste", new Producto());

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ── DELETE /api/productos/{id} ───────────────────────────────

    @Test
    void testEliminarProducto_Exitoso() {
        // Given
        doNothing().when(productoRepository).deleteById("prod001");

        // When
        ResponseEntity<Map<String, Object>> response = productoController.eliminarProducto("prod001");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));
        verify(productoRepository, times(1)).deleteById("prod001");
    }

    @Test
    void testEliminarProducto_IdVacio_Retorna400() {
        // When
        ResponseEntity<Map<String, Object>> response = productoController.eliminarProducto("");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("success"));
        verify(productoRepository, never()).deleteById(any());
    }

    // ── PUT /api/productos/{id}/stock ────────────────────────────

    @Test
    void testActualizarStock_Exitoso() {
        // Given
        when(productoRepository.findById("prod001")).thenReturn(Optional.of(productoTest));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArguments()[0]);

        Map<String, Integer> data = new HashMap<>();
        data.put("stock", 25);

        // When
        ResponseEntity<Map<String, Object>> response =
                productoController.actualizarStock("prod001", data);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));
        Producto resultado = (Producto) response.getBody().get("producto");
        assertEquals(25, resultado.getStock());
    }

    @Test
    void testActualizarStock_ProductoNoExistente_Retorna404() {
        // Given
        when(productoRepository.findById("noexiste")).thenReturn(Optional.empty());

        Map<String, Integer> data = new HashMap<>();
        data.put("stock", 5);

        // When
        ResponseEntity<Map<String, Object>> response =
                productoController.actualizarStock("noexiste", data);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testActualizarStock_SinCampoStock_Retorna400() {
        // Given
        when(productoRepository.findById("prod001")).thenReturn(Optional.of(productoTest));

        Map<String, Integer> data = new HashMap<>(); // sin "stock"

        // When
        ResponseEntity<Map<String, Object>> response =
                productoController.actualizarStock("prod001", data);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("success"));
    }

    // ── GET /api/productos/categoria/{categoria} ─────────────────

    @Test
    void testProductosPorCategoria_Exitoso() {
        // Given
        when(productoRepository.findByCategoria("Deportivo"))
                .thenReturn(Arrays.asList(productoTest));

        // When
        ResponseEntity<List<Producto>> response =
                productoController.productosPorCategoria("Deportivo");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testProductosPorCategoria_Vacia_Retorna400() {
        // When
        ResponseEntity<List<Producto>> response = productoController.productosPorCategoria("   ");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ── GET /api/productos/stock-bajo ────────────────────────────

    @Test
    void testProductosStockBajo() {
        // Given
        Producto stockBajo = new Producto();
        stockBajo.setStock(3);
        when(productoRepository.findByStockLessThan(10)).thenReturn(Arrays.asList(stockBajo));

        // When
        ResponseEntity<List<Producto>> response = productoController.productosStockBajo();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).getStock() < 10);
    }
}
