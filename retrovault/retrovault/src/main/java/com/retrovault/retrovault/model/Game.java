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

    @Column(name = "launch_date")
    private LocalDate launchDate; 

    private String status;
    @Min(value = 0, message = "La nota mínima es 0")
    @Max(value = 10, message = "La nota máxima es 10")
    private Integer rate;

    @Column(name = "cover_img") 
    private String coverImg;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "created_by")
    private String createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "console_id")
    @NotNull(message = "Debes elegir una plataforma")
    private Console console;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}