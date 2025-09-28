<template>
  <div class="manager-dashboard">
    <aside class="manager-sidebar">
      <div class="sidebar-header">
        <a @click="goHome" class="logo">쉼, 한국</a>
        <p class="manager-mode">호텔 매니저</p>
      </div>
      <nav class="sidebar-nav">
        <a @click="setActiveView('dashboard')" :class="{ active: activeView === 'dashboard' }">📈 매출 관리</a>
        <a @click="setActiveView('reservations')" :class="{ active: activeView === 'reservations' }">📅 예약 관리</a>
        <a @click="setActiveView('accommodation')" :class="{ active: activeView === 'accommodation' }">🏨 숙소/객실 관리</a>
        <a @click="setActiveView('reviews')" :class="{ active: activeView === 'reviews' }">✍️ 리뷰 관리</a>
        <a @click="setActiveView('account')" :class="{ active: activeView === 'account' }">👤 계정 관리</a>
      </nav>
      <div class="sidebar-footer">
        <button class="btn-logout">로그아웃</button>
      </div>
    </aside>

    <main class="manager-content">
      <section v-if="activeView === 'dashboard'" class="content-section">
        <header class="content-header">
          <h1>매출 및 예약 현황</h1>
          <p>기간별 매출과 오늘의 예약 현황을 확인합니다.</p>
        </header>
        
        <div class="filter-controls">
          <button v-for="type in salesFilterTypes" :key="type.key"
                  @click="salesFilterType = type.key"
                  :class="{ active: salesFilterType === type.key }">
            {{ type.text }}
          </button>
        </div>

        <div class="dashboard-grid">
          <div class="card metric-card" :class="{ active: selectedMetric === 'total' }" @click="selectedMetric = 'total'">
            <h4>총 매출</h4>
            <p class="metric">{{ filteredSales.total.toLocaleString() }}원</p>
          </div>
          <div class="card metric-card" :class="{ active: selectedMetric === 'monthly' }" @click="selectedMetric = 'monthly'">
            <h4>월간 매출 ({{ salesFilterType === 'all' ? '전체' : salesFilterType }})</h4>
            <p class="metric">{{ filteredSales.monthly.toLocaleString() }}원</p>
          </div>
          <div class="card metric-card" :class="{ active: selectedMetric === 'daily' }" @click="selectedMetric = 'daily'">
            <h4>일일 매출 ({{ salesFilterType === 'all' ? '전체' : salesFilterType }})</h4>
            <p class="metric">{{ filteredSales.daily.toLocaleString() }}원</p>
          </div>
          <div class="card metric-card" @click="isBookingModalVisible = true">
            <h4>오늘 신규 예약</h4>
            <p class="metric">{{ filteredSales.todayBookings }}건</p>
          </div>
          <div class="card metric-card alert-metric">
            <h4>총 예약 취소</h4>
            <p class="metric">{{ canceledBookingsCount }}건</p>
          </div>
        </div>
        <div class="card chart-card">
          <h4>{{ chartTitle }}</h4>
          <div style="height: 350px;">
            <Bar v-if="chartData.labels.length" :data="chartData" :options="chartOptions" />
          </div>
        </div>
      </section>

      <section v-if="activeView === 'reservations'" class="content-section">
        <header class="content-header">
          <h1>예약 관리</h1>
          <p>모든 숙소의 예약 현황을 확인하고 관리합니다.</p>
        </header>

        <div class="card search-card">
            <div class="search-controls">
                <select v-model="searchType" class="search-select">
                    <option value="guestName">고객 이름</option>
                    <option value="id">예약 번호</option>
                </select>
                <input type="text" v-model="searchQuery" :placeholder="searchPlaceholder" class="search-input">
            </div>
            <div class="status-filter-controls">
                <button @click="statusFilter = 'all'" :class="{ active: statusFilter === 'all' }">전체</button>
                <button v-for="option in statusOptions" :key="option.code"
                        @click="statusFilter = option.code"
                        :class="{ active: statusFilter === option.code }">
                    {{ option.text }}
                </button>
            </div>
        </div>

        <div class="card">
          <div class="filter-tabs">
            <button @click="userTypeFilter = 'all'" :class="{ active: userTypeFilter === 'all' }">
              전체 예약 ({{ filteredBySearchAndStatus.length }})
            </button>
            <button @click="userTypeFilter = 'member'" :class="{ active: userTypeFilter === 'member' }">
              회원 예약 ({{ memberReservations.length }})
            </button>
            <button @click="userTypeFilter = 'guest'" :class="{ active: userTypeFilter === 'guest' }">
              비회원 예약 ({{ guestReservations.length }})
            </button>
          </div>

          <div class="table-responsive">
            <table>
              <thead>
                <tr>
                  <th>예약 번호</th>
                  <th>고객명</th>
                  <th>숙소명</th>
                  <th>객실명</th>
                  <th>체크인</th>
                  <th>체크아웃</th>
                  <th>상태</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="finalFilteredReservations.length === 0">
                  <td colspan="7" class="no-results">해당하는 예약 내역이 없습니다.</td>
                </tr>
                <tr v-for="booking in finalFilteredReservations" :key="booking.orderId">
                  <td>{{ booking.orderId }}</td>
                  <td>
                    <span :class="['user-type-badge', booking.isMember ? 'member' : 'guest']">
                      {{ booking.isMember ? '회원' : '비회원' }}
                    </span>
                    {{ booking.guestName }}
                  </td>
                  <td>{{ booking.hotelName }}</td>
                  <td>{{ booking.roomName }}</td>
                  <td>{{ booking.checkIn }}</td>
                  <td>{{ booking.checkOut }}</td>
                  <td>
                    <span :class="['status-badge', `status-${booking.status.code}`]">{{ booking.status.text }}</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>

      <section v-if="activeView === 'accommodation'" class="content-section">
        <div v-if="accommodationView === 'typeSelection'">
          <header class="content-header">
            <h1>숙소 유형 선택</h1>
            <p>관리할 숙소의 유형을 선택해주세요.</p>
          </header>
          <div class="property-type-grid">
            <div class="property-type-card" @click="selectPropertyType('호텔')">
              <span class="icon">🏨</span>
              <h3>호텔</h3>
              <p>{{ getPropertyCount('호텔') }}개 등록됨</p>
            </div>
            <div class="property-type-card" @click="selectPropertyType('모텔')">
              <span class="icon">🏩</span>
              <h3>모텔</h3>
              <p>{{ getPropertyCount('모텔') }}개 등록됨</p>
            </div>
            <div class="property-type-card" @click="selectPropertyType('펜션')">
              <span class="icon">🏡</span>
              <h3>펜션 & 풀빌라</h3>
              <p>{{ getPropertyCount('펜션') }}개 등록됨</p>
            </div>
            <div class="property-type-card" @click="selectPropertyType('게스트하우스')">
                <span class="icon">🧑‍🤝‍🧑</span>
                <h3>게스트하우스</h3>
                <p>{{ getPropertyCount('게스트하우스') }}개 등록됨</p>
            </div>
            <div class="property-type-card" @click="selectPropertyType('한옥')">
              <span class="icon">🏯</span>
              <h3>한옥</h3>
              <p>{{ getPropertyCount('한옥') }}개 등록됨</p>
            </div>
            <div class="property-type-card" @click="selectPropertyType('관광호텔')">
              <span class="icon">🏢</span>
              <h3>관광호텔</h3>
              <p>{{ getPropertyCount('관광호텔') }}개 등록됨</p>
            </div>
          </div>
        </div>

        <div v-if="accommodationView === 'list'">
          <header class="content-header with-back-button">
            <button @click="accommodationView = 'typeSelection'" class="btn-back">‹ 뒤로</button>
            <div>
              <h1>{{ selectedPropertyType }} 목록</h1>
              <p>등록된 숙소를 확인하고 관리합니다.</p>
            </div>
          </header>
          <div class="property-list">
            <div v-for="prop in filteredProperties" :key="prop.id" class="card property-card" @click="editProperty(prop)">
              <img 
                :src="prop.image || 'https://placehold.co/400x200?text=No+Image'" 
                class="property-image" 
                alt="숙소 대표 이미지"
              >
              <div class="property-info">
                <h4>{{ prop.name }}</h4>
                <p>{{ prop.location }}</p>
              </div>
              <div class="property-manage-footer">
                <button class="btn-manage" @click="editProperty(prop)">관리</button>
                <button class="btn-delete" @click="deleteProperty(prop.id, prop.name)">삭제</button>
              </div>
            </div>
            <div class="card property-card add-new-card" @click="addNewProperty">
              <span class="add-icon">+</span>
              <h4>새 {{ selectedPropertyType }} 추가하기</h4>
            </div>
          </div>
        </div>
        
        <div v-if="accommodationView === 'edit' && editableHotel" class="edit-form-wrapper">
          <header class="content-header with-back-button">

            <button @click="accommodationView = 'list'" class="btn-back">‹ 목록으로</button>
            <div>
              <h1>{{ editFormTitle }}</h1>
            </div>
          </header>
          
          <div class="edit-form-layout">
            <div class="form-main">
              <div class="card">
                <h4>사진 관리</h4>
                <div class="photo-management-grid">
                  <div class="main-photo">
                    <img :src="editableHotel.images && editableHotel.images[0]" v-if="editableHotel.images && editableHotel.images[0]">
                    <div v-else class="photo-placeholder">+</div>
                    <button class="btn-photo-edit" @click="triggerFileInput('main', 0)">수정</button>
                  </div>
                  <div class="sub-photo" v-for="i in 4" :key="i">
                    <img :src="editableHotel.images && editableHotel.images[i]" v-if="editableHotel.images && editableHotel.images[i]">
                    <div v-else class="photo-placeholder">+</div>
                     <button class="btn-photo-edit" @click="triggerFileInput('sub', i)">수정</button>
                    </div>
                  </div>
              </div>
              
              <div class="card">
                <h4>기본 정보</h4>
                <div class="form-grid">
                  <div class="form-group"><label>숙소 유형</label><input type="text" :value="selectedPropertyType" disabled></div>
                  <div class="form-group" v-if="selectedPropertyType === '호텔' || selectedPropertyType === '관광호텔'"><label>호텔 성급</label><input type="number" v-model.number="editableHotel.stars" min="1" max="5"></div>
                  <div class="form-group full-width"><label>숙소 이름 <span class="required">*</span></label><input type="text" v-model="editableHotel.name"></div>
                  <div class="form-group full-width"><label>숙소 위치 <span class="required">*</span></label><input type="text" v-model="editableHotel.location"></div>
                  <div class="form-group"><label>위도</label><input type="text" v-model="editableHotel.latitude" placeholder="예: 37.5665"></div>
                  <div class="form-group"><label>경도</label><input type="text" v-model="editableHotel.longitude" placeholder="예: 126.9780"></div>
                </div>
              </div>
              
              <div class="card">
                <h4>객실 관리</h4>
                <div v-for="(room, index) in editableHotel.rooms" :key="index" class="room-edit-card">
                  <div class="room-photo">
                    <img :src="room.image" v-if="room.image">
                    <div v-else class="photo-placeholder small">+</div>
                    <button class="btn-photo-edit small" @click="triggerFileInput('room', index)">수정</button>
                  </div>
                  <div class="room-inputs-grid">
                    <div class="form-group-small room-name-input">
                      <label>객실 종류</label>
                      <input type="text" v-model="room.type" placeholder="예: 스탠다드 더블">
                    </div>
                    <div class="form-group-small">
                      <label>판매가 (원)</label>
                      <input type="number" v-model.number="room.price" placeholder="150000">
                    </div>
                    <div class="form-group-small">
                      <label>총 객실 수</label>
                      <input type="number" v-model.number="room.count" placeholder="20">
                    </div>
                    <div class="form-group-small">
                      <label>최대 인원</label>
                      <input type="number" v-model.number="room.people" placeholder="2">
                    </div>
                    <div class="form-group-small">
                        <label>체크인 시간</label>
                        <input type="time" v-model="room.checkinTime">
                    </div>
                    <div class="form-group-small">
                        <label>체크아웃 시간</label>
                        <input type="time" v-model="room.checkoutTime">
                    </div>
                  </div>
                  <button @click="removeRoom(index)" class="btn-remove-room">-</button>
                </div>
                <button @click="addRoom" class="btn-add-room">+ 새 객실 추가</button>
              </div>

              <div class="card">
                <h4>서비스 및 부대시설</h4>
                <div class="amenities-checkbox-grid">
                  <label v-for="amenity in allAmenities" :key="amenity">
                    <input type="checkbox" :value="amenity" v-model="editableHotel.amenities"> {{ amenity }}
                  </label>
                </div>
              </div>
            </div>
            
            <div class="form-sidebar">
              <div class="sticky-sidebar">
                <div class="card save-panel">
                  <h4>저장 및 관리</h4>
                  <button class="btn-save" @click="saveChanges">변경사항 저장</button>
                  <button class="btn-cancel" @click="cancelChanges">수정 취소</button>
                  <button 
                    v-if="selectedPropertyForEdit" 
                    class="btn-delete-in-form" 
                    @click="deleteProperty(editableHotel.id, editableHotel.name)"
                  >
                    이 숙소 삭제하기
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section v-if="activeView === 'reviews'" class="content-section">
        <header class="content-header">
          <h1>리뷰 관리</h1>
          <p>고객 리뷰에 답글을 달거나 악성 리뷰를 관리합니다.</p>
        </header>
        <div class="review-list">
          <div v-for="review in reviews" :key="review.id" class="card review-card">
            <div class="review-header">
              <div>
                <strong>{{ review.userName }}</strong>
                <p class="review-hotel-name">{{ review.hotelName }}</p>
              </div>
              <span class="review-rating">{{ '★'.repeat(review.stars) }}</span>
              <span class="review-date">{{ review.date }}</span>
            </div>
            <p class="review-text">"{{ review.text }}"</p>
            <div class="review-actions">
              <textarea placeholder="답글을 입력하세요..."></textarea>
              <div class="action-buttons">
                <button class="btn-delete-request">삭제 요청</button>
                <button>답글 등록</button>
              </div>
            </div>
          </div>
        </div>
      </section>
      
      <section v-if="activeView === 'account'" class="content-section">
        <header class="content-header">
          <h1>계정 관리</h1>
          <p>사업자 정보를 확인하고 수정합니다.</p>
        </header>
        <div class="card">
          <h4>사업자 정보</h4>
          <div class="form-group"><label>상호명</label><input type="text" :value="managerAccount.companyName" disabled></div>
          <div class="form-group"><label>사업자 등록번호</label><input type="text" :value="managerAccount.businessNumber" disabled></div>
          <button>정보 수정 요청</button>
        </div>
      </section>
    </main>
    <input type="file" ref="fileInputRef" @change="handleFileSelect" style="display: none" accept="image/*">

    <div v-if="isBookingModalVisible" class="modal-overlay" @click.self="isBookingModalVisible = false">
      <div class="modal-content">
        <button class="modal-close-btn" @click="isBookingModalVisible = false">&times;</button>
        <h3>오늘의 신규 예약 목록</h3>
        <ul class="booking-list">
          <li v-for="booking in todayBookingsDetails" :key="booking.id">
            <span class="booking-hotel-name">{{ booking.hotelName }}</span>
            <span class="booking-room-name">{{ booking.roomName }}</span>
            <span class="booking-guest-name">({{ booking.guestName }}님)</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { Bar } from 'vue-chartjs';
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

