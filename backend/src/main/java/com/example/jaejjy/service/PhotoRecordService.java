package com.example.jaejjy.service;

import com.example.jaejjy.dto.PhotoRecordDto;
import com.example.jaejjy.entity.PhotoRecord;
import com.example.jaejjy.repository.PhotoRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PhotoRecordService {
    
    private final PhotoRecordRepository photoRecordRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    public PhotoRecordDto uploadPhoto(MultipartFile file, String title, String description) throws IOException {
        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 고유한 파일명 생성
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null && originalFileName.contains(".") 
            ? originalFileName.substring(originalFileName.lastIndexOf(".")) 
            : "";
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        
        // 파일 저장
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 데이터베이스에 저장
        PhotoRecord photoRecord = PhotoRecord.builder()
            .title(title != null && !title.isEmpty() ? title : originalFileName)
            .description(description != null ? description : "")
            .fileName(originalFileName)
            .filePath(uniqueFileName)
            .fileType(file.getContentType())
            .fileSize(file.getSize())
            .build();
        
        PhotoRecord saved = photoRecordRepository.save(photoRecord);
        
        return convertToDto(saved);
    }
    
    @Transactional(readOnly = true)
    public List<PhotoRecordDto> getAllPhotos() {
        return photoRecordRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PhotoRecordDto getPhotoById(Long id) {
        PhotoRecord photoRecord = photoRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("사진을 찾을 수 없습니다: " + id));
        return convertToDto(photoRecord);
    }
    
    public void deletePhoto(Long id) throws IOException {
        PhotoRecord photoRecord = photoRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("사진을 찾을 수 없습니다: " + id));
        
        // 파일 삭제
        Path filePath = Paths.get(uploadDir).resolve(photoRecord.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        
        // 데이터베이스에서 삭제
        photoRecordRepository.delete(photoRecord);
    }
    
    private PhotoRecordDto convertToDto(PhotoRecord photoRecord) {
        return PhotoRecordDto.builder()
            .id(photoRecord.getId())
            .title(photoRecord.getTitle())
            .description(photoRecord.getDescription())
            .fileName(photoRecord.getFileName())
            .filePath(photoRecord.getFilePath())
            .fileType(photoRecord.getFileType())
            .fileSize(photoRecord.getFileSize())
            .createdAt(photoRecord.getCreatedAt())
            .updatedAt(photoRecord.getUpdatedAt())
            .build();
    }
}

