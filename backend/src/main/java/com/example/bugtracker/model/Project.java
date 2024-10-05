package com.example.bugtracker.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import com.example.bugtracker.model.enums.Priority;
import com.example.bugtracker.model.enums.Status;
import java.time.LocalDateTime;



@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    public long getId() {
        return id;
    }
    
    
    @Column(name = "name", nullable = false )
    private String name;

    public String getName(){
        return name;
    }

    public void setName(String name) {
        if (name.length() > 50) {
            throw new IllegalArgumentException("Name is too long");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        this.name = name;
    }


    @Column(name = "description")
    private String description;

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }


    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = true)
    private Priority priority;

    public Priority getPriority(){
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creatorId;

    public User getCreatorId(){
        return creatorId;
    }

    public void setCreatorId(User creatorId) {
        if (creatorId == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }
        this.creatorId = creatorId;
    }


    @Column(name = "start_date")
    private LocalDateTime startDate;

    public LocalDateTime getStartDate(){
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }


    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
