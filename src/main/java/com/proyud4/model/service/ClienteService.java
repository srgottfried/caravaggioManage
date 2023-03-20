package com.proyud4.model.service;

import com.proyud4.model.dao.ClienteRepository;
import com.proyud4.model.dao.FacturaRepository;
import com.proyud4.model.dao.ProductoRepository;
import com.proyud4.model.entity.Cliente;
import com.proyud4.model.entity.Factura;
import com.proyud4.model.entity.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio asociado a la clase `Cliente` que implementa la operativa CRUD.
 */
@Service
public class ClienteService implements IClienteService {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ProductoRepository productoRepository;

    @Autowired
    FacturaRepository facturaRepository;

    @Transactional
    public void save(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente findById(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    @Transactional
    public List<Cliente> findAll() {
        return (List<Cliente>) clienteRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cliente> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByNombre(String term) {
        return productoRepository.findByNombre(term);
    }

    @Override
    @Transactional
    public void saveFactura(Factura factura) {
        facturaRepository.save(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findProductById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Factura findFacturaById(Long id) {
        return facturaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteFactura(Long id) {
        facturaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Factura fetchByIdWithClienteFacturaProducto(Long id) {
        return facturaRepository.fetchByIdWithClienteFacturaProducto(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente fetchByIdWithFacturas(Long id) {
        return clienteRepository.fetchByIdWithFacturas(id);
    }


}
