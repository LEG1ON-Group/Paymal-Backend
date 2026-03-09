package com.example.paymal.repositories;

import com.example.paymal.model.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
}
