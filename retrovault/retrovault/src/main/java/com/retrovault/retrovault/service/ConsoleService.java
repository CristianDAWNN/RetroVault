package com.retrovault.retrovault.service;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.ConsoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsoleService {

    @Autowired
    private ConsoleRepository consoleRepository;

    public List<Console> getAllConsoles() {
        return consoleRepository.findAll();
    }

    public void saveConsole(Console console) {
        consoleRepository.save(console);
    }

    // CORRECCIÓN 1: Este método debe ser void y cerrar su llave antes de empezar el siguiente
    public void deleteConsole(Long id) {
        consoleRepository.deleteById(id);
    } 

    // CORRECCIÓN 2: Este método va fuera, separado del anterior
    public List<Console> getConsolesByUser(User user) {
        return consoleRepository.findByUser(user);
    }

    public boolean existsByNameAndUser(String name, User user) {
        return consoleRepository.existsByNameAndUser(name, user);
    }
}