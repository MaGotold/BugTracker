package com.example.bugtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.HashSet;
import java.util.Set;
import com.example.bugtracker.model.enums.Role;



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

        if (username.length() > 20) { 
            throw new IllegalArgumentException("Username cannot exceed 50 characters");
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

    //todo add password validation logic/criteria
    @Column(name = "password", nullable =false)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    public Role getRole(){
        return role;
    }

    public void setRole(Role role){
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be empty");
        }
        this.role = role;
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


    @OneToMany(mappedBy = "creatorId", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Project> projects = new HashSet<>();

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }
 

    @OneToMany(mappedBy = "creator", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<Bug> bugs = new HashSet<>();

    public Set<Bug> getBugs() {
        return bugs;
    }

    public void addBug(Bug bug) {
        bugs.add(bug);
        bug.setCreator(this);
    }

    public void removeBug(Bug bug) {
        bugs.remove(bug);
        bug.setCreator(null);
    }


    @ManyToMany(mappedBy = "assignees", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Bug> assignedBugs = new HashSet<>();

    public Set<Bug> getAssignedBugs() {
        return assignedBugs;
    }

    public void setAssignedBugs(Set<Bug> assignedBugs) {
        this.assignedBugs = assignedBugs;
    }

    public void assignBug(Bug bug) {
        assignedBugs.add(bug);
        bug.getAssignees().add(this);  
    }
}
