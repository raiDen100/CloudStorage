package com.raiden.cloudstorage.controllers;

import com.raiden.cloudstorage.entities.StoredFile;
import com.raiden.cloudstorage.services.FileService;
import com.raiden.cloudstorage.services.StorageService;
import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/api/thumbnail")
@AllArgsConstructor
public class ThumbnailController {

    private final FileService fileService;
    private final StorageService storageService;

    @GetMapping(path = "/{fileId}", produces = "image/jpg")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable(name = "fileId") String fileId) throws IOException {

        StoredFile storedFile = fileService.getFileById(fileId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Thumbnails.of(storageService.getFile(storedFile))
                .size(400, 400)
                .outputFormat("jpg")
                .toOutputStream(baos);

        CacheControl cacheControl = CacheControl
                .maxAge(72, TimeUnit.HOURS)
                .noTransform()
                .mustRevalidate();

        return ResponseEntity.ok()
                .cacheControl(cacheControl)
                .body(baos.toByteArray());
    }
}
