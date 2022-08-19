package com.raiden.cloudstorage.services;

import com.raiden.cloudstorage.entities.StoredFile;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@Service
@AllArgsConstructor
public class StorageService {

    private final Environment env;

    @PostConstruct
    private void createStorageDirectoryIfNotExists(){
        File directory = new File(Objects.requireNonNull(env.getProperty("upload.directory")));
        if(!directory.exists())
            directory.mkdirs();
    }

    public void createDirectory(String path){
        File directory = new File(Objects.requireNonNull(env.getProperty("upload.directory"))+ path);
        if(!directory.exists()) {
            directory.mkdirs();
        }
    }

    public void saveFile(MultipartFile file, String path) {
        try{
            file.transferTo(new File(env.getProperty("upload.directory") + path));
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public Resource getFileToDownload(String p) throws IOException {
        Path path = Path.of(env.getProperty("upload.directory") + p);
        return new UrlResource(path.toUri());
    }

    public File getFile(StoredFile file){
        return new File(Objects.requireNonNull(env.getProperty("upload.directory")) + file.getPath());
    }

    public void deleteFile(StoredFile file) {
        File f = new File(Objects.requireNonNull(env.getProperty("upload.directory")) + file.getPath());
        f.delete();
    }
}
