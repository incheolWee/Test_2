package com.example.test_2.controller;

import com.example.test_2.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/pdf")  // All routes in this controller will now start with /pdf
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/")
    public String listFiles(Model model) {
        List<String> files = fileStorageService.loadAllFiles();
        model.addAttribute("files", files);
        return "/pdf/files";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        fileStorageService.storeFile(file);
        redirectAttributes.addFlashAttribute("message", "File uploaded successfully! [" + file.getOriginalFilename() + "]");
        return "redirect:/pdf/";  // Redirects within the /pdf context
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load the file as a resource.
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Set the default content type if the file's type cannot be determined
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
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
            // Default to a binary file type if mime type detection fails
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
        redirectAttributes.addFlashAttribute("message", "File deleted successfully: " + fileName);
        return "redirect:/pdf/";  // Ensures redirection stays within the /pdf context after delete
    }

    // Example of a commented-out viewFilePage method, remove if not needed
    // @GetMapping("/viewFilePage/{fileName:.+}")
    // public String viewFilePage(@PathVariable String fileName, Model model) {
    //     model.addAttribute("fileName", fileName);
    //     return "viewFile"; // Moves to viewFile.html page
    // }

}
