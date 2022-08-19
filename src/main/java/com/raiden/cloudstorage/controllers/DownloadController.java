package com.raiden.cloudstorage.controllers;

import com.raiden.cloudstorage.entities.StoredFile;
import com.raiden.cloudstorage.entities.StoredZip;
import com.raiden.cloudstorage.services.StorageService;
import com.raiden.cloudstorage.services.UserService;
import com.raiden.cloudstorage.services.ZipService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(path = "/api/download")
@AllArgsConstructor
public class DownloadController {

    private final UserService userService;
    private final StorageService storageService;
    private final ZipService zipService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "id") StoredFile file/*, @RequestHeader("Authorization") String bearer*/) throws IOException{
        //User user = userService.getUserByToken(bearer);

        Resource resource = storageService.getFileToDownload(file.getPath());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(file.getDisplayName() + "." + file.getExtension()));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    @GetMapping(path = "zip/{id}")
    public void downloadZipFile(HttpServletResponse response, @PathVariable(name = "id") String zipFileId/*, @RequestHeader("Authorization") String bearer*/) throws IOException{
        //User user = userService.getUserByToken(bearer);

        StoredZip zip = zipService.getZipById(zipFileId);
        zipService.createNewZip(response, zip);

    }
}
