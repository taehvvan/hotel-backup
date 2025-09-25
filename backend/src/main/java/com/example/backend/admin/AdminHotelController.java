package com.example.backend.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 관리자(Admin)가 호텔을 관리하기 위한 API 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/admin/hotels")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminHotelController {

    private final AdminHotelService adminHotelService;

    /**
     * 특정 타입의 호텔 목록과 통계를 조회하는 API입니다.
     * @param type 쿼리 파라미터 (예: /api/admin/hotels?type=호텔)
     * @return 호텔 DTO 리스트
     */
    @GetMapping
    public ResponseEntity<List<AdminHotelDto>> getHotels(@RequestParam String type) {
        return ResponseEntity.ok(adminHotelService.getHotels(type));
    }

    /**
     * 호텔의 활성화 상태를 변경하는 API입니다.
     * @param hotelId URL 경로의 호텔 ID (예: /api/admin/hotels/15/status)
     * @param payload 요청 본문의 'active' 상태(true/false)
     * @return 성공 응답
     */
    @PutMapping("/{hotelId}/status")
    public ResponseEntity<Void> updateHotelStatus(
            @PathVariable Long hotelId,
            @RequestBody Map<String, Boolean> payload) {
        
        adminHotelService.updateHotelStatus(hotelId, payload.get("active"));
        return ResponseEntity.ok().build();
    }
}