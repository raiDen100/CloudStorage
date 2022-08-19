package com.raiden.cloudstorage.controllers;

import com.raiden.cloudstorage.dto.MultiResourceRequest;
import com.raiden.cloudstorage.entities.StoredZip;
import com.raiden.cloudstorage.entities.User;
import com.raiden.cloudstorage.services.ResourceService;
import com.raiden.cloudstorage.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/api/resources")
@AllArgsConstructor
public class ResourceController {

    private final UserService userService;
    private final ResourceService resourceService;

    @PutMapping(path = "/moveto/{folderId}")
    public void moveResources(
            @PathVariable(name = "folderId") String folderId,
            @RequestBody MultiResourceRequest multiResourceRequest,
            @RequestHeader("Authorization") String bearer){
        User user = userService.getUserByToken(bearer);

        resourceService.moveResources(multiResourceRequest, folderId);
    }

    @DeleteMapping(path = "/delete")
    public void deleteResources(
            @RequestBody MultiResourceRequest multiResourceRequest,
            @RequestHeader("Authorization") String bearer){
        User user = userService.getUserByToken(bearer);

        resourceService.deleteResources(multiResourceRequest);
    }

    @PostMapping(path = "/zip")
    public StoredZip zipResources(
            HttpServletResponse response,
            @RequestBody MultiResourceRequest multiResourceRequest,
            @RequestHeader("Authorization") String bearer){
        User user = userService.getUserByToken(bearer);

        return resourceService.createNewZip(response, multiResourceRequest, user);
    }
}
