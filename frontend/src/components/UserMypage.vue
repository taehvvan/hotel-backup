<template>
  <div class="mypage-container">
    <div class="content-wrapper">
      <div class="user-profile">
        <div class="profile-info">
          <h3>{{ userInfo.name }}님</h3>
          <p>{{ userInfo.email }}</p>
          <div class="profile-actions">
            <button @click="changeTab('profile')">회원정보 수정</button>
            <button @click="logout">로그아웃</button>
          </div>
        </div>
      </div>

      <div class="mypage-content">
        <div class="mypage-menu">
          <button
            :class="{ active: activeTab === 'reservations' }"
            @click="changeTab('reservations')"
          >
            <span>예약 내역</span>
          </button>
          <button
            :class="{ active: activeTab === 'reviews' }"
            @click="changeTab('reviews')"
          >
            <span>내가 쓴 후기</span>
          </button>
          <button
            :class="{ active: activeTab === 'coupons' }"
            @click="changeTab('coupons')"
          >
            <span>쿠폰함</span>
          </button>
        </div>

        <div class="tab-content">
          <div v-if="activeTab === 'reservations'" class="tab-pane">
            <div class="section-header">
              <h4>예약 내역</h4>
              <span class="header-line"></span>
            </div>

            <!-- 예약 완료 리스트 -->
            <div v-if="reservations.filter(r => r.status === '예약 완료').length > 0" class="reservation-list">
              <h3>예약 완료</h3>
              <div 
                v-for="reservation in completedReservations" 
                :key="reservation.reservationId" 
                class="reservation-card"
              >
                <div class="card-image">
                  <img
                    :src="reservation.image"
                    :alt="reservation.roomType || reservation.hotelName"
                  />
                </div>
                <div class="card-info">
                  <h5 class="place-name">{{ reservation.placeName }}</h5>
                  <p class="reservation-details">
                    <span><strong>객실:</strong> {{ reservation.roomType }}</span>
                    <span><strong>주소:</strong> {{ reservation.address }}</span>
                  </p>
                  <div class="reservation-actions">
                    <p class="status-badge completed">{{ reservation.statusText }}</p>
                    <p class="dates">{{ reservation.checkIn }} ~ {{ reservation.checkOut }}</p>
                    <p class="price-people">
                      <span>가격: {{ reservation.price.toLocaleString() }}원</span>
                      <span>인원: {{ reservation.guests }}명</span>
                    </p>
                  <button
                    v-if="isReviewable(reservation)"
                    @click="openReviewModal(reservation)"
                    class="btn-review-write"
                  >
                    후기 작성
                  </button>
                  <button 
                    v-if="reservation.status !== 'cancelled'" 
                    @click="cancelReservation(reservation.reservationId)"
                    class="btn-cancel-reservation"
                  >
                    예약 취소
                  </button>
                   <p v-else-if="reservation.status === '리뷰 작성 완료'" class="status-badge reviewed">
                    작성 완료
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- 취소 내역 리스트 -->
          <div v-if="reservations.filter(r => r.status === '예약 취소').length > 0" class="reservation-list">
            <h3>취소 내역</h3>
            <div 
              v-for="reservation in cancelledReservations" 
              :key="reservation.reservationId" 
              class="reservation-card disabled"
            >
              <div class="card-image">
                <img
                  :src="reservation.hotelImage"
                  :alt="reservation.roomType || reservation.hotelName"
                />
              </div>
              <div class="card-info">
                <h5 class="place-name">{{ reservation.placeName }}</h5>
                <p class="reservation-details">
                  <span><strong>객실:</strong> {{ reservation.roomType }}</span>
                  <span><strong>주소:</strong> {{ reservation.address }}</span>
                </p>
                <div class="reservation-actions">
                  <p class="status-badge cancelled">{{ reservation.statusText }}</p>
                  <p class="dates">{{ reservation.checkIn }} ~ {{ reservation.checkOut }}</p>
                  <p class="price-people">
                    <span>가격: {{ reservation.price.toLocaleString() }}원</span>
                    <span>인원: {{ reservation.guests }}명</span>
                  </p>
                  <button 
                    @click="deleteCancelledReservation(reservation.reservationId)"
                    class="btn-delete-cancelled"
                  >
                    취소 내역 삭제
                  </button>
                </div>
              </div>
            </div>
          </div>
            <div v-else class="empty-state">
              <p>아직 예약 내역이 없습니다. 새로운 쉼을 찾아 떠나보세요! ✨</p>
            </div>
          </div>

          <div v-else-if="activeTab === 'reviews'" class="tab-pane">
            <div class="section-header">
              <h4>내가 쓴 후기</h4>
              <span class="header-line"></span>
            </div>
            <div v-if="myReviews.length > 0" class="review-list">
                <div v-for="review in myReviews" :key="review.reviewId" class="review-card">
                    <div class="review-card-header">
                        <span class="review-hotel-name">{{ review.hotelName }}</span>
                        <span class="review-date">{{ formatDate(review.createdAt) }}</span>
                    </div>
                    <div class="review-rating">
                        <span v-for="i in review.score" :key="i" class="star">★</span>
                    </div>
                    <p class="review-content">{{ review.content }}</p>
                </div>
            </div>
            <div v-else class="empty-state">
              <p>아직 작성한 후기가 없습니다. 📝</p>
            </div>
          </div>

          <div v-else-if="activeTab === 'coupons'" class="tab-pane">
            <div class="section-header">
                <h4>쿠폰함</h4>
                <span class="header-line"></span>
            </div>
            <div class="empty-state">
                <p>현재 사용 가능한 쿠폰이 없습니다. 💸</p>
            </div>
          </div>

          <div v-else-if="activeTab === 'profile'" class="tab-pane">
            <div class="section-header">
                <h4>회원정보 수정</h4>
                <span class="header-line"></span>
            </div>
            <form @submit.prevent class="profile-form">
                <div class="form-group">
                    <label for="name">이름</label>
                    <div class="input-with-button">
                        <input type="text" id="name" v-model="editInfo.name" required>
                    </div>
                </div>
                <div class="form-group">
                    <label for="phone">전화번호</label>
                    <div class="input-with-button">
                        <input type="tel" id="phone" v-model="editInfo.phone">
                        <button type="button" class="btn-update-field">전화번호 저장</button>
                    </div>
                </div>

                <div class="password-change-section">
                    <h5 class="password-section-title">비밀번호 변경</h5>
                    <div class="form-group">
                        <label for="old-password">기존 비밀번호</label>
                        <div class="input-with-button">
                            <input type="password" id="old-password" v-model="oldPassword" class="form-input">
                            <button type="button" class="btn-update-field" :disabled="!oldPassword">비밀번호 확인</button>
                        </div>
                    </div>
                </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-if="isReviewModalOpen" class="modal-overlay" @click.self="closeReviewModal">
    <div class="modal-content">
        <h4 class="modal-title">후기 작성</h4>
        <div class="modal-hotel-info">
            <strong>{{ selectedReservationForReview.placeName }}</strong>
            <p>{{ selectedReservationForReview.roomType }}</p>
        </div>
        <div class="review-form">
            <div class="form-group">
                <label>별점</label>
                <div class="star-rating">
                    <span v-for="star in 5" :key="star" @click="reviewData.score = star" :class="{ 'filled': star <= reviewData.score }">★</span>
                </div>
            </div>
            <div class="form-group">
                <label>후기 내용</label>
                <textarea v-model="reviewData.content" rows="5" placeholder="숙소에서의 경험을 공유해주세요."></textarea>
            </div>
            <div class="modal-actions">
                <button @click="closeReviewModal" class="btn-cancel">취소</button>
                <button @click="submitReview" class="btn-submit">등록하기</button>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import { useBookingStore } from '@/stores/booking';

