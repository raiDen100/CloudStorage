package com.raiden.cloudstorage.services;

import com.raiden.cloudstorage.entities.Folder;
import com.raiden.cloudstorage.entities.StoredFile;
import com.raiden.cloudstorage.entities.StoredZip;
import com.raiden.cloudstorage.entities.User;
import com.raiden.cloudstorage.repositories.ZipRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

@Service
@AllArgsConstructor
public class ZipService {

    private final Environment env;
    private final StorageService storageService;
    private final ZipRepository zipRepository;


    public StoredZip createNewZip(HttpServletResponse response, StoredZip zip){

        zipResources(response, zip);
        return zip;
    }
    private void zipResources(HttpServletResponse response, StoredZip zip){
        List<Folder> folders = zip.getFolders();
        List<StoredFile> files = zip.getFiles();
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=download.zip");
        try {
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            for (Folder folder : folders) {

                zipFolder(zos, folder, folder.getDisplayName());
            }
            for (StoredFile file : files) {

                zipResource(zos, file);
            }
            zos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void zipFolder(ZipOutputStream zos, Folder folder, String path){
        List<StoredFile> files = folder.getFiles();


        for (Folder folder1 : folder.getFolders())
            zipFolder(zos, folder1, path + "/" + folder1.getDisplayName());

        for (StoredFile f : files)
            zipResource(zos, f, path);

    }

    private void zipResource(ZipOutputStream zos, StoredFile storedFile){
        try {
            //add a new Zip Entry to the ZipOutputStream
            File file = storageService.getFile(storedFile);
            putZipEntry(zos, storedFile);
            //read the file and write to ZipOutputStream
            writeToZip(zos, file);
            zos.closeEntry();
            //Close the zip entry to write to zip file

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void zipResource(ZipOutputStream zos, StoredFile storedFile, String path){
        try {
            File file = storageService.getFile(storedFile);

            putZipEntry(zos, storedFile, path);
            writeToZip(zos, file);

            zos.closeEntry();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void putZipEntry(ZipOutputStream zos, StoredFile file) throws IOException {
        ZipEntry ze = new ZipEntry(file.getDisplayName() + "." + file.getExtension());
        zos.putNextEntry(ze);
    }
    private void putZipEntry(ZipOutputStream zos, StoredFile file, String path) throws IOException {
        try{
            ZipEntry ze = new ZipEntry(path + "/" + file.getDisplayName() + "." + file.getExtension());
            zos.putNextEntry(ze);
        }catch(ZipException e){
            putZipEntry(zos, file, path, 1);
        }

    }
    private void putZipEntry(ZipOutputStream zos, StoredFile file, String path, int i) throws IOException {
        String fileName = getFileDisplayname(file.getDisplayName(), i);
        try{
            ZipEntry ze = new ZipEntry(path + "/" + fileName + "." + file.getExtension());
            zos.putNextEntry(ze);
        }catch(ZipException e){
            putZipEntry(zos, file, path, ++i);
        }

    }

    private String getFileDisplayname(String displayName, int i){

        String previousSuffix = " (%s)".formatted(i-1);
        String newDisplayName = displayName;
        if (displayName.endsWith(previousSuffix))
            newDisplayName = newDisplayName.replace(previousSuffix, " (%s)".formatted(i));
        else
            newDisplayName += " (%s)".formatted(i);

        return newDisplayName;
    }

    private void writeToZip(ZipOutputStream zos, File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }
        fis.close();
    }

    public StoredZip createNewZip(List<Folder> folders, List<StoredFile> files, User user) {
        StoredZip zip = new StoredZip();
        zip.setOwner(user);
        zip.setFolders(folders);
        zip.setFiles(files);
        zipRepository.save(zip);
        return zip;
    }

    public StoredZip getZipById(String zipFileId) {
        return zipRepository.findById(zipFileId)
                .orElseThrow();
    }
}
