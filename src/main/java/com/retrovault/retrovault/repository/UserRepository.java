package com.retrovault.retrovault.repository;

import com.retrovault.retrovault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Interfaz que gestiona el acceso a datos para la entidad User
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Busca un usuario por su nombre de usuario (usado en login y perfiles)
    User findByUsername(String username);
    
    // Busca un usuario por su correo electrónico
    User findByEmail(String email);
}