package com.marketplace.lumiere.upload;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

// Under /api/admin — protected by JWT (only the admin can upload).
@RestController
@RequestMapping("/api/admin/upload")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    // Receives a file (multipart) and returns { "url": "https://..." }.
    @PostMapping
    public Map<String, String> upload(@RequestParam("file") MultipartFile file)
            throws IOException {
        String url = uploadService.uploadImage(file);
        return Map.of("url", url);
    }
}