const activeTab = ref('reservations');
const bookingStore = useBookingStore();
const hotel = computed(() => bookingStore.hotel);


const userInfo = reactive({
  name: '',
  email: '',
});
const editInfo = reactive({
  name: '',
  phone: '',
});
const reservations = ref([]);
const coupons = ref([]);
const oldPassword = ref('');
const newPassword = ref('');
const confirmPassword = ref('');
const isOldPasswordVerified = ref(false);
const route = useRoute();
const router = useRouter();

const completedReservations = computed(() =>
  (reservations.value || []).filter(r => r.status === '예약 완료')
);

const cancelledReservations = computed(() =>
  (reservations.value || []).filter(r => r.status === '예약 취소')
);

const myReviews = ref([]);
const isReviewModalOpen = ref(false);
const selectedReservationForReview = ref(null);
const reviewData = reactive({
    reservationId: null,
    score: 0,
    content: ''
});

onMounted(() => {
  fetchUserData();
  fetchReservations();
  fetchMyReviews();
});

const fetchUserData = async () => {
    try {
      const response = await axios.get('http://localhost:8888/api/users/info', {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
      });
      userInfo.name = response.data.name;
      userInfo.email = response.data.email;
      editInfo.name = response.data.name;
      editInfo.phone = response.data.phone;
    } catch (error) {
      console.error('사용자 정보 가져오기 실패:', error);
    }
};

