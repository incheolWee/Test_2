package com.example.test_2.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileStorageService {
    void storeFile(MultipartFile file);
    Resource loadFileAsResource(String fileName);
    List<String> loadAllFiles();
    public void deleteFile(String fileName);
}

