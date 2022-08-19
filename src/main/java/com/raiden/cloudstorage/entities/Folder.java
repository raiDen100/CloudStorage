package com.raiden.cloudstorage.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Folder extends Resource{

    @OneToMany(mappedBy = "parentFolder")
    private List<StoredFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "parentFolder")
    private List<Folder> folders = new ArrayList<>();
    private String folderType;

    @ManyToMany(mappedBy = "folders")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<StoredZip> zips;
}
