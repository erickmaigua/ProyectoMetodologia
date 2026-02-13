package com.zapateria.config;

<<<<<<< HEAD
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.zapateria.models.NodoTransito;
import com.zapateria.models.Producto;
import com.zapateria.models.Usuario;
import com.zapateria.repositories.NodoTransitoRepository;
import com.zapateria.repositories.ProductoRepository;
import com.zapateria.repositories.UsuarioRepository;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private NodoTransitoRepository nodoTransitoRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Value("${app.admin.username}")
    private String adminUsername;
    
    @Value("${app.admin.password}")
    private String adminPassword;
    
    @Value("${app.admin.email}")
    private String adminEmail;
    
    @Value("${app.admin.nombre}")
    private String adminNombre;
    
    @Value("${app.admin.apellido}")
    private String adminApellido;
    
    @Value("${app.admin.telefono}")
    private String adminTelefono;

    @Override
    public void run(String... args) throws Exception {
        // Crear admin - solo si no existe
        Usuario adminExistente = usuarioRepository.findByUsername(adminUsername);
        
        if (adminExistente == null) {
            Usuario admin = new Usuario();
            admin.setNombre(adminNombre);
            admin.setApellido(adminApellido);
            admin.setEmail(adminEmail);
            admin.setUsername(adminUsername);
            admin.setPassword(adminPassword);
            admin.setTelefono(adminTelefono);
            admin.setRol("ADMINISTRADOR");
            
            usuarioRepository.save(admin);
            logger.info("Usuario administrador creado exitosamente: {}", adminUsername);
        } else {
            logger.info("Usuario administrador ya existe en la base de datos: {}", adminUsername);
        }
        
        // Inicializar nodos de tránsito si no existen
        if (nodoTransitoRepository.count() == 0) {
            inicializarNodosTransito();
            logger.info("Nodos de tránsito inicializados automáticamente");
        }
        
        // NO CARGAR PRODUCTOS AUTOMÁTICAMENTE
        // Se deben agregar manualmente a través del panel administrativo
        logger.info("DataInitializer completado - Los productos deben agregarse manualmente a través del panel");
    }
    
    private void inicializarNodosTransito() {
        logger.info("Inicializando nodos de tránsito de ejemplo...");
        
        // Nodo 1: Quito Norte
        NodoTransito quitoNorte = new NodoTransito();
        quitoNorte.setNombre("Centro Distribución Norte");
        quitoNorte.setCiudad("Quito");
        quitoNorte.setLatitud(-0.1807);
        quitoNorte.setLongitud(-78.4678);
        Map<String, Double> tiemposQN = new HashMap<>();
        tiemposQN.put("guayaquil", 8.5);
        tiemposQN.put("cuenca", 6.0);
        quitoNorte.setTiemposEstimados(tiemposQN);
        nodoTransitoRepository.save(quitoNorte);
        
        // Nodo 2: Guayaquil
        NodoTransito guayaquil = new NodoTransito();
        guayaquil.setId("guayaquil");
        guayaquil.setNombre("Centro Distribución Guayaquil");
        guayaquil.setCiudad("Guayaquil");
        guayaquil.setLatitud(-2.1894);
        guayaquil.setLongitud(-79.8884);
        Map<String, Double> tiemposGYE = new HashMap<>();
        tiemposGYE.put("cuenca", 4.0);
        guayaquil.setTiemposEstimados(tiemposGYE);
        nodoTransitoRepository.save(guayaquil);
        
        // Nodo 3: Cuenca
        NodoTransito cuenca = new NodoTransito();
        cuenca.setId("cuenca");
        cuenca.setNombre("Centro Distribución Cuenca");
        cuenca.setCiudad("Cuenca");
        cuenca.setLatitud(-2.9001);
        cuenca.setLongitud(-79.0059);
        nodoTransitoRepository.save(cuenca);
        
        logger.info("Nodos de tránsito inicializados exitosamente");
    }
    
    // MÉTODO DESHABILITADO: Los productos deben agregarse manualmente a través del panel administrativo
    // Este método se mantenía como referencia pero ya no se ejecuta automáticamente
    /*
    private void inicializarProductosPorEdad() {
        logger.info("Inicializando productos por categorías de edad...");
        
        // Productos para NIÑOS
        Producto zapatoNino1 = new Producto();
        zapatoNino1.setNombre("Zapatillas Kids Adventure");
        zapatoNino1.setDescripcion("Zapatillas deportivas para niños, ideales para el día a día");
        zapatoNino1.setMarca("KidsRun");
        zapatoNino1.setCategoria("Deportivo");
        zapatoNino1.setCategoriaEdad("NINO");
        zapatoNino1.setPrecio(35.99);
        zapatoNino1.setStock(25);
        zapatoNino1.setImagen("/images/nino1.jpg");
        productoRepository.save(zapatoNino1);
        
        Producto zapatoNino2 = new Producto();
        zapatoNino2.setNombre("Botas Escolares Junior");
        zapatoNino2.setDescripcion("Botas escolares resistentes para niños");
        zapatoNino2.setMarca("SchoolKids");
        zapatoNino2.setCategoria("Escolar");
        zapatoNino2.setCategoriaEdad("NINO");
        zapatoNino2.setPrecio(29.99);
        zapatoNino2.setStock(30);
        zapatoNino2.setImagen("/images/nino2.jpg");
        productoRepository.save(zapatoNino2);
        
        // Productos para ADOLESCENTES
        Producto zapatoAdolescente1 = new Producto();
        zapatoAdolescente1.setNombre("Sneakers Teen Style");
        zapatoAdolescente1.setDescripcion("Zapatillas urbanas para adolescentes con estilo moderno");
        zapatoAdolescente1.setMarca("TeenTrend");
        zapatoAdolescente1.setCategoria("Urbano");
        zapatoAdolescente1.setCategoriaEdad("ADOLESCENTE");
        zapatoAdolescente1.setPrecio(55.99);
        zapatoAdolescente1.setStock(20);
        zapatoAdolescente1.setImagen("/images/teen1.jpg");
        productoRepository.save(zapatoAdolescente1);
        
        Producto zapatoAdolescente2 = new Producto();
        zapatoAdolescente2.setNombre("Deportivas Youth Pro");
        zapatoAdolescente2.setDescripcion("Zapatillas deportivas de alto rendimiento para jóvenes");
        zapatoAdolescente2.setMarca("YouthSport");
        zapatoAdolescente2.setCategoria("Deportivo");
        zapatoAdolescente2.setCategoriaEdad("ADOLESCENTE");
        zapatoAdolescente2.setPrecio(65.99);
        zapatoAdolescente2.setStock(18);
        zapatoAdolescente2.setImagen("/images/teen2.jpg");
        productoRepository.save(zapatoAdolescente2);
        
        // Productos para ADULTOS
        Producto zapatoAdulto1 = new Producto();
        zapatoAdulto1.setNombre("Zapatos Formales Executive");
        zapatoAdulto1.setDescripcion("Zapatos de cuero para oficina y ocasiones formales");
        zapatoAdulto1.setMarca("BusinessPro");
        zapatoAdulto1.setCategoria("Formal");
        zapatoAdulto1.setCategoriaEdad("ADULTO");
        zapatoAdulto1.setPrecio(89.99);
        zapatoAdulto1.setStock(15);
        zapatoAdulto1.setImagen("/images/adulto1.jpg");
        productoRepository.save(zapatoAdulto1);
        
        Producto zapatoAdulto2 = new Producto();
        zapatoAdulto2.setNombre("Running Pro Adult");
        zapatoAdulto2.setDescripcion("Zapatillas de running profesionales para adultos");
        zapatoAdulto2.setMarca("RunMaster");
        zapatoAdulto2.setCategoria("Deportivo");
        zapatoAdulto2.setCategoriaEdad("ADULTO");
        zapatoAdulto2.setPrecio(95.99);
        zapatoAdulto2.setStock(22);
        zapatoAdulto2.setImagen("/images/adulto2.jpg");
        productoRepository.save(zapatoAdulto2);
        
        logger.info("Productos por categorías de edad inicializados exitosamente");
    }
    */
=======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.zapateria.models.Usuario;
import com.zapateria.repositories.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        Usuario adminExistente = usuarioRepository.findByUsername("admin");
        
        if (adminExistente == null) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setEmail("admin@zapateria.com");
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setTelefono("0999999999");
            admin.setRol("ADMINISTRADOR");
            
            usuarioRepository.save(admin);
            
        } else {
            System.out.println("✅ Administrador ya existe en la base de datos");
        }
        
        
    }
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
}