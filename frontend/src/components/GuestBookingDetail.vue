<template>
  <div class="booking-detail-container">
    <header class="page-header">
      <h1>예약 상세 정보</h1>
      <p>고객님의 예약 내역을 확인하세요.</p>
    </header>

    <div v-if="reservations.length">
      <div
        v-for="reservation in reservations"
        :key="reservation.reservationId"
        class="reservation-card"
      >
        <!-- 호텔 이미지 -->
        <div class="hotel-image">
          <img
            :src="`http://localhost:8888/images/${reservation.hotelType || 'default'}/${reservation.hotelId || '0'}.jpg`"
            :alt="reservation.roomType || reservation.hotelName"
          />
        </div>

        <!-- 호텔 정보 (중앙 영역) -->
        <div class="reservation-info">
          <h2 class="hotel-name">{{ reservation.hotelName }}</h2>
          <p class="room-type">{{ reservation.roomType }}</p>
          <p class="hotel-address">{{ reservation.address }}</p>
          <p class="reservation-id"><strong>예약 번호:</strong> {{ reservation.reservationId }}</p>
        </div>

        <!-- 우측 영역: 상태, 날짜, 가격/인원 -->
        <div class="reservation-meta">
          <span
            class="status-badge"
            :class="{
              'status-complete': reservation.status === '예약 완료',
              'status-pending': reservation.status === '대기',
              'status-cancel': reservation.status === '취소됨'
            }"
          >
            {{ reservation.status }}
          </span>
          <p class="reservation-dates">
            {{ reservation.checkIn }} ~ {{ reservation.checkOut }}
          </p>
          <p class="reservation-price">가격: <span>{{ reservation.price }}원</span></p>
          <p class="reservation-people">인원: {{ reservation.people }}명</p>
        </div>
      </div>
    </div>

    <div v-else class="empty-message">
      <p>예약 정보를 찾을 수 없습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { useBookingStore } from '@/stores/booking'

const route = useRoute()
const bookingStore = useBookingStore()

const reservations = route.query.reservations
  ? JSON.parse(route.query.reservations)
  : []

if (!reservations.length && route.query.reservationId) {
  reservations.push({
    reservationId: route.query.reservationId,
    checkIn: route.query.checkIn,
    checkOut: route.query.checkOut,
    people: route.query.people,
    price: route.query.price,
    status: route.query.status,
    roomType: route.query.roomType,
    hotelName: route.query.hotelName,
    address: route.query.address,
    hotelType: bookingStore.hotel?.type || 'default',
    hotelId: bookingStore.hotel?.hId || '0'
  })
}
</script>

<style scoped>
.booking-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
  font-family: "Segoe UI", Arial, sans-serif;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 32px; /* 글자 크기 증가 */
  font-weight: 700; /* 굵기 강화 */
  color: #222;
}

.page-header p {
  color: #555;
  margin-top: 8px;
  font-size: 16px; /* 글자 크기 조금 키움 */
}

/* 카드 레이아웃 */
.reservation-card {
  display: flex;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  margin-bottom: 24px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.reservation-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.15);
}

/* 호텔 이미지 */
.hotel-image {
  flex-shrink: 0;        /* 이미지 영역이 줄어들지 않도록 고정 */
  width: 200px;          /* 정사각형 기준 너비 */
  height: 200px;         /* 정사각형 높이 */
  overflow: hidden;      /* 이미지가 영역 밖으로 나오지 않도록 */
  border-radius: 12px 0 0 12px; /* 카드 왼쪽 모서리 둥글게 */
}

.hotel-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;     /* 비율 유지하면서 꽉 채움 */
  display: block;        /* 이미지 아래 여백 제거 */
}

/* 중앙 호텔 정보 영역 */
.reservation-info {
  flex: 1;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.hotel-name {
  font-size: 22px; /* 글자 크기 증가 */
  font-weight: 700; /* 굵기 강화 */
  color: #222;
  margin-bottom: 6px;
}

.room-type, .hotel-address {
  font-size: 16px; /* 조금 더 키움 */
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.reservation-id {
  font-size: 15px;
  font-weight: 500;
  color: #444;
}

/* 우측 예약 정보 */
.reservation-meta {
  width: 250px;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
}

.status-badge {
  padding: 8px 14px; /* 배지 크기 증가 */
  font-size: 14px; /* 글자 크기 증가 */
  font-weight: 700; /* 굵기 강화 */
  border-radius: 20px;
  margin-bottom: 12px;
  text-align: center;
}

.status-complete {
  background: #e6f8ec;
  color: #2d8a45;
}

.status-pending {
  background: #fff4e5;
  color: #c97a00;
}

.status-cancel {
  background: #fdeaea;
  color: #c53030;
}

.reservation-dates, .reservation-price, .reservation-people {
  font-size: 15px; /* 글자 크기 증가 */
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
}

.reservation-price span {
  font-weight: 700; /* 가격 강조 */
  color: #1e40af;
}

.empty-message {
  text-align: center;
  color: #888;
  font-size: 16px;
  padding: 40px 0;
}
</style>

