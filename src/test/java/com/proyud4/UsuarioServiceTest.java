package com.proyud4;


import com.proyud4.model.dao.UsuarioRepository;
import com.proyud4.model.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    public void crearUsuario() throws Exception {

        // GIVEN
        Usuario usuario = new Usuario();
        usuario.setUsername("test");
        usuario.setPassword("test");


        assertThat(usuario.getUsername()).isEqualTo("test");
        assertThat(usuario.getPassword()).isEqualTo("test");
    }

    @Test
    public void crearUsuarioBaseDatos() throws Exception {

        // GIVEN
        Usuario usuario = new Usuario();
        usuario.setUsername("test");
        usuario.setPassword("test");

        // WHEN

        usuarioRepository.save(usuario);

        // THEN
        assertThat(usuario.getId()).isNotNull();
        assertThat(usuario.getUsername()).isEqualTo("test");
        assertThat(usuario.getPassword()).isEqualTo("test");
    }

    @Test
    public void buscarUsuarioEnBaseDatos() {
        // GIVEN
        // Cargados datos de prueba del fichero datos-test.sql

        // WHEN

        Usuario usuario = usuarioRepository.findById(1L).orElse(null);

        // THEN
        assertThat(usuario).isNotNull();
        assertThat(usuario.getId()).isEqualTo(1L);
        assertThat(usuario.getUsername()).isEqualTo("admin");
    }


    @Test
    public void buscarUsuarioPorUsername() {
        // GIVEN
        // Cargados datos de prueba del fichero datos-test.sql

        // WHEN
        Usuario usuario = usuarioRepository.findByUsername("admin");

        // THEN
        assertThat(usuario.getUsername()).isEqualTo("admin");
    }
}