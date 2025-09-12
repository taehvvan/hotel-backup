<template>
  <div class="page-container">
    <section class="search-section" ref="searchSectionRef">
      <div class="content-wrapper">
        <div class="main-search-bar">
          <div class="search-input-group destination">
            <span class="icon-location">📍</span>
            <input
              type="text"
              placeholder="어디로 떠날까요?"
              v-model="searchQuery.destination"
              @focus="closeAllPopups"
            />
          </div>
  
          <div class="search-input-group dates" @click.stop="toggleCalendar" ref="calendarBtn">
            <span class="icon-calendar">🗓️</span>
            <span class="date-text">{{ checkInText }}</span>
            <span class="date-separator">-</span>
            <span class="date-text">{{ checkOutText }}</span>
            <span class="nights-badge">{{ nights }}박</span>
          </div>
  
          <div class="search-input-group guests" @click.stop="toggleGuestSelector" ref="guestBtn">
            <span class="icon-guests">👥</span>
            <span>성인 {{ searchQuery.adults }}명, 어린이 {{ searchQuery.children }}명</span>
          </div>
  
          <button type="button" class="search-button" @click="executeSearch">
            <span class="icon-search">🔍</span>
            검색
          </button>
        </div>
      </div>
    </section>
  
    <nav class="type-nav">
      <div class="content-wrapper">
        <button
          :class="{ active: activeNav === '호텔' }"
          @click="activeNav = '호텔'">호텔</button>
        <button
          :class="{ active: activeNav === '모텔' }"
          @click="activeNav = '모텔'">모텔</button>
        <button
          :class="{ active: activeNav === '한옥' }"
          @click="activeNav = '한옥'">한옥</button>
      </div>
    </nav>
  
    <div class="search-result-container">
      <div class="filters-column">
        <div class="filter-header">
          <h4>필터</h4>
          <button class="btn-reset">초기화</button>
        </div>
  
        <div class="filter-group">
          <h5>숙소 유형</h5>
          <div class="type-button-group">
            <button class="type-filter-btn active">호텔</button>
            <button class="type-filter-btn">모텔</button>
            <button class="type-filter-btn">한옥</button>
            <button class="type-filter-btn">펜션/풀빌라</button>
            <button class="type-filter-btn">게스트하우스/비앤비</button>
            <button class="type-filter-btn">리조트</button>
          </div>
          <button class="btn-more-types">더 보기</button>
        </div>
  
        <div class="filter-group">
          <h5>가격 (1박 기준)</h5>
          <div class="price-range-slider">
              <div class="slider-track"></div>
              <input type="range" class="price-slider-min" min="0" max="1000000" step="10000" v-model.number="priceRange.min">
              <input type="range" class="price-slider-max" min="0" max="1000000" step="10000" v-model.number="priceRange.max">
          </div>
          <div class="price-display">
            <span>₩{{ priceRange.min.toLocaleString() }}</span> -
            <span>₩{{ priceRange.max.toLocaleString() }}</span>
          </div>
        </div>
  
        <div class="filter-group">
          <h5>평점</h5>
          <div class="rating-filter-card">
            <div class="rating-filter">
              <button v-for="star in 5" :key="star" @click="rating = star" :class="{ active: rating >= star }">★</button>
            </div>
            <span>{{ rating.toFixed(1) }}점 이상</span>
          </div>
        </div>
  
        <div class="filter-group">
          <h5>편의시설</h5>
          <div class="checkbox-group">
            <label><input type="checkbox"> 🅿️ 주차 가능</label>
            <label><input type="checkbox"> 🏊 수영장</label>
            <label><input type="checkbox"> 🥐 조식 포함</label>
            <label><input type="checkbox"> 🐾 반려동물</label>
          </div>
        </div>
      </div>
  
      <div class="results-main-panel">
        <div class="search-summary">
          <h2><strong>'{{ pageTitleDestination }}'</strong> 검색 결과</h2>
          <div class="sort-options">
            <select>
              <option>추천순</option>
              <option>가격 낮은순</option>
              <option>가격 높은순</option>
              <option>평점 높은순</option>
            </select>
          </div>
        </div>
  
        <div class="results-list">
          <router-link
            v-for="item in searchResults"
            :key="item.id"
            :to="{ name: 'HotelDetail', params: { id: item.id } }"
            class="result-card"
          >
            <div class="result-card-inner">
              <div class="image-wrapper">
                <img :src="item.image" :alt="item.name">
              </div>
              <div class="info-wrapper">
                <div class="info-header">
                  <div class="info-badges">
                    <span class="item-type">{{ item.type }}</span>
                  </div>
                  <h3>{{ item.name }}</h3>
                </div>
                <div class="rating-section">
                  <div class="rating-card">
                    <span class="score-badge">{{ item.rating.toFixed(1) }}</span>
                    <span class="rating-text">{{ getRatingText(item.rating) }}</span>
                  </div>
                  <span class="review-count">({{ item.reviews.toLocaleString() }}개 후기)</span>
                </div>
                <div class="details-group">
                  <p class="grade">
                    <span class="hotel-grade-stars">{{ '★'.repeat(item.stars) }}</span> {{ item.grade }}
                  </p>
                  <p class="location">
                    <span class="location-icon">📍</span>{{ item.location }}
                  </p>
                  <p class="amenities">
                    <strong>주요 편의시설:</strong> {{ item.amenities.join(', ') }}
                  </p>
                </div>
              </div>
              <div class="price-wrapper">
                <button class="like-button" @click.prevent>♡</button>
                <div class="final-price-box">
                  <span class="price-label">1박 최저가</span>
                  <strong>{{ item.price.toLocaleString() }}원</strong>
                </div>
              </div>
            </div>
          </router-link>
        </div>
  
        <nav class="pagination">
          <a href="#">&lt;</a>
          <a href="#" class="active">1</a>
          <a href="#">2</a>
          <a href="#">3</a>
          <a href="#">4</a>
          <a href="#">5</a>
          <a href="#">&gt;</a>
        </nav>
      </div>
    </div>
  
    <Teleport to="body">
      <div v-if="isCalendarOpen" class="calendar-popup" :style="popupPosition.calendar">
        <div class="calendar-month">
          <div class="month-name">2025년 9월</div>
          <div class="days-header"><span>일</span><span>월</span><span>화</span><span>수</span><span>목</span><span>금</span><span>토</span></div>
          <div class="days-grid">
            <span class="day">...</span>
          </div>
        </div>
        <div class="calendar-month">
          <div class="month-name">2025년 10월</div>
          <div class="days-header"><span>일</span><span>월</span><span>화</span><span>수</span><span>목</span><span>금</span><span>토</span></div>
          <div class="days-grid">
            <span class="day">...</span>
          </div>
        </div>
      </div>
      <div v-if="isGuestSelectorOpen" class="guest-selector-popup" :style="popupPosition.guest">
        <div class="guest-row">
          <span class="label">성인</span>
          <div class="counter">
            <button type="button" @click="searchQuery.adults > 1 && searchQuery.adults--">-</button>
            <span>{{ searchQuery.adults }}</span>
            <button type="button" @click="searchQuery.adults < 10 && searchQuery.adults++">+</button>
          </div>
        </div>
        <div class="guest-row">
          <span class="label">어린이</span>
          <div class="counter">
            <button type="button" @click="searchQuery.children > 0 && searchQuery.children--">-</button>
            <span>{{ searchQuery.children }}</span>
            <button type="button" @click="searchQuery.children < 5 && searchQuery.children++">+</button>
          </div>
        </div>
        <div class="guest-actions">
          <button type="button" class="confirm-btn" @click="closeAllPopups">확인</button>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
  
