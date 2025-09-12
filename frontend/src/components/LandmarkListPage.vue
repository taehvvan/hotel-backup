<template>
    <div class="list-page-container">
      <div class="content-wrapper">
        <header class="page-header">
          <h1>인기 랜드마크</h1>
          <p>대한민국의 아름다운 문화재와 명소를 둘러보세요.</p>
        </header>
  
        <div class="filters-and-sort">
          <div class="filters">
            <button class="filter-btn">지역 선택</button>
            <button class="filter-btn">문화재 종류</button>
          </div>
          <div class="sort-options">
            <select>
              <option>인기순</option>
              <option>이름순</option>
            </select>
          </div>
        </div>
  
        <div class="landmarks-grid">
          <div v-for="landmark in allLandmarks" :key="landmark.id" class="landmark-card">
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
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  // 실제 앱에서는 API를 통해 더 많은 랜드마크 목록을 불러와야 합니다.
  import gyeongjuImage from '@/assets/images/card-gyeongju.jpg';
  import jejuImage from '@/assets/images/card-jeju.jpg';
  import jeonjuImage from '@/assets/images/card-jeonju.jpg';
  
  const allLandmarks = ref([
      { id: 1, name: '경복궁', location: '서울 종로구', image: gyeongjuImage, tags: ['#고궁', '#역사'] },
      { id: 2, name: '성산일출봉', location: '제주 서귀포시', image: jejuImage, tags: ['#자연', '#오름'] },
      { id: 3, name: '전주 한옥마을', location: '전북 전주시', image: jeonjuImage, tags: ['#한옥', '#문화'] },
      { id: 4, name: '감천 문화마을', location: '부산 사하구', image: gyeongjuImage, tags: ['#마을', '#예술'] },
      { id: 5, name: '첨성대', location: '경북 경주시', image: jejuImage, tags: ['#유적', '#신라'] },
      { id: 6, name: '수원 화성', location: '경기 수원시', image: jeonjuImage, tags: ['#성곽', '#유네스코'] },
      { id: 7, name: '안압지(동궁과 월지)', location: '경북 경주시', image: gyeongjuImage, tags: ['#야경', '#신라'] },
      { id: 8, name: '남이섬', location: '강원 춘천시', image: jejuImage, tags: ['#자연', '#드라마'] },
  ]);
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
  .landmarks-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 30px;
  }
  .landmark-card {
    background: #FFFFFF;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 15px rgba(0,0,0,0.05);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    cursor: pointer;
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
    transition: transform 0.4s ease;
  }
  .landmark-card:hover img {
    transform: scale(1.05);
  }
  .landmark-info {
    padding: 20px;
  }
  .landmark-tags {
    margin-bottom: 10px;
  }
  .landmark-tags span {
    display: inline-block;
    background-color: #f0f0f0;
    color: #888;
    font-size: 0.8rem;
    padding: 4px 8px;
    border-radius: 4px;
    margin-right: 5px;
  }
  .landmark-name {
    font-size: 1.2rem;
    font-weight: 700;
    margin: 0 0 5px 0;
  }
  .landmark-location {
    font-size: 0.9rem;
    color: #888;
    margin: 0;
  }
  </style>