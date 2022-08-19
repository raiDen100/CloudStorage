package com.raiden.cloudstorage.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Table
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StoredFile extends Resource{
    private String extension;

    private String fileType;

    @ManyToMany(mappedBy = "files")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<StoredZip> zips;
}
