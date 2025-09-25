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
          <div class="card"><h4>총 플랫폼 매출</h4><p class="metric">₩{{ dashboardData.summary.totalRevenue.toLocaleString() }}</p></div>
          <div class="card"><h4>월간 수수료 수익</h4><p class="metric">₩{{ dashboardData.summary.monthlyFee.toLocaleString() }}</p></div>
          <div class="card"><h4>신규 가입자 (월)</h4><p class="metric">{{ dashboardData.summary.newUsers.toLocaleString() }}명</p></div>
          <div class="card"><h4>신규 호텔 승인 대기</h4><p class="metric">{{ dashboardData.summary.pendingHotels }}개</p></div>
        </div>
        <div class="chart-grid">
          <div class="card chart-card"><h4>지역별 매출 분포</h4>
            <div class="chart-placeholder" ref="regionChart">
              <!-- 차트가 여기에 렌더링됩니다. -->
            </div>
          </div>
          <div class="card chart-card"><h4>월별 총 매출 추이</h4>
            <div class="chart-placeholder" ref="monthlySalesChart">
              <!-- 차트가 여기에 렌더링됩니다. -->
            </div>
          </div>
        </div>
      </section>
      
      <!-- 다른 섹션은 기존 코드와 동일 -->
      <section v-if="activeView === 'destinations'" class="content-section">...</section>
      <section v-if="activeView === 'approvals'" class="content-section">...</section>
      <section v-if="activeView === 'hotels'" class="content-section">...</section>
      <section v-if="activeView === 'users'" class="content-section">...</section>
      <section v-if="activeView === 'reviews'" class="content-section">...</section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

const activeView = ref('dashboard');
const dashboardData = ref({
  summary: { totalRevenue: 0, monthlyFee: 0, newUsers: 0, newHotels: 0 },
  salesByRegion: [],
  monthlySales: []
});

const regionChart = ref(null);
const monthlySalesChart = ref(null);

let regionChartInstance = null;
let monthlySalesChartInstance = null;

const fetchDashboardData = async () => {
  try {
    const token = localStorage.getItem('accessToken');
    if (!token) return;

    const [summaryRes, regionRes, monthlyRes] = await Promise.all([
      axios.get('http://localhost:8888/api/admin/dashboard/summary', { headers: { Authorization: `Bearer ${token}` } }),
      axios.get('http://localhost:8888/api/admin/dashboard/sales-by-region', { headers: { Authorization: `Bearer ${token}` } }),
      axios.get('http://localhost:8888/api/admin/dashboard/monthly-sales', { headers: { Authorization: `Bearer ${token}` } }),
    ]);

    dashboardData.value.summary = summaryRes.data;
    dashboardData.value.salesByRegion = regionRes.data;
    dashboardData.value.monthlySales = monthlyRes.data;

  } catch (error) {
    console.error('대시보드 데이터 로드 실패:', error);
  }
};

const renderCharts = () => {
  // 지역별 매출 차트 (도넛 차트)
  if (regionChartInstance) { regionChartInstance.destroy(); }
  if (regionChart.value && dashboardData.value.salesByRegion.length > 0) {
    regionChartInstance = new Chart(regionChart.value, {
      type: 'doughnut',
      data: {
        labels: dashboardData.value.salesByRegion.map(item => item.region),
        datasets: [{
          data: dashboardData.value.salesByRegion.map(item => item.totalSales),
          backgroundColor: ['#4A69A1', '#5B84B1', '#72A4C8', '#8CC4E0', '#B0D7EE'],
        }]
      },
    });
  }

  // 월별 총 매출 차트 (라인 차트)
  if (monthlySalesChartInstance) { monthlySalesChartInstance.destroy(); }
  if (monthlySalesChart.value && dashboardData.value.monthlySales.length > 0) {
    monthlySalesChartInstance = new Chart(monthlySalesChart.value, {
      type: 'line',
      data: {
        labels: dashboardData.value.monthlySales.map(item => item.month),
        datasets: [{
          label: '총 매출',
          data: dashboardData.value.monthlySales.map(item => item.totalSales),
          borderColor: '#4A69A1',
          tension: 0.1,
        }]
      },
    });
  }
};

onMounted(() => {
  fetchDashboardData();
});

watch(dashboardData, () => {
  if (activeView.value === 'dashboard') {
    renderCharts();
  }
}, { deep: true });

watch(activeView, (newView) => {
  if (newView === 'dashboard') {
    renderCharts();
  }
});

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
