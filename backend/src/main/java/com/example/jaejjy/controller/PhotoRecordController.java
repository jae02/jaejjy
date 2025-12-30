package com.example.jaejjy.controller;

import com.example.jaejjy.dto.PhotoRecordDto;
import com.example.jaejjy.service.PhotoRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PhotoRecordController {
    
    private final PhotoRecordService photoRecordService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @PostMapping("/upload")
    public ResponseEntity<PhotoRecordDto> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description) {
        try {
            PhotoRecordDto photoRecord = photoRecordService.uploadPhoto(file, title, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(photoRecord);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<PhotoRecordDto>> getAllPhotos() {
        List<PhotoRecordDto> photos = photoRecordService.getAllPhotos();
        return ResponseEntity.ok(photos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PhotoRecordDto> getPhotoById(@PathVariable Long id) {
        PhotoRecordDto photo = photoRecordService.getPhotoById(id);
        return ResponseEntity.ok(photo);
    }
    
    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getPhotoImage(@PathVariable Long id) {
        try {
            PhotoRecordDto photo = photoRecordService.getPhotoById(id);
            Path filePath = Paths.get(uploadDir).resolve(photo.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(photo.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + photo.getFileName() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        try {
            photoRecordService.deletePhoto(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

