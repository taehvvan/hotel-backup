<template>
  <div class="page-container">

    <SearchBar />
  
    <div class="search-result-container">
      <div class="filters-column">
        <div class="filter-header">
          <h4>필터</h4>
          <button class="btn-reset">초기화</button>
        </div>
  
        <div class="filter-group">
          <h5>숙소 유형</h5>
          <div class="type-button-group">
            <button class="type-filter-btn">호텔</button>
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
          <h2><strong>'{{ destination }}'</strong> 검색 결과</h2>
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
          <!-- 검색 결과가 있을 때 -->
          <div v-if="searchResults.length > 0">
            <router-link
              v-for="item in searchResults"
              :key="item.hid"
              :to="{ name: 'HotelDetail', params: { id: item.hid } }"
              class="result-card"
            >
              <div class="result-card-inner">
                <div class="image-wrapper">
                  <!-- <img :src="item.image" :alt="item.name"> -->
                  <img :src="a" :alt="item.hname">
                </div>
                <div class="info-wrapper">
                  <div class="info-header">
                    <div class="info-badges">
                      <!-- <span class="item-type">{{ item.type }}</span> -->
                      <span class="item-type">호텔</span>
                    </div>
                    <h3>{{ item.hname }}</h3>
                  </div>
                  <div class="rating-section">
                    <div class="rating-card">
                      <!-- <span class="score-badge">{{ item.rating.toFixed(1) }}</span>
                      <span class="rating-text">{{ getRatingText(item.rating) }}</span> -->
                    </div>
                    <!-- <span class="review-count">({{ item.reviews.toLocaleString() }}개 후기)</span> -->
                  </div>
                  <div class="details-group">
                    <p class="grade">
                      <!-- <span class="hotel-grade-stars">{{ '★'.repeat(item.stars) }}</span> {{ item.grade }} -->
                    </p>
                    <p class="location">
                      <!-- <span class="location-icon">📍</span>{{ item.location }} -->
                    </p>
                    <p class="amenities">
                      <!-- <strong>주요 편의시설:</strong> {{ item.amenities.join(', ') }} -->
                    </p>
                  </div>
                </div>
                <div class="price-wrapper">
                  <button class="like-button" @click.prevent>♡</button>
                  <div class="final-price-box">
                    <span class="price-label">1박 최저가</span>
                    <!-- <strong>{{ item.price.toLocaleString() }}원</strong> -->
                  </div>
                </div>
              </div>
            </router-link>
          </div>

          <!-- 검색 결과가 없을 때 -->
          <div v-else>
            <p>죄송합니다, 검색 조건에 맞는 결과를 찾을 수 없습니다. 검색 조건을 변경 후 다시 조회해 주시기 바랍니다.</p>
          </div>
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

  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import SearchBar from './SearchBar.vue';

// --- 검색창 로직 (전체) ---
const calendarBtn = ref(null);
const guestBtn = ref(null);

const route = useRoute();

const destination = ref('');
const checkIn = ref(null);
const checkOut = ref(null);
const rooms = ref(1);
const persons = ref(2);

const searchResults = ref([]);
const priceRange = ref({ min: 50000, max: 750000 });
const rating = ref(4.0);

// URL 쿼리 파라미터를 변수에 로드하는 함수
const loadSearchQueryFromUrl = () => {
  const query = route.query;

  destination.value = query.region || '';
  
  // URL에서 날짜 문자열을 Date 객체로 변환
  checkIn.value = query.startDate ? new Date(query.startDate) : null;
  checkOut.value = query.endDate ? new Date(query.endDate) : null;
  
  rooms.value = Number(query.rooms) || 1;
  persons.value = Number(query.persons) || 2;
};

// 검색 요청 함수
const sendSearchRequest = async () => {
  // 날짜 객체의 유효성을 확인
  const validStartDate = checkIn.value instanceof Date && !isNaN(checkIn.value);
  const validEndDate = checkOut.value instanceof Date && !isNaN(checkOut.value);

  const requestBody = {
    region: destination.value,
    startDate: validStartDate ? checkIn.value.toISOString().split('T')[0] : null,
    endDate: validEndDate ? checkOut.value.toISOString().split('T')[0] : null,
    numberOfRooms: rooms.value,
    numberOfPeople: persons.value,
  };

  console.log('백엔드로 보낼 검색 조건:', requestBody);

  try {
    const response = await fetch('http://localhost:8888/api/search', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestBody),
    });

    if (response.status === 204) {
      searchResults.value = [];
      return;
    }

    if (response.ok) {
      const data = await response.json();
      searchResults.value = data;
      console.log('검색 결과:', data);
    } else {
      console.error('검색 실패:', response.statusText);
      alert('검색 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('API 호출 중 오류:', error);
    alert('네트워크 오류가 발생했습니다.');
  }
};

// URL 쿼리가 변경될 때마다 이 로직이 실행됩니다.
// 컴포넌트 마운트 시에도 즉시 실행됩니다.
watch(
  () => route.query,
  () => {
    loadSearchQueryFromUrl();
    sendSearchRequest();
  },
  { immediate: true, deep: true }
);

// --- 검색 결과 페이지 로직 ---
const getRatingText = (rating) => {
  if (rating >= 4.5) return '최고에요';
  if (rating >= 4.0) return '아주 좋아요';
  if (rating >= 3.0) return '괜찮아요';
  return '보통이에요';
};

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