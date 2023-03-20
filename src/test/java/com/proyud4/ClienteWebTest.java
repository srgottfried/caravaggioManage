package com.proyud4;



import com.proyud4.model.entity.Cliente;
import com.proyud4.model.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ClienteWebTest {

    @Autowired
    private ClienteService clienteService;


    @Test
    public void buscarClienteEnBaseDatos() {
        // GIVEN
        // Cargados datos de prueba del fichero datos-test.sql

        // WHEN

        Cliente cliente = clienteService.findById(1L);

        // THEN
        assertThat(cliente).isNotNull();
        assertThat(cliente.getId()).isEqualTo(1L);
        assertThat(cliente.getNombre()).isEqualTo("Manuel");
    }


    @Test
    public void buscarClientePorId() {
        // GIVEN
        // Cargados datos de prueba del fichero datos-test.sql

        // WHEN
        Cliente cliente = clienteService.findById(1L);

        // THEN
        assertThat(cliente.getNombre()).isEqualTo("Manuel");
    }

}

