<template>
  <div v-if="hotel" class="hotel-detail-page">
    <section class="detail-header">
      <div class="image-gallery">
        <!-- Placeholder Images - real images will be fetched and displayed here -->
        <div class="main-image">
          <img src="https://placehold.co/1200x800?text=Hotel+Image+1" :alt="hotel.hname">
        </div>
     <!-- 메인 이미지 -->
<div class="main-image">
  <img :src="`http://localhost:8888/images/${hotel.type}/${hotel.hId}.jpg`" :alt="hotel.hname">
</div>

<!-- 서브 이미지 -->
<div class="sub-images">
  <img 
    v-for="index in 3" 
    :key="index" 
    :src="`http://localhost:8888/images/${hotel.type}/${hotel.hId}_${index}.jpg`" 
    :alt="hotel.hname + ' 이미지 ' + index"
  >
  <div class="more-images-overlay">
    <button>+ 0</button>
  </div>
</div>

      </div>
      <div class="header-content-wrapper">
        <div class="header-content">
          <div class="info-main">
            <p class="hotel-info-line">
              <span class="hotel-type-display">{{ hotel.type }}</span>
              <span class="elidia-level">
                <span v-for="i in hotel.star" :key="i" class="level-star">★</span>
              </span>
            </p>
            <h1>{{ hotel.hname }}</h1>
          </div>
          <div class="info-price">
            <strong class="price-value">최저가 {{ hotel.minPrice.toLocaleString() }}원</strong>
            <button class="like-button">♡</button>
          </div>
        </div>
      </div>
    </section>

    <div :class="['sticky-nav-bar', { 'is-visible': isStickyNavVisible }]" ref="stickyNavBarRef">
      <div class="sticky-nav-content">
        <nav class="sticky-nav-links">
          <a href="#rooms" @click.prevent="scrollToSection('rooms')">객실</a>
          <a href="#amenities" @click.prevent="scrollToSection('amenities')">시설</a>
          <a href="#amenities" @click.prevent="scrollToSection('amenities')">서비스 및 부대시설</a>
          <a href="#map-section" @click.prevent="scrollToSection('map-section')">위치</a>
          <a href="#reviews-section" @click.prevent="scrollToSection('reviews-section')">리뷰</a>
        </nav>
        <div class="sticky-nav-booking">
          <span class="sticky-nav-price">{{ hotel.minPrice.toLocaleString() }}원</span>
          <button class="sticky-nav-button">예약하기</button>
        </div>
      </div>
    </div>

    <main class="detail-main-content">
      <div class="content-column">
        <div class="summary-cards-wrapper">
          <div class="summary-card rating-review-card">
            <h3 class="card-title">
              <div class="rating-badge-wrapper">
                <span class="score-badge-yellow">{{ hotel.avgScore.toFixed(1) }}</span>
                <span class="rating-text-yellow">{{ getRatingText(hotel.avgScore) }}</span>
              </div>
              <span class="review-count">({{ hotel.reviewCount.toLocaleString() }}명 평가)</span> 
              <span class="arrow-icon">›</span>
            </h3>

            <!-- 최고 평점 리뷰 -->
            <p class="review-quote-text" v-if="topRatedReview">
              "{{ topRatedReview.content }}"
            </p>
            <p v-else class="review-quote-text">
              "아직 리뷰가 없어요! 숙박하시고 리뷰를 작성해주세요!"
            </p>
          </div>

          <div class="summary-card amenities-preview-card">
            <h3 class="card-title">서비스 및 부대시설 <span class="arrow-icon">›</span></h3>
            <div class="amenities-preview-list">
              <span v-for="(service, index) in hotel.services" :key="index">
                ✔️ {{ service.serviceName }}
              </span>
            </div>
          </div>

          <div class="summary-card location-preview-card">
            <h3 class="card-title">위치 정보 <span class="arrow-icon">›</span></h3>
            <div class="location-preview-content">
              <span>📍 {{ hotel.address }}</span>
              <a href="#" class="btn-map">지도보기</a>
            </div>
          </div>
        </div>

        <section id="rooms" class="detail-section">
          <h2>객실 선택</h2>
          <div class="room-list">
            <div v-for="room in hotel.rooms" :key="room.rId" class="room-card">
              <div class="room-image">
                <img src="https://placehold.co/200x150?text=Room+Image" :alt="room.type">
              </div>
              <div class="room-info">
                <h4>{{ room.type }}</h4>
                <p class="room-spec">
                  <span>🛏️ 최대 {{ room.people }}명</span>
                  <span>✅ 재고: {{ room.count }}개</span>
                  <span>⏰ 체크인: {{ room.checkinTime }}</span>
                  <span>⏰ 체크아웃: {{ room.checkoutTime }}</span>
                </p>
                <a href="#" class="btn-more-info">상세 정보 ></a>
              </div>
              <div class="room-booking">
                <strong class="room-price">{{ room.price.toLocaleString() }}원</strong>
                <span>세금 및 봉사료 포함</span>
                <button class="btn-book" @click="goToCheckout(room)">예약하기</button>
              </div>
            </div>
          </div>
        </section>

        <section id="amenities" class="detail-section">
          <h2>서비스 및 부대시설</h2>
          <div class="amenities-grid">
            <span v-for="(service, index) in hotel.services" :key="index">✔️ {{ service.serviceName }}</span>
          </div>
        </section>

        <section id="hotel-info" class="detail-section">
          <h2>숙소 이용 정보</h2>
          <ul>
            <li><strong>체크인 : {{ hotel.checkinTime }} | 체크아웃 : {{ hotel.checkoutTime }}</strong></li>
            <li>정확한 체크인/체크아웃 시간은 숙소에 문의해주세요.</li>
          </ul>
        </section>

        <section id="map-section" class="detail-section map-section">
          <h2>위치</h2>
          <div class="map-placeholder">
            <div id="kakao" :style="{width: '100%', height: '400px'}"></div>
            <!-- <img src="https://via.placeholder.com/800x400?text=Hotel+Location+Map" alt="호텔 위치 지도"> -->
          </div>
        </section>

        <section id="nearby-attractions" class="detail-section nearby-attractions">
          <h2>근처 문화재 및 랜드마크</h2>
          <div class="attraction-list">
            <div class="attraction-item">
              <img src="https://via.placeholder.com/100x100?text=Attraction1" alt="문화재 이미지">
              <div class="attraction-info">
                <strong>경복궁</strong>
                <p>차량 10분</p>
              </div>
            </div>
            <div class="attraction-item">
              <img src="https://via.placeholder.com/100x100?text=Attraction2" alt="문화재 이미지">
              <div class="attraction-info">
                <strong>남산타워</strong>
                <p>차량 15분</p>
              </div>
            </div>
            <div class="attraction-item">
              <img src="https://via.placeholder.com/100x100?text=Attraction3" alt="문화재 이미지">
              <div class="attraction-info">
                <strong>명동</strong>
                <p>도보 5분</p>
              </div>
            </div>
          </div>
        </section>

        <section id="reviews-section" class="detail-section reviews-section">
          <h2>
            리얼 리뷰 
            <div class="rating-badge-wrapper">
              <span class="score-badge-yellow">{{ hotel.avgScore.toFixed(1) }}</span>
              <span class="review-count">({{ hotel.reviewCount.toLocaleString() }}개 평가)</span>
            </div>
            <a href="#" class="sort-reviews">▾ 정렬 및 필터</a>
          </h2>

          <div class="review-list">
            <div v-for="review in hotel.reviews" :key="review.reviewId" class="review-card">
              <div class="review-header">
                <img src="https://placehold.co/40x40?text=U" alt="프로필" class="user-profile-img">
                <div class="user-info">
                  <strong class="user-nickname">사용자{{ review.pId }}</strong>
                  <span class="review-date">2023.10.26</span> <!-- 실제 날짜가 있으면 review.date 사용 -->
                </div>
                <div class="review-rating">
                  <span v-for="i in review.score" :key="i">★</span>
                </div>
              </div>

              <p class="review-text">{{ review.content }}</p>

              <div v-if="review.image" class="review-images">
                <img :src="review.image" alt="리뷰 이미지">
              </div>
            </div>
          </div>
        </section>

      </div>

      <div class="sidebar-column">
        <div class="sticky-sidebar">
          <div class="coupon-box">
            <h4><strong>회원가입 10%</strong> 할인 쿠폰</h4>
            <button class="btn-download">⬇️ 쿠폰 받기</button>
            <a href="#" class="coupon-dropdown">적용 가능한 쿠폰 혜택 ⌄</a>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick, computed, toRaw } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useBookingStore } from '@/stores/booking'
