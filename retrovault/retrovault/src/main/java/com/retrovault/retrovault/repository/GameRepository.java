package com.retrovault.retrovault.repository;

import com.retrovault.retrovault.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    // Aquí pondremos búsquedas especiales en el futuro (ej: buscar por género)
}