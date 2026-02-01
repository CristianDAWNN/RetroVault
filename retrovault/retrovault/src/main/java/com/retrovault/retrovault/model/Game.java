package com.retrovault.retrovault.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate; // OJO: Importar LocalDate
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

    // FECHA DE LANZAMIENTO (Diagrama: LAUNCH_DATE)
    @Column(name = "launch_date")
    private LocalDate launchDate; 

    // ESTADO (Diagrama: STATUS) -> Ej: "Completado", "Pendiente"
    private String status;

    // VALORACIÓN (Diagrama: RATE) -> Ej: 0 a 10
    private Integer rate;

    // IMAGEN (Diagrama: COVER_IMG)
    // OJO: Antes lo llamábamos imageUrl, ahora lo cambiamos a coverImg para coincidir contigo
    @Column(name = "cover_img") 
    private String coverImg;

    // --- CAMPOS DE AUDITORÍA ---
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "created_by")
    private String createdBy;

    // RELACIÓN CON CONSOLA (Diagrama: CONSOLE_ID)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "console_id")
    private Console console;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}