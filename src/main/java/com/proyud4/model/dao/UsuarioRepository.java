package com.proyud4.model.dao;

import com.proyud4.model.entity.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    public Usuario findByUsername(String username);
}
