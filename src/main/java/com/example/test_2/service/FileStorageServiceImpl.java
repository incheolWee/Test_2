package com.example.test_2.service;

import com.example.test_2.entity.File;
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

    // 생성자를 통해 파일 저장 위치를 설정합니다.
    public FileStorageServiceImpl(@Value("${file.storage.location:uploads}") String fileStorageLocation) {
        this.fileStorageLocation = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
        try {
            // 파일 저장 위치에 해당하는 디렉토리를 생성합니다.
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    @Override
    public long storeFile(MultipartFile file) {
        // 파일명을 정리합니다(경로 순회를 방지하기 위함).
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // 파일명에 부적절한 문자가 있는지 검증합니다.
            if(fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            //파일을 데이터베이스에 저장하고 생선된 ID를 가져옵니다.
            File fileEntity = new File();
            fileEntity.setFileName(fileName);
            fileEntity = fileRepository.save(fileEntity);

            // 대상 위치에 파일을 복사합니다.
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileEntity.getId();
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(long id) {
        try {
            File fileEntity = fileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found with id " + id));
            Path filePath = this.fileStorageLocation.resolve(fileEntity.getFileName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + id);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not load file: " + id, ex);
        }
    }

    @Override
    public List<Long> loadAllFiles() {
        // 디렉토리를 순회하며 모든 파일의 목록을 생성합니다.
        return fileRepository.findAll().stream()
                .map(File::getId)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(long id) {
        try {
            File fileEntity = fileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found with id " + id));
            Path file = fileStorageLocation.resolve(fileEntity.getFileName()).normalize();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + id, e);
        }
    }
}

