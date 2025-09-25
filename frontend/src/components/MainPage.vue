<template>
  <div class="main-container">
    <main>
      <section class="main-showcase" :style="{ backgroundColor: currentBgColor }">
        <swiper
          :modules="[Autoplay, Navigation, Pagination]"
          :slides-per-view="1"
          :loop="true"
          :speed="1000"
          :autoplay="{ delay: 5000, disableOnInteraction: false }"
          :pagination="{ el: '.swiper-pagination-custom', type: 'fraction' }"
          :navigation="{ nextEl: '.swiper-button-next-custom', prevEl: '.swiper-button-prev-custom' }"
          @slideChange="onSlideChange"
          class="main-slider"
        >
          <swiper-slide v-for="slide in showcaseSlides" :key="slide.id">
            <div class="slide-layout">
              <div class="slide-text-content">
                <span class="slide-tag">{{ slide.tag }}</span>
                <h2 class="slide-title">{{ slide.title }}</h2>
                <a href="#" class="slide-link">자세히 보기</a>
              </div>
              <div class="slide-image-content">
                <img :src="slide.image" :alt="slide.title" class="slide-image" />
              </div>
            </div>
          </swiper-slide>
        </swiper>
        
        <div class="slider-controls-wrapper">
          <div class="slider-controls">
            <div class="swiper-pagination-custom"></div>
            <div class="swiper-navigation">
              <div class="swiper-button-prev-custom"></div>
              <div class="swiper-button-next-custom"></div>
            </div>
          </div>
        </div>
      </section>

      <SearchBar />

      <section class="recommendations-section">
        <div class="content-wrapper">
          <div class="section-header">
            <h3>주인의 안목이 깃든 공간</h3>
            <router-link to="/accommodations" class="more-link">더보기 +</router-link>
          </div>
          <div class="recommendations-grid">
            <div v-for="place in recommendedPlaces" :key="place.id" class="reco-card">
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
      </section>

      <section class="landmark-section">
        <div class="content-wrapper">
          <div class="section-header">
            <h3><router-link to="/landmarks" class="more-link">인기 랜드마크 둘러보기</router-link></h3>
            <router-link to="/landmarks" class="more-link">전체보기</router-link>
          </div>
          <swiper
            :modules="[Navigation]"
            :slides-per-view="'auto'"
            :space-between="20"
            :navigation="{ nextEl: '.landmark-button-next', prevEl: '.landmark-button-prev' }"
            class="landmark-slider"
          >
            <swiper-slide v-for="landmark in popularLandmarks" :key="landmark.id">
              <router-link :to="{ name: 'LandmarkDetail', params: { id: landmark.id } }" class="landmark-card-link">
                <div class="landmark-card">
                  <div class="landmark-image-container">
                    <img :src="landmark.image" :alt="landmark.name" />
                  </div>
                  <div class="landmark-info">
                    <div class="landmark-tags">
                      <span v-for="tag in landmark.tags" :key="tag">{{ tag }}</span>
                    </div>
                    <h4 class="landmark-name">{{ landmark.name }}</h4>
                    <p class="landmark-location">{{ landmark.location }}</p>
                  </div>
                </div>
              </router-link>
            </swiper-slide>
          </swiper>
          <div class="landmark-navigation">
            <div class="landmark-button-prev"></div>
            <div class="landmark-button-next"></div>
          </div>
        </div>
      </section>

      <section class="heritage-section">
        <div class="content-wrapper">
          <div class="section-header">
            <h3>우리의 보물, 문화재 추천</h3>
            <router-link to="/heritage" class="more-link">전체보기</router-link>
          </div>
        </div>
        <div class="heritage-slider-wrapper">
          <swiper
            :modules="[Navigation, Pagination, Autoplay, EffectFade]"
            :loop="true"
            :speed="1000"
            effect="fade"
            :autoplay="{ delay: 6000, disableOnInteraction: false }"
            :pagination="{ el: '.heritage-pagination', clickable: true }"
            :navigation="{ nextEl: '.heritage-button-next', prevEl: '.heritage-button-prev' }"
            class="heritage-slider"
          >
            <swiper-slide v-for="item in heritageItems" :key="item.id" class="heritage-slide" :style="{ backgroundImage: `url(${item.image})` }">
              <router-link :to="{ name: 'HeritageDetail', params: { id: item.id } }" class="heritage-slide-link">
                <div class="slide-overlay"></div>
                <div class="heritage-slide-content">
                  <span class="heritage-category">{{ item.category }}</span>
                  <h4 class="heritage-name">{{ item.name }}</h4>
                  <p class="heritage-description">{{ item.description }}</p>
                </div>
              </router-link>
            </swiper-slide>
          </swiper>
          <div class="heritage-controls">
            <div class="heritage-pagination"></div>
            <div class="heritage-navigation">
              <div class="heritage-button-prev"></div>
              <div class="heritage-button-next"></div>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Swiper, SwiperSlide } from 'swiper/vue';
