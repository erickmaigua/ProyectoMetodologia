package com.zapateria.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.zapateria.models.Usuario;
import com.zapateria.repositories.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    
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
    }
}