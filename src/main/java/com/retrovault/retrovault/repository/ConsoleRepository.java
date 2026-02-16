package com.retrovault.retrovault.repository;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;

// Interfaz que gestiona el acceso a datos para la entidad Console
public interface ConsoleRepository extends JpaRepository<Console, Long> {
    
    // Busca todas las consolas que pertenecen a un usuario concreto
    List<Console> findByUser(User user);

    // Para no repetir consolas en el mismo user
    boolean existsByNameAndUser(String name, User user);
    
    // Cuenta el número total de consolas registradas por un user
    long countByUser(User user);

    // Consulta personalizada JPQL para obtener las consolas más populares de la bbdoo
    @Query("SELECT c.name, c.company, COUNT(c) as total " +
           "FROM Console c " +
           "GROUP BY c.name, c.company " +
           "ORDER BY total DESC")
    List<Object[]> findMostPopularConsoles(Pageable pageable);
}