package com.zapateria.config;

<<<<<<< HEAD
<<<<<<< HEAD
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
=======
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
<<<<<<< HEAD
<<<<<<< HEAD
    
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);
=======
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
    
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configurar recursos estáticos
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0);
        
<<<<<<< HEAD
<<<<<<< HEAD
        logger.info("Recursos estáticos configurados: classpath:/static/");
=======
        System.out.println("✅ Recursos estáticos configurados: classpath:/static/");
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
        logger.info("Recursos estáticos configurados: classpath:/static/");
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirigir la raíz al index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        
<<<<<<< HEAD
<<<<<<< HEAD
        logger.info("Vista raíz configurada: / -> /index.html");
=======
        System.out.println("✅ Vista raíz configurada: / -> /index.html");
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
        logger.info("Vista raíz configurada: / -> /index.html");
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configurar CORS
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
        
<<<<<<< HEAD
<<<<<<< HEAD
        logger.info("CORS configurado para permitir todos los orígenes");
=======
        System.out.println("✅ CORS configurado");
>>>>>>> a09a18c754bf93d4a0edc2246d392dfb48fed16a
=======
        logger.info("CORS configurado para permitir todos los orígenes");
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
    }
}