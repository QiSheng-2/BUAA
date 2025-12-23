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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        //if (file.isEmpty()) {
        //    return ResponseEntity.badRequest().build();
        //}

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
            String storedName = originalFilename;
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

    //@PostMapping("/api/upload")
    public ResponseEntity<Map<String, String>> buploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        try {
            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

            // 保存文件到指定目录
            String uploadDir = "/path/to/upload/directory/"; // 修改为你的上传路径
            Files.copy(file.getInputStream(), Paths.get(uploadDir + fileName));

            // 返回文件URL和类型
            response.put("url", "/uploads/" + fileName);
            response.put("fileType", file.getContentType()); // 保持原始类型
            response.put("fileName", file.getOriginalFilename());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}