// --- 검색창 로직 (전체) ---
const searchSectionRef = ref(null);
const calendarBtn = ref(null);
const guestBtn = ref(null);
const isCalendarOpen = ref(false);
const isGuestSelectorOpen = ref(false);
const popupPosition = ref({ calendar: {}, guest: {} });
  
const route = useRoute();
const router = useRouter();

const searchQuery = ref({
  destination: '',
  checkIn: null,
  checkOut: null,
  adults: 2,
  children: 0,
});
  
const calculatePopupPosition = () => {
  if (isCalendarOpen.value && calendarBtn.value) {
    const rect = calendarBtn.value.getBoundingClientRect();
    popupPosition.value.calendar = {
      top: `${rect.bottom + window.scrollY + 10}px`,
      left: `${rect.left + window.scrollX}px`
    };
  }
  if (isGuestSelectorOpen.value && guestBtn.value) {
    const rect = guestBtn.value.getBoundingClientRect();
    popupPosition.value.guest = {
      top: `${rect.bottom + window.scrollY + 10}px`,
      left: `${rect.left + window.scrollX}px`
    };
  }
};
  
const closeAllPopups = () => {
  isCalendarOpen.value = false;
  isGuestSelectorOpen.value = false;
};
  
const toggleCalendar = () => {
  const wasOpen = isCalendarOpen.value;
  closeAllPopups();
  if (!wasOpen) {
    isCalendarOpen.value = true;
    nextTick(calculatePopupPosition);
  }
};
  
