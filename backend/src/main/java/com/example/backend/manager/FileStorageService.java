package com.example.backend.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    // application.properties 에 설정된 파일 업로드 기본 경로
    @Value("${file.upload-dir}")
    private String baseDir;

    public FileStorageResult saveHotelImage(Long hotelId, MultipartFile file) {
        try {
            String hotelDir = baseDir + "/hotels/" + hotelId + "/";
            File dir = new File(hotelDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(hotelDir + fileName);
            file.transferTo(filePath);

            return new FileStorageResult(hotelDir, fileName);
        } catch (IOException e) {
            throw new RuntimeException("호텔 이미지 저장 실패", e);
        }
    }

    // [추가] 리뷰 이미지를 저장하는 메소드
    public String saveReviewImage(MultipartFile file) {
        try {
            // 리뷰 이미지는 /reviews/ 폴더에 저장
            String reviewDir = baseDir + "/reviews/";
            File dir = new File(reviewDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 파일 이름 중복을 피하기 위해 UUID 사용
            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;

            Path filePath = Paths.get(reviewDir + fileName);
            file.transferTo(filePath);

            // 웹에서 접근 가능한 URL 경로를 반환 (예: /images/reviews/filename.jpg)
            return "/images/reviews/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("리뷰 이미지 저장 실패", e);
        }
    }
}