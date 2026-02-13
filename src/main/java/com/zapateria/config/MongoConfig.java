package com.zapateria.config;

<<<<<<< HEAD
import com.zapateria.exceptions.MongoConnectionException;
=======
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);
    
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;
    
    @Override
    protected String getDatabaseName() {
        // Extrae el nombre de la base de datos del URI
        ConnectionString connString = new ConnectionString(mongoUri);
        String database = connString.getDatabase();
        return database != null ? database : "zapateria_db";
    }
    
    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        // Configuración optimizada para MongoDB Atlas
        builder
            .applyConnectionString(new ConnectionString(mongoUri))
            .serverApi(ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build())
            .applyToConnectionPoolSettings(settings -> {
                settings
                    .maxSize(100)
                    .minSize(5)
                    .maxWaitTime(2, TimeUnit.MINUTES)
                    .maxConnectionIdleTime(10, TimeUnit.MINUTES);
            })
            .applyToSocketSettings(settings -> {
                settings
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS);
            });
        
        logger.info("MongoDB configurado para Atlas con Server API v1");
    }
    
    @Bean
    public MongoDatabaseFactory mongoDbFactory() {
        try {
            MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(mongoClient(), getDatabaseName());
            logger.info("Conexión exitosa a MongoDB Atlas - Base de datos: {}", getDatabaseName());
            return factory;
        } catch (Exception e) {
            logger.error("Error al conectar con MongoDB Atlas: {}", e.getMessage());
<<<<<<< HEAD
            throw new MongoConnectionException("No se pudo conectar a MongoDB Atlas. Verifica tu conexión y credenciales.", e);
=======
            throw new RuntimeException("No se pudo conectar a MongoDB Atlas. Verifica tu conexión y credenciales.", e);
>>>>>>> 8acb40e3ef805217e97bca6b237f58c67bb14c52
        }
    }
    
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }
}
