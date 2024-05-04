package com.example.test_2.service;

import com.example.test_2.model.File;
import com.example.test_2.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;
    @Autowired
    private FileRepository fileRepository;

    public FileStorageServiceImpl(@Value("${file.storage.location:uploads}") String fileStorageLocation) {
        this.fileStorageLocation = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    @Override
    public void storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Save file metadata to the database
            File dbFile = new File(fileName, file.getContentType(), file.getSize(), targetLocation.toString());
            fileRepository.save(dbFile);

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(f ilePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not load file: " + fileName, ex);
        }
    }

    @Override
    public List<String> loadAllFiles() {
        try {
            return Files.walk(this.fileStorageLocation, 1)
                    .filter(path -> !path.equals(this.fileStorageLocation))
                    .map(this.fileStorageLocation::relativize)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read stored files.", ex);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            Path file = fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(file);
            // Also delete metadata from database
            fileRepository.deleteAll(fileRepository.findAll().stream()
                    .filter(f -> f.getFileName().equals(fileName))
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + fileName, e);
        }
    }
}
