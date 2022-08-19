package com.raiden.cloudstorage.services;

import com.raiden.cloudstorage.entities.Role;
import com.raiden.cloudstorage.entities.User;
import com.raiden.cloudstorage.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@AllArgsConstructor
public class RoleService {

    private final Environment env;
    private final RoleRepository roleRepository;

    @PostConstruct
    public void createBasicRoles(){
        if (roleRepository.count() == 0){

            Role userRole = Role.builder()
                    .name("USER")
                    .build();
            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .build();
            roleRepository.save(userRole);
            roleRepository.save(adminRole);
        }
    }

    public void addRole(Role role){
        roleRepository.save(role);
    }
    public void assignDefaultRole(User user){
        Role role = roleRepository.findRoleByName("USER");
        user.addRole(role);
    }
}