const fetchReservations = async () => {
    try {
      const response = await axios.get('http://localhost:8888/mypage/reservations', {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
      });

      console.log('✅ [1단계] API 원본 응답:', response.data);

      reservations.value = response.data.map(item => {

      const finalImageSrc = item.hotelImage || `http://localhost:8888/images/${item.hotelType || 'default'}/${item.hotelId || '0'}.jpg`;
      
      // 🕵️‍♂️ 2. 각 예약마다 최종적으로 어떤 이미지 URL이 할당되었는지 확인
      console.log(`✅ [2단계] 예약 ID ${item.reservationId}의 최종 이미지 URL:`, finalImageSrc);
        
      return {
      reservationId: item.reservationId,
        placeName: item.hotelName,
        image: item.hotelImage || `http://localhost:8888/images/${item.hotelType || 'default'}/${item.hotelId || '0'}.jpg`,
        guests: item.people || item.guestCount,
        checkIn: item.checkIn,
        checkOut: item.checkOut,
        price: item.price,
        status: item.status,
        roomType: item.roomType,
        address: item.address,
        hotelType: item.hotelType || bookingStore.hotel?.type || 'default',
        hotelId: item.hotelId || bookingStore.hotel?.hId || '0'
      }
    });
    } catch (error) {
      console.error('예약 내역 가져오기 실패:', error);

      if (route.query.reservationId) {
        reservations.value.push({
          reservationId: route.query.reservationId,
          placeName: route.query.hotelName,
          image: `http://localhost:8888/images/${route.query.hotelType || 'default'}/${route.query.hotelId || '0'}.jpg`,
          guests: route.query.people,
          checkIn: route.query.checkIn,
          checkOut: route.query.checkOut,
          price: route.query.price,
          status: route.query.status,
          roomType: route.query.roomType,
          address: route.query.address,
          hotelType: route.query.hotelType || bookingStore.hotel?.type || 'default',
          hotelId: route.query.hotelId || bookingStore.hotel?.hId || '0'
        })
      }
    }
};

const fetchMyReviews = async () => {
    try {
        const response = await axios.get('http://localhost:8888/api/reviews/my-reviews', {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
        });
        myReviews.value = response.data;
    } catch (error) {
        console.error('내가 쓴 후기 목록 가져오기 실패:', error);
    }
};

const openReviewModal = (reservation) => {
    selectedReservationForReview.value = reservation;
    reviewData.reservationId = reservation.reservationId;
    reviewData.score = 0;
    reviewData.content = '';
    isReviewModalOpen.value = true;
};

const closeReviewModal = () => {
    isReviewModalOpen.value = false;
};

const submitReview = async () => {
    if (reviewData.score === 0) {
        alert('별점을 선택해주세요.');
        return;
    }
    if (!reviewData.content.trim()) {
        alert('후기 내용을 입력해주세요.');
        return;
    }

    try {
        await axios.post('http://localhost:8888/api/reviews', {
            reservationId: reviewData.reservationId,
            score: reviewData.score,
            content: reviewData.content
        }, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
        });
        alert('후기가 성공적으로 등록되었습니다.');
        closeReviewModal();
        fetchReservations();
        fetchMyReviews();
    } catch (error) {
        console.error('리뷰 제출 실패:', error);
        alert(error.response?.data?.message || '후기 등록에 실패했습니다.');
    }
};

const isReviewable = (reservation) => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const checkoutDate = new Date(reservation.checkOut);
    return reservation.status === '예약 완료' && checkoutDate < today;
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR');
};

const changeTab = (tabName) => {
  activeTab.value = tabName;
  if (tabName === 'reviews') {
      fetchMyReviews();
  }
};

const statusToClass = (status) => {
    if (!status) return '';
    return status.replace(/\s+/g, '-').toLowerCase();
}

const logout = () => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  router.push('/');
  alert('로그아웃 되었습니다.');
};

