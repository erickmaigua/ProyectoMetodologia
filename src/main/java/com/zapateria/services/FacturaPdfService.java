package com.zapateria.services;

import com.zapateria.models.Pedido;
import com.zapateria.models.Pedido.ItemPedido;
import com.zapateria.repositories.PedidoRepository;
import com.zapateria.exceptions.PedidoNotFoundException;
import com.zapateria.exceptions.FacturaGenerationException;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Service
public class FacturaPdfService {
    
    private static final Logger logger = LoggerFactory.getLogger(FacturaPdfService.class);
    private static final String SUCCESS_MESSAGE = "success";
    private static final String FECHA_PATTERN = "dd/MM/yyyy HH:mm";

    private final PedidoRepository pedidoRepository;

    public FacturaPdfService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public byte[] generarFacturaPedido(String pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido no encontrado con ID: " + pedidoId));

        try {
            return generarPdf(pedido);
        } catch (DocumentException e) {
            logger.error("Error al generar la factura PDF para el pedido {}", pedidoId, e);
            throw new FacturaGenerationException("Error al generar la factura PDF", e);
        }
    }
    
    private byte[] generarPdf(Pedido pedido) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(doc, baos);
        doc.open();

        // Fuentes (creadas una sola vez)
        Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font negrita = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normal = new Font(Font.HELVETICA, 12, Font.NORMAL);

        // Título
        agregarTitulo(doc, tituloFont);
        agregarDatosEmpresa(doc, negrita, normal);
        
        SimpleDateFormat sdf = new SimpleDateFormat(FECHA_PATTERN, Locale.getDefault());
        agregarCabeceraPedido(doc, pedido, negrita, normal, sdf);
        
        double subtotal = agregarTablaItems(doc, pedido, negrita, normal);
        agregarTotales(doc, pedido, subtotal, negrita, normal);
        
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Gracias por su compra.", normal));

