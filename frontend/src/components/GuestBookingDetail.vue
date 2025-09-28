<template>
  <div class="booking-detail-container">
    <header class="page-header">
      <h1>예약 상세 정보</h1>
      <p>고객님의 예약 내역을 확인하세요.</p>
    </header>

    <div v-if="reservation" class="reservation-card" :class="{ disabled: reservation.status === '예약 취소' }">
      <div class="hotel-image">
        <img
          :src="reservation.hotelImage"
          :alt="reservation.roomType || reservation.hotelName"
        />
      </div>

      <div class="reservation-info">
        <h2 class="hotel-name">{{ reservation.hotelName }}</h2>
        <p class="room-type">{{ reservation.roomType }}</p>
        <p class="hotel-address">{{ reservation.address }}</p>
        <p class="reservation-id"><strong>예약 번호:</strong> {{ reservation.orderId }}</p>
      </div>

      <div class="reservation-meta">
        <div>
          <span
            class="status-badge"
            :class="{
              'status-complete': reservation.status === '예약 완료',
              'status-cancel': reservation.status === '예약 취소'
            }"
          >
            {{ reservation.status }}
          </span>
          <p class="reservation-dates">
            {{ reservation.checkIn }} ~ {{ reservation.checkOut }}
          </p>
          <p class="reservation-price">가격: <span>{{ reservation.price.toLocaleString() }}원</span></p>
          <p class="reservation-people">인원: {{ reservation.people }}명</p>
        </div>

        <div class="action-buttons">
          <button 
            v-if="reservation.status === '예약 완료'"
            @click="cancelReservation"
            class="btn btn-cancel-reservation"
          >
            예약 취소
          </button>
          <button 
            v-if="reservation.status === '예약 취소'"
            @click="deleteCancelledReservation"
            class="btn btn-delete-cancelled"
          >
            취소 내역 삭제
          </button>
        </div>
      </div>
    </div>

    <div v-else class="empty-message">
      <p>예약 정보를 불러오는 중입니다...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter(); // router 인스턴스 추가
const reservation = ref(null);

onMounted(async () => {
  const orderId = route.params.orderId;
  if (!orderId) {
    console.error("URL에서 orderId를 찾을 수 없습니다.");
    return;
  }

  try {
    const response = await axios.get(`http://localhost:8888/api/reservations/order/${orderId}`);
    reservation.value = response.data;
  } catch (error) {
    console.error('예약 상세 정보 조회 실패:', error);
  }
});

// 👇 [추가] 예약 취소 함수
const cancelReservation = async () => {
  if (!reservation.value) return;

  const confirmed = confirm('정말 예약을 취소하시겠습니까?');
  if (!confirmed) return;

  try {
    // UserMypage.vue와 동일하게 reservationId를 사용합니다.
    await axios.put(`http://localhost:8888/api/reservations/${reservation.value.reservationId}/cancel`);
    
    alert('예약이 성공적으로 취소되었습니다.');
    // 상태를 즉시 UI에 반영
    reservation.value.status = '예약 취소';
  } catch (error) {
    console.error('예약 취소 실패:', error);
    alert(error.response?.data?.message || '예약 취소에 실패했습니다.');
  }
};

// 👇 [추가] 취소 내역 삭제 함수
const deleteCancelledReservation = async () => {
  if (!reservation.value) return;

  const confirmed = confirm('정말 이 취소된 예약 내역을 삭제하시겠습니까?');
  if (!confirmed) return;

  try {
    await axios.delete(`http://localhost:8888/api/reservations/${reservation.value.reservationId}/delete`);

    alert('취소 내역이 삭제되었습니다. 예약 확인 페이지로 돌아갑니다.');
    // 삭제 후에는 상세 페이지에 머무를 이유가 없으므로, 예약 확인 페이지로 이동
    router.push('/booking-check');
  } catch (error) {
    console.error('취소 내역 삭제 실패:', error);
    alert(error.response?.data?.message || '취소 내역 삭제에 실패했습니다.');
  }
};
</script>

<style scoped>
/* ... (기존 스타일 유지) ... */
.reservation-card.disabled {
  opacity: 0.6;
  background-color: #f9f9f9;
}

.reservation-meta {
  /* 👇 [수정] 버튼 공간 확보를 위해 flex-direction 변경 */
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-end;
}

.action-buttons {
  margin-top: 15px;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.btn {
  width: 100%;
  padding: 10px 15px;
  border-radius: 8px;
  border: none;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-cancel-reservation {
  background-color: #ff4141;
  color: #fff;
}
.btn-cancel-reservation:hover {
  background-color: #e03030;
}

.btn-delete-cancelled {
  background-color: #888;
  color: #fff;
}
.btn-delete-cancelled:hover {
  background-color: #555;
}
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
  font-size: 32px;
  font-weight: 700;
  color: #222;
}
.page-header p {
  color: #555;
  margin-top: 8px;
  font-size: 16px;
}
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
.hotel-image {
  flex-shrink: 0;
  width: 200px;
  height: 200px;
  overflow: hidden;
  border-radius: 12px 0 0 12px;
}
.hotel-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.reservation-info {
  flex: 1;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.hotel-name {
  font-size: 22px;
  font-weight: 700;
  color: #222;
  margin-bottom: 6px;
}
.room-type, .hotel-address {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}
.reservation-id {
  font-size: 15px;
  font-weight: 500;
  color: #444;
}
.reservation-meta {
  width: 250px;
  padding: 16px 20px;
}
.status-badge {
  padding: 8px 14px;
  font-size: 14px;
  font-weight: 700;
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
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
}
.reservation-price span {
  font-weight: 700;
  color: #1e40af;
}
.empty-message {
  text-align: center;
  color: #888;
  font-size: 16px;
  padding: 40px 0;
}
.status-badge {
  margin-bottom: 6px;
}
</style>