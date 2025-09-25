<template>
  <div class="manager-dashboard">
    <aside class="manager-sidebar">
      <div class="sidebar-header">
        <a @click="goHome" class="logo">쉼, 한국</a>
        <p class="manager-mode">호텔 매니저</p>
      </div>
      <nav class="sidebar-nav">
        <a @click="activeView = 'dashboard'" :class="{ active: activeView === 'dashboard' }">📈 매출 관리</a>
        <a @click="activeView = 'reservations'" :class="{ active: activeView === 'reservations' }">📅 예약 관리</a>
        <a @click="activeView = 'accommodation'; accommodationView = 'typeSelection'" :class="{ active: activeView === 'accommodation' }">🏨 숙소/객실 관리</a>
        <a @click="activeView = 'reviews'" :class="{ active: activeView === 'reviews' }">✍️ 리뷰 관리</a>
        <a @click="activeView = 'account'" :class="{ active: activeView === 'account' }">👤 계정 관리</a>
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
          <p class="data-policy-note">※ 예약 데이터는 체크인 날짜 기준 3개월 동안 보관 후 자동으로 삭제됩니다.</p>
        </div>

        <div class="card">
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
                <tr v-for="booking in filteredReservations" :key="booking.id">
                  <td>{{ booking.id }}</td>
                  <td>{{ booking.guestName }}</td>
                  <td>{{ booking.hotelName }}</td>
                  <td>{{ booking.roomName }}</td>
                  <td>{{ booking.checkIn }}</td>
                  <td>{{ booking.checkOut }}</td>
                  <td>
                    <span :class="['status-badge', `status-${booking.status.code}`]">{{ booking.status.text }}</span>
                  </td>
                </tr>
                 <tr v-if="filteredReservations.length === 0">
                  <td colspan="7" class="no-results">검색 결과가 없습니다.</td>
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
            <div class="property-type-card" @click="selectPropertyType('펜션')">
              <span class="icon">🏡</span>
              <h3>펜션 & 풀빌라</h3>
              <p>{{ getPropertyCount('펜션') }}개 등록됨</p>
            </div>
            <div class="property-type-card" @click="selectPropertyType('한옥')">
              <span class="icon">🏯</span>
              <h3>한옥</h3>
              <p>{{ getPropertyCount('한옥') }}개 등록됨</p>
            </div>
<<<<<<< HEAD
=======

>>>>>>> c44adac929d8261adb1f062bd02f76eb353bc792
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
              <img :src="prop.image" class="property-image">
              <div class="property-info">
                <h4>{{ prop.name }}</h4>
                <p>{{ prop.location }}</p>
              </div>
              <div class="property-manage-footer">관리하기</div>
            </div>
<<<<<<< HEAD
=======

>>>>>>> c44adac929d8261adb1f062bd02f76eb353bc792
            <div class="card property-card add-new-card" @click="addNewProperty">
              <span class="add-icon">+</span>
              <h4>새 {{ selectedPropertyType }} 추가하기</h4>
            </div>
          </div>
        </div>
        
        <div v-if="accommodationView === 'edit' && editableHotel" class="edit-form-wrapper">
          <header class="content-header with-back-button">
<<<<<<< HEAD
            <button @click="accommodationView = 'list'" class="btn-back">‹ 목록으로</button>
=======

            <button @click="accommodationView = 'list'" class="btn-back">‹ 목록으로</button>

>>>>>>> c44adac929d8261adb1f062bd02f76eb353bc792
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
                    <img :src="editableHotel.images[0]" v-if="editableHotel.images[0]">
                    <div v-else class="photo-placeholder">+</div>
                    <button class="btn-photo-edit" @click="triggerFileInput('main', 0)">수정</button>
                  </div>
                  <div class="sub-photo" v-for="i in 4" :key="i">
                    <img :src="editableHotel.images[i]" v-if="editableHotel.images[i]">
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
                  <div class="form-group"><label>체크인 시간</label><input type="time" v-model="editableHotel.checkInTime"></div>
                  <div class="form-group"><label>체크아웃 시간</label><input type="time" v-model="editableHotel.checkOutTime"></div>
<<<<<<< HEAD
=======

>>>>>>> c44adac929d8261adb1f062bd02f76eb353bc792
                </div>
              </div>
              
              <div class="card">
<<<<<<< HEAD
=======

