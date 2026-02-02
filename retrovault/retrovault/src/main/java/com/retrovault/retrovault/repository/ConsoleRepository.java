package com.retrovault.retrovault.repository;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsoleRepository extends JpaRepository<Console, Long> {
    List<Console> findByUser(User user);
    boolean existsByNameAndUser(String name, User user);
}