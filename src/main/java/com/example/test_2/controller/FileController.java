package com.example.test_2.controller;

import com.example.test_2.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Controller
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/")
    public String listFiles(Model model) {
        // 파일 ID 리스트를 불러와서 모델에 추가
        List<Long> fileIds = fileStorageService.loadAllFiles();
        model.addAttribute("files", fileIds);
        return "files";  // 파일 목록을 보여주는 뷰 이름
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        // 파일을 저장하고 생성된 파일 ID를 받아옵니다.
        long fileId = fileStorageService.storeFile(file);
        redirectAttributes.addFlashAttribute("message", "File uploaded successfully! ID: " + fileId);
        return "redirect:/";
    }

    @GetMapping("/downloadFile/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable long fileId) {
        // 파일 ID를 사용하여 파일 리소스를 불러옵니다.
        Resource resource = fileStorageService.loadFileAsResource(fileId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/viewFile/{fileId}")
    public ResponseEntity<Resource> viewFile(@PathVariable long fileId) {
        // 파일 ID를 사용하여 파일 리소스를 불러오고 MIME 타입을 설정합니다.
        Resource resource = fileStorageService.loadFileAsResource(fileId);
        String mimeType;
        try {
            mimeType = Files.probeContentType(resource.getFile().toPath());
        } catch (IOException e) {
            // 기본값으로 설정; 브라우저가 결정하게 함
            mimeType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/deleteFile")
    public String deleteFile(@RequestParam("fileId") long fileId, RedirectAttributes redirectAttributes) {
        // 파일 ID를 사용하여 파일을 삭제합니다.
        fileStorageService.deleteFile(fileId);
        redirectAttributes.addFlashAttribute("message", "File deleted successfully. ID: " + fileId);
        return "redirect:/";
    }
}