const cancelReservation = async (reservationId) => {
    const confirmed = confirm('예약을 취소하시겠습니까?');
    if (!confirmed) return;

    try {
    // DELETE가 아니라 PUT으로 상태 변경 API 호출
      await axios.put(`http://localhost:8888/api/reservations/${reservationId}/cancel`, {}, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        }
      });

      alert('예약이 취소되었습니다.');

      // UI에서도 status만 변경
      const res = reservations.value.find(r => r.reservationId === reservationId);
      if (res) {
        res.status = 'cancelled';
        res.statusText = '예약 취소';
      }

    } catch (error) {
      console.error('예약 취소 실패:', error);
      alert(error.response?.data?.message || '예약 취소에 실패했습니다.');
    }
  };

  // 취소 내역 삭제
const deleteCancelledReservation = async (reservationId) => {
  const confirmed = confirm('정말 이 취소된 예약 내역을 삭제하시겠습니까?');
  if (!confirmed) return;

  try {
    await axios.delete(`http://localhost:8888/api/reservations/${reservationId}/delete`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
      }
    });

    // UI에서 삭제
    reservations.value = reservations.value.filter(r => r.reservationId !== reservationId);
    alert('취소 내역이 삭제되었습니다.');
  } catch (error) {
    console.error('취소 내역 삭제 실패:', error);
    alert(error.response?.data?.message || '취소 내역 삭제에 실패했습니다.');
  }
};


</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nanum+Myeongjo:wght@400;700;800&family=Noto+Sans+KR:wght@300;400;500;700&display=swap');

.mypage-container {
  min-height: 100vh;
  background-color: #F8F4EF;
  padding: 60px 0;
  font-family: 'Noto Sans KR', sans-serif;
  color: #333;
}

.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 40px;
  padding: 0 20px;
}

.user-profile {
  position: sticky;
  top: 40px;
  height: fit-content;
  background-color: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(8px);
  border: 1px solid #E0E0E0;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
}

.profile-info {
  margin-top: 20px;
}
.profile-info h3 {
  font-family: 'Nanum Myeongjo', serif;
  font-weight: 700;
  font-size: 1.5rem;
  margin-bottom: 5px;
}
.profile-info p {
  font-size: 0.9rem;
  color: #666;
  margin: 0;
}
.profile-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  justify-content: center;
}
.profile-actions button {
  background: none;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 8px 15px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}
.profile-actions button:hover {
  background-color: #EFEFEF;
}
.profile-actions button:last-child {
  background-color: #333;
  color: #fff;
  border-color: #333;
}
.profile-actions button:last-child:hover {
  background-color: #000;
}

.mypage-content {
  background-color: #fff;
  border: 1px solid #E0E0E0;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
}

.mypage-menu {
  display: flex;
  justify-content: space-around;
  margin-bottom: 40px;
  border-bottom: 2px solid #EEE;
  gap: 10px;
}
.mypage-menu button {
  flex: 1;
  padding: 15px 20px;
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  font-size: 1.1rem;
  font-weight: 600;
  color: #888;
  cursor: pointer;
  transition: all 0.3s;
}
.mypage-menu button.active {
  color: #333;
  border-bottom-color: #333;
}
.mypage-menu button:hover {
  color: #333;
}

.section-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: 30px;
}
.section-header h4 {
  font-family: 'Nanum Myeongjo', serif;
  font-weight: 800;
  font-size: 1.6rem;
  margin: 0;
}
.section-header .header-line {
  width: 100%;
  height: 2px;
  background: #333;
  margin-top: 5px;
  position: relative;
}
.section-header .header-line::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 40px;
  height: 2px;
  background: #E53935;
}

