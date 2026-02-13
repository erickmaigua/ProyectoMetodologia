package com.zapateria;

<<<<<<< HEAD
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
=======
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZapateriaApplication {

<<<<<<< HEAD
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
=======
    public static void main(String[] args) {
        SpringApplication.run(ZapateriaApplication.class, args);

        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║   Sistema de Zapatería - Versión Simple      ║");
        System.out.println("║   Servidor iniciado correctamente            ║");
        System.out.println("║   URL: http://localhost:8080                  ║");
        System.out.println("╚═══════════════════════════════════════════════╝\n");

        System.out.println("📚 Endpoints disponibles:");
        System.out.println("  • POST /api/usuarios/login         - Login");
        System.out.println("  • POST /api/usuarios/registro      - Registro");
        System.out.println("  • GET  /api/productos              - Listar productos");
        System.out.println("  • POST /api/productos              - Crear producto");
        System.out.println("  • GET  /api/clientes               - Listar clientes");
        System.out.println("  • POST /api/pedidos                - Crear pedido");
        System.out.println();
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
    }
}