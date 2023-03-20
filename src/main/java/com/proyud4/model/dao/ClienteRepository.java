package com.proyud4.model.dao;

import com.proyud4.model.entity.Cliente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ClienteRepository extends PagingAndSortingRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String s);

    @Query("select c from Cliente c left join fetch c.facturas f where c.id = ?1")
    Cliente fetchByIdWithFacturas(Long id);
}
