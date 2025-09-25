<template>
  <div class="mypage-container">
  <div class="content-wrapper">
  <div class="user-profile">
  <!--<div class="profile-image">
  <img :src="userInfo.profileImage" alt="프로필 사진">
  </div>-->
  <div class="profile-info">
  <h3>{{ userInfo.name }}님</h3>
  <p>{{ userInfo.email }}</p>
  <div class="profile-actions">
  <button @click="changeTab('profile')">회원정보 수정</button>
  <button @click="logout">로그아웃</button>
  </div>
  </div>
  </div>
  
    <div class="mypage-content">
      <div class="mypage-menu">
        <button 
          :class="{ active: activeTab === 'reservations' }" 
          @click="changeTab('reservations')"
        >
          <span>예약 내역</span>
        </button>
        <button 
          :class="{ active: activeTab === 'liked' }" 
          @click="changeTab('liked')"
        >
          <span>찜한 숙소</span>
        </button>
        <button 
          :class="{ active: activeTab === 'reviews' }" 
          @click="changeTab('reviews')"
        >
          <span>내가 쓴 후기</span>
        </button>
        <button 
          :class="{ active: activeTab === 'coupons' }" 
          @click="changeTab('coupons')"
        >
          <span>쿠폰함</span>
        </button>
      </div>
  
      <div class="tab-content">
        <div v-if="activeTab === 'reservations'" class="tab-pane">
          <div class="section-header">
            <h4>예약 내역</h4>
            <span class="header-line"></span>
          </div>
          <div v-if="reservations.length > 0" class="reservation-list">
            <div v-for="reservation in reservations" :key="reservation.id" class="reservation-card">
              <div class="card-image">
                <img :src="reservation.image" :alt="reservation.placeName">
              </div>
              <div class="card-info">
                <h5 class="place-name">{{ reservation.placeName }}</h5>
                <p class="reservation-details">
                  <span><strong>체크인:</strong> {{ reservation.checkIn }}</span>
                  <span><strong>체크아웃:</strong> {{ reservation.checkOut }}</span>
                  <span><strong>인원:</strong> {{ reservation.guests }}</span>
                </p>
                <div class="reservation-actions">
                  <p class="status-badge" :class="reservation.status">{{ reservation.statusText }}</p>
                  <button 
                    v-if="reservation.status === 'completed'" 
                    @click="writeReview(reservation.id)"
                    class="btn-review-write"
                  >
                    후기 작성
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <p>아직 예약 내역이 없습니다. 새로운 쉼을 찾아 떠나보세요! ✨</p>
          </div>
        </div>
  
        <div v-else-if="activeTab === 'liked'" class="tab-pane">
          <div class="section-header">
            <h4>찜한 숙소</h4>
            <span class="header-line"></span>
          </div>
          <div v-if="likedItems.length > 0" class="liked-list">
            <div v-for="item in likedItems" :key="item.id" class="liked-card">
              <img :src="item.image" :alt="item.name" class="liked-image">
              <div class="liked-info">
                <h5>{{ item.name }}</h5>
                <p class="liked-location">{{ item.location }}</p>
                <span class="liked-price"><strong>{{ item.price.toLocaleString() }}</strong>원 / 박</span>
              </div>
              <button class="remove-btn">삭제</button>
            </div>
          </div>
          <div v-else class="empty-state">
            <p>마음에 드는 숙소를 찜하고 다음에 또 방문해보세요! ❤️</p>
          </div>
        </div>
  
        <div v-else-if="activeTab === 'reviews'" class="tab-pane">
          <div class="section-header">
            <h4>내가 쓴 후기</h4>
            <span class="header-line"></span>
          </div>
          <div class="empty-state">
            <p>아직 작성한 후기가 없습니다. 📝</p>
          </div>
        </div>
        
        <div v-else-if="activeTab === 'coupons'" class="tab-pane">
          <div class="section-header">
            <h4>쿠폰함</h4>
            <span class="header-line"></span>
          </div>
          <div v-if="coupons.length > 0" class="coupon-list">
            <div v-for="coupon in coupons" :key="coupon.id" class="coupon-card" :class="{ 'used': coupon.isUsed }">
              <div class="coupon-info">
                <h5 class="coupon-name">{{ coupon.name }}</h5>
                <p class="coupon-desc">{{ coupon.description }}</p>
                <p class="coupon-expiry">유효기간: {{ coupon.expiryDate }}</p>
              </div>
              <div class="coupon-status">
                <span v-if="coupon.isUsed" class="status-used">사용 완료</span>
                <span v-else class="status-available">사용 가능</span>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <p>현재 사용 가능한 쿠폰이 없습니다. 💸</p>
          </div>
        </div>
  
        <div v-else-if="activeTab === 'profile'" class="tab-pane">
      <div class="section-header">
          <h4>회원정보 수정</h4>
          <span class="header-line"></span>
      </div>
      <form @submit.prevent class="profile-form">
          <div class="form-group">
              <label for="name">이름</label>
              <div class="input-with-button">
                  <input type="text" id="name" v-model="editInfo.name" required>
              </div>
          </div>
          <div class="form-group">
              <label for="phone">전화번호</label>
              <div class="input-with-button">
                  <input type="tel" id="phone" v-model="editInfo.phone">
                  <button type="button" @click="updatePhoneNumber" class="btn-update-field">전화번호 저장</button>
              </div>
          </div>
  
          <div class="password-change-section">
            <h5 class="password-section-title">비밀번호 변경</h5>
              <div class="form-group">
                  <label for="old-password">기존 비밀번호</label>
                  <div class="input-with-button">
                  <input type="password" id="old-password" v-model="oldPassword" class="form-input">
                  <button type="button" @click="verifyOldPassword" class="btn-update-field" :disabled="!oldPassword">비밀번호 확인</button>
              </div>
          </div>
          
          <div v-if="isOldPasswordVerified">
                  <div class="form-group">
                    <label for="new-password">새 비밀번호</label>
                    <input
                      type="password"
                      id="new-password"
                      v-model="newPassword"
                      placeholder="새 비밀번호를 입력하세요."
                    >
                </div>
                <div class="form-group">
                    <label for="confirm-password">새 비밀번호 확인</label>
                    <input
                      type="password"
                      id="confirm-password"
                      v-model="confirmPassword"
                      placeholder="새 비밀번호를 다시 입력하세요."
                    >
                  </div>
                <button
                    type="button"
                    class="btn-update"
                    @click="changePassword"
                    :disabled="!newPassword || newPassword !== confirmPassword"
                  >
                    비밀번호 변경
                  </button>
                </div>
              <div v-else class="password-placeholder">
                  <p>기존 비밀번호 확인 후 새 비밀번호를 설정할 수 있습니다.</p>
                </div>
                </div>
              </form>
          </div>
      </div>
    </div>
  </div>
  
  </div>
  </template>
  
  <script setup>
  import { ref, reactive, onMounted } from 'vue';
  import axios from 'axios';
  import { useRouter } from 'vue-router';
  
  const activeTab = ref('reservations');
  
  const userInfo = reactive({
  name: '',
  email: '',
  //profileImage: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?q=80&w=1974&auto=format&fit=crop',
  });
  
  const editInfo = reactive({
  name: '',
  phone: '',
  //newPassword: '', // 사용하지 않음
  });
  
  const reservations = ref([
  { id: 1, placeName: '경주 한옥마을 스테이', image: 'https://images.unsplash.com/photo-1582268611958-ebfd161ef9cf?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80', checkIn: '2025.09.20', checkOut: '2025.09.22', guests: '성인 2명', status: 'completed', statusText: '이용 완료' },
  { id: 2, placeName: '설악산 펜션', image: 'https://images.unsplash.com/photo-1559810842-7041a99f18c2?q=80&w=1969&auto=format&fit=crop', checkIn: '2025.10.15', checkOut: '2025.10.18', guests: '성인 4명', status: 'upcoming', statusText: '예약 완료' },
  { id: 3, placeName: '제주 돌담집', image: 'https://images.unsplash.com/photo-1518780722-e4210a562f6b?q=80&w=1974&auto=format&fit=crop', checkIn: '2025.08.01', checkOut: '2025.08.05', guests: '성인 2명, 어린이 1명', status: 'cancelled', statusText: '취소됨' },
  ]);
  
  const likedItems = ref([
  { id: 101, name: '남산 한옥마을', location: '서울 중구', price: 250000, image: 'https://images.unsplash.com/photo-1616763327685-613d9406004b?q=80&w=2070&auto=format&fit=crop'},
  { id: 102, name: '부산 해운대 리조트', location: '부산 해운대구', price: 400000, image: 'https://images.unsplash.com/photo-1542314831-068cd1dbb5eb?q=80&w=2070&auto=format&fit=crop'},
  { id: 103, name: '강릉 오션뷰 펜션', location: '강원 강릉시', price: 180000, image: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=2070&auto=format&fit=crop'},
  ]);
  
  const coupons = ref([
  { id: 1, name: '신규 회원 가입 쿠폰', description: '숙소 10,000원 할인', expiryDate: '2025.12.31', isUsed: false },
  { id: 2, name: '가을 여행 5% 할인 쿠폰', description: '모든 숙소 5% 할인', expiryDate: '2025.11.30', isUsed: false },
  { id: 3, name: '지난 이벤트 당첨 쿠폰', description: '숙소 20,000원 할인', expiryDate: '2025.08.01', isUsed: true },
  ]);
  
  
  const oldPassword = ref('');
  const newPassword = ref('');
  const confirmPassword = ref('');
  const isOldPasswordVerified = ref(false);
  
  const router = useRouter();
  
  // 페이지 로딩 시 사용자 정보 불러오기
  onMounted(() => {
  fetchUserData();
  });
  
  // 사용자 정보 불러오기 (토큰 기반)
  const fetchUserData = async () => {
    try {
      console.log('Sending token:', localStorage.getItem('accessToken'));
      const response = await axios.get('http://localhost:8888/api/users/info', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
      userInfo.name = response.data.name;
      userInfo.email = response.data.email;
      editInfo.name = response.data.name;
      editInfo.phone = response.data.phone;
    } catch (error) {
      console.error('사용자 정보 가져오기 실패:', error);
      alert('사용자 정보를 불러오는 데 실패했습니다.');
      // 실패 시 로그인 페이지로 이동하는 로직이 있다면 주석 해제하여 사용하세요.
      // router.push('/login');
    }
  };
  
  const changeTab = (tabName) => {
  activeTab.value = tabName;
  };
  
  // 이름 업데이트 지금은 안쓰는데 추가 고민해봐야할듯함
  const updateName = async () => {
    try {
       const response = await axios.put('http://localhost:8888/api/users/name', {
       name: editInfo.name
      }, {
        headers: {
         'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
       }
      });
      alert(response.data.message);
      userInfo.name = editInfo.name;
    } catch (error) {
      console.error('이름 업데이트 실패:', error);
      alert(error.response.data.message || '이름 업데이트에 실패했습니다.');
    }
  };
  
  // 4. 전화번호 수정 함수 (새로 추가)
  const updatePhoneNumber = async () => {
    try {
      const response = await axios.put('http://localhost:8888/api/users/phone', {
        phone: editInfo.phone
      }, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
      alert(response.data.message);
      userInfo.phone = editInfo.phone;
    } catch (error) {
      console.error('전화번호 업데이트 실패:', error);
      alert(error.response.data.message || '전화번호 업데이트에 실패했습니다.');
    }
  };
  const verifyOldPassword = async () => {
    try {
      const response = await axios.post('http://localhost:8888/api/users/verify-password', {
       oldPassword: oldPassword.value
      }, {
       headers: {
         'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
      }
      });
      alert(response.data.message);
      isOldPasswordVerified.value = true;
    } catch (error) {
      console.error('비밀번호 확인 실패:', error);
      alert(error.response.data.message || '기존 비밀번호가 일치하지 않습니다.');
      isOldPasswordVerified.value = false;
    }
  };
  
  
  // 기존 변경
  const changePassword = async () => {
    if (newPassword.value !== confirmPassword.value) {
      alert('새 비밀번호와 비밀번호 확인이 일치하지 않습니다.');
      return;
    }
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    if (!passwordRegex.test(newPassword.value)) {
      alert('비밀번호는 최소 8자 이상이며, 영문, 숫자, 특수문자를 포함해야 합니다.');
      return;
    }
  
    try {
      const response = await axios.put('http://localhost:8888/api/users/password', {
        newPassword: newPassword.value,
        oldPassword: oldPassword.value // 기존 비밀번호를 함께 보냄
      }, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
      alert(response.data.message);
      // 비밀번호 변경 후 상태 초기화
      isOldPasswordVerified.value = false;
      oldPassword.value = '';
      newPassword.value = '';
      confirmPassword.value = '';
    } catch (error) {
      console.error('비밀번호 변경 실패:', error);
      alert(error.response.data.message || '비밀번호 변경에 실패했습니다.');
    }
  };
  
  const logout = () => {
  // 실제 로그아웃 로직 구현
  alert('로그아웃 되었습니다.');
  };
  
  const writeReview = (reservationId) => {
  // 실제 후기 작성 페이지로 라우팅하는 로직 구현
  //alert("${reservationId}번 예약에 대한 후기 작성 페이지로 이동합니다.");
  };
  </script>
  
  <style scoped>
  @import url('https://fonts.googleapis.com/css2?family=Nanum+Myeongjo:wght@400;700;800&family=Noto+Sans+KR:wght@300;400;500;700&display=swap');
  
  .mypage-container {
  min-height: 100vh;
  background-color: #F8F4EF; /* 옅은 미색 바탕 */
  padding: 60px 0;
  font-family: 'Noto Sans KR', sans-serif;
  color: #333;
  }
  
  .content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 40px;
  padding: 0 20px;
  }
  
  .user-profile {
  position: sticky;
  top: 40px;
  height: fit-content;
  background-color: rgba(255, 255, 255, 0.5); /* 은은한 반투명 */
  backdrop-filter: blur(8px);
  border: 1px solid #E0E0E0;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  }
  
  .profile-image img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #fff;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
  }
  
  .profile-info {
  margin-top: 20px;
  }
  .profile-info h3 {
  font-family: 'Nanum Myeongjo', serif;
  font-weight: 700;
  font-size: 1.5rem;
  margin-bottom: 5px;
  }
  .profile-info p {
  font-size: 0.9rem;
  color: #666;
  margin: 0;
  }
  .profile-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  justify-content: center;
  }
  .profile-actions button {
  background: none;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 8px 15px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
  }
  .profile-actions button:hover {
  background-color: #EFEFEF;
  }
  .profile-actions button:last-child {
  background-color: #333;
  color: #fff;
  border-color: #333;
  }
  .profile-actions button:last-child:hover {
  background-color: #000;
  }
  
  .mypage-content {
  background-color: #fff;
  border: 1px solid #E0E0E0;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  }
  
  .mypage-menu {
  display: flex;
  justify-content: space-around;
  margin-bottom: 40px;
  border-bottom: 2px solid #EEE;
  gap: 10px;
  }
  .mypage-menu button {
  flex: 1;
  padding: 15px 20px;
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  font-size: 1.1rem;
  font-weight: 600;
  color: #888;
  cursor: pointer;
  transition: all 0.3s;
  }
  .mypage-menu button.active {
  color: #333;
  border-bottom-color: #333;
  }
  .mypage-menu button:hover {
  color: #333;
  }
  
  .section-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: 30px;
  }
  .section-header h4 {
  font-family: 'Nanum Myeongjo', serif;
  font-weight: 800;
  font-size: 1.6rem;
  margin: 0;
  }
  .section-header .header-line {
  width: 100%;
  height: 2px;
  background: #333;
  margin-top: 5px;
  position: relative;
  }
  .section-header .header-line::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 40px;
  height: 2px;
  background: #E53935;
  }
  
  /* 예약 내역 */
  .reservation-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  }
  .reservation-card {
  display: flex;
  gap: 20px;
  border: 1px solid #E0E0E0;
  border-radius: 12px;
  padding: 15px;
  transition: box-shadow 0.2s;
  }
  .reservation-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  }
  .reservation-card .card-image {
  width: 150px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  }
  .reservation-card .card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  }
  .reservation-card .card-info {
  flex-grow: 1;
  text-align: left;
  }
  .reservation-card .place-name {
  font-weight: 700;
  font-size: 1.2rem;
  margin-top: 0;
  margin-bottom: 10px;
  }
  .reservation-card .reservation-details span {
  display: block;
  font-size: 0.9rem;
  color: #555;
  margin-bottom: 5px;
  }
  .reservation-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
  }
  .status-badge {
  display: inline-block;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  }
  .status-badge.upcoming {
  background-color: #E0F7FA;
  color: #00796B;
  }
  .status-badge.completed {
  background-color: #E8F5E9;
  color: #388E3C;
  }
  .status-badge.cancelled {
  background-color: #FFEBEE;
  color: #D32F2F;
  }
  .btn-review-write {
  background-color: #4A69A1;
  color: #fff;
  border: none;
  padding: 8px 15px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 600;
  }
  
  /* 찜한 숙소 */
  .liked-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  }
  .liked-card {
  display: grid;
  grid-template-columns: 120px 1fr auto;
  align-items: center;
  gap: 20px;
  border: 1px solid #E0E0E0;
  border-radius: 12px;
  padding: 15px;
  transition: box-shadow 0.2s;
  }
  .liked-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  }
  .liked-image {
  width: 100%;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
  }
  .liked-info {
  text-align: left;
  }
  .liked-info h5 {
  font-weight: 700;
  font-size: 1.1rem;
  margin: 0 0 5px;
  }
  .liked-location, .liked-price {
  font-size: 0.9rem;
  color: #666;
  margin: 0;
  }
  .liked-price strong {
  font-size: 1.1rem;
  color: #E53935;
  }
  .remove-btn {
  background: none;
  border: 1px solid #E53935;
  color: #E53935;
  padding: 8px 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  }
  .remove-btn:hover {
  background-color: #E53935;
  color: #fff;
  }
  
  /* 쿠폰함 */
  .coupon-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  }
  .coupon-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border: 2px solid #4A69A1;
  border-radius: 12px;
  padding: 20px;
  }
  .coupon-card.used {
  border-color: #ddd;
  background-color: #f5f5f5;
  color: #999;
  }
  .coupon-info h5 {
  margin: 0 0 5px;
  font-weight: 700;
  }
  .coupon-info p {
  margin: 0;
  font-size: 0.9rem;
  }
  .coupon-status {
  min-width: 80px;
  text-align: right;
  }
  .status-available {
  font-weight: 700;
  color: #4A69A1;
  }
  .status-used {
  color: #999;
  }
  
  /* 회원정보 수정 */
  .profile-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 500px;
  margin-top: 20px;
  }
  .form-group {
  text-align: left;
  }
  .form-group label {
  display: block;
  font-weight: 500;
  margin-bottom: 8px;
  color: #555;
  }
  .form-input {
    width: 100%;
    padding: 12px 15px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
  }
  .form-group input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  }
  .input-with-button {
    display: flex;
    gap: 10px;
    align-items: center;
  }
  .input-with-button input {
    flex-grow: 1; 
    padding: 12px 15px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
  }
  
  .btn-update-field {
    padding: 12px 15px;
    background-color: #4A69A1;
    color: #fff;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    white-space: nowrap;
    font-weight: 600;
  }
  .btn-update {
  width: 100%;
  padding: 15px;
  font-size: 1rem;
  font-weight: 700;
  color: #fff;
  background-color: #4A69A1;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
  }
  .input-with-button {
    display: flex;
    align-items: center;
    gap: 10px; 
  }
  
  .btn-update:hover {
  background-color: #3A5280;
  }
  
  .verification-input-group {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  }
  .verification-input-group input {
  flex-grow: 1;
  }
  .btn-verify {
  padding: 10px 15px;
  background-color: #4CAF50;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  }
  .btn-verify:disabled {
  background-color: #ccc;
  cursor: not-allowed;
  }
  
  .empty-state {
  text-align: center;
  padding: 50px 20px;
  color: #999;
  font-size: 1rem;
  background-color: #F8F4EF;
  border-radius: 12px;
  }
  
  /* 추가된 스타일 */
  
  .btn-update-field {
    padding: 12px 15px; 
    background-color: #4A69A1;
    color: #fff;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    white-space: nowrap;
    font-weight: 600;
  }
  
  .btn-update-field:hover {
    background-color: #3A5280;
  }
  
  .password-change-section {
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px; /* 비밀번호 섹션 내부 간격 통일 */
  }
  .password-change-section .btn-update-password {
    width: 100%;
    padding: 15px;
    font-size: 1rem;
    font-weight: 700;
    color: #fff;
    background-color: #E53935;
    border: none;
    border-radius: 8px;
    cursor: pointer;
  }
  .password-change-section .btn-update-password:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
  
  .btn-update-password {
    width: 100%;
    padding: 15px;
    font-size: 1rem;
    font-weight: 700;
    color: #fff;
    background-color: #E53935;
    border: none;
    border-radius: 8px;
    cursor: pointer;
  }
  .btn-update-password:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
  
  .password-placeholder {
    text-align: center;
    padding: 20px;
    background-color: #f5f5f5;
    border-radius: 8px;
    color: #777;
    font-style: italic;
  }
  .password-section-title {
      font-size: 1.2rem;
      font-weight: 700;
      margin-bottom: 0;
      border-bottom: 2px solid #333;
      padding-bottom: 5px;
  }
  </style>