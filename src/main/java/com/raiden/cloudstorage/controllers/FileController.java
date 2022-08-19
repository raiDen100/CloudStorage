package com.raiden.cloudstorage.controllers;

import com.raiden.cloudstorage.dto.RenameResourceRequest;
import com.raiden.cloudstorage.entities.Folder;
import com.raiden.cloudstorage.entities.StoredFile;
import com.raiden.cloudstorage.entities.User;
import com.raiden.cloudstorage.services.FileService;
import com.raiden.cloudstorage.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;
    private final UserService userService;

    @PostMapping(path = "/upload/{id}")
    public StoredFile saveFile(@RequestParam("files") MultipartFile[] file, @PathVariable(name = "id") Folder parentFolder, @RequestHeader("Authorization") String bearer){
        User user = userService.getUserByToken(bearer);

        return fileService.addFiles(file, parentFolder, user);
    }

    @PutMapping(path = "/rename/{fileId}")
    public void renameFile(@PathVariable(name = "fileId") String fileId, @RequestBody RenameResourceRequest renameRequest, @RequestHeader("Authorization") String bearer){
        User user = userService.getUserByToken(bearer);

        fileService.renameFile(fileId, renameRequest.getDisplayName());
    }

    @DeleteMapping(path = "/delete/{fileId}")
    public void deleteFile(@PathVariable(name = "fileId") String fileId, @RequestHeader("Authorization") String bearer){
        User user = userService.getUserByToken(bearer);

        StoredFile file = fileService.getFileById(fileId);
        fileService.deleteFile(file);
    }
}
