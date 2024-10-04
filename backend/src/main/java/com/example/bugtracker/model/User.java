package com.example.bugtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "users")
public class User {
    

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    public long getId() {
        return id;
    }


    @Column(name = "username", nullable = false, unique = true)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username;
    }

    //todo add email validation
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        this.email = email;
    }

    //todo add password validation logic
    @Column(name = "password", nullable =false)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Column(name = "is_admin")
    private short isAdmin = 0;

    public short getIsAdmin(){
        return isAdmin;
    }

    public void setIsAdmin(short isAdmin){
        this.isAdmin = isAdmin;
    }


    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

   

    
}