>>>>>>> c44adac929d8261adb1f062bd02f76eb353bc792
                <h4>객실 관리</h4>
                <div v-for="(room, index) in editableHotel.rooms" :key="index" class="room-edit-card">
                  <div class="room-photo">
                    <img :src="room.image" v-if="room.image">
                    <div v-else class="photo-placeholder small">+</div>
                    <button class="btn-photo-edit small" @click="triggerFileInput('room', index)">수정</button>
                  </div>
                  <div class="room-inputs-grid">
                    <div class="form-group-small room-name-input">
                      <label>객실 이름</label>
                      <input type="text" v-model="room.name" placeholder="예: 스탠다드 더블">
                    </div>
                    <div class="form-group-small">
                      <label>판매가 (원)</label>
                      <input type="number" v-model.number="room.price" placeholder="150000">
                    </div>
                    <div class="form-group-small">
                      <label>총 객실 수</label>
                      <input type="number" v-model.number="room.quantity" placeholder="20">
                    </div>
                    <div class="form-group-small">
                      <label>최대 인원</label>
                      <input type="number" v-model.number="room.maxOccupancy" placeholder="2">
                    </div>
                  </div>
                  <button @click="removeRoom(index)" class="btn-remove-room">-</button>
<<<<<<< HEAD
=======

>>>>>>> c44adac929d8261adb1f062bd02f76eb353bc792
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
<<<<<<< HEAD
=======

>>>>>>> c44adac929d8261adb1f062bd02f76eb353bc792
            </div>
            
            <div class="form-sidebar">
              <div class="sticky-sidebar">
                <div class="card save-panel">
                  <h4>저장 및 관리</h4>
                  <p>모든 변경사항을 저장하거나 수정을 취소할 수 있습니다.</p>
                  <button class="btn-save" @click="saveChanges">변경사항 저장</button>
                  <button class="btn-cancel" @click="cancelChanges">수정 취소</button>
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
import { ref, computed } from 'vue';
import { Bar } from 'vue-chartjs';
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

const activeView = ref('dashboard');
const accommodationView = ref('typeSelection');
const selectedPropertyType = ref(null);
const selectedPropertyForEdit = ref(null);
const editableHotel = ref(null);
const fileInputRef = ref(null);
const imageUpdateTarget = ref({ type: null, index: null });
const selectedMetric = ref('monthly');
const isBookingModalVisible = ref(false);

// --- 예약 관리 관련 상태 ---
const searchQuery = ref('');
const searchType = ref('guestName');
const statusFilter = ref('all'); // [추가] 예약 상태 필터링을 위한 ref, 기본값 'all'

const statusOptions = ref([
  { code: 'confirmed', text: '예약 확정' },
  { code: 'pending', text: '입금 대기' },
  { code: 'cancelled', text: '예약 취소' }
]);
// ------------------------------------

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
  { id: 3, hotelName: '고요, 경주 한옥', roomName: '사랑채', guestName: '박고객' },
  { id: 4, hotelName: '쉼, 서울 호텔', roomName: '디럭스 트윈', guestName: '최신규' },
  { id: 5, hotelName: '시티투어, 서울 관광호텔', roomName: '비즈니스 트윈', guestName: '강비즈' },
  { id: 6, hotelName: '고요, 경주 한옥', roomName: '사랑채', guestName: '한예약' },
  { id: 7, hotelName: '오션뷰, 부산 펜션', roomName: '오션뷰 스파', guestName: '정숙박' },
]);

const reservations = ref([
  { id: 'R20250915-001', guestName: '이예약', hotelName: '쉼, 서울 호텔', roomName: '스탠다드 더블', checkIn: '2025-09-20', checkOut: '2025-09-22', status: { code: 'confirmed', text: '예약 확정' } },
  { id: 'R20250915-002', guestName: '최숙박', hotelName: '고요, 경주 한옥', roomName: '사랑채', checkIn: '2025-09-25', checkOut: '2025-09-26', status: { code: 'confirmed', text: '예약 확정' } },
  { id: 'R20250914-005', guestName: '강여행', hotelName: '오션뷰, 부산 펜션', roomName: '오션뷰 스파', checkIn: '2025-10-01', checkOut: '2025-10-03', status: { code: 'pending', text: '입금 대기' } },
  { id: 'R20250913-001', guestName: '박취소', hotelName: '쉼, 서울 호텔', roomName: '디럭스 트윈', checkIn: '2025-09-18', checkOut: '2025-09-19', status: { code: 'cancelled', text: '예약 취소' } },
]);

