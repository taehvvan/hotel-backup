import { defineStore } from 'pinia'
import { toRaw } from 'vue';
import axios from 'axios'


export const useBookingStore = defineStore('booking', {
  state: () => ({
    search: JSON.parse(localStorage.getItem('booking_search') || '{}'),
    hotel: JSON.parse(localStorage.getItem('booking_hotel') || 'null') || null,
    room: JSON.parse(localStorage.getItem('booking_room') || 'null') || null,   // null 처리 개선
    reservationId: null,
  }),

  actions: {
    setBooking(search, hotel, room) {
      this.search = search;
      this.hotel = toRaw(hotel);
      this.room = room;

      // localStorage에 저장
      localStorage.setItem('booking_search', JSON.stringify(search));
      localStorage.setItem('booking_hotel', JSON.stringify(hotel));
      localStorage.setItem('booking_room', JSON.stringify(room));

      // 상태를 세팅 후 로그로 출력하여 확인
      console.log('Booking set:', this.search, this.hotel, this.room);
    },

    setReservationId(id) { // ← 새 액션
      this.reservationId = id;
      console.log('Reservation ID set:', id);
    },

    clearBooking() {
      this.search = {};
      this.hotel = null;
      this.room = null;
      this.reservationId = null;

      // localStorage 초기화
      localStorage.removeItem('booking_search');
      localStorage.removeItem('booking_hotel');
      localStorage.removeItem('booking_room');
      
      // 상태를 초기화 후 로그로 출력하여 확인
      console.log('Booking cleared');
    },

    clearLocalStorage() {
      localStorage.removeItem('booking_search');
      localStorage.removeItem('booking_hotel');
      localStorage.removeItem('booking_room');
    },

    async fetchBookingDetails() {
      console.log("데이터 복구를 시도합니다. (fetchBookingDetails 호출됨)");

      // 1. state에 이미 정보가 있다면 실행할 필요가 없습니다.
      if (this.hotel && this.room) {
        console.log("State에 데이터가 이미 존재하여 fetch를 건너뜁니다.");
        return;
      }

      // 2. localStorage에서 ID 값을 가져옵니다.
      const storedHotel = JSON.parse(localStorage.getItem('booking_hotel') || 'null');
      const storedRoom = JSON.parse(localStorage.getItem('booking_room') || 'null');

      // ID가 없으면 데이터를 불러올 수 없으므로 에러를 발생시키고 종료합니다.
      if (!storedHotel?.hid || !storedRoom?.rid) {
        this.clearBooking(); // 잘못된 정보가 남지 않도록 초기화
        throw new Error("localStorage에 예약 정보를 불러올 ID가 없습니다.");
      }

      const hotelId = storedHotel.hid;
      const roomId = storedRoom.rid;

      try {
        // 3. ID를 이용해 서버에 호텔과 방 정보를 각각 요청합니다.
        // (주의: 아래 URL은 실제 백엔드 API 주소에 맞게 수정해야 합니다.)
        console.log(`ID [hotel: ${hotelId}, room: ${roomId}]로 서버에 데이터 요청...`);
        
        const [hotelResponse, roomResponse] = await Promise.all([
          axios.get(`http://localhost:8888/api/hotels/${hotelId}`),
          axios.get(`http://localhost:8888/api/rooms/${roomId}`) 
        ]);

        // 4. 성공적으로 받아온 데이터로 state와 localStorage를 업데이트합니다.
        this.hotel = hotelResponse.data;
        this.room = roomResponse.data;
        
        localStorage.setItem('booking_hotel', JSON.stringify(this.hotel));
        localStorage.setItem('booking_room', JSON.stringify(this.room));

        console.log("✅ 데이터 복구 및 state 업데이트 완료!");

      } catch (error) {
        console.error("서버에서 예약 정보를 불러오는 중 오류 발생:", error);
        this.clearBooking(); // 오류 발생 시 깨끗하게 초기화
        throw error; // 에러를 호출한 컴포넌트로 다시 전달
      }
    }
  }
});
