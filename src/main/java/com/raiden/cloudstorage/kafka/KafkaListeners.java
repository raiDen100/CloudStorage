package com.raiden.cloudstorage.kafka;

import com.raiden.cloudstorage.entities.StoredFile;
import com.raiden.cloudstorage.services.FileService;
import com.raiden.cloudstorage.services.ThumbnailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class KafkaListeners {

    private final ThumbnailService thumbnailService;
    private final FileService fileService;

    @KafkaListener(topics = "thumbnail", groupId = "foo")
    void listener(String data){
        StoredFile storedFile = fileService.getFileById(data);
        try{
            thumbnailService.createThumbnail(storedFile);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
