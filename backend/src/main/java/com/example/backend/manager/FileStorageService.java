package com.example.backend.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final String baseDir = "uploads/hotels/";

    public FileStorageResult saveHotelImage(Long hotelId, MultipartFile file) {
        try {
            // 디렉토리 생성
            String hotelDir = baseDir + hotelId + "/";
            File dir = new File(hotelDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 저장 파일명 (UUID로 하면 충돌 방지)
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // 실제 저장
            Path filePath = Paths.get(hotelDir + fileName);
            file.transferTo(filePath);

            // 경로 + 파일명 분리해서 리턴
            return new FileStorageResult(hotelDir, fileName);

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}