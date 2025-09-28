<template>
    <div class="payment-success-page">
      <div class="content-wrapper">
        <h1>🎉 결제가 성공적으로 완료되었습니다!</h1>
        <p>예약이 정상적으로 처리되었습니다. 이용해주셔서 감사합니다.</p>
        
        <div class="info-box" v-if="orderId">
          <strong>예약 번호:</strong> {{ orderId }}
        </div>
        <div class="button-group">
          <button class="btn-home" @click="goHome">홈으로 돌아가기</button>
          <button class="btn-bookings" @click="goToBookings">예약 내역 확인하기</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useRouter, useRoute } from 'vue-router'
  import { useAuthStore } from '../stores/auth';
  import { useBookingStore } from '../stores/booking';

    const route = useRoute()
    const router = useRouter();
    const authStore = useAuthStore();
    const bookingStore = useBookingStore();
    const orderId = ref('');

    onMounted(() => {
      const token = localStorage.getItem('accessToken');

    // 1. URL 쿼리에서 orderId를 가져와 ref에 할당합니다.
    orderId.value = route.query.orderId || '';
    
    // 2. (선택) 콘솔에 orderId를 출력하여 확인합니다.
    console.log('결제 성공 페이지 로드, 예약 번호:', orderId.value);
    
    // 3. 불필요해진 로컬 스토리지의 예약 정보를 삭제합니다.
    bookingStore.clearBooking();
    
    console.log('localStorage의 예약 정보가 삭제되었습니다.');
    // 이 곳에서 서버로 결제 완료 사실을 알리는 API를 호출할 수도 있습니다 (선택 사항).
    });
  
  const goHome = () => {
    router.push('/')
  }
  
  const goToBookings = () => {
    if (authStore.isLoggedIn) {
      router.push('/mypage') // 회원이면 마이페이지
    } else {
      router.push('/booking-check') // 비회원이면 예약 조회 페이지
    }
  }

  console.log('결제 성공 orderId:', route.query.orderId)
  </script>
  
  <style scoped>
  .payment-success-page {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 80vh;
    text-align: center;
    padding: 40px 20px;
    font-family: 'Noto Sans KR', sans-serif;
    background-color: #f9f9f9;
    color: #333;
  }
  
  .content-wrapper {
    background-color: #fff;
    padding: 40px 30px;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.1);
    max-width: 500px;
    width: 100%;
  }
  
  h1 {
    font-size: 1.8rem;
    margin-bottom: 20px;
    color: #0A2A66;
  }
  
  p {
    font-size: 1rem;
    margin-bottom: 30px;
    color: #555;
  }
  
  .button-group {
    display: flex;
    justify-content: center;
    gap: 15px;
  }
  
  .button-group button {
    padding: 12px 20px;
    border-radius: 8px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    border: none;
    transition: all 0.2s ease-in-out;
  }
  
  .btn-home {
    background-color: #0A2A66;
    color: #fff;
  }
  
  .btn-home:hover {
    background-color: #09305a;
  }
  
  .btn-bookings {
    background-color: #fff;
    color: #0A2A66;
    border: 2px solid #0A2A66;
  }
  
  .btn-bookings:hover {
    background-color: #0A2A66;
    color: #fff;
  }
  </style>
  