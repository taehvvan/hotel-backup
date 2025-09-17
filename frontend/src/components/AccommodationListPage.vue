<template>
    <div class="list-page-container">
      <div class="content-wrapper">
        <header class="page-header">
          <h1>여행지를 찾아보세요</h1>
          <p>쉼, 한국이 엄선한 최고의 공간들을 만나보세요.</p>
        </header>
  
        <div class="filters-and-sort">
          <div class="filters">
            <button class="filter-btn">지역 선택</button>
            <button class="filter-btn">가격대</button>
            <button class="filter-btn">숙소 유형</button>
          </div>
          <div class="sort-options">
            <select>
              <option>추천순</option>
              <option>가격 낮은순</option>
              <option>가격 높은순</option>
            </select>
          </div>
        </div>
  
        <div class="accommodations-grid">
          <div v-for="place in allPlaces" :key="place.id" class="reco-card">
            <img :src="place.image" :alt="place.name" class="reco-card-image" />
            <div class="reco-card-overlay"></div>
            <div class="price-info">
              <strong>{{ formatPrice(place.price) }}</strong> / 박
            </div>
            <div class="reco-card-content">
              <span class="reco-card-location">{{ place.location }}</span>
              <h4>{{ place.name }}</h4>
            </div>
            <button 
              class="like-button" 
              :class="{ 'liked': place.liked }" 
              @click="toggleLike(place)" 
              aria-label="찜하기"
            >
              <span v-if="!place.liked" class="heart-icon empty">♡</span>
              <span v-else class="heart-icon filled">❤️</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  // 실제 앱에서는 API를 통해 더 많은 숙소 목록을 불러와야 합니다.
  // 여기서는 MainPage와 동일한 데이터를 예시로 사용합니다.
  import gyeongjuImage from '@/assets/images/card-gyeongju.jpg';
  import jejuImage from '@/assets/images/card-jeju.jpg';
  import jeonjuImage from '@/assets/images/card-jeonju.jpg';
  
  const allPlaces = ref([
    { id: 1, name: '유교랜드', location: '경상북도 안동시', image: gyeongjuImage, price: 180000, liked: false },
    { id: 2, name: '증평솔밭', location: '경상북도 청송군', image: jejuImage, price: 250000, liked: true },
    { id: 3, name: '비자림', location: '제주특별자치도', image: jeonjuImage, price: 320000, liked: false },
    { id: 4, name: '여수 해상 케이블카', location: '전라남도 여수시', image: gyeongjuImage, price: 210000, liked: false },
    { id: 5, name: '첨성대', location: '경북 경주시', image: jejuImage, price: 150000, liked: false },
    { id: 6, name: '감천 문화마을', location: '부산 사하구', image: gyeongjuImage, price: 175000, liked: true },
    { id: 7, name: '성산일출봉', location: '제주 서귀포시', image: jejuImage, price: 280000, liked: false },
    { id: 8, name: '경복궁 근처 한옥', location: '서울 종로구', image: jeonjuImage, price: 450000, liked: false },
  ]);
  
  const toggleLike = (place) => {
    place.liked = !place.liked;
  };
  const formatPrice = (value) => {
    return value.toLocaleString('ko-KR') + '원';
  };
  </script>
  
  <style scoped>
  .list-page-container {
    padding: 60px 0;
  }
  .content-wrapper {
    max-width: 1280px;
    margin: 0 auto;
    padding: 0 20px;
  }
  .page-header {
    text-align: center;
    margin-bottom: 50px;
  }
  .page-header h1 {
    font-size: 2.5rem;
    font-weight: 700;
    margin: 0 0 10px 0;
  }
  .page-header p {
    font-size: 1.1rem;
    color: #666;
    margin: 0;
  }
  .filters-and-sort {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40px;
  }
  .filters {
    display: flex;
    gap: 10px;
  }
  .filter-btn {
    background: #f1f1f1;
    border: 1px solid #ddd;
    border-radius: 30px;
    padding: 8px 16px;
    font-size: 0.9rem;
    cursor: pointer;
  }
  .sort-options select {
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 8px 12px;
    font-size: 0.9rem;
  }
  .accommodations-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 30px;
  }
  
  /* 카드 스타일은 MainPage.vue의 .reco-card와 동일합니다. */
  .reco-card {
    position: relative;
    overflow: hidden;
    border-radius: 12px;
    height: 380px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.05);
    cursor: pointer;
  }
  .reco-card-image {
    width: 100%; height: 100%; object-fit: cover;
    transition: transform 0.4s ease;
  }
  .reco-card:hover .reco-card-image { transform: scale(1.05); }
  .reco-card-overlay {
    position: absolute; bottom: 0;
    width: 100%; height: 70%;
    background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, transparent 100%);
  }
  .price-info {
    position: absolute; top: 15px; left: 15px;
    background-color: rgba(0,0,0,0.4);
    color: #fff; padding: 6px 12px; border-radius: 20px;
    font-size: 0.9rem; font-weight: 500;
  }
  .reco-card-content {
    position: absolute; bottom: 20px; left: 20px; right: 20px;
    color: #fff;
  }
  .like-button {
    position: absolute; top: 15px; right: 15px;
    background-color: rgba(0,0,0,0.3); border-radius: 50%;
    width: 40px; height: 40px;
    border: none; cursor: pointer;
  }
  .heart-icon { color: #fff; font-size: 1.5rem; }
  .heart-icon.filled { color: #FF3B30; }
  </style>