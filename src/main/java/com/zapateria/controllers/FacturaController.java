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
<<<<<<< HEAD
<<<<<<< HEAD
@CrossOrigin(origins = "*") // ajusta si usas otro puerto para front
=======
@CrossOrigin(origins = "http://localhost:8080") // ajusta si usas otro puerto para front
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
@CrossOrigin(origins = "http://localhost:8080") // ajusta si usas otro puerto para front
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
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
