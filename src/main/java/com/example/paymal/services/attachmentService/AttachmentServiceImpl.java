package com.example.paymal.services.attachmentService;

import com.example.paymal.model.entity.Attachment;
import com.example.paymal.repositories.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final String uploadDir = "uploads";

    @Override
    public Attachment upload(MultipartFile file) {
        try {
            validateImage(file);

            String datePath = LocalDate.now().toString().replace("-", "/");
            Path folderPath = Paths.get(uploadDir, datePath);
            Files.createDirectories(folderPath);

            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = folderPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            Attachment attachment = Attachment.builder()
                    .originalName(originalName)
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .path(filePath.toString())
                    .build();

            return attachmentRepository.save(attachment);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file", e);
        }
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Invalid file type: Only images are allowed");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new RuntimeException("Invalid file name");
        }
        String extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        if (!extension.matches("jpg|jpeg|png|webp")) {
            throw new RuntimeException("Invalid image extension. Allowed: jpg, jpeg, png, webp");
        }

        try {
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(file.getInputStream());
            if (img == null) {
                throw new RuntimeException("Invalid image content");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image content");
        }
    }

    @Override
    public ResponseEntity<Resource> download(UUID id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        File file = new File(attachment.getPath());
        if (!file.exists()) {
            throw new RuntimeException("File not found on server");
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getOriginalName() + "\"")
                .body(resource);
    }
}