const router = useRouter();
const activeView = ref('reservations'); // 기본 뷰를 'reservations'로 설정
const accommodationView = ref('typeSelection');
const selectedPropertyType = ref(null);
const selectedPropertyForEdit = ref(null);
const editableHotel = ref(null);
const fileInputRef = ref(null);
const imageUpdateTarget = ref({ type: null, index: null });
const selectedMetric = ref('monthly');
const isBookingModalVisible = ref(false);
const imageFiles = ref([]);

// --- 예약 관리 관련 상태 ---
const reservations = ref([]); // 서버에서 받은 원본 예약 목록
const searchQuery = ref('');
const searchType = ref('guestName');
const statusFilter = ref('all');
const userTypeFilter = ref('all'); // [추가] 회원/비회원 필터 상태 ('all', 'member', 'guest')

const statusOptions = ref([
  { code: 'confirmed', text: '예약 완료' },
  { code: 'cancelled', text: '예약 취소' }
]);

const managedProperties = ref([]);
const propertyTypeMapping = {
  '호텔': 'Hotel',
  '모텔': 'Motel',
  '펜션': 'Pension',
  '게스트하우스': 'Guesthouse',
  '관광호텔': 'Tourist Hotel'
};

// --- [핵심 수정] 필터링 로직 ---

