package com.example.test_2.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileStorageService {
    long storeFile(MultipartFile file);
    Resource loadFileAsResource(long id);
    List<Long> loadAllFiles();
    public void deleteFile(long id);
}

