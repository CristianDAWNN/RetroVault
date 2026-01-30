package com.retrovault.retrovault.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "launch_date")
    private LocalDate launchDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status;

    private Integer rate;
    
    @Column(name = "cover_img")
    private String coverImg;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "image_url") // Guardaremos la ruta o nombre del archivo
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "console_id", nullable = false)
    @ToString.Exclude
    private Console console;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}