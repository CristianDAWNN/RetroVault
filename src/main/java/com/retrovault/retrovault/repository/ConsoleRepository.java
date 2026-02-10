package com.retrovault.retrovault.repository;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ConsoleRepository extends JpaRepository<Console, Long> {
    
    List<Console> findByUser(User user);
    boolean existsByNameAndUser(String name, User user);
    
    long countByUser(User user);

    @Query("SELECT c.name, c.company, COUNT(c) as total " +
           "FROM Console c " +
           "GROUP BY c.name, c.company " +
           "ORDER BY total DESC")
    List<Object[]> findMostPopularConsoles(Pageable pageable);
}