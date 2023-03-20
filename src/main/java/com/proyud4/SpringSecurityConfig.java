package com.proyud4;

import com.proyud4.auth.handler.LoginSuccessHandler;
import com.proyud4.model.service.JpaUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Activamos el uso de anotaciones (anotacioón @Secured("ROLE_LOQUESEA") para dar seguridad a los endpoints.
// Esto lo hacemos limitando el acceso por role a los controladores (y por tanto a sus endpoints)
@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginSuccessHandler succesHandler;

    @Autowired
    private JpaUserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración para el acceso seguro a endpoints a través de roles.
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // definimos permisos para las distintas rutas de acceso
        http.authorizeRequests()
                // rutas de acceso público
                .antMatchers(
                        "/",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/clientes").permitAll()
                .anyRequest().authenticated()
                // implementamos login y logout
                .and()
                .formLogin().successHandler(succesHandler).loginPage("/login").permitAll()
                .and()
                .logout().permitAll();
                // implementamos ventana de acceso denegado

    }

    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception {

        builder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

}
