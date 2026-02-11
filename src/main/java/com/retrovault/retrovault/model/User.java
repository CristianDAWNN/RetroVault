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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Console> consoles;

    //SISTEMA DE NIVELES
    private int level = 1;
    private int experience = 0;
    
    public int getXpToNextLevel() {
        return this.level * 100; 
    }

    public int getExpPercentage() {
        int meta = getXpToNextLevel();
        if (meta == 0) return 0;
        return (int) ((double) this.experience / meta * 100);
    }

    //SISTEMA DE FOLLOWS
    
    //(Following)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_follows", // Nombre de la tabla intermedia
        joinColumns = @JoinColumn(name = "follower_id"), // Yo (el que sigue)
        inverseJoinColumns = @JoinColumn(name = "followed_id") // Al que sigo
    )
    private List<User> following = new ArrayList<>();

    //(Followers)
    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private List<User> followers = new ArrayList<>();

    public void follow(User userToFollow) {
        if (this.following == null) {
            this.following = new ArrayList<>();
        }
        if (!this.following.contains(userToFollow)) {
            this.following.add(userToFollow);
        }
    }

    public void unfollow(User userToUnfollow) {
        if (this.following != null) {
            this.following.remove(userToUnfollow);
        }
    }

    // --- AUDITORÍA ---

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}