import { useUserStore } from '@/stores/user'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const bookingStore = useBookingStore()
const userStore = useUserStore()

const isStickyNavVisible = ref(false);
const stickyNavBarRef = ref(null);

const search = bookingStore.search;
const hotel = ref(bookingStore.hotel);
const room = bookingStore.room;

const checkIn = ref(null);
const checkOut = ref(null);
const rooms = ref(1);
const persons = ref(2);

const uId = userStore.user?.id || null;
const hId = ref(route.params.hId);

const getRatingText = (rating) => {
  if (rating >= 4.5) return '최고에요';
  if (rating >= 4.0) return '아주 좋아요';
  if (rating >= 3.0) return '괜찮아요';
  return '보통이에요';
};

const handleScroll = () => {
  const header = document.querySelector('.header-content-wrapper');
  if (!header) return;
  isStickyNavVisible.value = window.scrollY > header.offsetHeight;
};

const scrollToSection = (id) => {
  const element = document.getElementById(id);
  if (element) {
    const offset = stickyNavBarRef.value ? stickyNavBarRef.value.offsetHeight : 0;
    window.scrollTo({
      top: element.offsetTop - offset - 20,
      behavior: 'smooth',
    });
  }
};

const loadDetailQueryFromUrl = () => {
  const query = route.query;
  checkIn.value = query.startDate ? new Date(query.startDate) : null;
  checkOut.value = query.endDate ? new Date(query.endDate) : null;
  rooms.value = Number(query.rooms) || 1;
  persons.value = Number(query.persons) || 2;
  hId.value = route.params.id || null; // 여기서 가져와야 함
  console.log('Loaded hId from route.params:', hId.value); // 확인용
};