const toggleGuestSelector = () => {
  const wasOpen = isGuestSelectorOpen.value;
  closeAllPopups();
  if (!wasOpen) {
    isGuestSelectorOpen.value = true;
    nextTick(calculatePopupPosition);
  }
};
  
const checkInText = computed(() => searchQuery.value.checkIn ? `${searchQuery.value.checkIn.getMonth() + 1}/${searchQuery.value.checkIn.getDate()}` : '날짜 선택');
const checkOutText = computed(() => searchQuery.value.checkOut ? `${searchQuery.value.checkOut.getMonth() + 1}/${searchQuery.value.checkOut.getDate()}` : '날짜 선택');
const nights = computed(() => {
  if (searchQuery.value.checkIn && searchQuery.value.checkOut) {
    const diff = Math.floor((searchQuery.value.checkOut - searchQuery.value.checkIn) / (1000 * 60 * 60 * 24));
    return diff > 0 ? diff : 0;
  }
  return 0;
});
  
const executeSearch = () => {
  closeAllPopups();
  router.push({
    path: '/search',
    query: {
      destination: searchQuery.value.destination,
      checkIn: searchQuery.value.checkIn ? searchQuery.value.checkIn.toISOString() : '',
      checkOut: searchQuery.value.checkOut ? searchQuery.value.checkOut.toISOString() : '',
      adults: searchQuery.value.adults,
      children: searchQuery.value.children
    }
  });
};
  
// --- 검색 결과 페이지 로직 ---
const pageTitleDestination = ref('');
const activeNav = ref('호텔');
const priceRange = ref({ min: 50000, max: 750000 });
const rating = ref(4.0);
  
