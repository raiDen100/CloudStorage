package com.raiden.cloudstorage.controllers;

import com.raiden.cloudstorage.entities.Role;
import com.raiden.cloudstorage.services.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/role")
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public void addRole(@RequestBody Role role){
        roleService.addRole(role);
    }
}