// --- 예약 관리 관련 computed 속성 및 함수 ---

const searchPlaceholder = computed(() => {
  return searchType.value === 'guestName' ? '고객 이름으로 검색...' : '예약 번호로 검색...';
});

// [변경] computed 속성에 상태 필터 로직 추가
const filteredReservations = computed(() => {
  let tempReservations = reservations.value;

  // 1. 상태 필터링
  if (statusFilter.value !== 'all') {
    tempReservations = tempReservations.filter(booking => booking.status.code === statusFilter.value);
  }

  // 2. 검색어 필터링
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    tempReservations = tempReservations.filter(booking => {
      if (searchType.value === 'guestName') {
        return booking.guestName.toLowerCase().includes(query);
      }
      if (searchType.value === 'id') {
        return booking.id.toLowerCase().includes(query);
      }
      return false;
    });
  }

  return tempReservations;
});

const canceledBookingsCount = computed(() => {
  return reservations.value.filter(b => b.status.code === 'cancelled').length;
});

// [제거] 테이블 내 상태 변경 함수는 이제 필요 없음

// [설명] 예약 데이터 자동 삭제 정책
// 실제 프로덕션 환경에서는 프론트엔드가 아닌 백엔드(서버)에서
// 매일 정해진 시간에 스케줄러(Scheduler, 예: cron job)를 실행하여
// 3개월이 지난 예약 데이터를 삭제하는 로직을 구현해야 합니다.
// 예시 SQL: DELETE FROM reservations WHERE checkin_date < DATE_SUB(NOW(), INTERVAL 3 MONTH);

const reviews = ref([
  { id: 1, userName: '김여행', hotelName: '쉼, 서울 호텔', stars: 5, date: '2025-09-10', text: '위치도 좋고 시설도 깔끔해서 좋았어요. 다음에 또 방문할 의사 있습니다!' },
  { id: 2, userName: '박호캉스', hotelName: '고요, 경주 한옥', stars: 2, date: '2025-09-08', text: '방음이 너무 안돼서 잠을 설쳤습니다. 개선이 필요해 보입니다.' },
]);
const managerAccount = ref({ companyName: '(주)쉼호텔', businessNumber: '123-45-67890' });

const managedProperties = ref([
  { id: 1, name: '쉼, 서울 호텔', type: '호텔', location: '서울 중구', stars: 5, latitude: '37.5665', longitude: '126.9780', checkInTime: '15:00', checkOutTime: '11:00', image: 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1780&q=80', images: ['https://images.unsplash.com/photo-1571896349842-33c89424de2d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1780&q=80'], rooms: [{ id: 1, name: '스탠다드 더블', price: 150000, quantity: 20, maxOccupancy: 2, active: true, image: '' }], amenities: ['무료 Wi-Fi (모든 객실)', '24시간 프런트 데스크'] },
  { id: 2, name: '고요, 경주 한옥', type: '한옥', location: '경북 경주시', stars: 0, latitude: '35.8436', longitude: '129.2126', checkInTime: '16:00', checkOutTime: '11:00', image: 'https://images.unsplash.com/photo-1566649872520-227545d165f1?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1770&q=80', images: [], rooms: [{ id: 1, name: '사랑채', price: 250000, quantity: 3, maxOccupancy: 4, active: true, image: '' }], amenities: ['무료 Wi-Fi (모든 객실)', '정원'] },
  { id: 3, name: '오션뷰, 부산 펜션', type: '펜션', location: '부산 해운대구', stars: 0, latitude: '35.1631', longitude: '129.1636', checkInTime: '15:00', checkOutTime: '12:00', image: 'https://images.unsplash.com/photo-1598533804259-e931b2641042?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1858&q=80', images: [], rooms: [{ id: 1, name: '오션뷰 스파', price: 180000, quantity: 5, maxOccupancy: 3, active: true, image: '' }], amenities: ['무료 Wi-Fi (모든 객실)', '주차 가능'] },
  { id: 4, name: '시티투어, 서울 관광호텔', type: '관광호텔', location: '서울 강남구', stars: 4, latitude: '37.5172', longitude: '127.0473', checkInTime: '14:00', checkOutTime: '12:00', image: 'https://images.unsplash.com/photo-1561501900-3701fa6a0864?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1770&q=80', images: [], rooms: [{ id: 1, name: '비즈니스 트윈', price: 120000, quantity: 30, maxOccupancy: 2, active: true, image: '' }], amenities: ['무료 Wi-Fi (모든 객실)', '조식 서비스'] },
]);

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
  '펜션': [8000000, 9000000, 8500000, 11000000, 13000000, 8000000],
  '한옥': [5000000, 6000000, 5500000, 7000000, 9000000, 5500000],
  '관광호텔': [3000000, 4000000, 3000000, 6000000, 7000000, 4000000]
};

