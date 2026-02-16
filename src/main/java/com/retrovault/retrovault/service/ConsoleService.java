package com.retrovault.retrovault.service;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.ConsoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Servicio encargado de la lógica de la gestión de consolas
@Service
public class ConsoleService {

    @Autowired
    private ConsoleRepository consoleRepository;

    // Recupera todas las consolas registradas en el sistema (admin)
    public List<Console> getAllConsoles() {
        return consoleRepository.findAll();
    }

    // Guarda una nueva consola o actualiza una existente en la base de datos
    public void saveConsole(Console console) {
        consoleRepository.save(console);
    }

    // Elimina una consola mediante su id
    public void delete(Long id) {
        consoleRepository.deleteById(id);
    } 

    // Recupera la lista de consolas vinculadas a un usuario concreto
    public List<Console> getConsolesByUser(User user) {
        return consoleRepository.findByUser(user);
    }

    // Verifica si un usuario ya posee una consola con un nombre específico
    public boolean existsByNameAndUser(String name, User user) {
        return consoleRepository.existsByNameAndUser(name, user);
    }
    
    // Obtiene una consola por su ID, devolviendo null si no se encuentra
    public Console getConsoleById(Long id) {
        return consoleRepository.findById(id).orElse(null);
    }
}