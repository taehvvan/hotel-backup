<template>
    <div class="admin-dashboard">
      <aside class="admin-sidebar">
        <div class="sidebar-header">
          <a @click="activeView = 'dashboard'" class="logo">쉼, 한국</a>
          <p class="admin-mode">사이트 관리자</p>
        </div>
        <nav class="sidebar-nav">
          <a @click="activeView = 'dashboard'" :class="{ active: activeView === 'dashboard' }">📊 대시보드 (매출)</a>
          <a @click="activeView = 'destinations'; destinationView = 'list'" :class="{ active: activeView === 'destinations' }">🏞️ 추천 여행지 관리</a>
          <a @click="activeView = 'approvals'" :class="{ active: activeView === 'approvals' }">✅ 호텔/매니저 승인</a>
          <a @click="activeView = 'hotels'; hotelMgmtView = 'typeSelection'" :class="{ active: activeView === 'hotels' }">🏨 호텔 관리</a>
          <a @click="activeView = 'users'" :class="{ active: activeView === 'users' }">👥 사용자 관리</a>
          <a @click="activeView = 'reviews'" :class="{ active: activeView === 'reviews' }">📝 리뷰 삭제 요청</a>
        </nav>
        <div class="sidebar-footer">
          <button class="btn-logout">로그아웃</button>
        </div>
      </aside>
  
      <main class="admin-content">
        <section v-if="activeView === 'dashboard'" class="content-section">
          <header class="content-header">
            <h1>사이트 전체 현황</h1>
            <p>플랫폼의 주요 지표를 확인합니다.</p>
          </header>
          <div class="dashboard-grid">
            <div class="card"><h4>총 플랫폼 매출</h4><p class="metric">₩{{ siteSales.totalRevenue.toLocaleString() }}</p></div>
            <div class="card"><h4>월간 수수료 수익</h4><p class="metric">₩{{ siteSales.monthlyFee.toLocaleString() }}</p></div>
            <div class="card"><h4>신규 가입자 (월)</h4><p class="metric">{{ siteSales.newUsers.toLocaleString() }}명</p></div>
            <div class="card"><h4>신규 호텔 (월)</h4><p class="metric">{{ siteSales.newHotels }}개</p></div>
          </div>
          <div class="chart-grid">
            <div class="card chart-card"><h4>지역별 매출 분포</h4><div class="chart-placeholder"><p>지역별 매출 파이 차트</p></div></div>
            <div class="card chart-card"><h4>월별 총 매출 추이</h4><div class="chart-placeholder"><p>월별 총 매출 라인 차트</p></div></div>
          </div>
        </section>
  
        <section v-if="activeView === 'destinations'" class="content-section">
          <div v-if="destinationView === 'list'">
            <header class="content-header"><h1>추천 여행지 / 배너 관리</h1></header>
            <div class="card">
              <div class="card-header">
                <h4>등록된 여행지 목록</h4>
                <button @click="showDestinationForm(null)">+ 새 추천 여행지 등록</button>
              </div>
              <table>
                <thead><tr><th>ID</th><th>이미지</th><th>제목</th><th>상태</th><th>관리</th></tr></thead>
                <tbody>
                  <tr v-for="dest in recommendedDestinations" :key="dest.id">
                    <td>{{ dest.id }}</td>
                    <td><img :src="dest.image" class="preview-image"></td>
                    <td>{{ dest.title }}</td>
                    <td><label class="switch"><input type="checkbox" :checked="dest.active"><span class="slider round"></span></label></td>
                    <td>
                      <button @click="showDestinationForm(dest)" class="btn-sm btn-secondary">수정</button>
                      <button class="btn-sm btn-danger">삭제</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div v-if="destinationView === 'form'">
            <header class="content-header with-back-button">
              <button @click="destinationView = 'list'" class="btn-back">‹ 목록으로</button>
              <div><h1>추천 여행지 등록/수정</h1></div>
            </header>
            <div class="card">
              <div class="form-group"><label>제목</label><input type="text" v-model="editingDestination.title"></div>
              <div class="form-group"><label>설명</label><textarea rows="3" v-model="editingDestination.description"></textarea></div>
              <div class="form-group"><label>이미지</label><input type="file"></div>
              <div class="form-group"><label>연결 링크 (URL)</label><input type="text" v-model="editingDestination.link"></div>
              <button @click="destinationView = 'list'">저장하기</button>
            </div>
          </div>
        </section>
  
        <section v-if="activeView === 'approvals'" class="content-section">
          <header class="content-header"><h1>호텔 및 매니저 승인</h1><p>등록 대기 중인 호텔과 매니저 계정을 심사하고 승인합니다.</p></header>
          <div class="tabs">
            <button @click="approvalView = 'hotels'" :class="{ active: approvalView === 'hotels' }">호텔 승인 대기 ({{ pendingHotels.length }})</button>
            <button @click="approvalView = 'managers'" :class="{ active: approvalView === 'managers' }">매니저 승인 대기 ({{ pendingManagers.length }})</button>
          </div>
          <div class="card">
            <div v-if="approvalView === 'hotels'">
              <table>
                <thead><tr><th>상호명</th><th>사업자번호</th><th>신청일</th><th>관리</th></tr></thead>
                <tbody>
                  <tr v-for="hotel in pendingHotels" :key="hotel.id">
                    <td>{{ hotel.name }}</td><td>{{ hotel.bizNumber }}</td><td>{{ hotel.date }}</td>
                    <td><button class="btn-sm btn-approve">승인</button><button class="btn-sm btn-danger">반려</button></td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div v-if="approvalView === 'managers'">
               <table>
                <thead><tr><th>이름</th><th>이메일</th><th>신청일</th><th>관리</th></tr></thead>
                <tbody>
                  <tr v-for="manager in pendingManagers" :key="manager.id">
                    <td>{{ manager.name }}</td><td>{{ manager.email }}</td><td>{{ manager.date }}</td>
                    <td><button class="btn-sm btn-approve">승인</button><button class="btn-sm btn-danger">반려</button></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </section>
  
        <section v-if="activeView === 'hotels'" class="content-section">
          <div v-if="hotelMgmtView === 'typeSelection'">
            <header class="content-header"><h1>호텔 관리</h1><p>사이트에 등록된 모든 숙소를 관리합니다.</p></header>
            <div class="property-type-grid">
              <div class="property-type-card" @click="selectHotelType('호텔')"><h3>호텔</h3></div>
              <div class="property-type-card" @click="selectHotelType('펜션')"><h3>펜션 & 풀빌라</h3></div>
              <div class="property-type-card" @click="selectHotelType('한옥')"><h3>한옥</h3></div>
            </div>
          </div>
          <div v-if="hotelMgmtView === 'list'">
            <header class="content-header with-back-button">
              <button @click="hotelMgmtView = 'typeSelection'" class="btn-back">‹ 뒤로</button>
              <div><h1>{{ selectedHotelType }} 전체 목록</h1></div>
            </header>
            <div class="card">
              <table>
                <thead><tr><th>숙소명</th><th>지역</th><th>평점</th><th>월 매출</th><th>상태</th><th>관리</th></tr></thead>
                <tbody>
                  <tr v-for="hotel in allHotels" :key="hotel.id">
                    <td><strong>{{ hotel.name }}</strong></td><td>{{ hotel.location }}</td>
                    <td>⭐ {{ hotel.rating }}</td><td>₩{{ hotel.monthlySales.toLocaleString() }}</td>
                    <td><label class="switch"><input type="checkbox" :checked="hotel.isActive"><span class="slider round"></span></label></td>
                    <td><button class="btn-sm btn-secondary" @click="editHotel(hotel)">수정</button></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div v-if="hotelMgmtView === 'edit' && editableHotel" class="hotel-detail-page-as-editor">
             <header class="content-header with-back-button">
              <button @click="hotelMgmtView = 'list'" class="btn-back">‹ 목록으로</button>
              <div><h1>{{ editableHotel.name }} 수정</h1></div>
            </header>
            <div class="hotel-detail-page">
               <main class="detail-main-content">
                <div class="content-column">
                  <section class="detail-section">
                    <h2>기본 정보</h2>
                    <input type="text" v-model="editableHotel.name">
                  </section>
                </div>
                <aside class="sidebar-column">
                  <div class="sticky-sidebar">
                    <div class="card">
                      <h4>숙소 상태</h4>
                      <div class="status-toggle">
                        <span>비활성화</span>
                        <label class="switch"><input type="checkbox" v-model="editableHotel.isActive"><span class="slider round"></span></label>
                        <span>활성화</span>
                      </div>
                      <hr>
                      <button class="btn-save" @click="hotelMgmtView = 'list'">변경사항 저장</button>
                      <button class="btn-cancel" @click="hotelMgmtView = 'list'">취소</button>
                    </div>
                  </div>
                </aside>
              </main>
            </div>
          </div>
        </section>
  
        <section v-if="activeView === 'users'" class="content-section">
          <header class="content-header"><h1>사용자 관리</h1></header>
          <div class="card">
            <div class="card-header">
              <h4>사용자 목록</h4>
              <input type="text" placeholder="이름, 이메일로 검색..." class="search-input">
            </div>
            <table>
              <thead><tr><th>ID</th><th>이름</th><th>이메일</th><th>가입일</th><th>관리</th></tr></thead>
              <tbody>
                <tr v-for="user in users" :key="user.id">
                  <td>{{ user.id }}</td><td>{{ user.name }}</td><td>{{ user.email }}</td><td>{{ user.joinDate }}</td>
                  <td><button class="btn-sm btn-secondary">쿠폰 지급</button><button class="btn-sm btn-danger">계정 삭제</button></td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
  
        <section v-if="activeView === 'reviews'" class="content-section">
          <header class="content-header"><h1>리뷰 삭제 요청 관리</h1></header>
          <div class="review-request-list">
            <div v-for="req in reviewDeletionRequests" :key="req.id" class="card review-request-card">
              <div class="review-content">
                <p><strong>작성자:</strong> {{ req.review.userName }} | <strong>숙소:</strong> {{ req.review.hotelName }}</p>
                <p class="review-text">"{{ req.review.text }}"</p>
              </div>
              <div class="request-reason">
                <p><strong>요청 사유:</strong> {{ req.reason }}</p>
                <div class="action-buttons">
                  <button class="btn-approve">삭제 승인</button>
                  <button class="btn-secondary">요청 반려</button>
                </div>
              </div>
            </div>
          </div>
        </section>
  
      </main>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const activeView = ref('dashboard');
  const approvalView = ref('hotels');
  const hotelMgmtView = ref('typeSelection');
  const selectedHotelType = ref(null);
  const destinationView = ref('list');
  const editingDestination = ref(null);
  const editableHotel = ref(null);
  
  const siteSales = ref({ totalRevenue: 1258000000, monthlyFee: 12580000, newUsers: 1240, newHotels: 32 });
  const recommendedDestinations = ref([ { id: 1, image: 'https://via.placeholder.com/100x50', title: '경주의 가을', description: '', link: '', active: true }, { id: 2, image: 'https://via.placeholder.com/100x50', title: '제주 해변 특가', description: '', link: '', active: true } ]);
  const pendingHotels = ref([ { id: 1, name: '(주)새로운호텔', bizNumber: '111-22-33333', date: '2025-09-10' } ]);
  const pendingManagers = ref([ { id: 1, name: '홍길동', email: 'hong@example.com', date: '2025-09-11' } ]);
  const users = ref([ { id: 1, name: '김사용', email: 'user1@example.com', joinDate: '2025-01-05' }, { id: 2, name: '이예약', email: 'user2@example.com', joinDate: '2025-03-10' } ]);
  const reviewDeletionRequests = ref([ { id: 1, reason: '허위 사실 및 비방', review: { id: 2, userName: '박호캉스', hotelName: '고요, 경주 한옥', text: '방음이 너무 안돼서 잠을 설쳤습니다.' } } ]);
  const allHotels = ref([
    { id: 1, name: '쉼, 서울 호텔', location: '서울 중구', rating: 4.8, monthlySales: 15000000, isActive: true },
    { id: 2, name: '고요, 경주 한옥', location: '경북 경주시', rating: 4.9, monthlySales: 8500000, isActive: true },
    { id: 3, name: '오션뷰, 부산 펜션', location: '부산 해운대구', rating: 4.5, monthlySales: 6000000, isActive: false },
  ]);
  
  const selectHotelType = (type) => { selectedHotelType.value = type; hotelMgmtView.value = 'list'; };
  const showDestinationForm = (dest) => {
    if (dest) editingDestination.value = { ...dest };
    else editingDestination.value = { id: Date.now(), title: '', description: '', link: '', image: '', active: true };
    destinationView.value = 'form';
  };
  const editHotel = (hotel) => {
    editableHotel.value = JSON.parse(JSON.stringify(hotel));
    hotelMgmtView.value = 'edit';
  };
  </script>
  
  <style scoped>
  @import url('https://fonts.googleapis.com/css2?family=Nanum+Myeongjo:wght@700&display=swap');
  @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700;800&display=swap');
  
  .admin-dashboard { display: grid; grid-template-columns: 260px 1fr; height: 100vh; font-family: 'Noto Sans KR', sans-serif; }
  
  .admin-sidebar { background-color: #1F2937; color: white; display: flex; flex-direction: column; padding: 25px; }
  .sidebar-header .logo { font-family: 'Nanum Myeongjo', serif; font-size: 1.8rem; color: white; text-decoration: none; cursor: pointer; }
  .sidebar-header .admin-mode { background-color: #4F46E5; padding: 4px 8px; border-radius: 5px; font-size: 0.8rem; display: inline-block; margin-top: 10px; }
  .sidebar-nav { margin-top: 40px; display: flex; flex-direction: column; gap: 10px; }
  .sidebar-nav a { color: #D1D5DB; text-decoration: none; font-size: 1.1rem; padding: 15px 20px; border-radius: 8px; cursor: pointer; }
  .sidebar-nav a.active, .sidebar-nav a:hover { background-color: #374151; color: #fff; }
  .sidebar-footer { margin-top: auto; }
  .btn-logout { width: 100%; padding: 12px; background-color: #D9534F; color: white; border: none; font-size: 1rem; border-radius: 8px; cursor: pointer; }
  
  .admin-content { background-color: #F9FAFB; padding: 40px; overflow-y: auto; }
  .content-header { margin-bottom: 30px; }
  .content-header.with-back-button { display: flex; align-items: center; gap: 20px; }
  .content-header h1 { font-size: 2.5rem; font-weight: 800; color: #111827; margin: 0; }
  .content-header p { font-size: 1.1rem; color: #6B7280; margin-top: 5px; }
  
  .card { background-color: #fff; border-radius: 12px; padding: 25px; margin-bottom: 25px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
  .card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
  .card h4 { margin: 0; font-size: 1.2rem; font-weight: 600; color: #374151; }
  
  .dashboard-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 25px; }
  .card .metric { font-size: 2.5rem; font-weight: 700; color: #111827; margin: 10px 0 0 0; }
  .chart-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 25px; }
  .chart-placeholder { height: 300px; display: flex; align-items: center; justify-content: center; background-color: #f3f4f6; border-radius: 8px; color: #9ca3af; }
  
  table { width: 100%; border-collapse: collapse; }
  th, td { padding: 15px; text-align: left; border-bottom: 1px solid #eee; }
  th { font-weight: 600; color: #6B7280; font-size: 0.9rem; }
  .preview-image { width: 80px; height: 40px; object-fit: cover; border-radius: 4px; }
  td button { margin-right: 5px; }
  
  .tabs { margin-bottom: 20px; display: flex; gap: 10px; border-bottom: 1px solid #ddd; }
  .tabs button { background: none; border: none; padding: 15px 20px; font-size: 1.1rem; font-weight: 600; color: #6B7280; cursor: pointer; border-bottom: 3px solid transparent; }
  .tabs button.active { color: #4F46E5; border-bottom-color: #4F46E5; }
  
  .form-group { margin-bottom: 20px; }
  .form-group label { display: block; font-weight: 500; margin-bottom: 8px; }
  .form-group input, .form-group textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box; }
  
  .property-type-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 25px; }
  .property-type-card { text-align: center; cursor: pointer; border: 1px solid #eee; padding: 30px; border-radius: 12px; }
  
  .btn-back { background-color: #f0f0f0; color: #333; }
  
  .hotel-detail-page-as-editor .detail-main-content { display: grid; grid-template-columns: 1fr 320px; gap: 30px; }
  .hotel-detail-page-as-editor .sticky-sidebar { position: sticky; top: 40px; }
  .status-toggle { display: flex; align-items: center; justify-content: space-between; }
  
  button { padding: 10px 20px; font-size: 0.95rem; font-weight: 600; border-radius: 8px; border: none; cursor: pointer; background-color: #4F46E5; color: white; }
  .btn-sm { padding: 6px 12px; font-size: 0.85rem; }
  .btn-secondary { background-color: #E5E7EB; color: #374151; }
  .btn-danger { background-color: #EF4444; }
  .btn-approve { background-color: #10B981; }
  .btn-save { background-color: #10B981; width: 100%; margin-bottom: 10px; }
  .btn-cancel { background-color: #E5E7EB; color: #374151; width: 100%; }
  
  .switch{position:relative;display:inline-block;width:50px;height:28px}.switch input{opacity:0;width:0;height:0}.slider{position:absolute;cursor:pointer;top:0;left:0;right:0;bottom:0;background-color:#ccc;transition:.4s}.slider:before{position:absolute;content:"";height:20px;width:20px;left:4px;bottom:4px;background-color:#fff;transition:.4s}input:checked+.slider{background-color:#4F46E5}input:checked+.slider:before{transform:translateX(22px)}.slider.round{border-radius:34px}.slider.round:before{border-radius:50%}
  </style>