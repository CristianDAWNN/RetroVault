package com.retrovault.retrovault.service;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Servicio encargado de la gestion de juegos
@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    // Obtiene la colección completa de juegos registrados en la plataforma
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    // Guarda un nuevo juego o actualiza uno existente en la base de datos
    public void saveGame(Game game) {
        gameRepository.save(game);
    }

    // Elimina un juego
    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }
    
    // Busca un juego específico por ID, gestionando la posibilidad de que no exista
    public Game getGameById(Long id) {
        return gameRepository.findById(id).orElse(null);
    }

    // Recupera la lista de juegos creados por un usuario
    public List<Game> getGamesByUser(String username) {
        return gameRepository.findByCreatedBy(username);
    }

    // Implementa la lógica de filtrado para el buscador de la biblioteca personal
    public List<Game> searchGames(String keyword, String username) {
        if (keyword != null && !keyword.isEmpty()) {
            // Realiza una búsqueda parcial ignorando mayúsculas y minúsculas
            return gameRepository.findByTitleContainingIgnoreCaseAndCreatedBy(keyword, username);
        }
        return gameRepository.findByCreatedBy(username);
    }

    // Valida si un juego ya está registrado para una consola específica del usuario
    public boolean existsByTitleAndConsole(String title, Console console) {
        return gameRepository.existsByTitleAndConsole(title, console);
    }
}