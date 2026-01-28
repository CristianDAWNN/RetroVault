package com.retrovault.retrovault.service; // Fíjate que el paquete coincida con tu estructura

import com.retrovault.retrovault.model.Console;
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
}