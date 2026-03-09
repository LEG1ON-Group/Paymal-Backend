package com.example.paymal.model.entity;

import jakarta.persistence.PostRemove;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AttachmentListener {

    @PostRemove
    public void postRemove(Attachment attachment) {
        if (attachment.getPath() != null) {
            try {
                Path path = Paths.get(attachment.getPath());
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + attachment.getPath());
                e.printStackTrace();
            }
        }
    }
}
