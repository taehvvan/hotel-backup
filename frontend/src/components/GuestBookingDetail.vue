<template>
    <div class="booking-detail-container">
      <header class="page-header">
        <h1>예약 상세 정보</h1>
      </header>
  
      <div v-if="reservation">
        <div class="reservation-info">
          <p><strong>예약 번호:</strong> {{ reservation.reservationId }}</p>
          <p><strong>체크인 날짜:</strong> {{ reservation.checkIn }}</p>
          <p><strong>체크아웃 날짜:</strong> {{ reservation.checkOut }}</p>
          <p><strong>예약 인원:</strong> {{ reservation.people }}명</p>
          <p><strong>가격:</strong> {{ reservation.price }}원</p>
          <p><strong>예약 상태:</strong> {{ reservation.status }}</p>
          <p><strong>객실 타입:</strong> {{ reservation.roomType }}</p>
          <p><strong>호텔 이름:</strong> {{ reservation.hotelName }}</p>
          <p><strong>호텔 주소:</strong> {{ reservation.address }}</p>
        </div>
  
        <div class="hotel-image">
          <img :src="reservation.hotelImage" alt="호텔 이미지" />
        </div>
      </div>
  
      <div v-else>
        <p>예약 정보를 찾을 수 없습니다.</p>
      </div>
    </div>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router';
  
  // 예약 정보 state로 가져오기
  const router = useRouter();
  const reservation = router.currentRoute.value.state?.reservation;
  </script>
  
  <style scoped>
  .booking-detail-container {
    text-align: center;
    padding: 20px;
  }
  
  .page-header {
    margin-bottom: 20px;
  }
  
  .page-header h1 {
    font-size: 2rem;
    font-weight: bold;
  }
  
  .reservation-info {
    font-size: 1rem;
    text-align: left;
    margin-top: 20px;
  }
  
  .reservation-info p {
    margin: 10px 0;
  }
  
  .hotel-image {
    margin-top: 20px;
  }
  
  .hotel-image img {
    max-width: 100%;
    height: auto;
    border-radius: 8px;
  }
  </style>
  