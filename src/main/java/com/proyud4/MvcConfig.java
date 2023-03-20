package com.proyud4;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Controlador parametrizable usado para lanzar la vista personalizada del
     * error 403 en lugar de la por defecto.
     * <p>
     * Se hace uso de este formato de controlador parametrizable dada la simplicidad
     * del controlador que estamos implementando.
     *
     * @param registry
     */
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/error_403").setViewName("error_403");
    }
}
