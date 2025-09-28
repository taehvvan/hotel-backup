<template>
  <div class="checkout-page">
    <div class="content-wrapper">
      <div class="main-content">
        <nav class="breadcrumbs">
          <a href="#" @click.prevent="goBack">숙소 상세</a>
          <span>›</span>
          <span>결제</span>
        </nav>

        <section class="checkout-section selected-room-section">
          <h2>선택하신 객실</h2>
          <div class="room-summary-card">
            <img :src="roomImage" :alt="roomName" class="selected-room-image">
            <div class="room-details-wrapper">
              <h3>{{ bookingStore.room?.type }}</h3>
              <p class="hotel-name-small">{{ bookingStore.hotel?.hname }}</p>
              <p class="location">📍 {{ location }}</p>
            </div>
          </div>
          <div class="date-picker">
            <div class="date-item">
              <label>체크인</label>
              <strong>
                <span v-if="bookingStore.room">⏰ {{ bookingStore.room.checkinTime }}</span>
              </strong>
            </div>

            <div class="date-item">
              <label>체크아웃</label>
              <strong>
                <span v-if="bookingStore.room">⏰ {{ bookingStore.room.checkoutTime }}</span>
              </strong>
            </div>
          </div>
        </section>

        <section class="checkout-section coupon-section">
          <h2>쿠폰 사용</h2>
          <div class="coupon-box">
            <div v-if="selectedCoupon">
              <p class="applied-coupon-name">{{ selectedCoupon.name }}</p>
              <strong class="applied-coupon-discount">{{ formatDiscount(selectedCoupon) }} 할인 적용됨</strong>
            </div>
            <p v-else>보유 쿠폰: <strong>{{ availableCoupons.length }}장</strong></p>
            <button class="btn-open-coupon" @click="isCouponModalVisible = true">쿠폰함 열기</button>
          </div>
        </section>

        <section class="checkout-section payment-method-section">
          <h2>결제</h2>
          <div class="payment-info-box">
            <p>안전하고 간편한 결제를 위해 <strong>토스페이먼츠</strong>를 사용합니다. 아래 결제하기 버튼을 누르면 토스 결제 창으로 이동합니다.</p>
          </div>
        </section>

        <section class="checkout-section sms-dispatch-section">
          <h2>예약 정보</h2>
          <div class="sms-box">
            <label for="phone-number">휴대폰 번호</label>
            <input
              type="tel"
              id="phone-number"
              placeholder="휴대폰 번호를 입력해주세요"
              v-model="phoneNumber"
            />

            <!-- 체크박스 + 안내문구 한 줄 -->
            <div class="save-phone-row">
              <label class="checkbox-label">
                <input type="checkbox" v-model="savePhoneNumber" />
                전화번호 저장하기
              </label>
            </div>
          </div>
        </section>
      </div>

      <aside class="sidebar">
        <div class="order-summary">
          <div class="order-summary-header">
            <img :src="image" :alt="hotelName">
            <div class="hotel-info">
              <h3>{{ bookingStore.hotel?.name }}</h3>
              <p class="room-name-on-card">{{ bookingStore.room?.type }}</p>
              <div class="rating-info">
                <span class="score">{{ rating }}</span>
                <span>아주 좋아요</span>
                <span>·</span>
                <span>후기 {{ reviews }}개</span>
              </div>
            </div>
          </div>
          <hr>
          <h2>요금 세부정보</h2>
          <div class="price-details">
            <div class="price-row">
              <span>기본 요금</span>
              <span> ₩ {{ basePrice.toLocaleString() }} </span>
            </div>
            <div class="price-row highlight" v-if="couponDiscount > 0">
              <span>쿠폰 할인</span>
              <span class="discount-amount">-₩{{ couponDiscount.toLocaleString() }}</span>
            </div>
            <div class="price-row">
              <span>세금 및 수수료</span>
              <span> ₩ {{ taxes }}</span>
            </div>
            <hr class="total-divider">
            <div class="price-row total">
              <strong>총 결제 금액</strong>
              <strong>₩ {{ finalPrice.toLocaleString() }}</strong>
            </div>
          </div>
          <button class="btn-payment" @click="handlePayment" :disabled="isLoading">
            {{ isLoading ? '정보 로딩 중...' : '결제하기' }}
          </button>
        </div>
      </aside>
    </div>
  </div>
  
  <CouponModal 
    v-if="isCouponModalVisible"
    :coupons="availableCoupons"
    @close="isCouponModalVisible = false"
    @select-coupon="applyCoupon"
  />
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { watch } from 'vue';
import { useRouter } from 'vue-router';
import CouponModal from './CouponModal.vue';
import { useBookingStore } from '@/stores/booking'
import { watchEffect } from 'vue'
import { useAuthStore } from '@/stores/auth';
import axios from 'axios';