.reservation-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
}
.reservation-card {
    display: grid;
    grid-template-columns: 200px 1fr auto;
    gap: 20px;
    align-items: flex-start;
    border: 1px solid #E0E0E0;
    border-radius: 12px;
    padding: 20px;
    transition: box-shadow 0.2s;
    background-color: #fff;
}
.reservation-card:hover {
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
.reservation-card.disabled {
  opacity: 0.5;
  /* pointer-events: none; /* 클릭 불가 */ 
}
.card-image {
    width: 200px;
    height: 140px;
    border-radius: 8px;
    overflow: hidden;
    flex-shrink: 0;
}
.card-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}
.card-info {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
}
.place-name {
    font-size: 1.3rem;
    font-weight: 700;
    margin: 0 0 10px 0;
}
.reservation-details {
    font-size: 0.95rem;
    color: #555;
    line-height: 1.6;
}
.reservation-details span {
    display: block;
    margin-bottom: 4px;
}
.reservation-actions {
    margin-left: auto;
    text-align: right;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 8px;
}
.status-badge {
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 0.85rem;
    font-weight: 600;
    display: inline-block;
}
.status-badge.예약-완료 {
    background-color: #E8F5E9;
    color: #388E3C;
}
.status-badge.reviewed, .status-badge.리뷰-작성-완료 {
    background-color: #e0e0e0;
    color: #757575;
    font-weight: 500;
}
.dates {
    font-size: 0.9rem;
    color: #333;
    font-weight: 500;
}
.price-people {
    font-size: 0.95rem;
    font-weight: 500;
}
.price-people span {
    display: block;
}
.btn-review-write {
    background-color: #4A69A1;
    color: #fff;
    border: none;
    padding: 8px 15px;
    border-radius: 8px;
    cursor: pointer;
    font-size: 0.9rem;
    font-weight: 600;
}
.btn-review-write:hover {
    background-color: #3A5280;
}

.empty-state {
    text-align: center;
    padding: 50px 20px;
    color: #999;
    font-size: 1rem;
    background-color: #F8F4EF;
    border-radius: 12px;
}

/* 내가 쓴 후기 */
.review-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
}
.review-card {
    border: 1px solid #E0E0E0;
    border-radius: 12px;
    padding: 20px;
}
.review-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}
.review-hotel-name {
    font-weight: 700;
    font-size: 1.1rem;
}
.review-date {
    font-size: 0.9rem;
    color: #888;
}
.review-rating {
    margin-bottom: 10px;
    color: #FFC107;
}
.review-rating .star {
    font-size: 1.2rem;
}
.review-content {
    color: #555;
    line-height: 1.6;
}

/* Modal Styles */
.modal-overlay {
    position: fixed;
    top: 0; left: 0;
    width: 100%; height: 100%;
    background-color: rgba(0,0,0,0.6);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}
.modal-content {
    background-color: #fff;
    padding: 30px;
    border-radius: 12px;
    width: 90%;
    max-width: 500px;
}
.modal-title {
    font-size: 1.5rem;
    font-weight: 700;
    margin: 0 0 10px 0;
}
.modal-hotel-info {
    background-color: #f5f5f5;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 20px;
}
.review-form .form-group {
    margin-bottom: 15px;
}
.review-form label {
    display: block;
    font-weight: 500;
    margin-bottom: 8px;
}
.star-rating {
    font-size: 2rem;
    color: #ddd;
    cursor: pointer;
}
.star-rating .filled {
    color: #FFC107;
}
.review-form textarea {
    width: 100%;
    box-sizing: border-box;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 8px;
    resize: vertical;
}
.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 20px;
}
.btn-cancel, .btn-submit {
    padding: 10px 20px;
    border: none;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
}
.btn-cancel {
    background-color: #f0f0f0;
}
.btn-submit {
    background-color: #4A69A1;
    color: white;
}
.profile-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 500px;
  margin-top: 20px;
}
.form-group {
  text-align: left;
}
.form-group label {
  display: block;
  font-weight: 500;
  margin-bottom: 8px;
  color: #555;
}
.form-input {
    width: 100%;
    padding: 12px 15px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
}
.input-with-button {
    display: flex;
    gap: 10px;
    align-items: center;
}
.input-with-button input {
    flex-grow: 1; 
    padding: 12px 15px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
}
.btn-update-field {
    padding: 12px 15px;
    background-color: #4A69A1;
    color: #fff;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    white-space: nowrap;
    font-weight: 600;
}
.password-change-section {
    margin-top: 20px;
    border-top: 1px solid #eee;
    padding-top: 20px;
}
.password-section-title {
    font-size: 1.2rem;
    font-weight: 700;
    margin-bottom: 15px;
}

.btn-cancel-reservation {
  background-color: #ff4141; /* 회색 */
  color: #fff;
  border: none;
  padding: 8px 15px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 600;
  margin-top: 8px;
}

.btn-delete-cancelled {
  background-color: #888; /* 회색 */
  color: #fff;
  border: none;
  padding: 8px 15px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 600;
  margin-top: 8px;
}

.btn-delete-cancelled:hover {
  background-color: #555;
}
</style>