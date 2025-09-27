<template>
  <div class="list-page-container">
    <div class="content-wrapper">
      <!-- 검색 바 -->
      <div class="search-bar">
        <label>
          체크인:
          <input type="date" v-model="checkIn" />
        </label>
        <label>
          체크아웃:
          <input type="date" v-model="checkOut" />
        </label>
        <label>
          방 갯수:
          <input type="number" v-model.number="rooms" min="1" />
        </label>
        <label>
          인원:
          <input type="number" v-model.number="persons" min="1" />
        </label>
        <button @click="searchWishlist">검색</button>
      </div>

      <!-- 헤더 -->
      <header class="page-header">
        <h1>찜 목록</h1>
        <p>마음에 드는 숙소를 모아보고 여행 계획을 세워보세요.</p>
      </header>

      <!-- 찜 목록 -->
      <div v-if="wishlistItems.length > 0" class="accommodations-grid">
        <div
          v-for="place in wishlistItems"
          :key="place.hId"
          class="reco-card"
          :class="{ disabled: place.minPrice <= 0 }"
          @click="goToHotelDetail(place)"
        >
          <img :src="place.image || gyeongjuImage" :alt="place.hname" class="reco-card-image" />
          <div class="reco-card-overlay"></div>

          <div class="reco-card-content">
            <span class="reco-card-location">{{ place.region }}</span>
            <h4>{{ place.hName }}</h4>
            <div class="review-info">
              <template v-if="place.reviewCount > 0">
                <span class="review-score">
                  리뷰 평균: {{ place.avgScore?.toFixed(1) ?? 'N/A' }}
                </span>
                <span class="review-count"> ({{ place.reviewCount }}개)</span>
              </template>
              <template v-else>
                <span class="no-review">아직 리뷰가 없어요</span>
              </template>
            </div>
          </div>

          <div class="price-overlay">
            <p class="min-price-info">
              <span v-if="place.minPrice > 0"> 최저가 {{ place.minPrice?.toLocaleString() }}원</span>
              <span v-else>객실이 없습니다.</span>
            </p>
          </div>

          <button
            class="like-button liked"
            @click.stop="removeFromWishlist(place.hId)"
            aria-label="찜하기 취소"
          >
            <span class="heart-icon filled">❤️</span>
          </button>
        </div>
      </div>

      <!-- 찜 목록 없음 -->
      <div v-else class="empty-wishlist">
        <p>검색 조건에 맞는 찜한 숙소가 없습니다.</p>
        <router-link to="/" class="find-stay-btn">숙소 둘러보기</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, watch, onMounted } from "vue";
  import { useRouter } from "vue-router";
  import axios from "axios";
  import gyeongjuImage from '@/assets/images/card-gyeongju.jpg';

  const wishlistItems = ref([]);
  const token = localStorage.getItem("accessToken");
  const router = useRouter();

  const today = new Date();
  const tomorrow = new Date();
  tomorrow.setDate(today.getDate() + 1);

  const checkIn = ref(today.toISOString().split('T')[0]);
  const checkOut = ref(tomorrow.toISOString().split('T')[0]);
  const rooms = ref(1);
  const persons = ref(2);

  // 서버 API 호출
  const fetchWishlist = async () => {
    if (!token) return;
    try {
      const response = await axios.get('http://localhost:8888/api/wishlist', {
        headers: { Authorization: `Bearer ${token}` },
        params: {
          checkIn: checkIn.value,
          checkOut: checkOut.value,
          rooms: rooms.value,
          persons: persons.value
        }
      });
      wishlistItems.value = response.data.map(item => ({ hid: item.hid, ...item }));
    } catch (error) {
      console.error("찜 목록 불러오기 실패", error);
    }
  };

  // 상세 이동 (조건부)
  const goToHotelDetail = (place) => {
  if (!place.minPrice || place.minPrice <= 0) {
    return; // 객실 없으면 이동 X
  }

  router.push({
    path: `http://localhost:8888/hotel/${place.hId}`,
      query: {
        region: place.region,
        startDate: checkIn.value,
        endDate: checkOut.value,
        rooms: rooms.value,
        persons: persons.value
      }
    });
  };

  // 찜 해제
  const removeFromWishlist = async (hid) => {
    if (!token || !hid) return;
    try {
      await axios.delete(`http://localhost:8888/api/wishlist/${hid}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      wishlistItems.value = wishlistItems.value.filter(item => item.hId !== hid);
      alert('찜 목록에서 해제되었습니다.');
    } catch (error) {
      console.error('찜 해제 실패', error);
    }
  };

  // 조건 변화 감지 → 다시 API 호출
  watch([checkIn, checkOut, rooms, persons], () => {
    fetchWishlist();
  });

  // 검색 버튼 클릭
  const searchWishlist = () => {
    fetchWishlist();
  };

  onMounted(fetchWishlist);
</script>

<style scoped>
  /* 페이지 컨테이너 */
  .list-page-container {
    padding: 20px 60px; /* 두 정의 합침 */
    background-color: #f5f5f5;
  }

  .content-wrapper {
    max-width: 1200px; /* 더 작은 값으로 통일 */
    margin: 0 auto;
    padding: 0 20px;
  }

  /* 헤더 */
  .page-header {
    text-align: center;
    margin-bottom: 40px; /* 40, 50 → 작은 값으로 */
  }
  .page-header h1 {
    font-size: 2.5rem;
    font-weight: 700;
    margin: 0 0 10px 0;
    color: #333;
  }
  .page-header p {
    font-size: 1rem; /* 1.1 → 1rem */
    color: #666;
    margin-top: 10px;
  }

  /* 검색 바 */
  .search-bar {
    display: flex;
    gap: 15px; /* 1rem → 15px */
    align-items: center;
    justify-content: center; /* 중앙 정렬 */
    margin-bottom: 30px;
    padding: 20px;
    background-color: white;
    border-radius: 8px;
    flex-wrap: wrap;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  }
  .search-bar label {
    display: flex;
    flex-direction: column;
    font-weight: bold; /* 두 정의 중 더 강조 */
  }
  .search-bar input {
    padding: 8px; /* 두 정의 합침 */
    border-radius: 4px;
    border: 1px solid #ddd;
  }
  .search-bar button {
    padding: 8px 16px;
    background-color: #007bff; /* 파랑으로 통일 */
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s;
  }
  .search-bar button:hover {
    background-color: #0056b3;
  }

  /* 찜 목록이 없을 때 */
  .empty-wishlist {
    text-align: center;
    padding: 50px; /* 50, 80 중 작은 쪽 */
    background-color: white;
    border: 1px dashed #ddd;
    border-radius: 12px;
    margin-top: 50px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  }
  .empty-wishlist p {
    font-size: 1.2rem;
    color: #888;
    margin: 0 0 20px 0;
  }
  .find-stay-btn {
    display: inline-block;
    margin-top: 20px;
    padding: 12px 24px;
    background-color: #007bff;
    color: white;
    border-radius: 5px;
    text-decoration: none;
    font-weight: bold;
    transition: opacity 0.3s;
  }
  .find-stay-btn:hover {
    opacity: 0.8;
  }

  /* 숙소 카드 */
  .accommodations-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px; /* 20, 30 중 작은 쪽 */
  }
  .reco-card {
    position: relative;
    border-radius: 12px;
    overflow: hidden;
    height: 380px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s, box-shadow 0.3s;
    cursor: pointer;
  }
  .reco-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  }
  .reco-card.disabled {
    cursor: not-allowed;
    opacity: 0.7;
  }
  .reco-card.disabled:hover {
    transform: none;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  }
  .reco-card-image {
    width: 100%;
    height: 100%; /* 200, 380 중 카드 높이에 맞춤 */
    object-fit: cover;
    display: block;
    transition: transform 0.4s ease;
  }
  .reco-card:hover .reco-card-image {
    transform: scale(1.05);
  }
  .reco-card-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to bottom, rgba(0,0,0,0.1) 0%, rgba(0,0,0,0.5) 100%);
  }

  .reco-card-content {
    position: absolute;
    bottom: 0;
    left: 0;
    padding: 20px;
    color: white;
    width: 100%;
  }
  .reco-card-location {
    background-color: rgba(0, 0, 0, 0.6);
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 0.8em;
    font-weight: bold;
  }
  .reco-card-content h4 {
    font-size: 1.5rem;
    margin: 10px 0 5px;
    font-weight: 700;
  }
  .review-info {
    margin-bottom: 5px;
    font-size: 0.9em;
  }
  .review-score,
  .review-count {
    font-weight: bold;
    color: #fff;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
  }

  /* 가격 표시 */
  .price-overlay {
    position: absolute;
    bottom: 20px;
    right: 20px;
    color: white;
    font-weight: bold;
    text-align: right;
    z-index: 10;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
  }
  .price-overlay .min-price-info {
    font-size: 1.1rem;
    font-weight: 600;
    margin: 0;
  }

  /* 찜 버튼 */
  .like-button {
    position: absolute;
    top: 10px;
    right: 10px;
    background: none;
    border: none;
    cursor: pointer;
    z-index: 10;
  }
  .heart-icon {
    font-size: 24px;
    color: white;
    text-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
  }
  .heart-icon.filled {
    color: #ff6347;
    filter: drop-shadow(0 0 3px rgba(0, 0, 0, 0.5));
  }
  .no-review {
    font-style: italic;
    color: #bbb;
  }
</style>