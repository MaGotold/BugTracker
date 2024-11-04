package com.example.bugtracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;

import java.util.Set;


@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "permission", nullable = false)
    private String permission;

    @Lob
    @Column(name = "description", nullable = true, columnDefinition = "text")
    private String description;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;


    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }


    public String getPermission(){
        return permission;
    }

    public void setPermission(String permission){
        this.permission = permission;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }


    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