const sendDetailSearchRequest = async () => {
  // hId 값이 유효하지 않으면 요청을 보내지 않습니다.
  if (!hId.value || isNaN(Number(hId.value))) {
    console.error("Hotel ID is not valid. Cancelling API request.");
    return; // 함수를 즉시 종료
  }
  
  // 이 시점에서 hId는 항상 유효한 값(2)입니다.
  const requestBody = {
    hId: Number(hId.value),
    startDate: checkIn.value ? checkIn.value.toISOString().split('T')[0] : null,
    endDate: checkOut.value ? checkOut.value.toISOString().split('T')[0] : null,
    numberOfRooms: Number(rooms.value),
    numberOfPeople: Number(persons.value),
  };

  console.log('sending hId:', requestBody.hId);

  try {
    const response = await fetch('http://localhost:8888/api/detail', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestBody),
    });

    if (response.ok) {
      // 서버 응답이 성공(200 OK)일 경우
      hotel.value = await response.json(); // 응답 본문을 JSON으로 파싱하여 hotel 변수에 저장
      console.log('데이터 로드 성공:', hotel.value);
    } else {
      console.error('디테일 검색 실패:', response.status);
      // 에러 처리 로직
    }
  } catch (error) {
    console.error('API 호출 중 예외 발생:', error);
  }
};

// 스크롤 이벤트 등록
onMounted(() => {
  window.addEventListener('scroll', handleScroll);
});

// 스크롤 이벤트 해제
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

const loadKakaoMap = () => {
  return new Promise((resolve, reject) => {
    if (window.kakao && window.kakao.maps) {
      resolve(window.kakao);
      return;
    }

    const script = document.createElement('script');
    script.src = "https://dapi.kakao.com/v2/maps/sdk.js?appkey=e77831e9ccd11f157f3055f8800d5602&autoload=false";
    script.onload = () => {
      if (window.kakao && window.kakao.maps) {
        resolve(window.kakao);
      } else {
        reject(new Error("Kakao Maps SDK 로드 실패"));
      }
    };
    script.onerror = () => reject(new Error("Kakao Maps SDK 스크립트 로드 실패"));
    document.head.appendChild(script);
  });
};

