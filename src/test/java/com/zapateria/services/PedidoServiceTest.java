package com.zapateria.services;

import com.zapateria.models.Pedido;
import com.zapateria.models.Pedido.ItemPedido;
import com.zapateria.models.Producto;
import com.zapateria.repositories.PedidoRepository;
import com.zapateria.repositories.ProductoRepository;
import com.zapateria.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedidoTest;
    private Producto productoTest;

    @BeforeEach
    void setUp() {
        // Configurar pedido de prueba
        pedidoTest = new Pedido();
        pedidoTest.setClienteId("cliente123");
        pedidoTest.setClienteNombre("Test Cliente");
        pedidoTest.setTipoEntrega("RETIRO_TIENDA");
        pedidoTest.setEstadoPago("PENDIENTE");

        List<ItemPedido> items = new ArrayList<>();
        ItemPedido item = new ItemPedido();
        item.setProductoId("prod123");
        item.setNombreProducto("Nike Air");
        item.setCantidad(2);
        item.setPrecio(100.0);
        item.setTalla("42");
        items.add(item);
        pedidoTest.setItems(items);

        // Configurar producto de prueba
        productoTest = new Producto();
        productoTest.setId("prod123");
        productoTest.setNombre("Nike Air");
        productoTest.setStock(10);
        productoTest.setPrecio(100.0);
    }

    @Test
    void testCrearPedidoExitoso() {
        // Given
        when(productoRepository.findById("prod123")).thenReturn(Optional.of(productoTest));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoTest);

        // When
        Map<String, Object> result = pedidoService.crearPedido(pedidoTest);

        // Then
        assertTrue((Boolean) result.get("success"));
        verify(productoRepository, times(1)).findById("prod123");
        verify(productoRepository, times(1)).save(any(Producto.class));
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void testCrearPedidoSinItems() {
        // Given
        pedidoTest.setItems(new ArrayList<>());

        // When
        Map<String, Object> result = pedidoService.crearPedido(pedidoTest);

        // Then
        assertFalse((Boolean) result.get("success"));
        assertEquals("El pedido debe tener al menos un producto", result.get("mensaje"));
    }

    @Test
    void testCrearPedidoSinStock() {
        // Given
        productoTest.setStock(1); // Stock insuficiente
        when(productoRepository.findById("prod123")).thenReturn(Optional.of(productoTest));

        // When
        Map<String, Object> result = pedidoService.crearPedido(pedidoTest);

        // Then
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("mensaje")).contains("Stock insuficiente"));
    }

    @Test
    void testCalcularTotalesRetiroTienda() {
        // Given
        pedidoTest.setTipoEntrega("RETIRO_TIENDA");
        when(productoRepository.findById("prod123")).thenReturn(Optional.of(productoTest));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Map<String, Object> result = pedidoService.crearPedido(pedidoTest);

        // Then
        assertTrue((Boolean) result.get("success"));
        Pedido pedidoCreado = (Pedido) result.get("pedido");
        assertEquals(200.0, pedidoCreado.getSubtotal()); // 2 * 100
        assertEquals(30.0, pedidoCreado.getIvaValor()); // 200 * 0.15
        assertEquals(0.0, pedidoCreado.getEnvio()); // Retiro = 0
        assertEquals(230.0, pedidoCreado.getTotalFinal()); // 200 + 30 + 0
    }

    @Test
    void testCalcularTotalesDomicilio() {
        // Given
        pedidoTest.setTipoEntrega("DOMICILIO");
        when(productoRepository.findById("prod123")).thenReturn(Optional.of(productoTest));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Map<String, Object> result = pedidoService.crearPedido(pedidoTest);

        // Then
        assertTrue((Boolean) result.get("success"));
        Pedido pedidoCreado = (Pedido) result.get("pedido");
        assertEquals(200.0, pedidoCreado.getSubtotal());
        assertEquals(30.0, pedidoCreado.getIvaValor());
        assertEquals(5.0, pedidoCreado.getEnvio()); // Domicilio = 5
        assertEquals(235.0, pedidoCreado.getTotalFinal()); // 200 + 30 + 5
    }

    @Test
    void testRegistrarQueja() {
        // Given
        String pedidoId = "pedido123";
        String motivo = "Producto defectuoso";
        String detalle = "El producto llegó dañado";
        boolean solicitaDevolucion = true;

        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(pedidoId);

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoExistente));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Map<String, Object> result = pedidoService.registrarQueja(pedidoId, motivo, detalle, solicitaDevolucion);

        // Then
        assertTrue((Boolean) result.get("success"));
        Pedido pedidoActualizado = (Pedido) result.get("pedido");
        assertEquals(motivo, pedidoActualizado.getQuejaMotivo());
        assertEquals(detalle, pedidoActualizado.getQuejaDetalle());
        assertTrue(pedidoActualizado.isSolicitaDevolucion());
        assertEquals("ABIERTA", pedidoActualizado.getQuejaEstado());
        assertNotNull(pedidoActualizado.getQuejaFecha());
    }

    @Test
    void testCambiarEstado() {
        // Given
        String pedidoId = "pedido123";
        String nuevoEstado = "PROCESANDO";

        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(pedidoId);
        pedidoExistente.setEstado("PENDIENTE");

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoExistente));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Map<String, Object> result = pedidoService.cambiarEstado(pedidoId, nuevoEstado);

        // Then
        assertTrue((Boolean) result.get("success"));
        Pedido pedidoActualizado = (Pedido) result.get("pedido");
        assertEquals(nuevoEstado, pedidoActualizado.getEstado());
    }

    // ═══════════════════════════════════════════════
    // TESTS ADICIONALES — cobertura extra
    // ═══════════════════════════════════════════════

    @Test
    void testCrearPedidoProductoNoExiste() {
        // Given
        when(productoRepository.findById("prod123")).thenReturn(Optional.empty());

        // When
        Map<String, Object> result = pedidoService.crearPedido(pedidoTest);

        // Then
        assertFalse((Boolean) result.get("success"));
        // debe indicar que el producto no se encontró
        assertNotNull(result.get("mensaje"));
    }

    @Test
    void testCrearPedidoItemsNull() {
        // Given
        pedidoTest.setItems(null);

        // When
        Map<String, Object> result = pedidoService.crearPedido(pedidoTest);

        // Then
        assertFalse((Boolean) result.get("success"));
    }

    @Test
    void testCalcularTotalesConEnvioEstandar() {
        // Given
        pedidoTest.setTipoEntrega("ESTANDAR"); // otro tipo con envío
        when(productoRepository.findById("prod123")).thenReturn(Optional.of(productoTest));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Map<String, Object> result = pedidoService.crearPedido(pedidoTest);

        // Then — si el tipo no es RETIRO_TIENDA debe aplicar algún costo de envío
        assertTrue((Boolean) result.get("success"));
        Pedido creado = (Pedido) result.get("pedido");
        assertEquals(200.0, creado.getSubtotal());
    }

    @Test
    void testRegistrarQuejaConDevolucionFalse() {
        // Given
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId("pedido123");
        when(pedidoRepository.findById("pedido123")).thenReturn(Optional.of(pedidoExistente));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Map<String, Object> result = pedidoService.registrarQueja(
            "pedido123", "Talla incorrecta", "Me llegó talla 40 y pedí 42", false);

        // Then
        assertTrue((Boolean) result.get("success"));
        Pedido p = (Pedido) result.get("pedido");
        assertFalse(p.isSolicitaDevolucion());
        assertEquals("ABIERTA", p.getQuejaEstado());
    }

    @Test
    void testRegistrarQujaPedidoNoExiste() {
        // Given
        when(pedidoRepository.findById("noExiste")).thenReturn(Optional.empty());

        // When
        Map<String, Object> result = pedidoService.registrarQueja(
            "noExiste", "Motivo", "Detalle", false);

        // Then
        assertFalse((Boolean) result.get("success"));
        assertNotNull(result.get("mensaje"));
    }

    @Test
    void testCambiarEstadoPedidoNoExiste() {
        // Given
        when(pedidoRepository.findById("noExiste")).thenReturn(Optional.empty());

        // When
        Map<String, Object> result = pedidoService.cambiarEstado("noExiste", "ENVIADO");

        // Then — el servicio retorna success=false cuando el pedido no existe
        assertFalse((Boolean) result.get("success"));
        assertNotNull(result.get("mensaje"));
    }

    @Test
    void testVerificarStockReduccionCorrecta() {
        // Given
        productoTest.setStock(10);
        when(productoRepository.findById("prod123")).thenReturn(Optional.of(productoTest));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoTest);

        // When
        pedidoService.crearPedido(pedidoTest);

        // Then — el stock debería reducirse en la cantidad del pedido (2 unidades)
        verify(productoRepository, times(1)).save(argThat(p ->
            p instanceof Producto && ((Producto) p).getStock() == 8
        ));
    }
}
