package com.example.bugtracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.bugtracker.model.Permission;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;




@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    @Query("SELECT p.id FROM Permission p JOIN p.roles r WHERE r.id = :roleId")
    List<Permission> findPermissionByRoleId(@Param("roleId") long roleId);
}
