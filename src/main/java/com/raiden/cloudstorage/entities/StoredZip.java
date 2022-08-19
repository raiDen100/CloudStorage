package com.raiden.cloudstorage.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StoredZip extends Resource{

    @ManyToMany
    @JoinTable(
            name = "zip_files",
            joinColumns = @JoinColumn(name = "zip_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id"))
    private List<StoredFile> files = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "zip_folders",
            joinColumns = @JoinColumn(name = "zip_id"),
            inverseJoinColumns = @JoinColumn(name = "folder_id"))
    private List<Folder> folders = new ArrayList<>();
}