import { Autoplay, Navigation, Pagination, EffectFade } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import 'swiper/css/effect-fade';

import SearchBar from './SearchBar.vue';

import gyeongjuImage from '@/assets/images/card-gyeongju.jpg';
import jejuImage from '@/assets/images/card-jeju.jpg';
import jeonjuImage from '@/assets/images/card-jeonju.jpg';

const showcaseSlides = ref([
  { id: 1, tag: "트렌딩 여행지", title: "일상 잠시 멈춤, 안동 맹개마을", image: gyeongjuImage, bgColor: '#F0F4F7' },
  { id: 2, tag: "대한민국 구석구석 X TMAP", title: "APEC 개최지, 청량한 여름 여행", image: jejuImage, bgColor: '#E9F5F3' },
  { id: 3, tag: "시원한 바람이 반겨주는 곳", title: "푸른 청송 자연 속 힐링 코스", image: jeonjuImage, bgColor: '#FBF3EF' },
]);

const currentBgColor = ref(showcaseSlides.value[0].bgColor);
const onSlideChange = (swiper) => {
  const activeSlide = showcaseSlides.value[swiper.realIndex];
  if (activeSlide) { currentBgColor.value = activeSlide.bgColor; }
};

const recommendedPlaces = ref([
  { id: 1, name: '유교랜드', location: '경상북도 안동시', image: gyeongjuImage, price: 180000, liked: false },
  { id: 2, name: '증평솔밭', location: '경상북도 청송군', image: jejuImage, price: 250000, liked: true },
  { id: 3, name: '비자림', location: '제주특별자치도', image: jeonjuImage, price: 320000, liked: false },
  { id: 4, name: '여수 해상 케이블카', location: '전라남도 여수시', image: gyeongjuImage, price: 210000, liked: false },
]);

const popularLandmarks = ref([
    { id: 1, name: '경복궁', location: '서울 종로구', image: gyeongjuImage, tags: ['#고궁', '#역사'] },
    { id: 2, name: '성산일출봉', location: '제주 서귀포시', image: jejuImage, tags: ['#자연', '#오름'] },
    { id: 3, name: '전주 한옥마을', location: '전북 전주시', image: jeonjuImage, tags: ['#한옥', '#문화'] },
    { id: 4, name: '감천 문화마을', location: '부산 사하구', image: gyeongjuImage, tags: ['#마을', '#예술'] },
    { id: 5, name: '첨성대', location: '경북 경주시', image: jejuImage, tags: ['#유적', '#신라'] },
]);

const heritageItems = ref([
    { id: 1, name: '석굴암과 불국사', location: '경북 경주시', image: gyeongjuImage, category: '세계유산', description: '신라 불교예술의 정수로, 건축, 수리, 기하학, 종교, 예술이 총체적으로 실현된 유산입니다.' },
    { id: 2, name: '해인사 장경판전', location: '경남 합천군', image: jejuImage, category: '세계유산', description: '고려대장경(팔만대장경) 목판을 보관하는 보고로, 자연의 원리를 이용한 보존 과학이 돋보입니다.' },
    { id: 3, name: '종묘', location: '서울 종로구', image: jeonjuImage, category: '세계유산', description: '조선시대 역대 왕과 왕비의 신위를 모시고 제사를 지내는 유교 사당입니다.' },
]);

const toggleLike = (place) => {
  place.liked = !place.liked;
};
const formatPrice = (value) => {
  return value.toLocaleString('ko-KR') + '원';
};
</script>

