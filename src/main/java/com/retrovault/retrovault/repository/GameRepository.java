package com.retrovault.retrovault.repository;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    
    // Métodos básicos
    List<Game> findByCreatedBy(String username);
    List<Game> findByTitleContainingIgnoreCaseAndCreatedBy(String title, String createdBy);
    boolean existsByTitleAndConsole(String title, Console console);
    List<Game> findTop6ByCoverImgNotNullOrderByCreatedAtDesc();
    long countByCreatedBy(String username);
    
    // ESTADÍSTICAS
    
    // Top Géneros
    @Query("SELECT g.genre, COUNT(g) as c FROM Game g GROUP BY g.genre ORDER BY c DESC")
    List<Object[]> findTopGenres(Pageable pageable);

    // Top Juegos (Media de valoración)
    @Query("SELECT g.title, AVG(g.rate) as avgRate, MAX(g.coverImg) " +
           "FROM Game g " +
           "WHERE g.rate IS NOT NULL " +
           "GROUP BY g.title " +
           "ORDER BY avgRate DESC")
    List<Object[]> findTopRatedTitles(Pageable pageable);

    // Top Consolas
    @Query("SELECT MAX(c.name), COUNT(c) as total " +
           "FROM Console c " +
           "GROUP BY LOWER(TRIM(c.name)) " +
           "ORDER BY total DESC")
    List<Object[]> findRankedConsoles(Pageable pageable);

    // MÉTODOS PARA ACTIVIDAD

    // Busca juegos de una lista de usuarios
    List<Game> findByUserInOrderByCreatedAtDesc(List<User> users, Pageable pageable);

    // Busca juegos de un solo usuario especifico
    List<Game> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Legacy
    List<Game> findByCreatedByInOrderByCreatedAtDesc(List<String> usernames);
    
    // Busca los 5 mejores juegos de un género específico de la BBDO
    List<Game> findTop5ByGenreAndRateIsNotNullOrderByRateDesc(String genre);
}