package com.proyud4.model.dao;

import com.proyud4.model.entity.Factura;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface FacturaRepository extends CrudRepository<Factura, Long> {

    @Query("select f from Factura f join fetch f.cliente c join fetch f.items l join fetch l.producto where f.id = ?1")
    public Factura fetchByIdWithClienteFacturaProducto(Long id);
}
