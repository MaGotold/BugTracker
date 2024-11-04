package com.example.bugtracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.bugtracker.model.Permission;
import com.example.bugtracker.repository.PermissionRepository;
import java.util.List;

@Service
public class PermissionService {

    @Autowired
    PermissionRepository permissionRepository;
    
    public List<Permission> findPermissionByRoleId(long roleId){
        List<Permission> qwe =  permissionRepository.findPermissionByRoleId(roleId);
        return qwe;
    }
}
