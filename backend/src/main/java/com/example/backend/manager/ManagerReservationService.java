// 1. 이 자바 파일이 com.example.backend.manager 라는 폴더(패키지)에 속해있음을 알려줍니다.
package com.example.backend.manager;

// 2. 다른 폴더에 있는 기존 ReservationRepository 부품을 가져옵니다.
import com.example.backend.reservation.ReservationRepository;
// 3. 필요한 다른 부품들을 가져옵니다.
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// 4. 이 클래스가 비즈니스 로직을 처리하는 '서비스' 부품임을 스프링에게 알려줍니다.
@Service
// 5. Lombok 기능으로, final로 선언된 꼭 필요한 부품을 자동으로 연결해주는 생성자를 만듭니다.
@RequiredArgsConstructor
// 6. 이 클래스의 모든 기능은 데이터를 '읽기만' 하는 기능이므로, 더 빠르고 안전하게 작동하도록 설정합니다.
@Transactional(readOnly = true)
public class ManagerReservationService {

    // 7. 레포지토리(DB 창고 관리인) 부품을 연결합니다.
    private final ReservationRepository reservationRepository;

    // 8. 컨트롤러로부터 3개의 값(status, searchType, searchQuery)을 받아서 예약 목록을 찾아주는 기능을 정의합니다.
    public List<ManagerReservationDto> getReservations(String status, String searchType, String searchQuery) {
        // 9. 고객 이름을 담을 변수를 만들고, 일단 비워둡니다(null).
        String guestName = null;
        // 10. 예약 ID를 담을 변수를 만들고, 일단 비워둡니다(null).
        Integer reId = null;

        // 11. 만약 searchType이 "guestName"이고, 검색어(searchQuery)가 비어있지 않다면,
        if ("guestName".equals(searchType) && searchQuery != null && !searchQuery.isEmpty()) {
            // 12. guestName 변수에 검색어를 저장합니다.
            guestName = searchQuery;
        // 13. 만약 searchType이 "id"이고, 검색어가 비어있지 않다면,
        } else if ("id".equals(searchType) && searchQuery != null && !searchQuery.isEmpty()) {
            // 14. try-catch 블록은 혹시 모를 에러에 대비하기 위한 안전장치입니다.
            try {
                // 15. 검색어가 'R123' 같이 문자가 섞여 있을 수 있으니, 숫자(0-9)가 아닌 모든 글자를 지워버립니다.
                String numericQuery = searchQuery.replaceAll("[^0-9]", "");
                // 16. 숫자만 남겼는데도 글자가 남아있다면 (비어있지 않다면),
                if (!numericQuery.isEmpty()) {
                    // 17. 그 숫자 문자열을 진짜 숫자(Integer)로 바꿔서 reId 변수에 저장합니다.
                    reId = Integer.parseInt(numericQuery);
                }
            } catch (NumberFormatException e) {
                // 18. 만약 숫자로 바꿀 수 없는 이상한 글자(예: 'abc')가 들어오면, 에러를 내지 않고 그냥 조용히 무시하고 넘어갑니다.
            }
        }
        
        // 19. 레포지토리에게 위에서 준비된 값들(status, guestName, reId)을 넘겨주면서 "DB에서 데이터 찾아주세요!" 하고 최종 요청을 보냅니다.
        return reservationRepository.findReservationsForManager(status, guestName, reId);
    }
}