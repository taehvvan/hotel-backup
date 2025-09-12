<template>
    <div class="list-page-container">
      <div class="content-wrapper">
        <header class="page-header">
          <h1>찜 목록</h1>
          <p>마음에 드는 숙소를 모아보고 여행 계획을 세워보세요.</p>
        </header>
  
        <div v-if="wishlistItems.length > 0" class="accommodations-grid">
          <div v-for="place in wishlistItems" :key="place.id" class="reco-card">
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
              class="like-button liked" 
              @click="removeFromWishlist(place.id)" 
              aria-label="찜하기 취소"
            >
              <span class="heart-icon filled">❤️</span>
            </button>
          </div>
        </div>
        
        <div v-else class="empty-wishlist">
          <p>찜한 숙소가 없습니다.</p>
          <router-link to="/" class="find-stay-btn">숙소 둘러보기</router-link>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  
  // --- 임시 데이터 ---
  // 실제 앱에서는 로그인한 사용자의 찜 목록을 API를 통해 불러와야 합니다.
  // 여기서는 MainPage에 있던 데이터 중 'liked: true'인 항목만 가져오는 것을 시뮬레이션합니다.
  import gyeongjuImage from '@/assets/images/card-gyeongju.jpg';
  import jejuImage from '@/assets/images/card-jeju.jpg';
  
  const allPlaces = ref([
    { id: 1, name: '유교랜드', location: '경상북도 안동시', image: gyeongjuImage, price: 180000, liked: false },
    { id: 2, name: '증평솔밭', location: '경상북도 청송군', image: jejuImage, price: 250000, liked: true },
    { id: 6, name: '감천 문화마을', location: '부산 사하구', image: gyeongjuImage, price: 175000, liked: true },
  ]);
  
  const wishlistItems = ref([]);
  
  onMounted(() => {
    // 페이지가 로드될 때 찜한 항목만 필터링하여 보여줍니다.
    wishlistItems.value = allPlaces.value.filter(place => place.liked);
  });
  
  const removeFromWishlist = (placeId) => {
    // 찜 목록에서 해당 항목을 제거합니다.
    wishlistItems.value = wishlistItems.value.filter(place => place.id !== placeId);
    // 실제 앱에서는 서버에 찜 취소 요청을 보내야 합니다.
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
  .accommodations-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 30px;
  }
  
  .empty-wishlist {
    text-align: center;
    padding: 80px 0;
    border: 1px dashed #ddd;
    border-radius: 12px;
  }
  .empty-wishlist p {
    font-size: 1.2rem;
    color: #888;
    margin: 0 0 20px 0;
  }
  .find-stay-btn {
    background-color: #222;
    color: #fff;
    text-decoration: none;
    padding: 12px 24px;
    border-radius: 30px;
    font-weight: 500;
    transition: opacity 0.3s;
  }
  .find-stay-btn:hover {
    opacity: 0.8;
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
  .heart-icon.filled { color: #FF3B30; font-size: 1.5rem; }
  </style>