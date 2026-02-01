package com.retrovault.retrovault.service;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public void saveGame(Game game) {
        gameRepository.save(game);
    }

    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }
    
    public Game getGameById(Long id) {
        return gameRepository.findById(id).orElse(null);
    }

    // Faltaba cerrar este método con }
    public List<Game> getGamesByUser(String username) {
        return gameRepository.findByCreatedBy(username);
    }

    // Aquí empieza la búsqueda (he borrado el duplicado malo)
    public List<Game> searchGames(String keyword, String username) {
        if (keyword != null && !keyword.isEmpty()) {
            return gameRepository.findByTitleContainingIgnoreCaseAndCreatedBy(keyword, username);
        }
        // Si la búsqueda está vacía, devolvemos la lista normal
        return gameRepository.findByCreatedBy(username);
        }
    public boolean existsByTitleAndConsole(String title, Console console) {
    return gameRepository.existsByTitleAndConsole(title, console);
    }
}