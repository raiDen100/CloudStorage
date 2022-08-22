package com.raiden.cloudstorage.services;

import com.raiden.cloudstorage.entities.Folder;
import com.raiden.cloudstorage.entities.StoredFile;
import com.raiden.cloudstorage.entities.User;
import com.raiden.cloudstorage.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final StorageService storageService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Tika tika;


    public StoredFile getFileById(String id) {
        return fileRepository.findById(id)
                .orElseThrow();
    }

    public void renameFile(String fileId, String name){
        if (name.contains("/"))
            throw new IllegalArgumentException("Displayname cannot contain '/'");

        StoredFile file = getFileById(fileId);
        file.setDisplayName(getFileDisplayname(file.getParentFolder(), name, 1));
        fileRepository.save(file);
    }

    public StoredFile addFiles(MultipartFile[] files, Folder parentFolder, User owner) {
        for(MultipartFile multipartFile: files) {
            return addFile(multipartFile, parentFolder, owner);
        }
        return null;
    }

    public void deleteFile(StoredFile file){

        storageService.deleteFile(file);
        fileRepository.delete(file);
    }


    private StoredFile addFile(MultipartFile multipartFile, Folder parentFolder, User owner){
        String fileExtension = getFileExtension(multipartFile.getOriginalFilename());
        String fileName = multipartFile.getOriginalFilename();
        if (multipartFile.getOriginalFilename().contains(".")){
            StringBuilder newName = new StringBuilder(multipartFile.getOriginalFilename());
            newName.replace(multipartFile.getOriginalFilename().lastIndexOf(fileExtension)-1, multipartFile.getOriginalFilename().lastIndexOf(fileExtension) + fileExtension.length(), "");
            fileName = newName.toString();
            System.out.println(fileName);
        }

        String fileMimeType = getMimeType(multipartFile);

        StoredFile storedFile = StoredFile.builder()
                .extension(fileExtension)
                .displayName(getFileDisplayname(parentFolder, fileName, 1))
                .owner(owner)
                .path(parentFolder.getPath())
                .parentFolder(parentFolder)
                .fileType(fileMimeType)
                .build();

        fileRepository.save(storedFile);

        storedFile.setPath(storedFile.getPath() + "/" + storedFile.getId() + "." + fileExtension);
        fileRepository.save(storedFile);
        storageService.saveFile(multipartFile, storedFile.getPath());

        kafkaTemplate.send("thumbnail", storedFile.getId());
        return storedFile;
    }

    private String getMimeType(MultipartFile multipartFile) {
        try{
            return tika.detect(multipartFile.getInputStream());
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private String getFileExtension(String fileName){
        return FilenameUtils.getExtension(fileName);
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

        return (folderDisplayNameExists.isPresent() || fileDisplayNameExists.isPresent());
    }


}
