<template>
  <div class="list-page-container">
    <div class="content-wrapper">
      <header class="page-header">
        <h1>인기 랜드마크</h1>
        <p>대한민국의 아름다운 문화재와 명소를 둘러보세요.</p>
      </header>

      <!-- 필터와 정렬 -->
      <div class="filters-and-sort">
        <div class="filters">
          <!-- 지역 선택: 멀티 체크박스 + 스크롤 -->
          <div class="dropdown">
            <button class="filter-btn" @click="toggleRegionDropdown">
              지역 선택
            </button>
            <div v-show="isRegionDropdownOpen" class="dropdown-content">
              <label v-for="region in regionOptions" :key="region" class="checkbox-label">
                <input
                  type="checkbox"
                  :value="region"
                  v-model="selectedRegions"
                />
                {{ region }}
              </label>
            </div>
          </div>

          <!-- 문화재 종류: 멀티 체크박스 + 스크롤(기존 유지) -->
          <div class="dropdown">
            <button class="filter-btn" @click="toggleHeritageDropdown">
              문화재 종류
            </button>
            <div v-show="isHeritageDropdownOpen" class="dropdown-content">
              <label v-for="heritage in heritageOptions" :key="heritage" class="checkbox-label">
                <input
                  type="checkbox"
                  :value="heritage"
                  v-model="selectedHeritage"
                />
                {{ heritage }}
              </label>
            </div>
          </div>
        </div>

        <!-- 정렬 옵션 드롭다운 -->
        <div class="sort-options">
          <select v-model="sortOrder" @change="sortLandmarks">
            <option value="popularity">인기순</option>
            <option value="name">이름순</option>
          </select>
        </div>
      </div>

      <!-- 선택된 필터 칩(지역/문화재 각각 개별 X로 제거) -->
      <div class="selected-filters">
        <template v-for="region in selectedRegions" :key="'r-' + region">
          <span class="selected-item">
            {{ region }}
            <button @click="removeRegion(region)" class="clear-btn" aria-label="지역 제거">X</button>
          </span>
        </template>

        <template v-for="cat in selectedHeritage" :key="'h-' + cat">
          <span class="selected-item">
            {{ cat }}
            <button @click="removeHeritage(cat)" class="clear-btn" aria-label="카테고리 제거">X</button>
          </span>
        </template>

        <button
          v-if="selectedRegions.length || selectedHeritage.length"
          class="clear-all-btn"
          @click="clearAllFilters"
        >
          전체 초기화
        </button>
      </div>

      <!-- 랜드마크 카드들 -->
      <div class="landmarks-grid">
        <div
          v-for="landmark in filteredLandmarks"
          :key="landmark.id"
          class="landmark-card"
          role="button"
          tabindex="0"
          @click="goDetail(landmark)"
          @keyup.enter="goDetail(landmark)"
        >
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

        <!-- 결과 없음 처리 -->
        <div v-if="filteredLandmarks.length === 0" class="empty-state">
          조건에 맞는 랜드마크가 없습니다.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import gyeongjuImage from '@/assets/images/card-gyeongju.jpg';
import jejuImage from '@/assets/images/card-jeju.jpg';
import jeonjuImage from '@/assets/images/card-jeonju.jpg';

const router = useRouter();

// 더미 데이터 (API 연동 시 교체)
const allLandmarks = ref([
  { id: 1, name: '경복궁', location: '서울 종로구', image: gyeongjuImage, tags: ['#고궁', '#역사'], popularity: 5 },
  { id: 2, name: '성산일출봉', location: '제주 서귀포시', image: jejuImage, tags: ['#자연', '#오름'], popularity: 4 },
  { id: 3, name: '전주 한옥마을', location: '전북 전주시', image: jeonjuImage, tags: ['#한옥', '#문화'], popularity: 3 },
  { id: 4, name: '감천 문화마을', location: '부산 사하구', image: gyeongjuImage, tags: ['#마을', '#예술'], popularity: 5 },
  { id: 5, name: '첨성대', location: '경북 경주시', image: jejuImage, tags: ['#유적', '#신라'], popularity: 4 },
  { id: 6, name: '수원 화성', location: '경기 수원시', image: jeonjuImage, tags: ['#성곽', '#유네스코'], popularity: 2 },
  { id: 7, name: '안압지(동궁과 월지)', location: '경북 경주시', image: gyeongjuImage, tags: ['#야경', '#신라'], popularity: 4 },
  { id: 8, name: '남이섬', location: '강원 춘천시', image: jejuImage, tags: ['#자연', '#드라마'], popularity: 5 },
]);

// ▼ 옵션들
const regionOptions = ['서울', '제주', '전주', '부산', '경주', '수원', '춘천']; // 필요시 추가
const heritageOptions = ['고궁', '자연', '문화', '유적']; // 필요시 '#예술' 등 확장 가능

// ▼ 선택 상태
const selectedRegions = ref([]);          // 멀티 지역
const selectedHeritage = ref([]);         // 멀티 카테고리
const sortOrder = ref('popularity');      // 'popularity' | 'name'

// ▼ 드롭다운 상태
const isRegionDropdownOpen = ref(false);
const isHeritageDropdownOpen = ref(false);

// ▼ 상세 이동
const goDetail = (l) => {
  // 라우트 이름이 프로젝트와 다르면 아래를 바꿔줘.
  router.push({ name: 'LandmarkDetail', params: { id: String(l.id) } });
  // 혹은 path 방식:
  // router.push(`/landmarks/${l.id}`);
};

