package com.example.bugtracker.model;

import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import com.example.bugtracker.model.enums.Severity;
import com.example.bugtracker.model.enums.Status;
import com.example.bugtracker.model.enums.Priority;

@Entity
@Table(name = "bugs")
public class Bug {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    public long getId() {
        return id;
    }


    @ManyToOne( fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public Project getProject(){
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Column(name = "title", nullable = false)
    private String title;

    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        if (title.isEmpty()){
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (title.length() > 50) {
            throw new IllegalArgumentException("Title is too long");
        }

        this.title = title;
    }


    @Column(name = "description", nullable = false)
    private String description;

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        if ( description.isEmpty()){
            throw new IllegalArgumentException("You must describe the bug");
        }
        this.description = description;
    }


    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity;

    public Severity getServerity(){
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    public Status getStatus(){
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }


    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    public Priority getPriority(){
        return priority;
    }

    public void setPriority(Priority priority){
        this.priority = priority;
    }


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }


    @ManyToMany
    @JoinTable(
        name = "bug_assignees",
        joinColumns = @JoinColumn(name = "bug_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignees = new HashSet<>();

    public Set<User> getAssignees() {
        return assignees;
    }

    public void setAssignees(Set<User> assignees) {
        this.assignees = assignees;
    }

    public void addAssignee(User user) {
        assignees.add(user);
        user.getAssignedBugs().add(this);  
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


    @Column(name = "resolved_at", nullable = true)
    private LocalDateTime resolvedAt;

    public LocalDateTime getResolvedAt(){
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        if (feature == null) {
            throw new IllegalArgumentException("Feature cannot be null");
        }
        this.feature = feature;
    }

}
