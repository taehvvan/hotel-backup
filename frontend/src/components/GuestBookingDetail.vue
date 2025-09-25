<template>
    <div class="booking-detail-container p-6">
      <header class="page-header mb-6">
        <h1 class="text-3xl font-bold">예약 상세 정보</h1>
        <p class="text-gray-600">예약 내역을 확인하세요.</p>
      </header>
  
      <div v-if="reservations.length">
        <div
          v-for="reservation in reservations"
          :key="reservation.reservationId"
          class="reservation-card bg-white rounded-xl shadow-md p-5 mb-5 flex flex-col md:flex-row"
        >
          <!-- 호텔 이미지 -->
          <div class="hotel-image w-full md:w-1/3 mb-4 md:mb-0">
            <img
              :src="reservation.hotelImage"
              :alt="reservation.hotelName"
              class="rounded-lg w-full h-48 object-cover"
            />
          </div>
  
          <!-- 예약 정보 -->
          <div class="reservation-info w-full md:w-2/3 md:pl-6 flex flex-col justify-between">
            <div>
              <p><strong>예약 번호:</strong> {{ reservation.reservationId }}</p>
              <p><strong>체크인:</strong> {{ reservation.checkIn }}</p>
              <p><strong>체크아웃:</strong> {{ reservation.checkOut }}</p>
              <p><strong>예약 인원:</strong> {{ reservation.people }}명</p>
              <p><strong>가격:</strong> {{ reservation.price }}원</p>
              <p><strong>예약 상태:</strong> <span class="text-blue-600">{{ reservation.status }}</span></p>
              <p><strong>객실 타입:</strong> {{ reservation.roomType }}</p>
              <p><strong>호텔 이름:</strong> {{ reservation.hotelName }}</p>
              <p><strong>호텔 주소:</strong> {{ reservation.address }}</p>
            </div>
            <button class="mt-4 self-start bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">
              상세 보기
            </button>
          </div>
        </div>
      </div>
  
      <div v-else>
        <p class="text-gray-500">예약 정보를 찾을 수 없습니다.</p>
      </div>
    </div>
  </template>
  
  <script setup>
  import { useRoute } from 'vue-router'
  import { useBookingStore } from '@/stores/booking'
  
  const route = useRoute()
  const bookingStore = useBookingStore()
  
  // 예시: 여러 예약 내역 배열로 받기
  const reservations = route.query.reservations
    ? JSON.parse(route.query.reservations)
    : []
  
  // fallback으로 단일 예약일 경우
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
      hotelImage: bookingStore.hotel?.hotelImage || ''
    })
  }
  </script>
  
  <style scoped>
  .booking-detail-container {
    max-width: 1000px;
    margin: 0 auto;
  }
  
  .reservation-card {
    transition: transform 0.2s, box-shadow 0.2s;
  }
  
  .reservation-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
  }
  </style>
  