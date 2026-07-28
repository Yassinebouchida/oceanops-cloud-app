package com.oceanopscloud.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileService {

    private final String UPLOAD_DIR = "uploads/";

    public String saveFile(MultipartFile file) throws IOException {

        // Create directory if not exists
        File uploadFolder = new File(UPLOAD_DIR);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        // Unique name
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Full path
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        // Save file
        Files.write(filePath, file.getBytes());

        return filePath.toString(); // return path
    }
}
