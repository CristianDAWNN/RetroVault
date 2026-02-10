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

    public void delete(Long id) {
        consoleRepository.deleteById(id);
    } 

    public List<Console> getConsolesByUser(User user) {
        return consoleRepository.findByUser(user);
    }

    public boolean existsByNameAndUser(String name, User user) {
        return consoleRepository.existsByNameAndUser(name, user);
    }
    
    public Console getConsoleById(Long id) {
        return consoleRepository.findById(id).orElse(null);
    }
}