// 1. 검색어와 상태로 1차 필터링
const filteredBySearchAndStatus = computed(() => {
  let tempReservations = reservations.value;

  // 상태 필터링
  if (statusFilter.value !== 'all') {
    tempReservations = tempReservations.filter(booking => booking.status.code === statusFilter.value);
  }

  // 검색어 필터링
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    tempReservations = tempReservations.filter(booking => {
      if (searchType.value === 'guestName') {
        return booking.guestName.toLowerCase().includes(query);
      }
      if (searchType.value === 'orderId') {
        return String(booking.orderId).toLowerCase().includes(query);
      }
      return false;
    });
  }

  return tempReservations;
});

// 2. 회원/비회원으로 2차 필터링 (1차 필터링된 결과 기반)
const memberReservations = computed(() => 
  filteredBySearchAndStatus.value.filter(r => r.isMember)
);
const guestReservations = computed(() => 
  filteredBySearchAndStatus.value.filter(r => !r.isMember)
);

// 3. 최종적으로 화면에 보여줄 목록
const finalFilteredReservations = computed(() => {
  if (userTypeFilter.value === 'member') {
    return memberReservations.value;
  }
  if (userTypeFilter.value === 'guest') {
    return guestReservations.value;
  }
  return filteredBySearchAndStatus.value; // 'all'
});
// ------------------------------

