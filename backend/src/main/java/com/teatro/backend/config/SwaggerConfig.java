package com.teatro.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Sistema de Reservas del Teatro Gran Espectáculo")
                        .version("1.0.0")
                        .description("""
                    Backend del sistema de gestión de reservas del Teatro Gran Espectáculo.
                    Permite administrar eventos, reservas y clientes, con control de disponibilidad
                    y beneficios por fidelización para asistentes frecuentes.
                    """)
                        .license(new License()
                                .name("Propietaria - Uso Técnico Evaluativo")
                                .url("https://www.certant.com/")));
    }
}

