package com.example.paymal.services.attachmentService;

import com.example.paymal.model.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AttachmentService {
    Attachment upload(MultipartFile file);
    ResponseEntity<Resource> download(UUID id);
}