const searchPlaceholder = computed(() => {
  return searchType.value === 'guestName' ? '고객 이름으로 검색...' : '예약 번호로 검색...';
});

const canceledBookingsCount = computed(() => {
  return reservations.value.filter(b => b.status.code === 'cancelled').length;
});

// --- 데이터 로딩 함수 ---
const fetchManagedHotels = async () => {
  try {
    const response = await axios.get('http://localhost:8888/api/manager/hotels', {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
    });

    // --- 안전장치 추가 ---
    // 서버에서 받은 데이터가 배열인지 확인합니다.
    if (Array.isArray(response.data)) {
      managedProperties.value = response.data;
    } else {
      // 배열이 아니라면, 콘솔에 경고를 출력하고 빈 배열로 초기화하여 오류를 방지합니다.
      console.warn('API로부터 배열이 아닌 데이터가 수신되었습니다:', response.data);
      managedProperties.value = [];
    }
  } catch (error) {
    console.error('호텔 목록을 불러오는 데 실패했습니다:', error);
    managedProperties.value = []; // 오류 발생 시에도 안전하게 빈 배열로 설정
    alert('등록된 호텔 정보를 가져오는 데 실패했습니다.');
  }
};

const fetchReservations = async () => {
  try {
    const response = await axios.get('http://localhost:8888/api/manager/reservations', {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
    });

    reservations.value = response.data.map(r => ({
      orderId: r.orderId,
      guestName: r.guestName,
      hotelName: r.hotelName,
      roomName: r.roomType,
      checkIn: r.checkIn,
      checkOut: r.checkOut,
      isMember: r.userId !== null, // userId 존재 여부로 회원/비회원 구분
      status: {
        code: r.status === '예약 완료' ? 'confirmed' : 'cancelled',
        text: r.status
      }
    }));
  } catch (error) {
    console.error('예약 내역을 불러오는 데 실패했습니다:', error);
    alert('예약 정보를 가져올 수 없습니다.');
  }
};

// --- 뷰 변경 및 초기화 ---
const setActiveView = (viewName) => {
  activeView.value = viewName;
  if (viewName === 'reservations') {
    fetchReservations();
  } else if (viewName === 'accommodation') {
    accommodationView.value = 'typeSelection';
    fetchManagedHotels();
  }
};

onMounted(() => {
  // 페이지가 로드되면 현재 활성화된 뷰에 맞는 데이터를 불러옵니다.
  if (activeView.value === 'reservations') {
    fetchReservations();
  } else if (activeView.value === 'accommodation') {
    fetchManagedHotels();
  }
});


// ---------------- 이하 코드는 기존과 동일합니다 ----------------