        doc.close();
        return baos.toByteArray();
    }
    
    private void agregarTitulo(Document doc, Font tituloFont) throws DocumentException {
        Paragraph titulo = new Paragraph("FACTURA DE COMPRA", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);
        doc.add(new Paragraph(" "));
    }
    
    private void agregarDatosEmpresa(Document doc, Font negrita, Font normal) throws DocumentException {
        doc.add(new Paragraph("Zapatería Online", negrita));
        doc.add(new Paragraph("RUC: 9999999999", normal));
        doc.add(new Paragraph("Dirección: Quito - Ecuador", normal));
        doc.add(new Paragraph("Teléfono: 0999999999", normal));
        doc.add(new Paragraph(" "));
    }
    
    private void agregarCabeceraPedido(Document doc, Pedido pedido, Font negrita, Font normal, SimpleDateFormat sdf) throws DocumentException {
        String fechaStr = pedido.getFecha() != null ? sdf.format(pedido.getFecha()) : "-";

        PdfPTable tablaCabecera = new PdfPTable(2);
        tablaCabecera.setWidthPercentage(100);
        tablaCabecera.setWidths(new float[]{1, 1});

        // Columna izquierda: cliente + seguimiento + pago
        Paragraph clienteInfo = crearInfoCliente(pedido, negrita, normal, sdf);
        
        // Columna derecha: datos factura
        Paragraph facturaInfo = crearInfoFactura(pedido, negrita, normal, fechaStr);

        tablaCabecera.addCell(clienteInfo);
        tablaCabecera.addCell(facturaInfo);

        doc.add(tablaCabecera);
        doc.add(new Paragraph(" "));
    }
    
    private Paragraph crearInfoCliente(Pedido pedido, Font negrita, Font normal, SimpleDateFormat sdf) {
        Paragraph clienteInfo = new Paragraph();
        clienteInfo.add(new Chunk("Cliente: ", negrita));
        clienteInfo.add(new Chunk(pedido.getClienteNombre(), normal));
        clienteInfo.add(Chunk.NEWLINE);
        clienteInfo.add(new Chunk("ID Cliente: ", negrita));
        clienteInfo.add(new Chunk(pedido.getClienteId(), normal));
        clienteInfo.add(Chunk.NEWLINE);

        if (pedido.getEmpleadoAsignadoNombre() != null) {
            clienteInfo.add(new Chunk("Atendido por: ", negrita));
            clienteInfo.add(new Chunk(pedido.getEmpleadoAsignadoNombre(), normal));
            clienteInfo.add(Chunk.NEWLINE);
        }

        // Tipo de entrega y dirección
        String tipoEntrega = pedido.getTipoEntrega();
        if (tipoEntrega != null && !tipoEntrega.isEmpty()) {
            String tipoTexto = "RETIRO_TIENDA".equals(tipoEntrega) ? "Retiro en tienda" : "Entrega a domicilio";
            clienteInfo.add(new Chunk("Tipo entrega: ", negrita));
            clienteInfo.add(new Chunk(tipoTexto, normal));
            clienteInfo.add(Chunk.NEWLINE);
        }
        if (pedido.getDireccionEntrega() != null && !pedido.getDireccionEntrega().isEmpty()) {
            clienteInfo.add(new Chunk("Dirección: ", negrita));
            clienteInfo.add(new Chunk(pedido.getDireccionEntrega(), normal));
            clienteInfo.add(Chunk.NEWLINE);
        }

        agregarDatosSeguimiento(clienteInfo, pedido, negrita, normal, sdf);
        agregarDatosPago(clienteInfo, pedido, negrita, normal);
        
        return clienteInfo;
    }
    
    private void agregarDatosSeguimiento(Paragraph p, Pedido pedido, Font negrita, Font normal, SimpleDateFormat sdf) {
        if (pedido.getDespachoNombre() != null && !pedido.getDespachoNombre().trim().isEmpty()) {
            p.add(new Chunk("Despachador: ", negrita));
            p.add(new Chunk(pedido.getDespachoNombre(), normal));
            p.add(Chunk.NEWLINE);
        }

        if (pedido.getCodigoSeguimiento() != null && !pedido.getCodigoSeguimiento().trim().isEmpty()) {
            p.add(new Chunk("Código Seguimiento: ", negrita));
            p.add(new Chunk(pedido.getCodigoSeguimiento(), normal));
            p.add(Chunk.NEWLINE);
        }

        if (pedido.getEstadoDespacho() != null && !pedido.getEstadoDespacho().trim().isEmpty()) {
            p.add(new Chunk("Estado Despacho: ", negrita));
            p.add(new Chunk(pedido.getEstadoDespacho(), normal));
            p.add(Chunk.NEWLINE);
        }

        if (pedido.getUbicacionActual() != null && !pedido.getUbicacionActual().trim().isEmpty()) {
            p.add(new Chunk("Ubicación Actual: ", negrita));
            p.add(new Chunk(pedido.getUbicacionActual(), normal));
            p.add(Chunk.NEWLINE);
        }

        if (pedido.getFechaDespacho() != null) {
            p.add(new Chunk("Fecha Despacho: ", negrita));
            p.add(new Chunk(sdf.format(pedido.getFechaDespacho()), normal));
            p.add(Chunk.NEWLINE);
        }

        if (pedido.getFechaEntrega() != null) {
            p.add(new Chunk("Fecha Entrega: ", negrita));
            p.add(new Chunk(sdf.format(pedido.getFechaEntrega()), normal));
            p.add(Chunk.NEWLINE);
        }
    }
    
    private void agregarDatosPago(Paragraph p, Pedido pedido, Font negrita, Font normal) {
        if (pedido.getMetodoPago() != null && !pedido.getMetodoPago().trim().isEmpty()) {
            p.add(new Chunk("Método de Pago: ", negrita));
            p.add(new Chunk(pedido.getMetodoPago(), normal));
            p.add(Chunk.NEWLINE);
        }

        if (pedido.getEstadoPago() != null && !pedido.getEstadoPago().trim().isEmpty()) {
            p.add(new Chunk("Estado de Pago: ", negrita));
            p.add(new Chunk(pedido.getEstadoPago(), normal));
            p.add(Chunk.NEWLINE);
        }

        if (pedido.getTarjetaUltimos4() != null && !pedido.getTarjetaUltimos4().trim().isEmpty()) {
            p.add(new Chunk("Tarjeta (últimos 4): ", negrita));
            p.add(new Chunk("**** " + pedido.getTarjetaUltimos4(), normal));
            p.add(Chunk.NEWLINE);
        }
    }
    
    private Paragraph crearInfoFactura(Pedido pedido, Font negrita, Font normal, String fechaStr) {
        Paragraph facturaInfo = new Paragraph();
        facturaInfo.add(new Chunk("Nro. Factura: ", negrita));
        facturaInfo.add(new Chunk(
                pedido.getNumeroPedido() != null ? pedido.getNumeroPedido() : pedido.getId(), normal));
        facturaInfo.add(Chunk.NEWLINE);
        facturaInfo.add(new Chunk("Fecha: ", negrita));
        facturaInfo.add(new Chunk(fechaStr, normal));
        facturaInfo.add(Chunk.NEWLINE);
        facturaInfo.add(new Chunk("Estado del Pedido: ", negrita));
        facturaInfo.add(new Chunk(pedido.getEstado(), normal));
        
        return facturaInfo;
    }
    
    private double agregarTablaItems(Document doc, Pedido pedido, Font negrita, Font normal) throws DocumentException {
        PdfPTable tablaItems = new PdfPTable(4);
        tablaItems.setWidthPercentage(100);
        tablaItems.setWidths(new float[]{4, 1, 2, 2});

        // Encabezados
        tablaItems.addCell(new Phrase("Producto", negrita));
        tablaItems.addCell(new Phrase("Cant.", negrita));
        tablaItems.addCell(new Phrase("P. Unitario", negrita));
        tablaItems.addCell(new Phrase("Subtotal", negrita));

        double subtotal = 0.0;
        List<ItemPedido> items = pedido.getItems();
        
        // Crear Phrase una sola vez para reutilizar (evita creación en loop)
        if (items != null) {
            for (ItemPedido item : items) {
                double sub = item.getCantidad() * item.getPrecio();
                subtotal += sub;

                tablaItems.addCell(new Phrase(item.getNombreProducto(), normal));
                tablaItems.addCell(new Phrase(String.valueOf(item.getCantidad()), normal));
                tablaItems.addCell(new Phrase(String.format("$ %.2f", item.getPrecio()), normal));
                tablaItems.addCell(new Phrase(String.format("$ %.2f", sub), normal));
            }
        }

        doc.add(tablaItems);
        doc.add(new Paragraph(" "));
        
        return subtotal;
    }
    
    private void agregarTotales(Document doc, Pedido pedido, double subtotal, Font negrita, Font normal) throws DocumentException {
        double ivaPorcentaje = (pedido.getIvaPorcentaje() > 0) ? pedido.getIvaPorcentaje() : 0.15;
        double ivaValor = (pedido.getIvaValor() > 0) ? pedido.getIvaValor() : (subtotal * ivaPorcentaje);
        // Usar el valor de envío del pedido tal cual está guardado (0 para retiro, >0 para domicilio)
        double envio = pedido.getEnvio();
        double totalFinal = (pedido.getTotalFinal() > 0)
                ? pedido.getTotalFinal()
                : (subtotal + ivaValor + envio);

        PdfPTable tablaTotales = new PdfPTable(2);
        tablaTotales.setWidthPercentage(45);
        tablaTotales.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tablaTotales.setWidths(new float[]{2, 1});

        tablaTotales.addCell(new Phrase("SUBTOTAL:", negrita));
        tablaTotales.addCell(new Phrase(String.format("$ %.2f", subtotal), normal));

        tablaTotales.addCell(new Phrase(String.format("IVA (%.0f%%):", ivaPorcentaje * 100), negrita));
        tablaTotales.addCell(new Phrase(String.format("$ %.2f", ivaValor), normal));

        tablaTotales.addCell(new Phrase("ENVÍO:", negrita));
        tablaTotales.addCell(new Phrase(String.format("$ %.2f", envio), normal));

        tablaTotales.addCell(new Phrase("TOTAL FINAL:", negrita));
        tablaTotales.addCell(new Phrase(String.format("$ %.2f", totalFinal), negrita));

        doc.add(tablaTotales);
    }
}
