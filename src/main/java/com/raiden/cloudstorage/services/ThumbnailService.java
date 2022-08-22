package com.raiden.cloudstorage.services;

import com.raiden.cloudstorage.entities.StoredFile;
import com.raiden.cloudstorage.entities.Thumbnail;
import com.raiden.cloudstorage.repositories.ThumbnailRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

    private final ThumbnailRepository thumbnailRepository;
    private final StorageService storageService;

    public byte[] getThumbnailById(String thumbnailId){
        Thumbnail thumbnail = thumbnailRepository
                .findById(thumbnailId)
                .orElseThrow();

        return thumbnail.getFile64();
    }

    public void createThumbnail(StoredFile storedFile) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String fileType = storedFile.getFileType();
        if (fileType.contains("image"))
            createImageThumbnail(storedFile, baos);
        if (fileType.contains("video") || fileType.contains("x-matroska"))
            createVideoThumbnail(storedFile, baos);

        Thumbnail thumbnail = Thumbnail.builder()
                .file(storedFile)
                .file64(baos.toByteArray())
                .build();

        thumbnailRepository.save(thumbnail);
    }

    private void createVideoThumbnail(StoredFile storedFile, ByteArrayOutputStream baos) {
        throw new RuntimeException("Method not implemented");
    }

    private void createImageThumbnail(StoredFile storedFile, ByteArrayOutputStream baos) throws IOException {

        Thumbnails.of(storageService.getFile(storedFile))
                .size(400, 400)
                .outputFormat("jpg")
                .toOutputStream(baos);
    }
}
