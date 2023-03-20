package com.proyud4.model.service;

import com.proyud4.model.entity.Cliente;
import com.proyud4.model.entity.Factura;
import com.proyud4.model.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IClienteService {

    void save(Cliente cliente);

    Cliente findById(Long id);

    List<Cliente> findAll();

    void deleteById(Long id);

    Page<Cliente> findAll(Pageable pageable);

    List<Producto> findByNombre(String term);

    void saveFactura(Factura factura);

    Producto findProductById(Long id);

    Factura findFacturaById(Long id);

    void deleteFactura(Long id);

    Factura fetchByIdWithClienteFacturaProducto(Long id);

    Cliente fetchByIdWithFacturas(Long id);
}
