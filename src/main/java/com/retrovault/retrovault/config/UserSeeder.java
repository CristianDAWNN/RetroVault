package com.retrovault.retrovault.config;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.ConsoleRepository;
import com.retrovault.retrovault.repository.GameRepository;
import com.retrovault.retrovault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class UserSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private ConsoleRepository consoleRepository;
    @Autowired private GameRepository gameRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // --- GAMER 6: EL FAN DE SEGA ---
        User g6 = createUser("gamer6", "gamer6@test.com");
        Console megaDrive = createConsole("Mega Drive", "Sega", g6);
        createGame("Sonic The Hedgehog 2", "Plataformas", LocalDate.of(1992, 11, 21), 10, megaDrive, g6);
        createGame("Streets of Rage 2", "Acción", LocalDate.of(1992, 12, 20), 9, megaDrive, g6);
        
        Console dreamcast = createConsole("Dreamcast", "Sega", g6);
        createGame("Crazy Taxi", "Carreras", LocalDate.of(1999, 1, 1), 8, dreamcast, g6);

        // --- GAMER 7: EL FAN DE NINTENDO ---
        User g7 = createUser("gamer7", "gamer7@test.com");
        Console snes = createConsole("Super Nintendo", "Nintendo", g7);
        createGame("Super Metroid", "Aventura", LocalDate.of(1994, 3, 19), 10, snes, g7);
        createGame("Donkey Kong Country", "Plataformas", LocalDate.of(1994, 11, 21), 9, snes, g7);

        Console gb = createConsole("Game Boy", "Nintendo", g7);
        createGame("Tetris", "Puzzle", LocalDate.of(1989, 6, 14), 10, gb, g7);

        // --- GAMER 8: EL FAN DE SONY ---
        User g8 = createUser("gamer8", "gamer8@test.com");
        Console ps1 = createConsole("PlayStation 1", "Sony", g8);
        createGame("Gran Turismo 2", "Carreras", LocalDate.of(1999, 12, 11), 9, ps1, g8);
        createGame("Crash Bandicoot 3", "Plataformas", LocalDate.of(1998, 10, 31), 8, ps1, g8);

        Console ps2 = createConsole("PlayStation 2", "Sony", g8);
        createGame("God of War", "Acción", LocalDate.of(2005, 3, 22), 10, ps2, g8);

        // --- GAMER 9: MIXTO (XBOX & GAMECUBE) ---
        User g9 = createUser("gamer9", "gamer9@test.com");
        Console xbox = createConsole("Xbox Clásica", "Microsoft", g9);
        createGame("Halo: Combat Evolved", "Shooter", LocalDate.of(2001, 11, 15), 10, xbox, g9);

        Console gc = createConsole("GameCube", "Nintendo", g9);
        createGame("Super Smash Bros Melee", "Lucha", LocalDate.of(2001, 11, 21), 10, gc, g9);
        createGame("Luigi's Mansion", "Aventura", LocalDate.of(2001, 9, 14), 8, gc, g9);
        
        System.out.println("✅ ¡Datos sembrados correctamente!");
    }

    // --- MÉTODOS AUXILIARES ---

    private void deleteIfExists(String username) {
        User u = userRepository.findByUsername(username);
        if (u != null) {
            userRepository.delete(u);
            System.out.println("🗑️ Usuario antiguo borrado: " + username);
        }
    }

    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("1234"));
        user.setRole("USER");
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private Console createConsole(String name, String company, User user) {
        Console console = new Console();
        console.setName(name);
        console.setCompany(company); 
        console.setCreatedBy(user.getUsername());
        console.setUser(user); 
        return consoleRepository.save(console);
    }

    private void createGame(String title, String genre, LocalDate date, int rate, Console console, User user) {
        Game game = new Game();
        game.setTitle(title);
        game.setGenre(genre);
        game.setLaunchDate(date);
        game.setRate(rate);
        game.setConsole(console);
        game.setCreatedBy(user.getUsername());
        game.setStatus("Completado");
        game.setCreatedAt(LocalDateTime.now());
        gameRepository.save(game);
    }
}