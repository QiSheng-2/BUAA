/*
package com.example.chatroom.controller;

import com.example.chatroom.dto.UploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

//@RestController
//@RequestMapping("/api")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    //@PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                if (!uploadDir.mkdirs()) {
                    logger.error("Failed to create upload directory: {}", UPLOAD_DIR);
                    return ResponseEntity.internalServerError().build();
                }
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.lastIndexOf('.') > 0) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String storedName = UUID.randomUUID().toString().replace("-", "") + extension;
            Path filePath = Paths.get(UPLOAD_DIR, storedName);

            Files.write(filePath, file.getBytes());

            String url = "/uploads/" + storedName;
            UploadResponse response = new UploadResponse(url, originalFilename, file.getContentType(), file.getSize());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.error("File upload failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
*/