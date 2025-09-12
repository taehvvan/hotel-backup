<template>
    <div v-if="landmark" class="detail-page-container">
      <div class="content-wrapper">
        <main class="main-content-column">
          <nav class="breadcrumbs">
            <router-link to="/">홈</router-link>
            <span>›</span>
            <router-link to="/landmarks">랜드마크</router-link>
            <span>›</span>
            <span>{{ landmark.name }}</span>
          </nav>
  
          <div class="hero-image">
            <img :src="landmark.image" :alt="landmark.name">
          </div>
  
          <div class="info-header">
            <div class="tags" v-if="landmark.tags">
              <span v-for="tag in landmark.tags" :key="tag">{{ tag }}</span>
            </div>
            <h1>{{ landmark.name }}</h1>
            <p class="location">📍 {{ landmark.location }}</p>
          </div>
  
          <section class="description-section">
            <h2>소개</h2>
            <p>{{ landmark.description }}</p>
          </section>
        </main>
  
        <aside class="sidebar-column">
          <div class="sticky-sidebar">
            <div class="nearby-hotel-card">
              <h3>근처 숙소 찾아보기</h3>
              <p>'{{ landmark.location }}' 근처의 멋진 숙소들을 둘러보세요.</p>
              <router-link 
                :to="{ name: 'SearchResult', query: { destination: landmark.location } }"
                class="btn-find-hotels"
              >
                숙소 검색하기
              </router-link>
            </div>
          </div>
        </aside>
      </div>
    </div>
    <div v-else class="loading-container">
      <p>데이터를 불러오는 중입니다...</p>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, watch } from 'vue';
  import { useRoute } from 'vue-router';
  
  const route = useRoute();
  const landmark = ref(null);
  
  const sampleLandmarks = [
    { id: '1', name: '경복궁', location: '서울 종로구', image: '/src/assets/images/card-gyeongju.jpg', tags: ['#고궁', '#역사'], description: '조선 왕조 제일의 법궁으로, 서울의 중심에 위치한 대한민국의 대표적인 문화유산입니다. 웅장한 건축미와 아름다운 정원을 거닐며 역사의 숨결을 느껴보세요.' },
    { id: '2', name: '성산일출봉', location: '제주 서귀포시', image: '/src/assets/images/card-jeju.jpg', tags: ['#자연', '#오름'], description: '제주도의 동쪽 끝에 위치한 화산체로, 정상에서 바라보는 일출이 장관을 이룹니다. 유네스코 세계자연유산으로 지정된 제주의 상징적인 명소입니다.' },
    { id: '3', name: '전주 한옥마을', location: '전북 전주시', image: '/src/assets/images/card-jeonju.jpg', tags: ['#한옥', '#문화'], description: '700여 채의 한옥이 군락을 이루고 있는 국내 최대 규모의 한옥촌입니다. 전통 생활문화를 체험하고 다양한 먹거리를 즐길 수 있습니다.' },
    { id: '4', name: '감천 문화마을', location: '부산 사하구', image: '/src/assets/images/card-gyeongju.jpg', tags: ['#마을', '#예술'], description: '계단식 주거 형태와 파스텔톤의 아름다운 건물들이 조화를 이루는 곳으로, 골목마다 예술 작품이 숨어있어 방문객들에게 특별한 경험을 선사합니다.' },
    { id: '5', name: '첨성대', location: '경북 경주시', image: '/src/assets/images/card-jeju.jpg', tags: ['#유적', '#신라'], description: '신라 시대에 천문 관측을 위해 만들어진 건축물로, 동양에서 가장 오래된 천문대로 알려져 있습니다.' },
  ];
  
  const fetchData = () => {
    const id = route.params.id;
    landmark.value = sampleLandmarks.find(item => item.id === id);
  };
  
  onMounted(fetchData);
  watch(() => route.params.id, fetchData);
  </script>
  
  <style scoped>
  /* 이전과 동일한 스타일 */
  @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700;800&display=swap');
  .detail-page-container { font-family: 'Noto Sans KR', sans-serif; background-color: #fff; padding: 40px 0; }
  .content-wrapper { max-width: 1200px; margin: 0 auto; padding: 0 20px; display: grid; grid-template-columns: 1fr 350px; gap: 40px; align-items: flex-start; }
  .main-content-column, .sidebar-column { min-width: 0; }
  .breadcrumbs { font-size: 0.9rem; color: #888; margin-bottom: 25px; display: flex; align-items: center; gap: 8px; }
  .breadcrumbs a { color: #888; text-decoration: none; }
  .breadcrumbs a:hover { text-decoration: underline; }
  .breadcrumbs span:last-child { font-weight: 500; color: #333; }
  .hero-image { width: 100%; height: 450px; border-radius: 16px; overflow: hidden; margin-bottom: 30px; }
  .hero-image img { width: 100%; height: 100%; object-fit: cover; }
  .info-header { margin-bottom: 30px; }
  .tags { margin-bottom: 15px; display: flex; gap: 8px; }
  .tags span { display: inline-block; background-color: #f0f0f0; color: #888; font-size: 0.9rem; padding: 6px 12px; border-radius: 20px; }
  h1 { font-size: 2.8rem; font-weight: 800; margin: 0 0 10px 0; color: #222; }
  .location { font-size: 1.1rem; color: #555; font-weight: 500; }
  .description-section h2 { font-size: 1.6rem; font-weight: 700; margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px solid #eee; }
  .description-section p { font-size: 1.1rem; line-height: 1.8; color: #444; }
  .sticky-sidebar { position: sticky; top: 100px; }
  .nearby-hotel-card { background-color: #F8F9FA; border: 1px solid #E5E5E5; border-radius: 12px; padding: 25px; text-align: center; }
  .nearby-hotel-card h3 { font-size: 1.4rem; margin: 0 0 10px 0; }
  .nearby-hotel-card p { font-size: 1rem; color: #666; margin-bottom: 25px; }
  .btn-find-hotels { display: block; width: 100%; background-color: #0A2A66; color: #fff; border: none; border-radius: 8px; padding: 15px; font-size: 1.1rem; font-weight: 600; cursor: pointer; text-decoration: none; }
  .loading-container { display: flex; justify-content: center; align-items: center; height: 50vh; }
  </style>