const initMap = async (hname) => {
  try {
    const kakao = await loadKakaoMap();
    if (!kakao || !kakao.maps) {
      console.error("Kakao Maps SDK가 정의되지 않았습니다.");
      return;
    }

    kakao.maps.load(() => {
      const container = document.getElementById('kakao');
      if (!container) return;

      const options = {
        center: new kakao.maps.LatLng(33.450701, 126.570667),
        level: 3,
      };
      const map = new kakao.maps.Map(container, options);

      const markerPosition = new kakao.maps.LatLng(33.450701, 126.570667); // 마커 위치
      const marker = new kakao.maps.Marker({
        position: markerPosition,
      });

      // 마커를 지도에 표시
      marker.setMap(map);

      // (선택) 마커 클릭 시 정보창 표시
      const infowindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:5px;">${hname}</div>`,
      });

      kakao.maps.event.addListener(marker, 'click', function() {
        infowindow.open(map, marker);
      });
    });
  } catch (e) {
    console.error("Kakao Map 로드 실패:", e);
  }
};

const topRatedReview = computed(() => {
  if (!hotel.value || !hotel.value.reviews || hotel.value.reviews.length === 0) return null;
  // score가 가장 높은 리뷰 찾기
  return hotel.value.reviews.reduce((max, review) => {
    return review.score > (max?.score || 0) ? review : max;
  }, null);
});

// 호텔 데이터가 세팅되면 지도 초기화
watch(hotel, async (newVal) => {
  if (newVal) {
    await nextTick();
    initMap(newVal.hname);
  }
});

