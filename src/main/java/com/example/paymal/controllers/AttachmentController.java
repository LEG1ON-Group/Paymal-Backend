package com.example.paymal.controllers;

import com.example.paymal.model.entity.Attachment;
import com.example.paymal.services.attachmentService.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/upload")
    public Attachment upload(@RequestParam("file") MultipartFile file) {
        return attachmentService.upload(file);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        return attachmentService.download(id);
    }
}
