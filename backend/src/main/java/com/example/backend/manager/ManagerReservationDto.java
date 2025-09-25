// 1. 이 자바 파일이 com.example.backend.manager 라는 폴더(패키지)에 속해있음을 알려줍니다.
package com.example.backend.manager;

// 2. 필요한 부품들을 가져옵니다.
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 3. Lombok이 자동으로 Getter, Setter, ToString 등 꼭 필요한 코드들을 만들어줍니다.
@Data
// 4. Lombok이 아무것도 없는 비어있는 생성자(기본 생성자)를 만들어줍니다.
@NoArgsConstructor
// 5. Lombok이 모든 변수를 포함하는 생성자를 만들어줍니다.
@AllArgsConstructor
public class ManagerReservationDto {
    // 6. 이 꾸러미에는 아래 정보들이 들어갈 것이라고 정의합니다.
    private Integer reId;
    private String guestName;
    private String hotelName;
    private String roomName;
    private LocalDate checkin;
    private LocalDate checkout;
    private String status;
}