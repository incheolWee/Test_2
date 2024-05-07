package com.example.test_2.controller;

import com.example.test_2.service.ItextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/itext")
public class ItextController {

    @Autowired
    private ItextService itextService;
@RequestMapping("/")
public String home(){
    return "/itext/home";
}

    @PostMapping("/add-image")
    public ResponseEntity<String> addImageToPdf(
            @RequestParam("pdfFile") MultipartFile pdfFile,
            @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            String pdfFilename = UUID.randomUUID().toString() + "_" + pdfFile.getOriginalFilename();
            String imageFilename = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path pdfPath = Paths.get("uploads/" + pdfFilename);
            Path imagePath = Paths.get("uploads/" + imageFilename);

            pdfFile.transferTo(pdfPath);
            imageFile.transferTo(imagePath);

            itextService.addImageToPdf(pdfPath.toString(), "uploads/result.pdf", imagePath.toString());

            return ResponseEntity.ok("Image added successfully to PDF");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error adding image to PDF: " + e.getMessage());
        }
    }


    // HTML 파일 제공
    @GetMapping("/file")
    public ResponseEntity<Resource> getFileHtml() {
        try {
            Resource file = new ClassPathResource("templates/file.html");
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    @GetMapping("/result")
    public ResponseEntity<Resource> getResultPdf() {
        try {
            String filePath = "uploads/result.pdf";
            Path file = Paths.get(filePath);
            if (!Files.exists(file)) {
                return ResponseEntity.notFound().build();
            }
            Resource fileResource = new UrlResource(file.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileResource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileResource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest()
                    .body(null);
        }
    }

}