const searchResults = ref([
  { id: 1, name: '서울 신라호텔', type: '호텔', stars: 5, grade: '5성급 호텔', location: '서울 중구', image: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80', price: 450000, rating: 4.8, reviews: 1320, amenities: ['수영장', '피트니스', '주차 가능'] },
  { id: 2, name: '가평 하늘숲 펜션', type: '펜션/풀빌라', stars: 0, grade: '펜션', location: '경기 가평군', image: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80', price: 210000, rating: 4.5, reviews: 580, amenities: ['개별바베큐', '수영장'] },
  { id: 3, name: '롯데호텔 월드', type: '호텔', stars: 5, grade: '5성급 호텔', location: '서울 송파구', image: 'https://images.unsplash.com/photo-1542314831-068cd1dbb5eb?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80', price: 380000, rating: 3.7, reviews: 2150, amenities: ['피트니스', '조식포함'] },
  { id: 4, name: '경주 한옥마을 스테이', type: '한옥', stars: 0, grade: '한옥 스테이', location: '경북 경주시', image: 'https://images.unsplash.com/photo-1582268611958-ebfd161ef9cf?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80', price: 180000, rating: 4.9, reviews: 980, amenities: ['주차 가능', '조식포함'] },
]);
  
const getRatingText = (rating) => {
  if (rating >= 4.5) return '최고에요';
  if (rating >= 4.0) return '아주 좋아요';
  if (rating >= 3.0) return '괜찮아요';
  return '보통이에요';
};

const loadSearchQueryFromUrl = () => {
  const query = route.query;
  searchQuery.value.destination = query.destination || '';
  pageTitleDestination.value = query.destination || '전체';
  
  searchQuery.value.checkIn = query.checkIn ? new Date(query.checkIn) : null;
  searchQuery.value.checkOut = query.checkOut ? new Date(query.checkOut) : null;
  
  searchQuery.value.adults = Number(query.adults) || 2;
  searchQuery.value.children = Number(query.children) || 0;
};
  
onMounted(() => {
  loadSearchQueryFromUrl();
  window.addEventListener('resize', calculatePopupPosition);
});
  
onUnmounted(() => {
  window.removeEventListener('resize', calculatePopupPosition);
});

watch(
  () => route.query,
  (newQuery) => {
    loadSearchQueryFromUrl();
  }
);
</script>
  
<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap');
  
body {
  font-family: 'Noto Sans KR', sans-serif;
  color: #333;
  background-color: #FDFBF8;
}
  
.page-container { padding-bottom: 60px; }
.content-wrapper { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
  
.search-section { padding: 40px 0; background-color: #fff; border-bottom: 1px solid #e0e0e0; }
.main-search-bar { 
  display: flex; 
  align-items: center; 
  background-color: #f5f6f7; 
  border: 1px solid #E5E5E5; 
  border-radius: 12px; 
  height: 72px; 
  box-shadow: 0 8px 16px rgba(0,0,0,0.05); 
  max-width: 900px;
  margin: 0 auto;
}
.search-input-group { display: flex; align-items: center; flex: 1 1 0; height: 100%; padding: 0 20px; cursor: pointer; gap: 10px; }
.search-input-group:not(:last-of-type) { border-right: 1px solid #E5E5E5; }
.search-input-group.destination { flex-grow: 1.5; }
.search-input-group input { border: none; font-size: 1rem; width: 100%; font-weight: 500; outline: none; color: #333; background: transparent; }
.search-input-group input::placeholder { color: #888; }
.date-text, .guests span { font-weight: 500; font-size: 1rem; color: #222; }
.nights-badge { background-color: #e0f4ff; color: #007bff; border-radius: 20px; padding: 4px 10px; margin-left: auto; font-size: 0.8rem; }
.search-button { background-color: #007bff; color: #fff; border: none; border-radius: 8px; height: 56px; display: flex; align-items: center; gap: 8px; font-weight: 700; padding: 0 24px; cursor: pointer; margin: 0 8px; transition: background-color 0.2s ease; }
.search-button:hover { background-color: #0056b3; }
  
.type-nav { background-color: #fff; border-bottom: 1px solid #eee; margin-bottom: 30px; }
.type-nav .content-wrapper { display: flex; gap: 5px; }
.type-nav button { flex: 1; padding: 18px 20px; border: none; border-bottom: 3px solid transparent; background-color: #fff; font-size: 1.1rem; font-weight: 600; color: #888; cursor: pointer; transition: all 0.2s ease-in-out; text-align: center; }
.type-nav button:hover { color: #333; }
.type-nav button.active { color: #007bff; border-bottom: 3px solid #007bff; }
  
.search-result-container { display: grid; grid-template-columns: 280px 1fr; gap: 40px; max-width: 1200px; margin: 40px auto; padding: 0 20px; }
.results-main-panel { min-width: 0; }
.search-summary { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; padding-bottom: 15px; }
.search-summary h2 { margin: 0; font-size: 1.8rem; font-weight: 700; color: #222; }
.search-summary h2 strong { color: #007bff; }
.sort-options select { padding: 10px 15px; border-radius: 8px; border: 1px solid #ddd; font-size: 1rem; color: #555; background-color: #fff; cursor: pointer; }
.results-list { display: flex; flex-direction: column; gap: 0; }
  
.filters-column { height: fit-content; position: sticky; top: 100px; }
.filter-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; padding-bottom: 10px; border-bottom: 1px solid #f0f0f0; }
.filter-header h4 { margin: 0; font-size: 1.3rem; font-weight: 700; color: #222; }
.btn-reset { background: none; border: none; color: #888; font-size: 0.9rem; cursor: pointer; padding: 0; }
.filter-group { border-bottom: 1px solid #f0f0f0; padding: 25px 0; }
.filters-column .filter-group:last-of-type { border-bottom: none; }
.filter-group h5 { margin: 0 0 15px 0; font-size: 1.1rem; font-weight: 600; color: #333; }
.type-button-group { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 10px; }
.type-filter-btn { padding: 8px 16px; border: 1px solid #ddd; border-radius: 20px; background-color: #fff; color: #555; font-size: 0.9rem; cursor: pointer; transition: all 0.2s; }
.type-filter-btn.active { background-color: #007bff; color: #fff; border-color: #007bff; }
.btn-more-types { background: none; border: none; color: #007bff; font-weight: 500; margin-top: 5px; cursor: pointer; }
.rating-filter-card { display: flex; justify-content: space-between; align-items: center; background-color: #F8F9FA; border-radius: 8px; padding: 10px 15px; }
.rating-filter { display: flex; align-items: center; gap: 2px; }
.rating-filter button { background: none; border: none; font-size: 1.5rem; color: #E0E0E0; cursor: pointer; padding: 0; transition: color 0.2s ease; }
.rating-filter button.active { color: #FFD700; }
.rating-filter-card span { font-weight: 500; color: #555; }
.price-range-slider { position: relative; height: 20px; margin-bottom: 15px; }
.price-range-slider input[type=range] { position: absolute; width: 100%; -webkit-appearance: none; background: transparent; pointer-events: none; }
.price-range-slider input[type=range]::-webkit-slider-thumb { -webkit-appearance: none; pointer-events: all; width: 22px; height: 22px; border-radius: 50%; background: #007bff; border: 3px solid #fff; box-shadow: 0 0 5px rgba(0,0,0,0.2); cursor: pointer; }
.price-range-slider .slider-track { position: absolute; width: 100%; height: 6px; background-color: #E0E0E0; top: 7px; border-radius: 3px; }
.price-display { display: flex; justify-content: space-between; color: #333; font-weight: 600; margin-top: 10px; font-size: 1rem; }
  
.result-card { border-bottom: 1px solid #f0f0f0; padding: 20px 0; transition: background-color 0.2s; cursor: pointer; display: block; text-decoration: none; color: inherit; }
.results-list .result-card:last-child { border-bottom: none; }
.result-card:hover { background-color: #f9f9f9; }
.result-card-inner { display: grid; grid-template-columns: 300px 1fr 200px; gap: 25px; }
.image-wrapper { position: relative; width: 300px; min-height: 200px; overflow: hidden; border-radius: 12px; }
.image-wrapper img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s ease; }
.result-card:hover .image-wrapper img { transform: scale(1.05); }
.info-wrapper { flex-grow: 1; display: flex; flex-direction: column; gap: 8px; }
.info-header { display: flex; flex-direction: column; }
.info-badges { display: flex; align-items: center; gap: 8px; }
.item-type { font-size: 0.9rem; color: #888; }
h3 { margin: 5px 0; font-size: 1.4rem; font-weight: 700; color: #222; }
.rating-section { display: flex; align-items: center; gap: 10px; }
.rating-card { display: flex; align-items: center; gap: 5px; background-color: #FFFBEA; border-radius: 4px; padding: 5px 10px; }
.score-badge { background: none; color: #F7C44E; font-weight: 700; font-size: 1.1rem; }
.rating-text { font-size: 1rem; font-weight: 600; color: #F7C44E; }
.review-count { font-size: 0.9rem; color: #666; line-height: 1.5; }
.details-group { margin-top: 15px; padding-top: 15px; border-top: 1px solid #f0f0f0; display: flex; flex-direction: column; gap: 8px; font-size: 0.95rem; color: #555; }
.details-group p { margin: 0; line-height: 1.5; }
.hotel-grade-stars { color: #E91E63; }
.amenities strong { color: #333; }
.location-icon { margin-right: 5px; color: #777; }
  
.price-wrapper { width: 200px; display: flex; flex-direction: column; justify-content: space-between; align-items: flex-end; text-align: right; position: relative; }
.like-button { background: none; border: none; font-size: 1.8rem; cursor: pointer; color: #aaa; padding: 0; transition: all 0.2s ease; }
.like-button:hover { color: #E53935; transform: scale(1.1); }
.final-price-box { margin-top: auto; }
.final-price-box .price-label { font-size: 0.95rem; font-weight: 500; color: #555; }
.final-price-box strong { font-size: 1.7rem; font-weight: 800; color: #E53935; white-space: nowrap; min-width: 100%; }
  
.calendar-popup, .guest-selector-popup { position: absolute; background-color: #fff; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.15); border: 1px solid #E0E0E0; z-index: 1000; }
.guest-selector-popup { width: 320px; padding: 25px; }
.guest-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.counter { display: flex; align-items: center; gap: 15px; }
.counter button { width: 36px; height: 36px; border-radius: 50%; border: 1px solid #E0E0E0; background-color: #fff; font-size: 1.6rem; color: #888; cursor: pointer; }
.confirm-btn { background-color: #007bff; color: #fff; border: none; padding: 12px 25px; border-radius: 8px; font-weight: 600; cursor: pointer; font-size: 1rem; }
  
.pagination { display: flex; justify-content: center; gap: 10px; margin-top: 50px; }
.pagination a { display: flex; align-items: center; justify-content: center; width: 40px; height: 40px; border: 1px solid #ddd; border-radius: 8px; text-decoration: none; color: #333; font-weight: 500; }
.pagination a.active { background-color: #007bff; color: #fff; border-color: #007bff; font-weight: 700; }
</style>