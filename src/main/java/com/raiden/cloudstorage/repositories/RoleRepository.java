package com.raiden.cloudstorage.repositories;

import com.raiden.cloudstorage.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {

    Role findRoleByName(String name);
}
