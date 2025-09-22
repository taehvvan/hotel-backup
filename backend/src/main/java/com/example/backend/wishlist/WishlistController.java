package com.example.backend.wishlist;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.search.HotelDTO;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * 로그인한 사용자의 찜 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<HotelDTO>> getUserWishlist(
            @AuthenticationPrincipal String username,
            @RequestParam(required = false) LocalDate checkIn,
            @RequestParam(required = false) LocalDate checkOut,
            @RequestParam(required = false) Integer rooms,
            @RequestParam(required = false) Integer persons) {
        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(401).build(); // 인증 안됨
        }
        
        // WishlistService의 필터링 메서드 호출
        List<HotelDTO> wishlist = wishlistService.getWishlistByUserIdWithFilter(username, checkIn, checkOut, rooms, persons);
        
        return ResponseEntity.ok(wishlist);
    }

    /**
     * 찜 목록에 호텔 추가
     */
    @PostMapping("/{hid}")
    public ResponseEntity<Void> addToWishlist(
            @PathVariable Long hid,
            @AuthenticationPrincipal String username) {

        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        try {
            wishlistService.addByEmailAndHotelId(username, hid);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * 찜 목록에서 호텔 제거
     */
    @DeleteMapping("/{hid}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long hid,
            @AuthenticationPrincipal String username) {

        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        try {
            wishlistService.removeByEmailAndHotelId(username, hid);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