const chartData = computed(() => {
  const type = salesFilterType.value;
  const data = monthlySalesData[type] || monthlySalesData.all;
  
  return {
    labels: monthlySalesData.labels,
    datasets: [
      {
        label: `${type === 'all' ? '전체' : type} 매출 (원)`,
        backgroundColor: '#3498DB',
        borderRadius: 6,
        data: data,
      },
    ],
  };
});

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false,
    },
    tooltip: {
      callbacks: {
        label: function (context) {
          let label = context.dataset.label || '';
          if (label) {
            label += ': ';
          }
          if (context.parsed.y !== null) {
            label += new Intl.NumberFormat('ko-KR').format(context.parsed.y) + '원';
          }
          return label;
        },
      },
    },
  },
  scales: {
    y: {
      ticks: {
        callback: function (value) {
          return new Intl.NumberFormat('ko-KR', {
            notation: 'compact',
            compactDisplay: 'short',
          }).format(value);
        },
      },
    },
  },
};

const editFormTitle = computed(() => selectedPropertyForEdit.value ? `"${selectedPropertyForEdit.value.name}" 숙소 관리` : `새 ${selectedPropertyType.value} 등록`);
const filteredProperties = computed(() => managedProperties.value.filter(p => p.type === selectedPropertyType.value));

const goHome = () => { activeView.value = 'dashboard'; accommodationView.value = 'typeSelection'; };
const getPropertyCount = (type) => managedProperties.value.filter(p => p.type === type).length;
const selectPropertyType = (type) => { selectedPropertyType.value = type; accommodationView.value = 'list'; };
const editProperty = (property) => { selectedPropertyForEdit.value = property; editableHotel.value = JSON.parse(JSON.stringify(property)); accommodationView.value = 'edit'; };
const addNewProperty = () => {
  selectedPropertyForEdit.value = null;
  editableHotel.value = { 
    id: Date.now(), name: '', type: selectedPropertyType.value, location: '', stars: 0, 
    latitude: '', longitude: '', checkInTime: '15:00', checkOutTime: '11:00',
    image: '', images: [], rooms: [], amenities: [] 
  };
  accommodationView.value = 'edit';
};
const triggerFileInput = (type, index) => { imageUpdateTarget.value = { type, index }; fileInputRef.value.click(); };
const handleFileSelect = (event) => {
  const file = event.target.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = (e) => {
    const imageUrl = e.target.result;
    const { type, index } = imageUpdateTarget.value;
    if (type === 'main') editableHotel.value.images[index] = imageUrl;
    else if (type === 'sub') editableHotel.value.images[index] = imageUrl;
    else if (type === 'room') editableHotel.value.rooms[index].image = imageUrl;
  };
  reader.readAsDataURL(file);
  event.target.value = '';
};
const addRoom = () => {
  if (editableHotel.value) {
    editableHotel.value.rooms.push({ 
      id: Date.now(), name: '', price: 0, quantity: 1, maxOccupancy: 2, active: true, image: '' 
    });
  }
};
const removeRoom = (index) => { if (editableHotel.value) { editableHotel.value.rooms.splice(index, 1); } };
const saveChanges = () => { alert('변경사항이 저장되었습니다.'); accommodationView.value = 'list'; };
const cancelChanges = () => { accommodationView.value = 'list'; };
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
.status-filter-controls { display: flex; gap: 10px; margin-top: 20px; border-top: 1px solid #eee; padding-top: 20px; }
.status-filter-controls button { background-color: #fff; border: 1px solid #ddd; color: #555; padding: 8px 15px; border-radius: 20px; cursor: pointer; font-weight: 500; }
.status-filter-controls button.active { background-color: #3498DB; color: white; border-color: #3498DB; }
</style>
