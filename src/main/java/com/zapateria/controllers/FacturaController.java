package com.zapateria.controllers;

import com.zapateria.services.FacturaPdfService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "http://localhost:8080") // ajusta si usas otro puerto para front
public class FacturaController {

    private final FacturaPdfService facturaPdfService;

    public FacturaController(FacturaPdfService facturaPdfService) {
        this.facturaPdfService = facturaPdfService;
    }

    @GetMapping("/pedido/{id}")
    public ResponseEntity<byte[]> generarFactura(@PathVariable String id) {
        byte[] pdf = facturaPdfService.generarFacturaPedido(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename("factura-pedido-" + id + ".pdf")
                        .build()
        );

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
