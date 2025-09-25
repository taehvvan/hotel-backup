// 1. 이 자바 파일이 com.example.backend.manager 라는 폴더(패키지)에 속해있음을 알려줍니다.
package com.example.backend.manager;

// 2. 다른 폴더에 있는 필요한 부품(클래스)들을 가져와서 쓸 수 있게 준비합니다.
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// 3. 이 클래스가 인터넷 주소(URL) 요청에 응답하는 RESTful API의 컨트롤러임을 스프링에게 알려줍니다.
@RestController
// 4. '/api/manager/reservations' 라는 주소로 오는 모든 요청은 이 컨트롤러가 담당하도록 지정합니다.
@RequestMapping("/api/manager/reservations")
// 5. Lombok 기능으로, final로 선언된 꼭 필요한 부품(ManagerReservationService)을 자동으로 연결해주는 생성자를 만듭니다.
@RequiredArgsConstructor
// 6. 'http://localhost:5173' 주소(Vue 개발 서버)에서 오는 요청을 허락해줘서 CORS 에러를 막습니다.
@CrossOrigin(origins = "http://localhost:5173")
public class ManagerReservationController {

    // 7. 서비스(실질적인 일꾼) 부품을 연결합니다. final 키워드는 한번 연결되면 절대 바뀌지 않음을 의미합니다.
    private final ManagerReservationService managerReservationService;

    // 8. HTTP GET 방식('.../reservations' 주소)의 요청을 처리하는 기능을(메소드) 만든다고 알려줍니다.
    @GetMapping
    // 9. 'List<ManagerReservationDto>' 형태의 데이터를 담아서 성공/실패 여부와 함께 응답을 보낼 기능입니다.
    public ResponseEntity<List<ManagerReservationDto>> searchReservations(
            // 10. 웹 주소 뒤에 ?status=... 값이 오면 그 값을 'status' 변수에 저장합니다. 값이 없으면 기본으로 'all'을 넣습니다.
            @RequestParam(defaultValue = "all") String status,
            // 11. ?searchType=... 값이 오면 그 값을 'searchType' 변수에 저장합니다. 'required = false'는 이 값이 없어도 괜찮다는 뜻입니다.
            @RequestParam(required = false) String searchType,
            // 12. ?searchQuery=... 값이 오면 그 값을 'searchQuery' 변수에 저장합니다. 이 값도 필수는 아닙니다.
            @RequestParam(required = false) String searchQuery) {
        
        // 13. 서비스 부품에게 위에서 받은 3개의 값(status, searchType, searchQuery)을 넘겨주면서 "예약 목록 좀 찾아주세요" 하고 부탁합니다.
        List<ManagerReservationDto> reservations = managerReservationService.getReservations(status, searchType, searchQuery);
        
        // 14. 서비스가 찾아준 예약 목록(reservations)을 '성공(OK)' 신호와 함께 프론트엔드에 최종적으로 보내줍니다.
        return ResponseEntity.ok(reservations);
    }
}