// ▼ 필터링 + 정렬
const filteredLandmarks = computed(() => {
  let landmarks = [...allLandmarks.value];

  // 지역 필터: 하나라도 포함되면 통과(OR)
  if (selectedRegions.value.length > 0) {
    landmarks = landmarks.filter(l =>
      selectedRegions.value.some(r => l.location.includes(r))
    );
  }

  // 문화재 종류 필터: 하나라도 포함되면 통과(OR)
  if (selectedHeritage.value.length > 0) {
    landmarks = landmarks.filter(l =>
      selectedHeritage.value.some(h => l.tags.includes(`#${h}`))
    );
  }

  // 정렬
  if (sortOrder.value === 'popularity') {
    landmarks.sort((a, b) => b.popularity - a.popularity);
  } else if (sortOrder.value === 'name') {
    landmarks.sort((a, b) => a.name.localeCompare(b.name));
  }
  return landmarks;
});

// ▼ 드롭다운 토글
const toggleRegionDropdown = () => {
  isRegionDropdownOpen.value = !isRegionDropdownOpen.value;
  if (isRegionDropdownOpen.value) isHeritageDropdownOpen.value = false;
};
const toggleHeritageDropdown = () => {
  isHeritageDropdownOpen.value = !isHeritageDropdownOpen.value;
  if (isHeritageDropdownOpen.value) isRegionDropdownOpen.value = false;
};

// ▼ 개별 제거 & 초기화
const removeRegion = (region) => {
  selectedRegions.value = selectedRegions.value.filter(r => r !== region);
};
const removeHeritage = (cat) => {
  selectedHeritage.value = selectedHeritage.value.filter(h => h !== cat);
};
const clearAllFilters = () => {
  selectedRegions.value = [];
  selectedHeritage.value = [];
};

// ▼ 정렬 변경 훅(필요시 추가 훅 작업 가능)
const sortLandmarks = () => {
  // computed에서 정렬하므로 여기서는 트리거만 필요(reactivity 유지용)
};
</script>

<style scoped>
/* 기본 레이아웃 */
.list-page-container { padding: 60px 0; }
.content-wrapper { max-width: 1280px; margin: 0 auto; padding: 0 20px; }

.page-header { text-align: center; margin-bottom: 50px; }
.page-header h1 { font-size: 2.5rem; font-weight: 700; margin: 0 0 10px 0; }
.page-header p { font-size: 1.1rem; color: #666; margin: 0; }

.filters-and-sort {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px; /* 칩이 아래에서 붙으니 간격 좁힘 */
}
.filters { display: flex; gap: 10px; justify-content: flex-start; }

/* 버튼 */
.filter-btn {
  background: #f1f1f1; border: 1px solid #ddd; border-radius: 30px;
  padding: 8px 16px; font-size: 0.9rem; cursor: pointer;
}

/* 드롭다운 */
.dropdown { position: relative; }
.dropdown-content {
  position: absolute; background: #fff; border: 1px solid #ddd; border-radius: 8px;
  padding: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  max-height: 220px; overflow-y: auto; z-index: 1000; width: 200px;
}
.checkbox-label { display: flex; align-items: center; gap: 8px; margin: 6px 0; }

/* 선택된 필터 칩 */
.selected-filters {
  display: flex; gap: 10px; flex-wrap: wrap; margin: 12px 0 24px 0;
}
.selected-item {
  background: #f0f0f0; padding: 8px 12px; border-radius: 30px;
  font-size: 0.9rem; display: inline-flex; align-items: center; line-height: 1;
}
.clear-btn {
  background: transparent; border: none; color: #888; cursor: pointer; margin-left: 8px;
}
.clear-btn:hover { color: #333; }
.clear-all-btn {
  background: #eee; border: 1px solid #ddd; border-radius: 20px; padding: 6px 12px; cursor: pointer;
}

/* 정렬 */
.sort-options select {
  border: 1px solid #ddd; border-radius: 8px; padding: 8px 12px; font-size: 0.9rem;
}

/* 카드 그리드 */
.landmarks-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 30px;
}
.landmark-card {
  background: #fff; border-radius: 12px; overflow: hidden;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  transition: transform .3s ease, box-shadow .3s ease; cursor: pointer;
}
.landmark-card:hover { transform: translateY(-8px); box-shadow: 0 12px 25px rgba(0,0,0,0.1); }
.landmark-card:focus { outline: 2px solid #0A2A66; outline-offset: 2px; }
.landmark-image-container { width: 100%; height: 200px; overflow: hidden; }
.landmark-image-container img { width: 100%; height: 100%; object-fit: cover; transition: transform .4s ease; }
.landmark-card:hover img { transform: scale(1.05); }
.landmark-info { padding: 20px; }
.landmark-tags { margin-bottom: 10px; }
.landmark-tags span {
  display: inline-block; background-color: #f0f0f0; color: #888;
  font-size: .8rem; padding: 4px 8px; border-radius: 4px; margin-right: 5px;
}
.landmark-name { font-size: 1.2rem; font-weight: 700; margin: 0 0 5px 0; }
.landmark-location { font-size: .9rem; color: #888; margin: 0; }

/* 결과 없음 */
.empty-state {
  grid-column: 1 / -1; text-align: center; color: #777; padding: 40px 0;
}
</style>
