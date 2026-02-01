package com.retrovault.retrovault.repository;

import com.retrovault.retrovault.model.Game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByCreatedBy(String username);
    List<Game> findByTitleContainingIgnoreCaseAndCreatedBy(String title, String createdBy);
}