<style scoped>
:root {
  --text-dark: #222222;
  --text-light: #888888;
  --white: #FFFFFF;
  --border-color: #E5E5E5;
  --blue-accent: #3366FF;
  --red-accent: #FF3B30;
}
.main-container { background-color: var(--white); }
.content-wrapper { max-width: 100%; padding: 0 5%; box-sizing: border-box; }

/* [수정] router-link 기본 스타일 제거 */
.landmark-card-link, .heritage-slide-link {
  text-decoration: none;
  color: inherit;
  display: block; /* 링크 영역을 카드 전체로 확장 */
}
.heritage-slide-link {
  width: 100%;
  height: 100%;
}

.section-header { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  margin-bottom: 30px; 
}
.section-header h3 { 
  font-size: 1.8rem; 
  font-weight: 700; 
  margin: 0; 
}
.more-link { 
  text-decoration: none; 
  color: var(--text-dark); 
  font-weight: 500; 
}

.main-showcase {
  margin-top: -81px;
  position: relative;
  transition: background-color 0.8s ease;
}
.main-slider {
  width: 100%;
  height: 650px;
}
.slide-layout {
  display: flex;
  align-items: center;
  width: 100%;
  height: 100%;
}
.slide-text-content {
  width: 50%;
  height: 100%;
  display: flex; 
  flex-direction: column; 
  justify-content: center;
  padding: 0 5% 0 10%; 
  box-sizing: border-box;
}
.slide-image-content {
  width: 50%;
  height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  padding: 8% 5% 8% 0;
  box-sizing: border-box;
}
.slide-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25);
}
.slide-tag, .slide-title, .slide-link, .slide-image {
  opacity: 0; 
  transform: translateY(30px);
  transition: opacity 0.8s ease, transform 0.8s ease;
}
.swiper-slide-active .slide-tag,
.swiper-slide-active .slide-title,
.swiper-slide-active .slide-link,
.swiper-slide-active .slide-image {
  opacity: 1; 
  transform: translateY(0);
}
.slide-tag {
  display: inline-block; 
  padding: 6px 12px;
  border: 1px solid var(--text-light);
  border-radius: 20px; 
  font-size: 0.9rem; 
  color: var(--text-light);
}
.slide-title { 
  font-size: 3rem; 
  font-weight: 700; 
  margin: 20px 0; 
  line-height: 1.4; 
}
.slide-link { 
  text-decoration: underline; 
  color: var(--text-dark); 
}
.slider-controls-wrapper {
  position: absolute; 
  bottom: 60px; 
  left: 0; 
  width: 50%;
  padding-left: 10%; 
  box-sizing: border-box; 
  z-index: 10;
}
.slider-controls { 
  display: flex; 
  align-items: center; 
  gap: 20px; 
}
.swiper-pagination-custom { 
  width: auto; 
  font-size: 1rem; 
}
.swiper-navigation { 
  display: flex; 
  gap: 1rem; 
}
.swiper-button-prev-custom::after, 
.swiper-button-next-custom::after {
  font-size: 1.5rem; 
  color: var(--text-dark);
}
.swiper-button-prev-custom::after { content: '←'; }
.swiper-button-next-custom::after { content: '→'; }

.recommendations-section { 
  padding: 80px 0; 
}
.recommendations-grid { 
  display: grid; 
  grid-template-columns: repeat(4, 1fr); 
  gap: 25px; 
}
.reco-card { 
  position: relative; 
  overflow: hidden; 
  border-radius: 12px; 
  height: 380px; 
  box-shadow: 0 4px 15px rgba(0,0,0,0.05); 
  cursor: pointer;
  transition: transform 0.2s ease-out;
}
.reco-card:hover { transform: translateY(-5px); }
.reco-card-image { 
  width: 100%; 
  height: 100%; 
  object-fit: cover; 
  transition: transform 0.4s ease; 
}
.reco-card:hover .reco-card-image { transform: scale(1.05); }
.reco-card-overlay { 
  position: absolute; 
  bottom: 0; 
  width: 100%; 
  height: 70%;
  background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, transparent 100%);
}
.reco-card-content { 
  position: absolute; 
  bottom: 20px; 
  left: 20px; 
  right: 20px; 
  color: rgba(255, 255, 255, 0.95);
  z-index: 2;
}
.reco-card-content h4 {
  margin: 5px 0 0;
  font-size: 1.35rem;
  font-weight: 700;
  color: var(--white);
}
.price-info {
  position: absolute;
  top: 15px;
  left: 15px;
  background-color: rgba(0, 0, 0, 0.4); 
  color: var(--white);
  padding: 6px 12px; 
  border-radius: 20px;
  font-size: 0.9rem; 
  font-weight: 600;
  z-index: 3;
}
.like-button {
  position: absolute;
  top: 15px;
  right: 15px;
  background-color: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  padding: 0;
  z-index: 3;
}
.heart-icon { font-size: 1.5rem; }
.heart-icon.empty { color: var(--white); }
.heart-icon.filled { color: var(--red-accent); }

