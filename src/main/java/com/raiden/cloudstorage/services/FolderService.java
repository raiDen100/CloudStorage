package com.raiden.cloudstorage.services;

import com.raiden.cloudstorage.entities.Folder;
import com.raiden.cloudstorage.entities.StoredFile;
import com.raiden.cloudstorage.entities.User;
import com.raiden.cloudstorage.repositories.FolderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FolderService {
    private final FolderRepository folderRepository;
    private final StorageService storageService;

    public Folder getMainFolder(User user){

        return folderRepository.findAllByOwnerId(user.getId())
                .stream()
                .filter(f -> f.getFolderType().equals("main"))
                .findFirst()
                .orElseThrow();
    }

    public void createMainFolder(User owner){
        Folder folder = Folder.builder()
                .folderType("main")
                .parentFolder(null)
                .displayName(owner.getDisplayName() + "'s folder")
                .owner(owner)
                .path("/" + owner.getId())
                .build();

        folderRepository.save(folder);
        storageService.createDirectory(folder.getPath());
    }

    public Folder createFolder(String displayName, Folder parentFolder, User owner){
        if(!parentFolder.getOwner().getId().equals(owner.getId()))
            throw new RuntimeException("Access denied");

        Folder folder = Folder.builder()
                .folderType("subFolder")
                .parentFolder(parentFolder)
                .displayName(displayName.replace("/", ""))
                .owner(owner)
                .path(parentFolder.getPath())
                .parentFolder(parentFolder)
                .build();

        folderRepository.save(folder);
        return folder;
    }

    public Folder getFolderById(String id) {
        return folderRepository.findById(id)
                .orElseThrow();
    }

    public void deleteFolder(Folder folder) {

        for(StoredFile file : folder.getFiles()){
            storageService.deleteFile(file);
        }
        for (Folder f : folder.getFolders()){
            deleteFolder(f);
        }
        folderRepository.delete(folder);
    }

    public void renameFolder(String folderId, String displayName, User user) {
        if (displayName.contains("/"))
            throw new IllegalArgumentException("Displayname cannot contain '/'");

        Folder folder = getFolderById(folderId);
        if (!folder.getOwner().getId().equals(user.getId()))
            throw new RuntimeException("Access denied");

        folder.setDisplayName(displayName);
        folderRepository.save(folder);
    }
}
