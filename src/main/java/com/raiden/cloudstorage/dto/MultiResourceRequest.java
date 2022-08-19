package com.raiden.cloudstorage.dto;

import com.raiden.cloudstorage.entities.Folder;
import com.raiden.cloudstorage.entities.StoredFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultiResourceRequest {
    private List<Folder> folders;
    private List<StoredFile> files;
}