// 예약하기 버튼 클릭 시 실행
const goToCheckout = async (room) => {
  try {
    bookingStore.setBooking(search, toRaw(hotel.value), room);

    const formatDate = (date) => {
      const d = new Date(date);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };
    

    // 1. [예약 생성 요청]을 위한 데이터 준비
    const reservationData = {
      rId: room.rid,
      uId: userStore.user?.id || null,
      checkin: formatDate(checkIn.value),
      checkout: formatDate(checkOut.value),
      people: persons.value,
      price: room.price * rooms.value,
    };

    console.log('예약 데이터:', reservationData);

    // 1. 예약 생성 요청
    const reservationResponse = await axios.post('http://localhost:8888/api/reservations', reservationData, {
      headers: {
        // 로그인된 사용자의 경우 토큰을 함께 보냅니다.
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`, // 'accessToken' 대신 'jwtToken' 사용
      },
    });

    const reId = reservationResponse.data.reId;
    if (!reId) {
        throw new Error("서버로부터 예약 ID를 받지 못했습니다.");
    }

    console.log('생성된 예약 ID:', reId);

    // 3. Pinia 스토어에 예약 ID를 저장합니다.
    // 이렇게 하면 다음 페이지(/checkout)에서 이 ID를 사용할 수 있습니다.
    bookingStore.setReservationId(reId);

    // 결제 후 체크아웃 페이지로 이동
    router.push('/checkout');
 

  } catch (error) {
    console.error('예약 생성 중 오류 발생:', error);
    if (error.response) {
      console.error('서버 응답 데이터:', error.response.data);
      alert(`예약 생성에 실패했습니다: ${error.response.data.message || '서버 오류'}`);
    } else {
      alert('예약 생성에 실패했습니다. 네트워크 연결을 확인해주세요.');
    }
  }
};

watch(
  () => route.query,
  () => {
    loadDetailQueryFromUrl();
    sendDetailSearchRequest();
  },
  { immediate: true, deep: true }
);


</script>

<style>
  /* --- 1. 상단 헤더 및 이미지 갤러리 --- */
  .detail-header {
    width: 100%;
    padding-top: 20px;
    background-color: #fff;
  }
  .image-gallery { max-width: 1200px; margin: 0 auto; display: grid; grid-template-columns: 2fr 1fr; grid-template-rows: 250px 250px; gap: 10px; height: 510px; }
  .main-image { grid-column: 1 / 2; grid-row: 1 / 3; }
  .main-image img, .sub-images img { width: 100%; height: 100%; object-fit: cover; border-radius: 4px; }
  .main-image { border-radius: 8px; overflow: hidden; }
  .sub-images { grid-column: 2 / 3; grid-row: 1 / 3; display: grid; grid-template-columns: 1fr 1fr; grid-template-rows: 1fr 1fr; gap: 10px; position: relative; }
  .more-images-overlay { position: absolute; bottom: 10px; right: 10px; }
  .more-images-overlay button { background-color: rgba(0,0,0,0.7); color: white; border: none; border-radius: 8px; padding: 10px 15px; font-weight: 500; cursor: pointer; }
  .header-content-wrapper { border-bottom: 10px solid #F9F9F9; }
  .header-content { max-width: 1200px; margin: 0 auto; padding: 25px 20px; display: flex; justify-content: space-between; align-items: center; }
  
  .info-main {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }
  .hotel-info-line {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
    font-size: 1rem;
    color: #555;
    font-weight: 500;
  }
  .hotel-type-display { margin-right: 10px; }
  /* [수정] 엘리디아 레벨 별 디자인 */
  .elidia-level { display: flex; align-items: center; gap: 4px; color: #E53935; } /* 다홍색 별 */
  .level-star { font-size: 1.2rem; }
  
  h1 { font-size: 2.2rem; font-weight: 800; margin: 0; line-height: 1.2; color: #222; }
  .hotel-english-name { font-size: 1.1rem; color: #666; margin-top: 5px; font-weight: 400; }
  
  .info-price { display: flex; align-items: center; gap: 15px; }
  .info-price .price-value { font-size: 2rem; font-weight: 800; display: block; color: #222; }
  .info-price .like-button { background: none; border: 1px solid #ddd; border-radius: 50%; width: 48px; height: 48px; font-size: 1.8rem; cursor: pointer; color: #555; display: flex; align-items: center; justify-content: center; transition: all 0.2s; }
  .info-price .like-button:hover { background-color: #f0f0f0; color: #E53935; }
  
  /* --- 2. 스티키 네비게이션 바 --- */
  .sticky-nav-bar {
    position: fixed;
    top: 0; /* [수정] 원래 헤더 바로 아래가 아닌, 화면 상단에 고정 */
    left: 0;
    width: 100%;
    background-color: #fff;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    z-index: 1000;
    transform: translateY(-100%);
    transition: transform 0.3s ease-out;
  }
  .sticky-nav-bar.is-visible {
    transform: translateY(0);
  }
  .sticky-nav-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 15px 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .sticky-nav-links {
    display: flex;
    gap: 25px;
  }
  .sticky-nav-links a {
    text-decoration: none;
    color: #555;
    font-weight: 600;
    font-size: 1rem;
    padding: 5px 0;
    position: relative;
    transition: color 0.2s;
  }
  .sticky-nav-links a:hover {
    color: #007bff;
  }
  
  .sticky-nav-booking {
    display: flex;
    align-items: center;
    gap: 15px;
  }
  .sticky-nav-price {
    font-size: 1.5rem;
    font-weight: 800;
    color: #222;
  }
  .sticky-nav-button {
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 8px;
    padding: 10px 25px;
    font-size: 1rem;
    font-weight: 700;
    cursor: pointer;
    white-space: nowrap;
  }
  
  
  /* --- 3. 메인 콘텐츠 --- */
  .detail-main-content { max-width: 1200px; margin: 0 auto; padding: 20px; display: grid; grid-template-columns: 1fr 320px; gap: 30px; }
  .content-column { min-width: 0; }
  .sidebar-column { min-width: 0; }
  
  .detail-section { 
    background-color: #fff;
    padding: 30px;
    margin-top: 20px;
    border-radius: 12px;
  }
  .detail-section h2 { 
    font-size: 1.8rem; 
    font-weight: 700; 
    padding-bottom: 15px; 
    margin: 0 0 25px 0; 
    border-bottom: 1px solid #eee; 
    display: flex;
    align-items: center;
  }
  .detail-section h2 .rating-badge-wrapper {
    margin-left: 15px;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  
  /* [수정] summary-cards-wrapper와 개별 카드 스타일 */
  .summary-cards-wrapper {
    display: grid;
    grid-template-columns: 1.5fr 1fr 1fr; /* 후기/평점 카드를 더 넓게 */
    gap: 10px; /* 카드 간 간격 */
    margin-top: 20px;
    margin-bottom: 30px;
  }
  .summary-card {
    background-color: #fff;
    padding: 20px; /* 패딩 조정 */
    border-radius: 12px;
    border: 1px solid #eee;
    box-shadow: 0 2px 5px rgba(0,0,0,0.03);
    display: flex;
    flex-direction: column;
  }
  
  /* 카드 제목 스타일 */
  .card-title {
    font-size: 1.1rem;
    font-weight: 700;
    color: #222;
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .card-title .arrow-icon {
    font-size: 1.2rem;
    color: #888;
  }
  
  /* [수정] 평점, 후기 카드 - 이미지에 맞게 재구성 */
  .rating-review-card {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  .rating-review-card .card-title {
    margin-bottom: 0; /* 제목 자체에 이미 정보가 있으므로 아래 여백 줄임 */
  }
  .rating-badge-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .score-badge-yellow {
    background-color: #F7C44E; /* 노란색 배경 */
    color: #fff; /* 흰색 글씨 */
    padding: 6px 10px;
    border-radius: 6px;
    font-weight: 700;
    font-size: 1.1rem;
  }
  .rating-text-yellow {
    font-size: 1.1rem;
    font-weight: 600;
    color: #E8A800; /* 노란색 계열 글씨 */
  }
  .rating-review-card .review-count {
    font-size: 0.95rem;
    color: #666;
    font-weight: 500;
    margin-left: 8px;
  }
  .review-quote-text { 
    border-left: 3px solid #eee; 
    padding-left: 15px; 
    font-size: 0.95rem; 
    color: #555; 
    line-height: 1.5; 
    margin-top: auto; /* 하단 정렬 */
    padding-top: 15px;
    flex-grow: 1; /* 남은 공간 차지 */
  }
  
  /* 서비스 및 위치 정보 카드 */
  .amenities-preview-list, .location-preview-content { 
    font-size: 0.95rem; 
    color: #555; 
    display: flex; 
    flex-direction: column; 
    gap: 10px; 
    flex-grow: 1; /* 남은 공간 차지 */
    justify-content: center; /* 세로 중앙 정렬 */
  }
  .location-preview-content { flex-direction: row; justify-content: space-between; align-items: center; }
  .btn-map { font-size: 0.9rem; color: #007bff; text-decoration: none; font-weight: 500; }
  
  /* 객실 선택 */
  .room-list { display: flex; flex-direction: column; gap: 15px; }
  .room-card { display: grid; grid-template-columns: 200px 1fr auto; gap: 20px; padding: 20px; border: 1px solid #eee; border-radius: 12px; transition: box-shadow 0.2s; }
  .room-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
  .room-image img { width: 100%; height: 100%; object-fit: cover; border-radius: 8px; }
  .room-info h4 { margin: 0 0 10px 0; font-size: 1.2rem; font-weight: 600; }
  .room-info .room-spec { display: flex; flex-direction: column; gap: 8px; font-size: 0.9rem; color: #333; }
  .btn-more-info { font-size: 0.9rem; color: #555; text-decoration: none; margin-top: 10px; display: inline-block; }
  .room-booking { text-align: right; display: flex; flex-direction: column; align-items: flex-end; justify-content: center; }
  .room-booking .room-price { font-size: 1.6rem; font-weight: 700; color: #222;}
  .room-booking span { font-size: 0.8rem; color: #888; margin-bottom: 10px; }
  .btn-book { background-color: #007bff; color: white; border: none; border-radius: 8px; padding: 12px 30px; font-size: 1rem; font-weight: 700; cursor: pointer; }
  
  /* 서비스 및 부대시설, 숙소 이용 정보 */
  .amenities-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; font-size: 0.95rem; }
  #hotel-info ul { list-style: none; padding: 0; }
  #hotel-info li { margin-bottom: 10px; font-size: 0.95rem; color: #555; }
  
  /* [추가] 지도 섹션 */
  .map-section .map-placeholder {
    width: 100%;
    height: 400px; /* 지도 높이 */
    background-color: #e0e0e0;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    overflow: hidden;
  }
  .map-section .map-placeholder img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  /* [추가] 근처 문화재, 랜드마크 섹션 */
  .nearby-attractions .attraction-list {
    display: flex;
    gap: 20px;
    overflow-x: auto; /* 가로 스크롤 가능 */
    padding-bottom: 10px; /* 스크롤바 공간 */
  }
  .nearby-attractions .attraction-item {
    flex-shrink: 0; /* 아이템이 줄어들지 않도록 */
    width: 180px; /* 아이템 너비 */
    background-color: #f9f9f9;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 2px 5px rgba(0,0,0,0.05);
    text-align: center;
    padding-bottom: 15px;
  }
  .nearby-attractions .attraction-item img {
    width: 100%;
    height: 120px;
    object-fit: cover;
    margin-bottom: 10px;
  }
  .nearby-attractions .attraction-info strong {
    display: block;
    font-size: 1rem;
    color: #222;
    margin-bottom: 5px;
  }
  .nearby-attractions .attraction-info p {
    font-size: 0.9rem;
    color: #777;
    margin: 0;
  }
  
  /* [추가] 리뷰 칸 섹션 (이미지 기반 디자인) */
  .reviews-section h2 {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #eee;
    padding-bottom: 15px;
    margin-bottom: 25px;
  }
  .reviews-section h2 .rating-badge-wrapper {
    margin-left: 0; /* 전체 제목의 일부로 간주 */
  }
  .reviews-section .sort-reviews {
    font-size: 0.95rem;
    color: #555;
    text-decoration: none;
    font-weight: 500;
  }
  .review-list {
    display: flex;
    flex-direction: column;
    gap: 30px;
  }
  .review-card {
    border-bottom: 1px solid #f0f0f0;
    padding-bottom: 25px;
  }
  .review-list .review-card:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
  .review-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 15px;
  }
  .user-profile-img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
  }
  .user-info {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 5px 10px;
  }
  .user-nickname {
    font-weight: 700;
    font-size: 1.05rem;
    color: #222;
  }
  .user-level, .user-visits, .review-date {
    font-size: 0.85rem;
    color: #777;
  }
  .review-rating {
    margin-left: auto;
    color: #FFD700; /* 노란색 별 */
    font-size: 1.1rem;
  }
  .review-images {
    display: flex;
    gap: 8px;
    margin: 15px 0;
    overflow-x: auto;
    padding-bottom: 5px; /* 스크롤바 공간 */
    position: relative;
  }
  .review-images img {
    width: 150px;
    height: 100px;
    object-fit: cover;
    border-radius: 8px;
    flex-shrink: 0;
  }
  .more-review-images-overlay {
    position: absolute;
    right: 0;
    bottom: 5px; /* 스크롤바 위 */
    background-color: rgba(0,0,0,0.6);
    color: #fff;
    padding: 5px 10px;
    border-radius: 0 0 8px 8px;
    font-size: 0.9rem;
    font-weight: 500;
    pointer-events: none; /* 클릭 방지 */
  }
  .review-room-info {
    font-size: 0.9rem;
    color: #555;
    margin-bottom: 10px;
  }
  .review-text {
    font-size: 0.95rem;
    color: #333;
    line-height: 1.6;
    margin-bottom: 10px;
  }
  .btn-more-review {
    background: none;
    border: none;
    color: #007bff;
    font-weight: 500;
    cursor: pointer;
    padding: 0;
    margin-bottom: 15px;
  }
  .review-actions {
    display: flex;
    gap: 10px;
  }
  .like-review-button, .report-review-button {
    background-color: #f0f0f0;
    border: none;
    border-radius: 20px;
    padding: 8px 15px;
    font-size: 0.9rem;
    color: #555;
    cursor: pointer;
  }
  
  
  /* 오른쪽 사이드바 */
  .sticky-sidebar { position: sticky; top: 100px; }
  .coupon-box { 
    background-color: #fff;
    border: 1px solid #eee; 
    border-radius: 12px; 
    padding: 25px; 
    text-align: center; 
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  }
  .coupon-box h4 { margin: 0 0 15px 0; font-size: 1.2rem; font-weight: 600; }
  .coupon-box h4 strong { color: #007bff; }
  .btn-download { background-color: #007bff; color: white; border: none; border-radius: 8px; padding: 12px; width: 100%; font-size: 1rem; font-weight: 700; cursor: pointer; margin-bottom: 10px; }
  .coupon-dropdown { color: #555; text-decoration: none; font-size: 0.9rem; }
  
  </style>