const getPropertyCount = (koreanType) => {
  // --- 안전장치 추가 ---
  // managedProperties.value가 배열이 아닐 경우를 대비해 기본값 0을 반환합니다.
  if (!Array.isArray(managedProperties.value)) {
    return 0;
  }

  const englishType = propertyTypeMapping[koreanType];
  if (!englishType) return 0;
  return managedProperties.value.filter(p => p.type === englishType).length;
};

const filteredProperties = computed(() => {
  if (!selectedPropertyType.value) return [];
  // 사용자가 선택한 한글 유형을 영문 유형으로 변환합니다.
  const englishType = propertyTypeMapping[selectedPropertyType.value];
  if (!englishType) return [];
  // 변환된 영문 유형으로 필터링합니다.
  return managedProperties.value.filter(p => p.type === englishType);
});

const selectPropertyType = (type) => {
  selectedPropertyType.value = type;
  accommodationView.value = 'list';
};

const salesData = {
  all: { total: 125800000, monthly: 32500000, daily: 1200000, todayBookings: 7 },
  '호텔': { monthly: 15000000, daily: 600000 },
  '펜션': { monthly: 8000000, daily: 350000 },
  '한옥': { monthly: 5500000, daily: 150000 },
  '관광호텔': { monthly: 4000000, daily: 100000 },
};
const salesFilterType = ref('all');
const salesFilterTypes = [
  { key: 'all', text: '전체' },
  { key: '호텔', text: '🏨 호텔' },
  { key: '펜션', text: '🏡 펜션' },
  { key: '한옥', text: '🏯 한옥' },
  { key: '관광호텔', text: '🏢 관광호텔' },
];
const filteredSales = computed(() => {
  const type = salesFilterType.value;
  if (type === 'all' || !salesData[type]) {
    return salesData.all;
  }
  return {
    total: salesData.all.total,
    monthly: salesData[type].monthly,
    daily: salesData[type].daily,
    todayBookings: salesData.all.todayBookings
  };
});
const todayBookingsDetails = ref([
  { id: 1, hotelName: '쉼, 서울 호텔', roomName: '스탠다드 더블', guestName: '김예약' },
  { id: 2, hotelName: '오션뷰, 부산 펜션', roomName: '오션뷰 스파', guestName: '이바다' },
]);
const reviews = ref([
  { id: 1, userName: '김여행', hotelName: '쉼, 서울 호텔', stars: 5, date: '2025-09-10', text: '위치도 좋고 시설도 깔끔해서 좋았어요. 다음에 또 방문할 의사 있습니다!' },
  { id: 2, userName: '박호캉스', hotelName: '고요, 경주 한옥', stars: 2, date: '2025-09-08', text: '방음이 너무 안돼서 잠을 설쳤습니다. 개선이 필요해 보입니다.' },
]);
const managerAccount = ref({ companyName: '(주)쉼호텔', businessNumber: '123-45-67890' });

const allAmenities = ref([ '셀프 주차 (추가 비용 발생)', '무료 Wi-Fi (모든 객실)', '조식 서비스', '자동판매기', '금연 숙소', '여행 가방 보관 서비스', '24시간 프런트 데스크', '익스프레스 체크인', '정원', '24시간 피트니스 시설', '엘리베이터', '휠체어 접근 가능', '발코니/테라스', '공용 전자레인지', '다국어 가능 직원' ]);
const chartTitle = computed(() => {
  const filterText = salesFilterType.value === 'all' ? '전체' : salesFilterType.value;
  if (selectedMetric.value === 'total') return `총 매출 상세 분석`;
  if (selectedMetric.value === 'monthly') return `${filterText} 월별 매출 추이`;
  if (selectedMetric.value === 'daily') return `${filterText} 일별 매출 상세`;
  return '매출 현황';
});
const monthlySalesData = {
  labels: ['4월', '5월', '6월', '7월', '8월', '9월'],
  all: [28000000, 35000000, 31000000, 42000000, 51000000, 32500000],
  '호텔': [12000000, 16000000, 14000000, 18000000, 22000000, 15000000],
};
const chartData = computed(() => {
  const type = salesFilterType.value;
  const data = monthlySalesData[type] || monthlySalesData.all;
  return {
    labels: monthlySalesData.labels,
    datasets: [{
        label: `${type === 'all' ? '전체' : type} 매출 (원)`,
        backgroundColor: '#3498DB',
        borderRadius: 6,
        data: data,
    },],
  };
});

const handleFileSelect = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // --- 1. 실제 파일 저장 (나중에 '저장' 버튼 누를 때 사용) ---
  // '수정' 버튼 클릭 시 기억해둔 인덱스를 가져옵니다.
  const { index } = imageUpdateTarget.value;

  if (!imageFiles.value) imageFiles.value = [];
  imageFiles.value[index] = file;
    
  const reader = new FileReader();
  reader.onload = (e) => {
    const imageUrl = e.target.result;

    if (!editableHotel.value.images) {
      editableHotel.value.images = [];
    }

    // ★★★★★ 핵심 수정사항 ★★★★★
    // 하드코딩된 `[0]` 대신, 클릭된 버튼에 해당하는 `index` 변수를 사용하여
    // 미리보기 배열(editableHotel.images)의 올바른 위치를 업데이트합니다.
    editableHotel.value.images[index] = imageUrl;
  };

  reader.readAsDataURL(file);
  event.target.value = '';
};