.landmark-section {
  padding: 80px 0;
  background-color: #F8F9FA;
  position: relative;
}
.landmark-slider .swiper-slide { width: 280px; }
.landmark-card {
  background: var(--white);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.landmark-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 25px rgba(0,0,0,0.1);
}
.landmark-image-container {
  width: 100%;
  height: 200px;
  overflow: hidden;
}
.landmark-image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.landmark-info { padding: 20px; }
.landmark-tags span {
  display: inline-block;
  background-color: #f0f0f0;
  color: var(--text-light);
  font-size: 0.8rem;
  padding: 4px 8px;
  border-radius: 4px;
  margin-right: 5px;
}
.landmark-name {
  font-size: 1.2rem;
  font-weight: 700;
  margin: 10px 0 5px 0;
}
.landmark-location {
  font-size: 0.9rem;
  color: var(--text-light);
  margin: 0;
}
.landmark-navigation {
  position: absolute;
  top: 0;
  right: 5%;
  transform: translateY(-150%);
}
.landmark-button-prev, .landmark-button-next {
  display: inline-block;
  width: 40px;
  height: 40px;
  border: 1px solid var(--border-color);
  border-radius: 50%;
  background-color: var(--white);
  margin-left: 10px;
  cursor: pointer;
}
.landmark-button-prev::after, .landmark-button-next::after {
  font-size: 0.9rem;
  color: var(--text-dark);
}
.landmark-button-prev::after { content: '←'; }
.landmark-button-next::after { content: '→'; }

.heritage-section { padding: 80px 0; }
.heritage-slider-wrapper { position: relative; }
.heritage-slider {
  width: 100%;
  height: 450px;
  border-radius: 16px;
  overflow: hidden;
}
.heritage-slide {
  position: relative;
  background-size: cover;
  background-position: center;
}
.slide-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to top, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0.1) 60%, transparent 100%);
}
.heritage-slide-content {
  position: relative;
  z-index: 2;
  max-width: 60%;
}
.heritage-category {
  font-size: 0.9rem;
  font-weight: 700;
  padding: 5px 10px;
  background-color: rgba(0,0,0,0.3);
  border-radius: 20px;
  display: inline-block;
  margin-bottom: 1rem;
}
.heritage-name {
  font-size: 2rem;
  font-weight: 700;
  margin: 0 0 1rem 0;
  text-shadow: 0 2px 5px rgba(0,0,0,0.5);
  color: #FFFFFF;
}
.heritage-description {
  font-size: 1rem;
  line-height: 1.6;
  opacity: 0.95;
  text-shadow: 0 1px 4px rgba(0,0,0,0.7);
  color: rgba(255, 255, 255, 0.95);
}
.heritage-controls {
  position: absolute;
  bottom: 40px;
  right: 40px;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 20px;
}
.heritage-pagination {
  position: static;
  width: auto;
}
.heritage-pagination :deep(.swiper-pagination-bullet) {
  background-color: #fff;
  opacity: 0.5;
}
.heritage-pagination :deep(.swiper-pagination-bullet-active) {
  opacity: 1;
}
.heritage-navigation { display: flex; gap: 10px; }
.heritage-button-prev, .heritage-button-next {
  position: static;
  width: 40px;
  height: 40px;
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 50%;
  margin: 0;
}
.heritage-button-prev::after, .heritage-button-next::after {
  color: #fff;
  font-size: 1rem;
}
</style>