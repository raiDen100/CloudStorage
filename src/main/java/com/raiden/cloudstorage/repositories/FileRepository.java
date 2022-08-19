package com.raiden.cloudstorage.repositories;

import com.raiden.cloudstorage.entities.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<StoredFile, String> {
}
