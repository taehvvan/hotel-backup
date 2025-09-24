import { defineStore } from 'pinia'
import { toRaw } from 'vue';

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
    }
  }
});