const saveChanges = async () => {
  if (!editableHotel.value) return;
  if (!editableHotel.value.name || editableHotel.value.name.trim() === '') {
    alert('숙소 이름은 필수 항목입니다.');
    return;
  }

  const token = localStorage.getItem('accessToken');
  const isNewProperty = !selectedPropertyForEdit.value;

  try {
    if (isNewProperty) {
      // --- 신규 등록 ---
      const formData = new FormData();
      const hotelData = { ...editableHotel.value, type: propertyTypeMapping[selectedPropertyType.value] };
      delete hotelData.id;
      
      formData.append('hotelDto', new Blob([JSON.stringify(hotelData)], { type: 'application/json' }));
      imageFiles.value.forEach(file => formData.append('images', file));

      await axios.post('http://localhost:8888/api/manager/hotels', formData, {
        headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'multipart/form-data' }
      });
      alert('새로운 숙소가 성공적으로 등록되었습니다.');

    } else {
      // --- 기존 숙소 수정 ---
      const hotelId = editableHotel.value.id;

      // 1. 텍스트 정보 먼저 업데이트 (PUT)
      await axios.put(`http://localhost:8888/api/manager/hotels/${hotelId}`, editableHotel.value, {
        headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
      });

      // 2. 새로 선택된 이미지가 있으면, 이미지 업로드 (POST)
      if (imageFiles.value.length > 0) {
        const imageFormData = new FormData();
        imageFiles.value.forEach(file => imageFormData.append('images', file));
        await axios.post(`http://localhost:8888/api/manager/hotels/${hotelId}/images`, imageFormData, {
          headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'multipart/form-data' }
        });
      }
      alert('변경사항이 성공적으로 저장되었습니다.');
    }

    await fetchManagedHotels();
    accommodationView.value = 'list';

  } catch (error) {
    console.error('숙소 정보 저장 실패:', error);
    alert(`오류가 발생했습니다: ${error.response?.data?.message || error.message}`);
  }
};

const deleteProperty = async (hotelId, hotelName) => {
  if (!confirm(`'${hotelName}' 숙소를 정말로 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.`)) {
    return;
  }

  try {
    const token = localStorage.getItem('accessToken');
    await axios.delete(`http://localhost:8888/api/manager/hotels/${hotelId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    alert('숙소가 성공적으로 삭제되었습니다.');
    
    // 목록 뷰에 있었다면 목록 새로고침, 수정 뷰에 있었다면 목록으로 이동하며 새로고침
    await fetchManagedHotels();
    if (accommodationView.value === 'edit') {
      accommodationView.value = 'list';
    }

  } catch (error) {
    console.error('숙소 삭제 실패:', error);
    alert(`삭제 중 오류가 발생했습니다: ${error.response?.data?.message || error.message}`);
  }
};

const cancelChanges = () => {
  accommodationView.value = 'list';
};

const addRoom = () => {
  if (editableHotel.value) {
    editableHotel.value.rooms.push({
      id: Date.now(),
      // ★★★★★ 'name'을 'type'으로 변경 ★★★★★
      type: '', 
      price: 0,
      count: 1,
      people: 2,
      active: true,
      image: '',
      checkInTime: '15:00',
      checkOutTime: '11:00'
    });
  }
};

const editProperty = async (property) => {
  try {
    const token = localStorage.getItem('accessToken');
    // 백엔드에 호텔 상세 정보(객실 포함)를 요청하는 API가 필요합니다.
    // 여기서는 GET /api/manager/hotels/{id} 라고 가정합니다.
    const response = await axios.get(`http://localhost:8888/api/manager/hotels/${property.id}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    selectedPropertyForEdit.value = response.data;
    editableHotel.value = JSON.parse(JSON.stringify(response.data));
    accommodationView.value = 'edit';
    imageFiles.value = []; // 이미지 파일 선택 목록 초기화
    
  } catch (error) {
    console.error('호텔 상세 정보를 불러오는 데 실패했습니다:', error);
    alert('호텔 정보를 불러오는 중 오류가 발생했습니다.');
  }
};



const chartOptions = { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false, }, tooltip: { callbacks: { label: function (context) { let label = context.dataset.label || ''; if (label) { label += ': '; } if (context.parsed.y !== null) { label += new Intl.NumberFormat('ko-KR').format(context.parsed.y) + '원'; } return label; }, }, }, }, scales: { y: { ticks: { callback: function (value) { return new Intl.NumberFormat('ko-KR', { notation: 'compact', compactDisplay: 'short', }).format(value); }, }, }, }, };
const editFormTitle = computed(() => selectedPropertyForEdit.value ? `"${selectedPropertyForEdit.value.name}" 숙소 관리` : `새 ${selectedPropertyType.value} 등록`);

const goHome = () => { router.push('/'); };
const addNewProperty = () => { selectedPropertyForEdit.value = null; editableHotel.value = { id: Date.now(), name: '', type: selectedPropertyType.value, location: '', stars: 0, latitude: '', longitude: '', checkInTime: '15:00', checkOutTime: '11:00', image: '', images: [], rooms: [], amenities: [] }; accommodationView.value = 'edit'; };
const triggerFileInput = (type, index) => { imageUpdateTarget.value = { type, index }; fileInputRef.value.click(); };

