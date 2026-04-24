package com.cropsys.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cropsys.backend.model.AppRole;
import com.cropsys.backend.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByRoleName(AppRole roleName);
    

}
