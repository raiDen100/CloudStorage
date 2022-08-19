package com.raiden.cloudstorage.controllers;

import com.raiden.cloudstorage.dto.RenameResourceRequest;
import com.raiden.cloudstorage.entities.Folder;
import com.raiden.cloudstorage.entities.User;
import com.raiden.cloudstorage.services.FolderService;
import com.raiden.cloudstorage.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/folder")
@AllArgsConstructor
public class FolderController {

    private final FolderService folderService;
    private final UserService userService;

    @PostMapping(path = "/{id}")
    public Folder createFolder(@RequestBody Folder folder, @PathVariable(name = "id") Folder parentFolder, @RequestHeader("Authorization") String bearer){
        User user = userService.getUserByToken(bearer);
        return folderService.createFolder(folder.getDisplayName(), parentFolder, user);
    }

    @GetMapping(path = "/{folderId}")
    public Folder getFolder(@PathVariable(name = "folderId") String folderId){

        return folderService.getFolderById(folderId);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteFolder(@PathVariable(name = "id") String folderId, @RequestHeader("Authorization") String bearer){
        User user = userService.getUserByToken(bearer);

        Folder folder = folderService.getFolderById(folderId);
        folderService.deleteFolder(folder);
    }
    @PutMapping(path = "/rename/{folderId}")
    public void renameFile(@PathVariable(name = "folderId") String fileId, @RequestBody RenameResourceRequest renameRequest, @RequestHeader("Authorization") String bearer){
        User user = userService.getUserByToken(bearer);

        folderService.renameFile(fileId, renameRequest.getDisplayName());
    }

    @GetMapping
    public Folder getMainFolder(@RequestHeader("Authorization") String bearer){
        User user = userService.getUserByToken(bearer);
        return folderService.getMainFolder(user);
    }
}
