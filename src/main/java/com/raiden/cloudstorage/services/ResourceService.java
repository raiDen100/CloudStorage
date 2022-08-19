package com.raiden.cloudstorage.services;

import com.raiden.cloudstorage.dto.MultiResourceRequest;
import com.raiden.cloudstorage.entities.Folder;
import com.raiden.cloudstorage.entities.StoredFile;
import com.raiden.cloudstorage.entities.StoredZip;
import com.raiden.cloudstorage.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResourceService {

    private final FolderService folderService;
    private final FileService fileService;
    private final ZipService zipService;

    @Transactional
    public void moveResources(MultiResourceRequest moveRequest, String destinationId){
        List<Folder> folders = moveRequest.getFolders();
        List<StoredFile> files = moveRequest.getFiles();

        Folder destination = folderService.getFolderById(destinationId);

        for (Folder f : folders){
            if (Objects.equals(destination.getId(), f.getId()))
                throw new RuntimeException();

            Folder folder = folderService.getFolderById(f.getId());
            folder.setParentFolder(destination);
        }
        for (StoredFile f : files){
            StoredFile file = fileService.getFileById(f.getId());
            file.setDisplayName(getFileDisplayname(destination, file.getDisplayName(), 1));
            file.setParentFolder(destination);
        }
    }

    private String getFileDisplayname(Folder parentFolder, String displayName, int i){

        if (displayNameExistsInFolder(parentFolder, displayName)){
            String previousSuffix = " (%s)".formatted(i-1);
            String newDisplayName = displayName;
            if (displayName.endsWith(previousSuffix))
                newDisplayName = newDisplayName.replace(previousSuffix, " (%s)".formatted(i));
            else
                newDisplayName += " (%s)".formatted(i);

            return getFileDisplayname(parentFolder, newDisplayName, ++i);
        }

        return displayName;
    }

    private boolean displayNameExistsInFolder(Folder folder, String displayName){
        Optional<StoredFile> fileDisplayNameExists = folder.getFiles()
                .stream()
                .filter((StoredFile f) -> f.getDisplayName().equals(displayName))
                .findAny();

        Optional<Folder> folderDisplayNameExists = folder.getFolders()
                .stream()
                .filter((Folder f) -> f.getDisplayName().equals(displayName))
                .findAny();

        return folderDisplayNameExists.isPresent() || fileDisplayNameExists.isPresent();
    }

    @Transactional
    public MultiResourceRequest deleteResources(MultiResourceRequest multiResourceRequest) {
        List<Folder> folders = multiResourceRequest.getFolders();
        List<StoredFile> files = multiResourceRequest.getFiles();

        for (Folder f : folders){

            folderService.deleteFolder(f);
        }

        for (StoredFile f : files)
            fileService.deleteFile(f);

        return multiResourceRequest;
    }

    public StoredZip createNewZip(HttpServletResponse response, MultiResourceRequest multiResourceRequest, User user) {
        List<Folder> dummyFolders = multiResourceRequest.getFolders();
        List<StoredFile> dummyFiles = multiResourceRequest.getFiles();

        List<Folder> folders = new ArrayList<>();
        List<StoredFile> files = new ArrayList<>();

        for (Folder f : dummyFolders)
            folders.add(folderService.getFolderById(f.getId()));

        for (StoredFile f : dummyFiles)
            files.add(fileService.getFileById(f.getId()));

        return zipService.createNewZip(folders, files, user);
    }
}
