<template>
    <div class="manager-dashboard">
      <aside class="manager-sidebar">
        <div class="sidebar-header">
          <a @click="goHome" class="logo">쉼, 한국</a>
          <p class="manager-mode">호텔 매니저</p>
        </div>
        <nav class="sidebar-nav">
          <a @click="activeView = 'dashboard'" :class="{ active: activeView === 'dashboard' }">📈 매출 관리</a>
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
            <h1>매출 관리</h1>
            <p>기간별, 객실별 매출 현황을 확인합니다.</p>
          </header>
          <div class="dashboard-grid">
            <div class="card metric-card" :class="{ active: selectedMetric === 'total' }" @click="selectedMetric = 'total'">
              <h4>총 매출</h4>
              <p class="metric">{{ sales.total.toLocaleString() }}원</p>
            </div>
            <div class="card metric-card" :class="{ active: selectedMetric === 'monthly' }" @click="selectedMetric = 'monthly'">
              <h4>월간 매출 (이번 달)</h4>
              <p class="metric">{{ sales.monthly.toLocaleString() }}원</p>
            </div>
            <div class="card metric-card" :class="{ active: selectedMetric === 'daily' }" @click="selectedMetric = 'daily'">
              <h4>일일 매출 (오늘)</h4>
              <p class="metric">{{ sales.daily.toLocaleString() }}원</p>
            </div>
          </div>
          <div class="card chart-card">
            <h4>{{ chartTitle }}</h4>
            <div class="chart-placeholder">
              <p>{{ selectedMetric }} 데이터 기반 차트가 여기에 표시됩니다.</p>
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
                    <div class="form-group" v-if="selectedPropertyType === '호텔'"><label>호텔 성급</label><input type="number" v-model.number="editableHotel.stars" min="1" max="5"></div>
                    <div class="form-group full-width"><label>숙소 이름 <span class="required">*</span></label><input type="text" v-model="editableHotel.name"></div>
                    <div class="form-group full-width"><label>영문 이름</label><input type="text" v-model="editableHotel.englishName"></div>
                    <div class="form-group full-width"><label>숙소 위치 <span class="required">*</span></label><input type="text" v-model="editableHotel.location"></div>
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
                     <div class="room-inputs">
                       <input type="text" v-model="room.name" placeholder="객실 이름 (예: 스탠다드 더블)">
                       <input type="number" v-model.number="room.price" placeholder="가격">
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
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from 'vue';
  
  const activeView = ref('dashboard');
  const accommodationView = ref('typeSelection');
  const selectedPropertyType = ref(null);
  const selectedPropertyForEdit = ref(null);
  const editableHotel = ref(null);
  const selectedMetric = ref('monthly');
  const fileInputRef = ref(null);
  const imageUpdateTarget = ref({ type: null, index: null });
  
  const sales = ref({ total: 125800000, monthly: 32500000, daily: 1200000 });
  const reviews = ref([
    { id: 1, userName: '김여행', hotelName: '쉼, 서울 호텔', stars: 5, date: '2025-09-10', text: '위치도 좋고 시설도 깔끔해서 좋았어요. 다음에 또 방문할 의사 있습니다!' },
    { id: 2, userName: '박호캉스', hotelName: '고요, 경주 한옥', stars: 2, date: '2025-09-08', text: '방음이 너무 안돼서 잠을 설쳤습니다. 개선이 필요해 보입니다.' },
  ]);
  const managerAccount = ref({ companyName: '(주)쉼호텔', businessNumber: '123-45-67890' });
  
  const managedProperties = ref([
    { id: 1, name: '쉼, 서울 호텔', type: '호텔', location: '서울 중구', englishName: 'Shym, Seoul Hotel', stars: 5, image: 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1780&q=80', images: ['https://images.unsplash.com/photo-1571896349842-33c89424de2d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1780&q=80'], rooms: [{ id: 1, name: '스탠다드 더블', price: 150000, active: true, image: '' }], amenities: ['무료 Wi-Fi (모든 객실)', '24시간 프런트 데스크'] },
    { id: 2, name: '고요, 경주 한옥', type: '한옥', location: '경북 경주시', englishName: 'Goyo, Gyeongju Hanok', stars: 0, image: 'https://images.unsplash.com/photo-1566649872520-227545d165f1?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1770&q=80', images: [], rooms: [{ id: 1, name: '사랑채', price: 250000, active: true, image: '' }], amenities: ['무료 Wi-Fi (모든 객실)', '정원'] },
    { id: 3, name: '오션뷰, 부산 펜션', type: '펜션', location: '부산 해운대구', englishName: 'Ocean View Pension', stars: 0, image: 'https://images.unsplash.com/photo-1598533804259-e931b2641042?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1858&q=80', images: [], rooms: [{ id: 1, name: '오션뷰 스파', price: 180000, active: true, image: '' }], amenities: ['무료 Wi-Fi (모든 객실)', '주차 가능'] },
  ]);
  
  const allAmenities = ref([ '셀프 주차 (추가 비용 발생)', '무료 Wi-Fi (모든 객실)', '조식 서비스', '자동판매기', '금연 숙소', '여행 가방 보관 서비스', '24시간 프런트 데스크', '익스프레스 체크인', '정원', '24시간 피트니스 시설', '엘리베이터', '휠체어 접근 가능', '발코니/테라스', '공용 전자레인지', '다국어 가능 직원' ]);
  
  const chartTitle = computed(() => {
      if (selectedMetric.value === 'total') return '총 매출 상세';
      if (selectedMetric.value === 'monthly') return '월별 매출 추이';
      if (selectedMetric.value === 'daily') return '일별 매출 상세';
      return '매출 현황';
  });
  const editFormTitle = computed(() => selectedPropertyForEdit.value ? `"${selectedPropertyForEdit.value.name}" 숙소 관리` : `새 ${selectedPropertyType.value} 등록`);
  const filteredProperties = computed(() => managedProperties.value.filter(p => p.type === selectedPropertyType.value));
  
  const goHome = () => {
    activeView.value = 'dashboard';
    accommodationView.value = 'typeSelection';
  };
  const getPropertyCount = (type) => managedProperties.value.filter(p => p.type === type).length;
  const selectPropertyType = (type) => {
    selectedPropertyType.value = type;
    accommodationView.value = 'list';
  };
  const editProperty = (property) => {
    selectedPropertyForEdit.value = property;
    editableHotel.value = JSON.parse(JSON.stringify(property));
    accommodationView.value = 'edit';
  };
  const addNewProperty = () => {
    selectedPropertyForEdit.value = null;
    editableHotel.value = { id: Date.now(), name: '', type: selectedPropertyType.value, location: '', englishName: '', stars: 0, image: '', images: [], rooms: [], amenities: [] };
    accommodationView.value = 'edit';
  };
  
  const triggerFileInput = (type, index) => {
    imageUpdateTarget.value = { type, index };
    fileInputRef.value.click();
  };
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
      editableHotel.value.rooms.push({ id: Date.now(), name: '', price: 0, active: true, image: '' });
    }
  };
  const removeRoom = (index) => {
    if (editableHotel.value) {
      editableHotel.value.rooms.splice(index, 1);
    }
  };
  
  const saveChanges = () => {
    alert('변경사항이 저장되었습니다. (실제 앱에서는 API 연동 필요)');
    accommodationView.value = 'list';
  };
  const cancelChanges = () => {
    accommodationView.value = 'list';
  };
  </script>
  
  <style scoped>
  @import url('https://fonts.googleapis.com/css2?family=Nanum+Myeongjo:wght@700&display=swap');
  @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700;800&display=swap');
  
  .manager-dashboard { display: grid; grid-template-columns: 260px 1fr; height: 100vh; font-family: 'Noto Sans KR', sans-serif; }
  
  /* --- 사이드바 --- */
  .manager-sidebar { background-color: #2C3E50; color: white; display: flex; flex-direction: column; padding: 25px; }
  .sidebar-header .logo { font-family: 'Nanum Myeongjo', serif; font-size: 1.8rem; color: white; text-decoration: none; cursor: pointer; }
  .sidebar-header .manager-mode { background-color: #3498DB; color: white; padding: 4px 8px; border-radius: 5px; font-size: 0.8rem; display: inline-block; margin-top: 10px; }
  .sidebar-nav { margin-top: 40px; display: flex; flex-direction: column; gap: 10px; }
  .sidebar-nav a { color: #ECF0F1; text-decoration: none; font-size: 1.1rem; padding: 15px 20px; border-radius: 8px; cursor: pointer; }
  .sidebar-nav a.active, .sidebar-nav a:hover { background-color: #34495E; }
  .sidebar-footer { margin-top: auto; }
  .btn-logout { width: 100%; padding: 12px; background-color: #E74C3C; color: white; border: none; border-radius: 8px; cursor: pointer; }
  
  /* --- 메인 콘텐츠 --- */
  .manager-content { background-color: #F4F6F9; padding: 40px; overflow-y: auto; }
  .content-header { margin-bottom: 30px; }
  .content-header.with-back-button { display: flex; align-items: center; gap: 20px; }
  .content-header h1 { font-size: 2.2rem; font-weight: 800; color: #2C3E50; margin: 0; }
  .content-header p { font-size: 1.1rem; color: #555; margin-top: 5px; }
  
  .card { background-color: #fff; border-radius: 12px; padding: 30px; margin-bottom: 25px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
  .card h4 { margin: 0 0 20px 0; font-size: 1.3rem; font-weight: 600; color: #333; }
  
  /* 매출 관리 */
  .dashboard-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 25px; }
  .metric-card { cursor: pointer; transition: all 0.2s; border: 2px solid transparent; }
  .metric-card.active { border-color: #3498DB; }
  .card .metric { font-size: 2.5rem; font-weight: 700; color: #2C3E50; margin: 0; }
  .chart-placeholder { height: 300px; display: flex; align-items: center; justify-content: center; background-color: #f0f0f0; border-radius: 8px; color: #888; }
  
  /* 숙소 유형 선택 */
  .property-type-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 25px; }
  .property-type-card { text-align: center; cursor: pointer; border: 1px solid #eee; padding: 30px; border-radius: 12px; }
  .property-type-card .icon { font-size: 3rem; }
  .property-type-card h3 { font-size: 1.5rem; margin: 15px 0 5px 0; }
  
  /* 숙소 목록 */
  .btn-back { background: none; border: 1px solid #ccc; color: #555; font-weight: 600; border-radius: 8px; }
  .property-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 25px; }
  .property-card { padding: 0; display: flex; flex-direction: column; cursor: pointer; overflow: hidden; }
  .property-image { width: 100%; height: 150px; object-fit: cover; }
  .property-info { padding: 20px; flex-grow: 1; }
  .property-manage-footer { background-color: #3498DB; color: white; padding: 12px; text-align: center; }
  .add-new-card { align-items: center; justify-content: center; border-style: dashed; color: #aaa; }
  .add-new-card .add-icon { font-size: 3rem; }
  
  /* --- 수정/등록 폼 --- */
  .edit-form-wrapper { max-width: 1200px; }
  .edit-form-layout { display: grid; grid-template-columns: 1fr 320px; gap: 30px; align-items: flex-start; }
  .form-main { min-width: 0; }
  .form-sidebar { min-width: 0; }
  .sticky-sidebar { position: sticky; top: 40px; }
  .save-panel { text-align: center; }
  .save-panel p { margin: 15px 0; font-size: 0.95rem; color: #666; }
  .btn-save { background-color: #27ae60; color: white; width: 100%; padding: 14px; font-size: 1.1rem; }
  .btn-cancel { background-color: #f0f0f0; color: #555; width: 100%; padding: 14px; font-size: 1.1rem; }
  
  /* 사진 관리 */
  .photo-management-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 15px; }
  .main-photo { grid-column: span 3; grid-row: span 2; }
  .sub-photo { grid-column: span 1; grid-row: span 1; }
  .main-photo, .sub-photo { position: relative; border-radius: 8px; overflow: hidden; background-color: #f0f0f0; aspect-ratio: 4 / 3; }
  .photo-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 3rem; color: #ccc; }
  .main-photo img, .sub-photo img { width: 100%; height: 100%; object-fit: cover; }
  .btn-photo-edit { position: absolute; bottom: 8px; right: 8px; background-color: rgba(0,0,0,0.5); color: white; border: none; padding: 5px 10px; font-size: 0.8rem; border-radius: 5px; cursor: pointer; }
  .btn-photo-edit.small { padding: 4px 8px; font-size: 0.75rem; }
  
  /* 폼 그룹 */
  .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
  .form-group.full-width { grid-column: span 2; }
  .form-group label { display: block; font-weight: 600; margin-bottom: 8px; }
  .form-group .required { color: #E74C3C; }
  input[type="text"], input[type="number"], select { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 6px; }
  
  /* 객실 관리 */
  .room-edit-card { display: flex; gap: 15px; align-items: center; margin-bottom: 15px; background-color: #f9f9f9; padding: 15px; border-radius: 8px; }
  .room-photo { width: 100px; height: 75px; flex-shrink: 0; position: relative; border-radius: 6px; overflow: hidden; background-color: #e9e9e9; }
  .room-photo .photo-placeholder.small { font-size: 1.5rem; }
  .room-inputs { flex-grow: 1; display: flex; flex-direction: column; gap: 10px; }
  .btn-remove-room { background-color: #E74C3C; color: white; padding: 8px 12px; font-size: 1rem; }
  .btn-add-room { background: none; border: 1px dashed #ccc; width: 100%; padding: 12px; margin-top: 10px; color: #555; font-weight: 600; }
  
  /* 편의시설 */
  .amenities-checkbox-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; }
  .amenities-checkbox-grid label { display: flex; align-items: center; gap: 8px; font-size: 0.95rem; }
  
  /* 리뷰 관리 */
  .review-list { display: flex; flex-direction: column; gap: 20px; }
  .review-header { display: flex; align-items: flex-start; gap: 15px; margin-bottom: 10px; }
  .review-hotel-name { font-size: 0.9rem; color: #888; margin-top: 4px; }
  .review-rating { margin-left: auto; color: #F39C12; }
  .review-actions textarea { height: 80px; width: 100%; box-sizing: border-box; margin-bottom: 10px; }
  .action-buttons { display: flex; justify-content: flex-end; gap: 10px; }
  .btn-delete-request { background-color: #E74C3C; color: white; }
  button { padding: 12px 25px; font-size: 1rem; font-weight: 600; border-radius: 8px; border: none; cursor: pointer; background-color: #3498DB; color: white; }
  </style>