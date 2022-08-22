package com.raiden.cloudstorage.repositories;

import com.raiden.cloudstorage.entities.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbnailRepository extends JpaRepository<Thumbnail, String> {
}
