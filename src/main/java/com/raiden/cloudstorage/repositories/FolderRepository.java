package com.raiden.cloudstorage.repositories;

import com.raiden.cloudstorage.entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, String> {

    List<Folder> findAllByOwnerId(String id);
}
