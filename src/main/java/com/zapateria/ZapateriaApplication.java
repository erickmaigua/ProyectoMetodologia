package com.zapateria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZapateriaApplication {

    private static final Logger logger = LoggerFactory.getLogger(ZapateriaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ZapateriaApplication.class, args);

        logger.info("╔═══════════════════════════════════════════════╗");
        logger.info("║   Sistema de Zapatería - Versión Simple      ║");
        logger.info("║   Servidor iniciado correctamente            ║");
        logger.info("║   URL: http://localhost:8080                  ║");
        logger.info("╚═══════════════════════════════════════════════╝");

        logger.info("📚 Endpoints disponibles:");
        logger.info("  • POST /api/usuarios/login         - Login");
        logger.info("  • POST /api/usuarios/registro      - Registro");
        logger.info("  • GET  /api/productos              - Listar productos");
        logger.info("  • POST /api/productos              - Crear producto");
        logger.info("  • GET  /api/clientes               - Listar clientes");
        logger.info("  • POST /api/pedidos                - Crear pedido");
    }
}