const removeRoom = (index) => { if (editableHotel.value) { editableHotel.value.rooms.splice(index, 1); } };
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nanum+Myeongjo:wght@700&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700;800&display=swap');
.manager-dashboard { display: grid; grid-template-columns: 260px 1fr; height: 100vh; font-family: 'Noto Sans KR', sans-serif; }
.manager-sidebar { background-color: #2C3E50; color: white; display: flex; flex-direction: column; padding: 25px; }
.sidebar-header .logo { font-family: 'Nanum Myeongjo', serif; font-size: 1.8rem; color: white; text-decoration: none; cursor: pointer; }
.sidebar-header .manager-mode { background-color: #3498DB; color: white; padding: 4px 8px; border-radius: 5px; font-size: 0.8rem; display: inline-block; margin-top: 10px; }
.sidebar-nav { margin-top: 40px; display: flex; flex-direction: column; gap: 10px; }
.sidebar-nav a { color: #ECF0F1; text-decoration: none; font-size: 1.1rem; padding: 15px 20px; border-radius: 8px; cursor: pointer; display: flex; align-items: center; gap: 10px; }
.sidebar-nav a.active, .sidebar-nav a:hover { background-color: #34495E; }
.sidebar-footer { margin-top: auto; }
.btn-logout { width: 100%; padding: 12px; background-color: #E74C3C; color: white; border: none; border-radius: 8px; cursor: pointer; }
.manager-content { background-color: #F4F6F9; padding: 40px; overflow-y: auto; }
.content-header { margin-bottom: 30px; }
.content-header.with-back-button { display: flex; align-items: center; gap: 20px; }
.content-header h1 { font-size: 2.2rem; font-weight: 800; color: #2C3E50; margin: 0; }
.content-header p { font-size: 1.1rem; color: #555; margin-top: 5px; }
.card { background-color: #fff; border-radius: 12px; padding: 30px; margin-bottom: 25px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
.card h4 { margin: 0 0 20px 0; font-size: 1.3rem; font-weight: 600; color: #333; }
.filter-controls { display: flex; gap: 10px; margin-bottom: 25px; flex-wrap: wrap;}
.filter-controls button { background-color: #fff; border: 1px solid #ddd; color: #555; padding: 8px 15px; border-radius: 20px; cursor: pointer; font-weight: 500; }
.filter-controls button.active { background-color: #2C3E50; color: white; border-color: #2C3E50; }
.dashboard-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 25px; }
.metric-card { cursor: pointer; transition: all 0.2s; border: 2px solid transparent; }
.metric-card.active { border-color: #3498DB; box-shadow: 0 4px 20px rgba(52, 152, 219, 0.4); }
.metric-card.alert-metric .metric { color: #E74C3C; }
.card .metric { font-size: 2.5rem; font-weight: 700; color: #2C3E50; margin: 0; }
.property-type-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 25px; }
.property-type-card { text-align: center; cursor: pointer; border: 1px solid #eee; padding: 30px; border-radius: 12px; }
.property-type-card .icon { font-size: 3rem; }
.property-type-card h3 { font-size: 1.5rem; margin: 15px 0 5px 0; }
.btn-back { background: none; border: 1px solid #ccc; color: #555; font-weight: 600; border-radius: 8px; }
.property-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 25px; }
.property-card { padding: 0; display: flex; flex-direction: column; cursor: pointer; overflow: hidden; }
.property-image { width: 100%; height: 150px; object-fit: cover; }
.property-info { padding: 20px; flex-grow: 1; }
.property-manage-footer { background-color: #3498DB; color: white; padding: 12px; text-align: center; }
.add-new-card { align-items: center; justify-content: center; border-style: dashed; color: #aaa; }
.add-new-card .add-icon { font-size: 3rem; }
.edit-form-wrapper { max-width: 1200px; }
.edit-form-layout { display: grid; grid-template-columns: 1fr 320px; gap: 30px; align-items: flex-start; }
.form-main, .form-sidebar { min-width: 0; }
.sticky-sidebar { position: sticky; top: 40px; }
.save-panel { text-align: center; }
.save-panel p { margin: 15px 0; font-size: 0.95rem; color: #666; }
.btn-save { background-color: #27ae60; color: white; width: 100%; padding: 14px; font-size: 1.1rem; }
.btn-cancel { background-color: #f0f0f0; color: #555; width: 100%; padding: 14px; font-size: 1.1rem; }
.photo-management-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 15px; }
.main-photo { grid-column: span 3; grid-row: span 2; }
.main-photo, .sub-photo { position: relative; border-radius: 8px; overflow: hidden; background-color: #f0f0f0; aspect-ratio: 4 / 3; }
.photo-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 3rem; color: #ccc; }
.main-photo img, .sub-photo img { width: 100%; height: 100%; object-fit: cover; }
.btn-photo-edit { position: absolute; bottom: 8px; right: 8px; background-color: rgba(0,0,0,0.5); color: white; border: none; padding: 5px 10px; font-size: 0.8rem; border-radius: 5px; cursor: pointer; }
.btn-photo-edit.small { padding: 4px 8px; font-size: 0.75rem; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.form-group.full-width { grid-column: span 2; }
.form-group label { display: block; font-weight: 600; margin-bottom: 8px; }
.form-group .required { color: #E74C3C; }
input[type="text"], input[type="number"], input[type="time"], select { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 6px; box-sizing: border-box; }
.room-edit-card { display: flex; gap: 15px; align-items: center; margin-bottom: 15px; background-color: #f9f9f9; padding: 15px; border-radius: 8px; }
.room-photo { width: 100px; height: 75px; flex-shrink: 0; position: relative; border-radius: 6px; overflow: hidden; background-color: #e9e9e9; }
.room-photo .photo-placeholder.small { font-size: 1.5rem; }
.room-inputs-grid { flex-grow: 1; display: grid; grid-template-columns: repeat(3, 1fr); grid-template-rows: auto auto; gap: 15px; }
.form-group-small { display: flex; flex-direction: column; }
.form-group-small label { font-size: 0.85rem; font-weight: 500; color: #555; margin-bottom: 5px; }
.form-group-small input { padding: 8px; }
.room-name-input { grid-column: 1 / -1; }
.btn-remove-room { background-color: #E74C3C; color: white; padding: 8px 12px; font-size: 1rem; }
.btn-add-room { background: none; border: 1px dashed #ccc; width: 100%; padding: 12px; margin-top: 10px; color: #555; font-weight: 600; }
.amenities-checkbox-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; }
.amenities-checkbox-grid label { display: flex; align-items: center; gap: 8px; font-size: 0.95rem; }
.review-list { display: flex; flex-direction: column; gap: 20px; }
.review-header { display: flex; align-items: flex-start; gap: 15px; margin-bottom: 10px; }
.review-hotel-name { font-size: 0.9rem; color: #888; margin-top: 4px; }
.review-rating { margin-left: auto; color: #F39C12; }
.review-actions textarea { height: 80px; width: 100%; box-sizing: border-box; margin-bottom: 10px; }
.action-buttons { display: flex; justify-content: flex-end; gap: 10px; }
.btn-delete-request { background-color: #E74C3C; color: white; }
button { padding: 12px 25px; font-size: 1rem; font-weight: 600; border-radius: 8px; border: none; cursor: pointer; background-color: #3498DB; color: white; }
.table-responsive { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; text-align: left; }
th, td { padding: 12px 15px; border-bottom: 1px solid #eee; vertical-align: middle; }
th { background-color: #f9fafb; font-weight: 600; color: #555; }
.no-results { text-align: center; color: #888; padding: 40px; }
.status-badge { padding: 4px 10px; border-radius: 12px; font-size: 0.85rem; font-weight: 600; color: white; display: inline-block; }
.status-confirmed { background-color: #27AE60; }
.status-pending { background-color: #F39C12; }
.status-cancelled { background-color: #E74C3C; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.6); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background-color: white; padding: 30px; border-radius: 12px; width: 90%; max-width: 500px; box-shadow: 0 5px 20px rgba(0,0,0,0.2); position: relative; }
.modal-close-btn { position: absolute; top: 15px; right: 15px; background: none; border: none; font-size: 1.8rem; cursor: pointer; color: #888; }
.modal-content h3 { margin-top: 0; margin-bottom: 20px; font-size: 1.5rem; color: #2C3E50; }
.booking-list { list-style: none; padding: 0; margin: 0; max-height: 400px; overflow-y: auto; }
.booking-list li { padding: 12px 0; border-bottom: 1px solid #eee; display: flex; align-items: center; gap: 10px; flex-wrap: wrap;}
.booking-list li:last-child { border-bottom: none; }
.booking-hotel-name { font-weight: 600; }
.booking-room-name { color: #555; }
.booking-guest-name { margin-left: auto; color: #888; font-size: 0.9rem; }

/* --- 새로운 스타일 --- */
.search-card { padding-bottom: 15px; }
.search-controls { display: flex; gap: 10px; }
.search-select { padding: 10px; border-radius: 6px; border: 1px solid #ccc; font-weight: 500; }
.search-input { flex-grow: 1; padding: 10px; border-radius: 6px; border: 1px solid #ccc; }
.data-policy-note { font-size: 0.85rem; color: #888; margin-top: 15px; text-align: right; }
/* [추가] 예약 상태 필터 스타일 */
.status-filter-controls { display: flex; gap: 10px; margin-top: 20px; color: #555; border-top: 1px solid #eee; padding-top: 20px; }
.status-filter-controls button { background-color: #fff; border: 1px solid #ddd; color: #555; padding: 8px 15px; border-radius: 20px; cursor: pointer; font-weight: 500; }
.status-filter-controls button.active { background-color: #3498DB; color: white; border-color: #3498DB; }

.filter-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}
.filter-tabs button {
  padding: 8px 18px;
  border-radius: 20px;
  border: 1px solid #ddd;
  color: #555;
  background-color: #fff;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.filter-tabs button:hover {
  background-color: #f0f0f0;
}
.filter-tabs button.active {
  background-color: #2C3E50;
  color: white;
  border-color: #2C3E50;
}

.user-type-badge {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 700;
  margin-right: 8px;
  color: white;
  vertical-align: middle;
}
.user-type-badge.member { background-color: #27AE60; } /* 초록색 */
.user-type-badge.guest { background-color: #F39C12; } /* 주황색 */

</style>
