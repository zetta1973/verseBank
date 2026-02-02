package com.versebank.users;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración del slice de usuarios.
 * Habilita el escaneo de componentes en el paquete de usuarios.
 */
@Configuration
@ComponentScan(basePackages = "com.versebank.users")
public class UsersConfig {
}
