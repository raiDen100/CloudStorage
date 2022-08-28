package com.raiden.cloudstorage.services;

import com.raiden.cloudstorage.entities.StoredFile;
import lombok.AllArgsConstructor;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
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

    public void saveFile(HttpServletRequest request, String path) throws IOException, FileUploadException {
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iterStream = upload.getItemIterator(request);
        while (iterStream.hasNext()) {
            FileItemStream item = iterStream.next();
            String name = item.getFieldName();
            InputStream stream = item.openStream();
            if (!item.isFormField()) {
                // Process the InputStream
                try (
                    InputStream uploadedStream = stream;
                    OutputStream out = new FileOutputStream(env.getProperty("upload.directory") + path)) {

                    IOUtils.copy(uploadedStream, out);
                }
            } else {
                String formFieldValue = Streams.asString(stream);
            }
        }
    }

    public void saveFile(InputStream inputStream, String path) throws IOException, FileUploadException {

        // Process the InputStream
        try (
                InputStream uploadedStream = inputStream;
                OutputStream out = new FileOutputStream(env.getProperty("upload.directory") + path)) {

            IOUtils.copy(uploadedStream, out);
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
