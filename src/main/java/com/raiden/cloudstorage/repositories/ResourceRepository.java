package com.raiden.cloudstorage.repositories;

import com.raiden.cloudstorage.entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, String> {
}
