package com.raiden.cloudstorage.repositories;

import com.raiden.cloudstorage.entities.StoredZip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZipRepository extends JpaRepository<StoredZip, String> {
}
