package com.example.bugtracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.bugtracker.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPassword(String password);

    User findByUsername(String username);

    @Query("SELECT u.password FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    String findHasedPasswordByUsernameOrEmail(String usernameOrEmail);
} 
    
    

