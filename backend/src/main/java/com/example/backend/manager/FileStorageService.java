package com.example.backend.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    // application.properties 에 설정된 파일 업로드 기본 경로
    @Value("${file.upload-dir}")
    private String baseDir;

    public FileStorageResult saveHotelImage(Long hotelId, String hotelType, MultipartFile file, int imageIndex) {
        try {
            String hotelDir = baseDir + "/" + hotelType + "/";
            File dir = new File(hotelDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // --- 파일명 생성 로직 ---
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = "";
            if (originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String newFileName;
            if (imageIndex == 0) {
                // 첫 번째 이미지(메인 이미지)는 h_id로만 저장
                newFileName = hotelId + extension;
            } else {
                // 두 번째 이미지부터는 h_id_[순번]으로 저장
                newFileName = hotelId + "_" + imageIndex + extension;
            }
            // ---------------------

            Path filePath = Paths.get(hotelDir + newFileName);
            file.transferTo(filePath);

            return new FileStorageResult(hotelDir, newFileName);
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