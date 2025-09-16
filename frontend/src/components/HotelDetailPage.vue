<template>
    <div v-if="hotel" class="hotel-detail-page">
      <section class="detail-header">
        <div class="image-gallery">
          <div class="main-image">
            <img :src="hotel.image" :alt="hotel.name">
          </div>
          <div class="sub-images">
            <img :src="hotel.image" :alt="hotel.name + ' 이미지 2'">
            <img :src="hotel.image" :alt="hotel.name + ' 이미지 3'">
            <img :src="hotel.image" :alt="hotel.name + ' 이미지 4'">
            <img :src="hotel.image" :alt="hotel.name + ' 이미지 5'">
            <div class="more-images-overlay">
              <button>+ {{ hotel.imageCount }}</button>
            </div>
          </div>
        </div>
        <div class="header-content-wrapper">
          <div class="header-content">
            <div class="info-main">
              <p class="hotel-info-line">
                <span class="hotel-type-display">{{ hotel.type }}</span>
                <span class="elidia-level">
                  <span v-for="i in hotel.elidiaLevel" :key="i" class="level-star">★</span>
                </span>
              </p>
              <h1>{{ hotel.name }}</h1>
              <p class="hotel-english-name">{{ hotel.englishName }}</p>
            </div>
            <div class="info-price">
              <strong class="price-value">{{ hotel.price.toLocaleString() }}원</strong>
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
            <span class="sticky-nav-price">{{ hotel.price.toLocaleString() }}원</span>
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
                  <span class="score-badge-yellow">{{ hotel.rating.toFixed(1) }}</span>
                  <span class="rating-text-yellow">{{ getRatingText(hotel.rating) }}</span>
                </div>
                <span class="review-count">({{ hotel.reviews.toLocaleString() }}명 평가)</span> 
                <span class="arrow-icon">›</span>
              </h3>
              <p class="review-quote-text">"{{ hotel.reviewSnippet }}"</p>
            </div>
  
            <div class="summary-card amenities-preview-card">
              <h3 class="card-title">서비스 및 부대시설 <span class="arrow-icon">›</span></h3>
              <div class="amenities-preview-list">
                <span>🅿️ 셀프 주차 (추가 비용 발생)</span>
                <span>📶 무료 Wi-Fi (모든 객실)</span>
                <span>🍴 조식 서비스</span>
              </div>
            </div>
  
            <div class="summary-card location-preview-card">
              <h3 class="card-title">위치 정보 <span class="arrow-icon">›</span></h3>
              <div class="location-preview-content">
                <span>📍 {{ hotel.locationDetail }}</span>
                <a href="#" class="btn-map">지도보기</a>
              </div>
            </div>
          </div>
  
          <section id="rooms" class="detail-section">
            <h2>객실 선택</h2>
            <div class="room-list">
              <div v-for="room in hotel.rooms" :key="room.id" class="room-card">
                <div class="room-image">
                  <img :src="room.image" :alt="room.name">
                </div>
                <div class="room-info">
                  <h4>{{ room.name }}</h4>
                  <p class="room-spec">
                    <span v-if="room.freeCancel">✔️ 무료취소 - {{ room.freeCancelUntil }} 까지</span>
                    <span v-if="room.breakfastIncluded">🍴 조식 포함</span>
                    <span>🛏️ {{ room.bedType }}</span>
                  </p>
                  <a href="#" class="btn-more-info">상세 정보 ></a>
                </div>
                <div class="room-booking">
                  <strong class="room-price">{{ room.price.toLocaleString() }}원</strong>
                  <span>세금 및 봉사료 포함</span>
                  <button class="btn-book">예약하기</button>
                </div>
              </div>
            </div>
          </section>
          
          <section id="amenities" class="detail-section">
            <h2>서비스 및 부대시설</h2>
            <div class="amenities-grid">
              <span v-for="amenity in hotel.fullAmenities" :key="amenity">✔️ {{ amenity }}</span>
            </div>
          </section>
  
          <section id="hotel-info" class="detail-section">
            <h2>숙소 이용 정보</h2>
            <ul>
              <li><strong>체크인 : 15:00 | 체크아웃 : 12:00</strong></li>
              <li>정확한 체크인/체크아웃 시간은 숙소에 문의해주세요.</li>
            </ul>
          </section>
  
          <section id="map-section" class="detail-section map-section">
            <h2>위치</h2>
            <div class="map-placeholder">
              <img src="https://via.placeholder.com/800x400?text=Hotel+Location+Map" alt="호텔 위치 지도">
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
            <h2>리얼 리뷰 
              <div class="rating-badge-wrapper">
                <span class="score-badge-yellow">{{ hotel.rating.toFixed(1) }}</span>
                <span class="review-count">({{ hotel.reviews.toLocaleString() }}개 평가)</span>
              </div>
              <a href="#" class="sort-reviews">▾ 정렬 및 필터</a>
            </h2>
            <div class="review-list">
              <div v-for="(review, index) in hotel.userReviews" :key="index" class="review-card">
                <div class="review-header">
                  <img :src="review.userProfileImg" alt="프로필" class="user-profile-img">
                  <div class="user-info">
                    <strong class="user-nickname">{{ review.userName }}</strong>
                    <span class="user-level">{{ review.userLevel }}</span>
                    <span class="user-visits">{{ review.userVisits }}</span>
                    <span class="review-date">{{ review.date }}</span>
                  </div>
                  <div class="review-rating">
                    <span v-for="star in review.stars" :key="star">★</span>
                  </div>
                </div>
                <div class="review-images" v-if="review.images && review.images.length">
                  <img v-for="(img, idx) in review.images" :key="idx" :src="img" :alt="'리뷰 이미지 ' + (idx+1)">
                  <div v-if="review.extraImagesCount > 0" class="more-review-images-overlay">
                    <span>+{{ review.extraImagesCount }}</span>
                  </div>
                </div>
                <p class="review-room-info">{{ review.roomType }}</p>
                <p class="review-text">{{ review.text }}</p>
                <button class="btn-more-review">더보기</button>
                <div class="review-actions">
                  <button class="like-review-button">👍 0</button>
                  <button class="report-review-button">⛔ 신고</button>
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
  import { ref, onMounted, onUnmounted, nextTick } from 'vue';
  
  const props = defineProps(['id']);
  
  const hotel = ref(null);
  const isStickyNavVisible = ref(false);
  const stickyNavBarRef = ref(null); // sticky nav bar의 DOM 요소를 참조하기 위한 ref
  
  const getRatingText = (rating) => {
    if (rating >= 4.5) return '최고에요';
    if (rating >= 4.0) return '아주 좋아요';
    if (rating >= 3.0) return '괜찮아요';
    return '보통이에요';
  };
  
  const handleScroll = () => {
    const headerContentWrapper = document.querySelector('.header-content-wrapper');
    if (!headerContentWrapper) return;
  
    const headerBottom = headerContentWrapper.getBoundingClientRect().bottom;
    // 헤더 콘텐츠 wrapper가 화면 상단으로 사라질 때 스티키 네비게이션 표시
    isStickyNavVisible.value = headerBottom < 0; 
  };
  
  // 섹션으로 부드럽게 스크롤하는 함수
  const scrollToSection = (id) => {
    const element = document.getElementById(id);
    if (element) {
      // 스티키 헤더의 높이를 고려하여 스크롤 위치 조정
      const offset = stickyNavBarRef.value ? stickyNavBarRef.value.offsetHeight : 0;
      window.scrollTo({
        top: element.offsetTop - offset - 20, // 20px 추가 여백
        behavior: 'smooth'
      });
    }
  };
  
  
  onMounted(() => {
    console.log("전달받은 호텔 ID:", props.id);
  
    hotel.value = {
      id: props.id, 
      name: '호텔 한큐 레스파이어 오사카', 
      englishName: 'Hotel Hankyu RESPIRE OSAKA',
      type: '호텔', 
      elidiaLevel: 4, 
      grade: '5성급 호텔', 
      location: '오사카', 
      locationDetail: 'Kita, Osaka, Osaka, 1-1 Ofukacho, 기타, 오사카, 일본, 530-0011', 
      image: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80', 
      price: 248951, 
      rating: 8.9, 
      reviews: 42116, 
      imageCount: 13,
      reviewSnippet: '한큐 레스파이어 오사카 스탠디드 트윈 후기입니다. 6월 말 커플여행으로 숙박했습니다. 🧡 JR 오사카역과 오사카메...', 
      fullAmenities: [
        '셀프 주차 (추가 비용 발생)', '무료 Wi-Fi (모든 객실)', '조식 서비스', '자동판매기',
        '금연 숙소', '여행 가방 보관 서비스', '24시간 프런트 데스크', '익스프레스 체크인',
        '정원', '24시간 피트니스 시설', '엘리베이터', '휠체어 접근 가능', '발코니/테라스',
        '공용 전자레인지', '다국어 가능 직원'
      ],
      rooms: [
        { 
          id: 101, 
          name: '스탠다드 룸, 트윈 베드, [금연]', 
          image: 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80',
          freeCancel: true,
          freeCancelUntil: '25.10.12 23:59',
          breakfastIncluded: true,
          bedType: '싱글베드 2개',
          price: 248951 
        },
        { 
          id: 102, 
          name: '디럭스 룸, 킹 베드, [금연]', 
          image: 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80',
          freeCancel: true,
          freeCancelUntil: '25.10.12 23:59',
          breakfastIncluded: true,
          bedType: '킹베드 1개',
          price: 266734 
        },
      ],
      userReviews: [ // [추가] 사용자 리뷰 데이터
        {
          userProfileImg: 'https://via.placeholder.com/40x40?text=User1',
          userName: '베스트리뷰어',
          userLevel: '25만원 이상 결제',
          userVisits: '숙박 2회, 장소 10',
          date: '1주 전',
          stars: 4,
          images: [
            'https://via.placeholder.com/150x100?text=ReviewImg1-1',
            'https://via.placeholder.com/150x100?text=ReviewImg1-2',
            'https://via.placeholder.com/150x100?text=ReviewImg1-3',
            'https://via.placeholder.com/150x100?text=ReviewImg1-4',
          ],
          extraImagesCount: 0,
          roomType: '12~13층 스탠다드 트윈(금연)',
          text: '한큐 레스파이어 오사카 스탠디드 트윈 후기입니다. 6월 말 커플여행으로 숙박했습니다. 🧡 JR 오사카역과 오사카메트로 우메다역이 연결되어 있고 긴자선이랑 리무진 버스도 호텔 바로 앞에서 탑승 가능해요. 스탠다드 트윈룸이라 큰 기대를 안 했는데...',
        },
        {
          userProfileImg: 'https://via.placeholder.com/40x40?text=User2',
          userName: '베스트리뷰어',
          userLevel: '다이아몬드고객',
          userVisits: '숙박 4회, 장소 126 - 장소 39',
          date: '1주 전',
          stars: 5,
          images: [
            'https://via.placeholder.com/150x100?text=ReviewImg2-1',
            'https://via.placeholder.com/150x100?text=ReviewImg2-2',
            'https://via.placeholder.com/150x100?text=ReviewImg2-3',
          ],
          extraImagesCount: 0,
          roomType: '디럭스룸 (24~29층) (금연)',
          text: '이번 오사카는 쟈가여행을 이용했는데 이번엔 조급 길게가기 싫어서 한 휴식 여행을 오게 되어서 어디에서 머무는지가 정말 많이 고민했어요. ㅠㅠ 그러던 중 주변 인프라가 맘에 들고 숙소 퀄리티도 위생이나 접근성이 불편함 없이 상급이라고 하는 글을 찾아 여기로 결정했어요. 가장 기능이 좋은 본관보다가 평이 좋길래 ㅠㅠ 참 잘한 결정 같아요. 침구도 괜찮았구요. 시설 불편함이 없었구요. 누가봐도 오사카의 주관하는 호텔 숙소와 모든 베드가 큰 차이도 없구요. 저희가 일본여행이 다...',
        },
      ]
    };
  
    window.addEventListener('scroll', handleScroll);
  });
  
  onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll);
  });
  </script>
  
  <style scoped>
  @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap');
  
  .hotel-detail-page {
    font-family: 'Noto Sans KR', sans-serif;
    background-color: #F9F9F9;
    color: #333; /* 기본 텍스트 색상 */
  }
  
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