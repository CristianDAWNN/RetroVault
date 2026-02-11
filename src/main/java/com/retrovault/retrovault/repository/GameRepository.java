package com.retrovault.retrovault.repository;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.Game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByCreatedBy(String username);
    List<Game> findByTitleContainingIgnoreCaseAndCreatedBy(String title, String createdBy);
    boolean existsByTitleAndConsole(String title, Console console);
    List<Game> findTop6ByCoverImgNotNullOrderByCreatedAtDesc();
    long countByCreatedBy(String username);
    
    // Top Géneros
    @Query("SELECT g.genre, COUNT(g) as c FROM Game g GROUP BY g.genre ORDER BY c DESC")
    List<Object[]> findTopGenres(Pageable pageable);

    // --- TOP JUEGOS MEDIA ---
    @Query("SELECT g.title, AVG(g.rate) as avgRate, MAX(g.coverImg) " +
           "FROM Game g " +
           "WHERE g.rate IS NOT NULL " +
           "GROUP BY g.title " +
           "ORDER BY avgRate DESC")
    List<Object[]> findTopRatedTitles(Pageable pageable);

    // Busca juegos creados lista de usuarios y los ordena por fecha
    List<Game> findByCreatedByInOrderByCreatedAtDesc(List<String> usernames);
}