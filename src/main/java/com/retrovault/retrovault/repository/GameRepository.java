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
    
    // Métodos de consulta básicos y filtrado por users
    List<Game> findByCreatedBy(String username);
    List<Game> findByTitleContainingIgnoreCaseAndCreatedBy(String title, String createdBy);
    boolean existsByTitleAndConsole(String title, Console console);
    
    // Obtiene los últimos juegos añadidos que contienen imagen de portada para el index
    List<Game> findTop6ByCoverImgNotNullOrderByCreatedAtDesc();
    
    long countByCreatedBy(String username);
    
    // --- CONSULTAS DE ESTADÍSTICAS Y RANKINGS ---
    
    // Obtiene el conteo de juegos por genero para las gráficas
    @Query("SELECT g.genre, COUNT(g) as c FROM Game g GROUP BY g.genre ORDER BY c DESC")
    List<Object[]> findTopGenres(Pageable pageable);

    // Calcula el ranking de títulos mejor valorados por la comunidad
    @Query("SELECT g.title, AVG(g.rate) as avgRate, MAX(g.coverImg) " +
           "FROM Game g " +
           "WHERE g.rate IS NOT NULL " +
           "GROUP BY g.title " +
           "ORDER BY avgRate DESC")
    List<Object[]> findTopRatedTitles(Pageable pageable);

    // Genera el ranking de consolas más utilizadas
    @Query("SELECT MAX(c.name), COUNT(c) as total " +
           "FROM Console c " +
           "GROUP BY LOWER(TRIM(c.name)) " +
           "ORDER BY total DESC")
    List<Object[]> findRankedConsoles(Pageable pageable);

    // --- LÓGICA DEL SISTEMA SOCIAL ---

    // Recupera la actividad reciente de una lista de usuarios
    List<Game> findByUserInOrderByCreatedAtDesc(List<User> users, Pageable pageable);

    // Recupera la actividad reciente de un usuario específico
    List<Game> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<Game> findByCreatedByInOrderByCreatedAtDesc(List<String> usernames);
    
    // Recupera títulos destacados de un género para el motor de recomendaciones
    List<Game> findTop5ByGenreAndRateIsNotNullOrderByRateDesc(String genre);
}