package com.retrovault.retrovault.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 4, max = 20, message = "El usuario debe tener entre 4 y 20 caracteres")
    @Pattern(regexp = "^\\S+$", message = "El usuario no puede contener espacios")
    private String username;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debes introducir un correo válido")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    @Pattern(regexp = "^\\S+$", message = "La contraseña no puede contener espacios")
    private String password;

    private String role;

    private String avatar;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación de uno a muchos con la entidad Console
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Console> consoles;

    // Atributos para el sistema de exp
    private int level = 1;
    private int experience = 0;
    
    // Calcula la experiencia necesaria para subir al siguiente nivel
    public int getXpToNextLevel() {
        return this.level * 100; 
    }

    // Calcula el porcentaje de progreso de nivel para la barra de exp
    public int getExpPercentage() {
        int meta = getXpToNextLevel();
        if (meta == 0) return 0;
        return (int) ((double) this.experience / meta * 100);
    }

    // Seguidos
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_follows", 
        joinColumns = @JoinColumn(name = "follower_id"), 
        inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    private List<User> following = new ArrayList<>();

    // Seguidores
    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private List<User> followers = new ArrayList<>();

    // Lógica para seguir a un user
    public void follow(User userToFollow) {
        if (this.following == null) {
            this.following = new ArrayList<>();
        }
        if (!this.following.contains(userToFollow)) {
            this.following.add(userToFollow);
        }
    }

    // Lógica para dejar de seguir a un usuario
    public void unfollow(User userToUnfollow) {
        if (this.following != null) {
            this.following.remove(userToUnfollow);
        }
    }

    // Asignación automática de fecha al crear el registro
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Actualización automática de fecha al modificar el registro
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}