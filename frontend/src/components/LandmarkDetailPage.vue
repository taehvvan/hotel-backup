<template>
  <div v-if="landmark" class="detail-page-container">
    <div class="content-wrapper">
      <main class="main-content-column">
        <!-- breadcrumbs -->
        <nav class="breadcrumbs" aria-label="Breadcrumb">
          <router-link to="/">홈</router-link>
          <span aria-hidden="true">›</span>
          <router-link to="/landmarks">랜드마크</router-link>
          <span aria-hidden="true">›</span>
          <span>{{ landmark.name }}</span>
        </nav>

        <!-- GALLERY: 메인 이미지 + 썸네일 -->
        <section class="gallery" aria-label="이미지 갤러리">
          <div
            class="hero-image"
            tabindex="0"
            @keydown.left.prevent="prev"
            @keydown.right.prevent="next"
          >
            <button class="nav prev" @click="prev" aria-label="이전 사진">‹</button>
            <img
              class="hero-img"
              :src="gallery[currentIndex].src"
              :alt="gallery[currentIndex].alt"
              decoding="async"
              fetchpriority="high"
            />
            <button class="nav next" @click="next" aria-label="다음 사진">›</button>
          </div>

          <div class="thumbs" ref="thumbsEl" role="listbox" aria-label="썸네일">
            <button
              v-for="(img, i) in gallery"
              :key="img.src + i"
              class="thumb"
              :class="{ active: i === currentIndex }"
              @click="go(i)"
              :aria-selected="i === currentIndex"
              :title="img.alt"
            >
              <img :src="img.src" :alt="img.alt" loading="lazy" decoding="async" />
            </button>
          </div>
        </section>

        <!-- 헤더 -->
        <div class="info-header">
          <div class="tags" v-if="landmark.tags?.length">
            <span v-for="tag in landmark.tags" :key="tag">{{ tag }}</span>
          </div>
          <h1>{{ landmark.name }}</h1>
          <p class="location">📍 {{ landmark.location }}</p>
        </div>

        <!-- 탭: 기본정보 / 이용안내 / 상세정보 -->
        <section class="info-card">
          <div class="tabs">
            <button :class="{active: tab==='basic'}" @click="tab='basic'">기본정보</button>
            <button :class="{active: tab==='guide'}" @click="tab='guide'">이용안내</button>
            <button :class="{active: tab==='detail'}" @click="tab='detail'">상세정보</button>
          </div>

          <table v-if="tab !== 'detail'" class="info-table">
            <tbody>
              <tr v-for="row in (tab==='basic' ? landmark.basic : landmark.guide)" :key="row.label">
                <th scope="row">{{ row.label }}</th>
                <td>{{ row.value }}</td>
              </tr>
            </tbody>
          </table>

          <!-- 상세정보: 더보기/접기 -->
          <div v-else class="detail-wrap">
            <div
              ref="detailRef"
              class="detail-text"
              :class="{ collapsed: !isDetailExpanded }"
            >
              {{ landmark.detail }}
            </div>

            <!-- 하단 그라데이션 (접힌 상태 & 넘칠 때만) -->
            <div
              v-if="!isDetailExpanded && isOverflow"
              class="fade"
              aria-hidden="true"
            ></div>

            <!-- 더보기/접기 버튼 (넘칠 때만 노출) -->
            <div v-if="isOverflow" class="more-wrap">
              <button class="btn-more" @click="toggleExpand">
                {{ isDetailExpanded ? '접기' : '더보기' }}
              </button>
            </div>
          </div>
        </section>

    
      </main>

      <!-- 사이드 -->
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
import { ref, computed, watchEffect, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'

/** 안전한 이미지 경로 (Vite 권장) */
const imgGyeongju = new URL('@/assets/images/card-gyeongju.jpg', import.meta.url).href
const imgJeju     = new URL('@/assets/images/card-jeju.jpg', import.meta.url).href
const imgJeonju   = new URL('@/assets/images/card-jeonju.jpg', import.meta.url).href

const route = useRoute()
const landmark = ref(null)
const currentIndex = ref(0)
const tab = ref('guide')
const thumbsEl = ref(null)

/** 상세 더보기 상태/측정 */
const detailRef = ref(null)
const isOverflow = ref(false)
const isDetailExpanded = ref(false)
const toggleExpand = () => { isDetailExpanded.value = !isDetailExpanded.value }

/** 샘플 데이터 */
const sampleLandmarks = [
  {
    id: '1',
    name: '경복궁',
    location: '서울 종로구',
    image: imgGyeongju,
    images: [
      { src: imgGyeongju, alt: '경복궁 근정전 전경' },
      { src: imgJeonju,   alt: '경복궁 사계절 풍경' },
      { src: imgJeju,     alt: '경복궁 야간 개장' }
    ],
    tags: ['#고궁', '#역사'],
    description: '조선 왕조 제일의 법궁으로, 웅장한 건축미와 아름다운 정원을 거닐며 역사의 숨결을 느껴보세요.',
    basic: [
      { label: '주소', value: '서울특별시 종로구 사직로 161' },
      { label: '홈페이지', value: 'royalpalace.go.kr' }
    ],
    guide: [
      { label: '문의 및 안내', value: '02-3700-3900' },
      { label: '쉬는날', value: '화요일' },
      { label: '이용시간', value: '09:00~18:00(계절별 상이)' }
    ],
    detail: `경복궁은 1395년에 창건된 조선 왕조의 법궁으로 설치된 이후 ... (길게) 
역사·건축·정원·문화재 등 다양한 유산을 품고 있으며, 계절마다 다른 풍경으로 시민과 관광객에게 사랑받는 명소입니다. 
야간 개장 기간에는 조명과 함께 색다른 분위기를 느낄 수 있습니다.`
  },
  {
    id: '2',
    name: '성산일출봉',
    location: '제주 서귀포시',
    image: imgJeju,
    images: [
      { src: imgJeju,   alt: '성산일출봉 전경' },
      { src: imgGyeongju, alt: '성산 둘레길' },
      { src: imgJeonju,  alt: '일출 명소' }
    ],
    tags: ['#자연', '#오름'],
    description: '정상에서 바라보는 일출이 장관. 유네스코 세계자연유산.',
    basic: [
      { label: '주소', value: '제주특별자치도 서귀포시 성산읍' },
      { label: '주차', value: '주차장 보유' }
    ],
    guide: [
      { label: '문의 및 안내', value: '064-000-0000' },
      { label: '이용시간', value: '상시 개방' }
    ],
    detail: `성산일출봉은 약 5천 년 전 바닷속에서 분출한 화산체로 형성되었으며 ...
정상에서 바라보는 일출이 특히 장관으로 알려져 있습니다.`
  },
  {
    id: '3',
    name: '전주 한옥마을',
    location: '전북 전주시',
    image: imgJeonju,
    images: [
      { src: imgJeonju, alt: '전주 한옥마을 길' },
      { src: imgGyeongju, alt: '전통 가옥' }
    ],
    tags: ['#한옥', '#문화'],
    description: '700여 채의 한옥이 군락을 이루는 국내 최대 규모의 한옥촌.',
    basic: [
      { label: '주소', value: '전북 전주시 완산구 기린대로 일대' }
    ],
    guide: [
      { label: '이용시간', value: '상시 개방' }
    ],
    detail: `전주 한옥마을은 전통과 현대가 공존하는 공간으로 ...
전통문화 체험과 다양한 음식, 카페, 전시가 밀집해 있어 여행객에게 인기입니다.`
  },
  {
    id: '5',
    name: '첨성대',
    location: '경북 경주시',
    image: imgJeju,
    images: [
      { src: imgJeju, alt: '첨성대 전경' },
      { src: imgGyeongju, alt: '경주 야경' }
    ],
    tags: ['#유적', '#신라'],
    description: '신라 시대 천문 관측 시설로 동양에서 가장 오래된 천문대.',
    basic: [
      { label: '주소', value: '경북 경주시 인왕동 839-1' }
    ],
    guide: [
      { label: '이용시간', value: '상시 개방' }
    ],
    detail: `첨성대는 통일신라 이전에 축조된 석조 건축물로 ...
야간 조명과 함께 고즈넉한 분위기를 자아내며 많은 이들이 찾는 명소입니다.
야간 조명과 함께 고즈넉한 분위기를 자아내며 많은 이들이 찾는 명소입니다
야간 조명과 함께 고즈넉한 분위기를 자아내며 많은 이들이 찾는 명소입니다
야간 조명과 함께 고즈넉한 분위기를 자아내며 많은 이들이 찾는 명소입니다
야간 조명과 함께 고즈넉한 분위기를 자아내며 많은 이들이 찾는 명소입니다
야간 조명과 함께 고즈넉한 분위기를 자아내며 많은 이들이 찾는 명소입니다
야간 조명과 함께 고즈넉한 분위기를 자아내며 많은 이들이 찾는 명소입니다
야간 조명과 함께 고즈넉한 분위기를 자아내며 많은 이들이 찾는 명소입니다
야간 조명과 함께 고즈넉한 분위기를 자아내며 많은 이들이 찾는 명소입니다
야간 조명과 함께 고즈넉한 분위기를 자아내며 많은 이들이 찾는 명소입니다
야간 조명과 함께 고즈넉한 분위기를 자아내며 많은 이들이 찾는 명소입니다
야간 조명과 함께 고즈넉한 분위기를 자아내며 많은 이들이 찾는 명소입니다`

  }
]

/** 현재 라우트 id에 맞는 랜드마크 찾기 */
watchEffect(() => {
  const id = String(route.params.id ?? '')
  landmark.value = sampleLandmarks.find(x => x.id === id) ?? null
  currentIndex.value = 0
  tab.value = 'guide'
  isDetailExpanded.value = false
  nextTick(() => {
    thumbsEl.value?.scrollTo({ left: 0 })
    measureOverflow()
  })
})

/** 갤러리 소스 (없으면 단일 이미지로 fallback) */
const gallery = computed(() => {
  if (!landmark.value) return []
  return landmark.value.images?.length
    ? landmark.value.images
    : [{ src: landmark.value.image, alt: landmark.value.name }]
})

/** 갤러리 이동 */
const go = (i) => {
  const len = gallery.value.length
  if (!len) return
  currentIndex.value = (i + len) % len
  scrollActiveThumbIntoView()
}
const prev = () => go(currentIndex.value - 1)
const next = () => go(currentIndex.value + 1)

const scrollActiveThumbIntoView = () => {
  const wrap = thumbsEl.value
  if (!wrap) return
  const active = wrap.querySelector('.thumb.active')
  if (!active) return
  const aw = active.offsetWidth
  const al = active.offsetLeft
  const vw = wrap.clientWidth
  const sl = wrap.scrollLeft
  if (al < sl) wrap.scrollTo({ left: al - 8, behavior: 'smooth' })
  else if (al + aw > sl + vw) wrap.scrollTo({ left: al - vw + aw + 8, behavior: 'smooth' })
}

/** 상세 탭이 열릴 때/리사이즈 때 overflow 여부 측정 */
const measureOverflow = () => {
  const el = detailRef.value
  if (!el) { isOverflow.value = false; return }
  // 접힌 상태에서 실제로 넘치는지 확인
  const wasExpanded = isDetailExpanded.value
  isDetailExpanded.value = false
  nextTick(() => {
    isOverflow.value = el.scrollHeight > el.clientHeight + 1
    // 원래 확장 상태였다면 복구
    isDetailExpanded.value = wasExpanded
  })
}

watchEffect(() => {
  if (tab.value === 'detail') nextTick(measureOverflow)
})

const onResize = () => { if (tab.value === 'detail') measureOverflow() }
onMounted(() => window.addEventListener('resize', onResize))
onBeforeUnmount(() => window.removeEventListener('resize', onResize))
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700;800&display=swap');

.detail-page-container { font-family: 'Noto Sans KR', sans-serif; background-color: #fff; padding: 40px 0; }
.content-wrapper { max-width: 1200px; margin: 0 auto; padding: 0 20px;
  display: grid; grid-template-columns: 1fr 350px; gap: 40px; align-items: flex-start; }
.main-content-column, .sidebar-column { min-width: 0; }

.breadcrumbs { font-size: 0.9rem; color: #888; margin-bottom: 18px; display: flex; align-items: center; gap: 8px; }
.breadcrumbs a { color: #888; text-decoration: none; }
.breadcrumbs a:hover { text-decoration: underline; }
.breadcrumbs span:last-child { font-weight: 500; color: #333; }

/* Gallery */
.gallery { margin-bottom: 22px; }
.hero-image { position: relative; width: 100%; height: clamp(208px, 38.4vw, 360px); /* 20% 축소 반영 */
  border-radius: 16px; overflow: hidden; outline: none; }
.hero-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.nav { position: absolute; top: 50%; transform: translateY(-50%); z-index: 2;
  width: 42px; height: 42px; border-radius: 50%; border: none;
  background: rgba(0,0,0,0.45); color: #fff; font-size: 24px; cursor: pointer; }
.prev { left: 12px; } .next { right: 12px; } .nav:hover { background: rgba(0,0,0,0.6); }

.thumbs { margin-top: 12px; display: flex; gap: 8px; overflow-x: auto; padding-bottom: 4px; scrollbar-width: thin; }
.thumb { flex: 0 0 auto; width: 90px; height: 68px; border-radius: 8px; overflow: hidden; padding: 0;
  border: 2px solid transparent; background: #fff; cursor: pointer; }
.thumb img { width: 100%; height: 100%; object-fit: cover; filter: grayscale(30%); opacity: .9; transition: .2s; }
.thumb:hover img { filter: none; opacity: 1; transform: scale(1.02); }
.thumb.active { border-color: #0A2A66; }
.thumb.active img { filter: none; opacity: 1; }

.info-header { margin: 6px 0 22px; }
.tags { margin-bottom: 12px; display: flex; gap: 8px; flex-wrap: wrap; }
.tags span { display: inline-block; background-color: #f0f0f0; color: #888; font-size: 0.9rem; padding: 6px 12px; border-radius: 20px; }
h1 { font-size: 2.4rem; font-weight: 800; margin: 0 0 8px; color: #222; line-height: 1.2; }
.location { font-size: 1.05rem; color: #555; font-weight: 500; }

/* Tabs */
.info-card { background: #fff; border: 1px solid #E5E5E5; border-radius: 12px; padding: 16px; margin-bottom: 24px; }
.tabs { display: flex; gap: 6px; margin-bottom: 12px; flex-wrap: wrap; }
.tabs button { border: 1px solid #d6d6d6; background: #f9f9f9; color: #333;
  padding: 8px 14px; border-radius: 999px; cursor: pointer; font-weight: 600; }
.tabs button.active { background: #0A2A66; border-color: #0A2A66; color: #fff; }

.info-table { width: 100%; border-collapse: collapse; }
.info-table th, .info-table td { border-bottom: 1px solid #eee; padding: 10px 8px; text-align: left; }
.info-table th { width: 28%; color: #6b7280; font-weight: 600; background: #fafafa; }

/* 상세 더보기 */
.detail-wrap { position: relative; }
.detail-text { line-height: 1.7; white-space: pre-line; color: #444; transition: max-height .25s ease; }
.detail-text.collapsed { max-height: 7.2em; /* 대략 4~5줄 */ overflow: hidden; }
.fade {
  position: absolute; left: 0; right: 0; bottom: 42px; height: 48px;
  background: linear-gradient(180deg, rgba(255,255,255,0) 0%, #fff 70%);
  pointer-events: none;
}
.more-wrap { display: flex; justify-content: flex-end; margin-top: 8px; }
.btn-more {
  background: #0A2A66; color: #fff; border: none; border-radius: 18px;
  padding: 6px 14px; font-weight: 700; cursor: pointer;
}
.btn-more:hover { filter: brightness(0.96); }

/* 소개 */
.description-section h2 { font-size: 1.4rem; font-weight: 700; margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px solid #eee; }
.description-section p { font-size: 1.05rem; line-height: 1.8; color: #444; }

.sticky-sidebar { position: sticky; top: 100px; }
.nearby-hotel-card { background-color: #F8F9FA; border: 1px solid #E5E5E5; border-radius: 12px; padding: 22px; text-align: center; }
.nearby-hotel-card h3 { font-size: 1.3rem; margin: 0 0 8px; }
.nearby-hotel-card p { font-size: 0.98rem; color: #666; margin-bottom: 18px; }
.btn-find-hotels { display: block; width: 100%; background-color: #0A2A66; color: #fff; border: none;
  border-radius: 8px; padding: 14px; font-size: 1.05rem; font-weight: 700; cursor: pointer; text-decoration: none; }

.loading-container { display: flex; justify-content: center; align-items: center; height: 50vh; }

/* 반응형 */
@media (max-width: 992px) {
  .content-wrapper { grid-template-columns: 1fr; gap: 28px; }
  .sticky-sidebar { position: static; }
}
</style>
