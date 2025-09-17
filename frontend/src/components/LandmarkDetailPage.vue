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
              :src="gallery[currentIndex]?.src"
              :alt="gallery[currentIndex]?.alt || landmark.name"
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
/**
 * Excel(XLSX) → 화면 렌더까지 풀 파이프라인
 * - /public/data 안의 여러 xlsx를 순회 로드
 * - 컬럼 이름 유연 매핑(한글/영문 혼용, basic:/guide: 확장)
 * - 이미지: URL 또는 /public/images/파일명.jpg 자동 처리
 */
import { ref, computed, watchEffect, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import * as XLSX from 'xlsx'

const route = useRoute()

/** 상태 */
const landmarks = ref([])
const landmark = ref(null)
const currentIndex = ref(0)
const tab = ref('guide')
const thumbsEl = ref(null)
const isLoading = ref(true)

/** 상세 더보기 상태/측정 */
const detailRef = ref(null)
const isOverflow = ref(false)
const isDetailExpanded = ref(false)
const toggleExpand = () => { isDetailExpanded.value = !isDetailExpanded.value }

/** ========== 유틸 ========== */
/** 문자열 분리: , ; | 구분자 모두 허용 */
const splitList = (v) => {
  if (v == null) return []
  return String(v).split(/[;,|]/).map(s => s.trim()).filter(Boolean)
}
/** 이미지 경로 처리: 절대/상대/파일명 → 최종 URL */
const resolveImage = (p) => {
  if (!p) return ''
  const s = String(p).trim()
  if (s.startsWith('http://') || s.startsWith('https://') || s.startsWith('/')) return s
  // 파일명만 왔을 때는 /public/images 밑에서 찾는다.
  return `/images/${s}`
}
/** 태그 전처리: # 없으면 붙여준다 */
const normalizeTags = (arr) =>
  arr.map(t => t.startsWith('#') ? t : `#${t}`)

/** 한 행 → 랜드마크 객체 매핑 */
const mapRowToLandmark = (r) => {
  const name = r.name || r.Name || r.이름 || '이름없음'

  // 이미지: images(다중) 우선, 없으면 image(단일)
  const imageList = splitList(r.images ?? r.Images ?? r.이미지 ?? r.이미지들 ?? '')
  const images = imageList.length
    ? imageList.map(src => ({ src: resolveImage(src), alt: `${name} 사진` }))
    : ((r.image || r.Image || r.대표이미지) ? [{ src: resolveImage(r.image || r.Image || r.대표이미지), alt: name }] : [])

  // 기본정보 / 이용안내 표
  const basic = []
  const guide = []
  // 고정 필드(있으면 자동 주입)
  if (r.basic_address || r.주소) basic.push({ label: '주소', value: r.basic_address || r.주소 })
  if (r.basic_homepage || r.홈페이지) basic.push({ label: '홈페이지', value: r.basic_homepage || r.홈페이지 })
  if (r.guide_phone || r.문의 || r.문의번호) guide.push({ label: '문의 및 안내', value: r.guide_phone || r.문의 || r.문의번호 })
  if (r.guide_closed || r.쉬는날) guide.push({ label: '쉬는날', value: r.guide_closed || r.쉬는날 })
  if (r.guide_hours || r.이용시간) guide.push({ label: '이용시간', value: r.guide_hours || r.이용시간 })

  // 자유 확장: basic:라벨 / guide:라벨
  Object.keys(r).forEach(k => {
    const low = k.toLowerCase()
    if (low.startsWith('basic:')) basic.push({ label: k.slice(6).trim(), value: r[k] })
    if (low.startsWith('guide:')) guide.push({ label: k.slice(6).trim(), value: r[k] })
  })

  return {
    id: String(r.id ?? r.ID ?? r.아이디 ?? r.No ?? ''),
    name,
    location: r.location ?? r.Location ?? r.지역 ?? '',
    image: images[0]?.src || '',
    images,
    tags: normalizeTags(splitList(r.tags ?? r.Tags ?? r.태그 ?? '')),
    description: r.description ?? r.Description ?? r.소개 ?? '',
    basic,
    guide,
    detail: r.detail ?? r.Detail ?? r.상세 ?? ''
  }
}

/** ========== 엑셀 로딩 ========== */
/** 개별 파일 로드 → JSON 배열 */
const fetchSheet = async (url) => {
  try {
    const res = await fetch(url)
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    const buf = await res.arrayBuffer()
    const wb = XLSX.read(buf, { type: 'array' })
    const ws = wb.Sheets[wb.SheetNames[0]]
    return XLSX.utils.sheet_to_json(ws, { defval: '' })
  } catch (e) {
    // 파일이 없을 수도 있으니 조용히 빈 배열 반환
    console.warn('[엑셀 로딩 스킵]', url, e?.message || e)
    return []
  }
}

/** 여러 파일 합쳐서 landmarks 구성 */
const DATA_FILES = [
  // 필요에 맞게 수정해서 /public/data 에 배치
  '/data/관광명소 안보관광.xlsx',
  '/data/관광명소 문.xlsx',
  // '/data/landmarks.xlsx', // 통합본을 쓸 거라면 이 한 줄만 남겨도 됨
]

const loadAllExcels = async () => {
  isLoading.value = true
  try {
    let rows = []
    for (const f of DATA_FILES) {
      const part = await fetchSheet(f)
      rows = rows.concat(part)
    }
    landmarks.value = rows.map(mapRowToLandmark).filter(x => x.id && x.name)
  } finally {
    isLoading.value = false
  }
}

/** 초기 로딩 */
onMounted(loadAllExcels)

/** 라우트 id에 맞는 랜드마크 선택 */
watchEffect(() => {
  const id = String(route.params.id ?? '')
  landmark.value =
    landmarks.value.find(x => String(x.id) === id) ??
    landmarks.value[0] ??
    null

  currentIndex.value = 0
  tab.value = 'guide'
  isDetailExpanded.value = false
  nextTick(() => {
    thumbsEl.value?.scrollTo({ left: 0 })
    measureOverflow()
  })
})

/** 갤러리 소스 (없으면 빈 배열) */
const gallery = computed(() => {
  if (!landmark.value) return []
  return landmark.value.images?.length
    ? landmark.value.images
    : (landmark.value.image ? [{ src: landmark.value.image, alt: landmark.value.name }] : [])
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
.hero-image { position: relative; width: 100%; height: clamp(208px, 38.4vw, 360px);
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
