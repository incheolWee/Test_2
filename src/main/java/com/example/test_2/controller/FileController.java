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
        List<String> files = fileStorageService.loadAllFiles();
        model.addAttribute("files", files);
        return "files";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        fileStorageService.storeFile(file);
        redirectAttributes.addFlashAttribute("message", "File.java uploaded successfully! [" + file.getOriginalFilename() + "]");
        return "redirect:/";
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    @GetMapping("/viewFile/{fileName:.+}")
    public ResponseEntity<Resource> viewFile(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
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
    public String deleteFile(@RequestParam("fileName") String fileName, RedirectAttributes redirectAttributes) {
        fileStorageService.deleteFile(fileName);
        redirectAttributes.addFlashAttribute("message", "File.java deleted successfully: " + fileName);
        return "redirect:/";
    }

    @GetMapping("/sign/{fileName:.+}")
    public String signFile(@PathVariable String fileName, Model model) {
        model.addAttribute("fileName", fileName);
        return "sign";
    }

}
