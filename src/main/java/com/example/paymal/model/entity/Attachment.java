package com.example.paymal.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AttachmentListener.class)
@Table(name = "attachment")
public class Attachment extends BaseEntity {

    @Column
    private String originalName;

    @Column
    private long size;

    @Column
    private String contentType;

    @Column
    private String path;
}