const router = useRouter();

const bookingStore = useBookingStore()
const authStore = useAuthStore();

const isLoading = ref(true);

const hotel = computed(() => bookingStore.hotel);
const room = computed(() => bookingStore.room);

const hotelImageUrl = computed(() => {
  if (!hotel.value?.type || !hotel.value?.hId) {
    return ''; // 호텔 정보가 없으면 빈 경로 반환
  }
  return `http://localhost:8888/images/${hotel.value.type}/${hotel.value.hId}.jpg`;
});

const roomImage = computed(() => hotelImageUrl.value);

// 사이드바에 표시될 이미지
const image = computed(() => hotelImageUrl.value);

console.log('검색 조건:', bookingStore.search)
console.log('호텔 정보:', bookingStore.hotel)
console.log('선택 객실:', bookingStore.room)

const phoneNumber = ref('');
const savePhoneNumber = ref(false);

// 체크박스 상태 변경 시 localStorage 처리
watch([savePhoneNumber, phoneNumber], ([saveChecked, number]) => {
  if (saveChecked && number) {
    localStorage.setItem('savedPhoneNumber', number)
  } else {
    localStorage.removeItem('savedPhoneNumber')
  }
})

const isCouponModalVisible = ref(false);
const selectedCoupon = ref(null);
const availableCoupons = ref([]);
const taxes = ref(5000);

// 객실 가격 가져오기
const basePrice = computed(() => bookingStore.room?.price || 0)

// 쿠폰 할인 계산
const couponDiscount = computed(() => {
  if (!selectedCoupon.value) return 0
  if (selectedCoupon.value.type === 'percent') {
    return Math.floor(basePrice.value * (selectedCoupon.value.discount / 100))
  }
  return selectedCoupon.value.discount || 0
})

const finalPrice = computed(() => basePrice.value - couponDiscount.value + taxes.value)

const tossPayments = ref(null);
const clientKey = 'test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq';

onMounted(async () => {

  // --- ✅ 데이터 로딩 로직 추가 ---
  try {
    // bookingStore에 방 정보가 없다면 (새로고침 등)
    if (!bookingStore.room) {
      // store에 저장된 ID를 기반으로 상세 정보를 다시 불러오는 액션을 호출합니다.
      // 이 fetchBookingDetails 액션은 stores/booking.js 안에 직접 만들어야 합니다.
      await bookingStore.fetchBookingDetails(); 
    }
  } catch (error) {
    console.error("예약 정보를 불러오는 데 실패했습니다:", error);
    alert("예약 정보를 불러오지 못했습니다. 이전 페이지로 돌아갑니다.");
    router.go(-1); // 이전 페이지로 돌려보내기
  } finally {
    isLoading.value = false; // 데이터 로딩 완료 (성공/실패 모두)
  }
  // ---------------------------

  const script = document.createElement('script');
  script.src = "https://js.tosspayments.com/v1";
  script.onload = () => {
    try {
      tossPayments.value = TossPayments(clientKey);
    } catch (e) {
      console.error("Toss Payments SDK 초기화 실패:", e);
    }
  };
  document.head.appendChild(script);

  availableCoupons.value = [
    { id: 'coupon1', name: '신규 회원 10% 할인 쿠폰', type: 'percent', discount: 10, expiryDate: '2025-12-31' },
    { id: 'coupon2', name: '가을맞이 20,000원 할인', type: 'fixed', discount: 20000, expiryDate: '2025-10-31' },
  ];

  const savedNumber = localStorage.getItem('savedPhoneNumber')
  if (savedNumber) {
    phoneNumber.value = savedNumber
    savePhoneNumber.value = true
  }
});

const goBack = () => {
  router.go(-1);
};

const applyCoupon = (coupon) => {
  selectedCoupon.value = coupon;
  isCouponModalVisible.value = false;
};

