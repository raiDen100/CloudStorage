package com.raiden.cloudstorage.services;

import com.raiden.cloudstorage.entities.Folder;
import com.raiden.cloudstorage.entities.StoredFile;
import com.raiden.cloudstorage.entities.User;
import com.raiden.cloudstorage.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Optional;
import java.util.UUID;

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

    public void renameFile(String fileId, String name, User user){
        if (name.contains("/"))
            throw new IllegalArgumentException("Displayname cannot contain '/'");

        StoredFile file = getFileById(fileId);
        if(!file.getOwner().getId().equals(user.getId()))
            throw new RuntimeException("Access denied");

        file.setDisplayName(getFileDisplayname(file.getParentFolder(), name, 1));
        fileRepository.save(file);
    }

    public StoredFile addFiles(Folder parentFolder, User owner, HttpServletRequest request) throws IOException, FileUploadException {
        System.out.println(parentFolder.getOwner().getDisplayName());
        if(!parentFolder.getOwner().getId().equals(owner.getId()))
            throw new RuntimeException("Access denied");

        return addFile(parentFolder, owner, request);
    }


    public void deleteFile(StoredFile file){

        storageService.deleteFile(file);
        fileRepository.delete(file);
    }


    private StoredFile addFile(Folder parentFolder, User owner, HttpServletRequest request) throws IOException, FileUploadException {

        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iterStream = upload.getItemIterator(request);

        StoredFile storedFile = StoredFile.builder()
        .extension("")
        .displayName(UUID.randomUUID().toString())
        .owner(owner)
        .path(parentFolder.getPath())
        .parentFolder(parentFolder)
        .fileType("")
        .build();

        fileRepository.save(storedFile);

        while (iterStream.hasNext()) {
            FileItemStream item = iterStream.next();
            InputStream stream = item.openStream();

            String fileExtension = getFileExtension(item.getName());
            String fileName = item.getName();
            if (item.getName().contains(".")){
                StringBuilder newName = new StringBuilder(item.getName());
                newName.replace(item.getName().lastIndexOf(fileExtension)-1, item.getName().lastIndexOf(fileExtension) + fileExtension.length(), "");
                fileName = newName.toString();
                System.out.println(fileName);
            }

            storedFile.setDisplayName(getFileDisplayname(parentFolder, fileName, 1));
            storedFile.setExtension(fileExtension);
            storedFile.setPath(storedFile.getPath() + "/" + storedFile.getId() + "." + fileExtension);
            fileRepository.save(storedFile);

            if (!item.isFormField()) {
                storageService.saveFile(stream, storedFile.getPath());
            }
            File file = storageService.getFile(storedFile);
            String fileMimeType = getMimeType(new FileInputStream(file));
            storedFile.setFileType(fileMimeType);

        }

        fileRepository.save(storedFile);
        kafkaTemplate.send("thumbnail", storedFile.getId());
        return storedFile;
    }

    private String getMimeType(InputStream inputStream) {
        try{
            return tika.detect(inputStream);
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
