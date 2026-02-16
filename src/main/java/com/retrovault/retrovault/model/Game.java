package com.retrovault.retrovault.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @NotBlank(message = "El título es obligatorio") 
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "El género es obligatorio")
    private String genre;

    @Column(name = "launch_date")
    private LocalDate launchDate; 

    private String status; // Estados: Pendiente, Jugando, Completado

    @Min(value = 0, message = "La nota mínima es 0")
    @Max(value = 10, message = "La nota máxima es 10")
    private Integer rate;

    @Column(name = "cover_img") 
    private String coverImg;

    // Campos de auditoría
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    // Relación Many-To-One: Varios juegos pertenecen a una consola
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "console_id")
    @NotNull(message = "Debes elegir una plataforma")
    private Console console;

    // Relación Many-To-One: Varios juegos pertenecen a un usuario
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    // Asignación automática de marcas de tiempo al crear el registro
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Actualización automática de la fecha de modificación
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}