const formatDiscount = (coupon) => {
  if (coupon.type === 'percent') return `${coupon.discount}%`;
  if (coupon.type === 'fixed') return `${coupon.discount.toLocaleString()}원`;
  return '';
};

const handlePayment = async () => {
if (authStore.isLoading) {
    alert("사용자 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.");
    return;
}

// room 객체나 room.rid가 유효한지 한번 더 확인 (최후의 안전장치)
if (!bookingStore.room || !bookingStore.room.rId) {
  return alert('예약 정보가 올바르지 않습니다. 페이지를 새로고침 후 다시 시도해주세요.');
}

const room = bookingStore.room;
const hotel = bookingStore.hotel;
const search = bookingStore.search;
const reservationId = bookingStore.reservationId;
const userId = authStore.userId;

// ✅ 객실 잔여 수 확인
// availabilities 배열이 비어있으면 총 count를 사용
let availableCount = room.availabilities.length > 0 
  ? Math.min(...room.availabilities.map(a => a.availableCount)) // 선택 기간 중 최소 잔여 수
  : room.count;

if (availableCount <= 0) return alert('죄송합니다. 선택한 날짜에 잔여 객실이 없습니다.');

if (!tossPayments.value) return alert('결제 모듈이 준비되지 않았습니다.');

if (!room || !hotel || !search) return alert('예약 정보가 올바르지 않습니다.');

try {
    // --- 1. [핵심] 백엔드에 '결제 준비' API를 호출하여 임시 예약을 생성합니다. ---
    const prepareResponse = await axios.post('http://localhost:8888/api/reservations/prepare', {
      // 서버(ReservationRequest.java)에서 요구하는 필드들을 전송합니다.
      rId: bookingStore.room.rId,
      hId: bookingStore.hotel.hId,
      uId: authStore.isLoggedIn ? authStore.userId : null,
      checkin: bookingStore.checkIn,
      checkout: bookingStore.checkout,
      people: bookingStore.guests,
      price: finalPrice.value,
    });

    // --- 2. API 응답에서 백엔드가 생성한 8자리 orderId와 임시 예약 ID(reId)를 추출합니다. ---
    const { orderId, reservationId } = prepareResponse.data;

    if (!orderId || !reservationId) {
      throw new Error("서버로부터 유효한 예약 정보를 받지 못했습니다.");
    }

    // --- 3. 결제 완료 후 Callback 페이지에서 사용할 정보를 localStorage에 저장합니다. ---
    const paymentInfo = {
      reservationId: reservationId, // 백엔드에서 받은 임시 예약 ID
      roomId: bookingStore.room.rId,
      hotelId: bookingStore.hotel.hId,
      userId: authStore.isLoggedIn ? authStore.userId : null,
      phone: phoneNumber.value,
      // amount와 orderName은 토스 결제창에 직접 전달하므로 저장할 필요는 없습니다.
    };
    localStorage.setItem('paymentInfo', JSON.stringify(paymentInfo));

    // --- 4. 백엔드에서 받은 orderId로 토스페이먼츠 결제창을 호출합니다. ---
    await tossPayments.value.requestPayment('card', { // '카드' 외 다른 결제수단 가능
      amount: finalPrice.value,
      orderId: orderId, // ✨ 백엔드에서 받은 8자리 예약번호
      orderName: `${bookingStore.hotel.hname} - ${bookingStore.room.type}`,
      customerName: authStore.userName || '비회원 고객', // 비회원일 경우를 대비한 기본값
      successUrl: `${window.location.origin}/payment-callback`,
      failUrl: `${window.location.origin}/payment-fail`,
    });

  } catch (error) {
    console.error('결제 처리 중 오류 발생:', error);
    // Axios 오류인 경우 서버에서 보낸 메시지를, 그 외에는 일반 오류 메시지를 표시합니다.
    const errorMessage = error.response?.data?.message || error.message || '알 수 없는 오류가 발생했습니다.';
    alert(`결제 처리 중 오류가 발생했습니다: ${errorMessage}`);
    // 실패 시 저장했던 정보 삭제
    localStorage.removeItem('paymentInfo');
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700;800&display=swap');

.checkout-page {
  font-family: 'Noto Sans KR', sans-serif;
  background-color: #F9F9F9;
  padding: 40px 0;
  min-height: 100vh;
  color: #333;
}
.content-wrapper {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 40px;
  align-items: flex-start;
}
.main-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.breadcrumbs {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.breadcrumbs a { color: #666; text-decoration: none; }
.breadcrumbs a:hover { color: #0A2A66; }
.breadcrumbs span:last-child { font-weight: 500; color: #333; }

.checkout-section {
  background-color: #fff;
  border-radius: 12px;
  padding: 25px;
  border: 1px solid #EAEAEA;
}
.checkout-section h2 {
  font-size: 1.4rem;
  font-weight: 700;
  margin: 0 0 20px 0;
  color: #222;
}

.room-summary-card {
  display: flex;
  align-items: center;
  gap: 20px;
}
.selected-room-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
}
.room-details-wrapper { flex-grow: 1; }
.room-details-wrapper h3 { font-size: 1.3rem; margin: 0 0 5px 0; font-weight: 600; }
.hotel-name-small, .location { font-size: 0.9rem; color: #666; margin: 2px 0; }
.date-picker {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}
.date-item { text-align: left; flex: 1; }
.date-item label { font-size: 0.85rem; color: #888; display: block; }
.date-item strong { font-size: 1.1rem; font-weight: 600; color: #444; }
.nights { color: #999; text-align: center; flex-basis: 50px; }

.coupon-box { display: flex; justify-content: space-between; align-items: center; }
.coupon-box p { margin: 0; font-size: 1rem; color: #555; }
.coupon-box strong { color: #00796B; }
.btn-open-coupon {
  background-color: #fff;
  color: #0A2A66;
  border: 1px solid #0A2A66;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
}
.applied-coupon-name { font-weight: 600; color: #333; }
.applied-coupon-discount { font-size: 1rem; font-weight: 700; color: #00796B; margin-top: 4px; display: block; }

.payment-info-box { background-color: #f0f8ff; border-left: 4px solid #0A2A66; padding: 20px; font-size: 0.95rem; line-height: 1.6; }

.sms-box label { display: block; font-weight: 500; margin-bottom: 8px; }
.sms-box input { width: 100%; padding: 12px 15px; border: 1px solid #ddd; border-radius: 8px; font-size: 1rem; box-sizing: border-box; }

.sidebar { position: relative; }
.order-summary {
  position: sticky;
  top: 40px;
  padding: 25px;
  background-color: #fff;
  border-radius: 12px;
  border: 1px solid #EAEAEA;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
.order-summary-header { display: flex; gap: 15px; align-items: flex-start; margin-bottom: 20px; }
.order-summary-header img { width: 80px; height: 80px; border-radius: 8px; object-fit: cover; }
.hotel-info h3 { font-size: 1.2rem; margin: 0 0 5px 0; font-weight: 600; }
.hotel-info p { font-size: 0.95rem; color: #555; margin: 0; }
.rating-info { display: flex; align-items: center; gap: 5px; font-size: 0.9rem; color: #777; margin-top: 8px; }
.rating-info .score { background: #0A2A66; color: #fff; padding: 2px 6px; border-radius: 4px; font-weight: 700; }
.order-summary hr { border: 0; border-top: 1px solid #eee; margin: 20px 0; }
.order-summary h2 { font-size: 1.4rem; font-weight: 700; margin-bottom: 20px; }
.price-details .price-row { display: flex; justify-content: space-between; margin-bottom: 15px; font-size: 1rem; }
.price-details .price-row.highlight { color: #E53935; }
.price-details .discount-amount { font-weight: 500; }
.total-divider { margin-top: 15px; }
.price-details .price-row.total { font-size: 1.3rem; margin-top: 15px; font-weight: 800; }
.btn-payment {
  width: 100%;
  padding: 14px;
  font-size: 1.2rem;
  font-weight: 700;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  background-color: #0A2A66;
  color: #fff;
  margin-top: 20px;
}

.sms-box {
display: flex;
flex-direction: column;
gap: 10px;
}

.save-phone-row {
display: flex;
align-items: center;
gap: 8px; /* 체크박스-라벨-안내문 간격 */
font-size: 0.9rem;
color: #555;
}

.checkbox-label {
display: flex;
align-items: center;
gap: 4px; /* 체크박스와 라벨 텍스트 간격 */
margin: 0;
font-weight: normal;
}

.checkbox-label input[type="checkbox"] {
width: 16px;  /* 체크박스 크기 조정 */
height: 16px; /* 체크박스 크기 조정 */
}

</style>
