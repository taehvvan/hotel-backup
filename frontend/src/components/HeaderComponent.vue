<template>
  <header class="main-header">
    <div class="content-wrapper">
      <router-link to="/" class="logo">
        <h1>쉼, 한국</h1>
      </router-link>
      <nav class="main-nav">
        <router-link to="/">홈</router-link>
        <router-link to="/accommodations">지역</router-link>
        <router-link to="/landmarks">랜드마크</router-link>
        <router-link to="/heritage">문화재</router-link>



      </nav>
      <div class="user-nav">
        <router-link to="/booking-check" class="nav-link">예약확인</router-link>
        <router-link to="/wishlist" class="nav-link favorites">❤️ 찜하기</router-link>
        <div class="nav-divider"></div>
        <router-link to="/login" class="nav-link">로그인</router-link>
        <router-link to="/register" class="signup-btn">회원가입</router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
// 이 컴포넌트는 스크립트가 필요 없습니다.
</script>

<style scoped>
/* [추가] '나눔명조' 폰트 import */
@import url('https://fonts.googleapis.com/css2?family=Nanum+Myeongjo:wght@700&display=swap');

.content-wrapper {
    max-width: 100%;
    padding: 0 5%;
    box-sizing: border-box;
}

.main-header {
    height: 80px;
    display: flex;
    align-items: center;
    position: sticky;
    top: 0;
    z-index: 990;
    border-bottom: 1px solid #E5E5E5;
    background-color: #FFFFFF;
    transition: background-color 0.3s ease;
    justify-content: space-between; /* 기본적으로 좌우 정렬 */
}

.main-header .content-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 0 5%;
    box-sizing: border-box;
}

.logo {
    justify-self: start;
    text-decoration: none;
    display: flex;
    align-items: center;
}

/* "쉼, 한국" 옆에 글자가 나오게 */
.logo h1 {
    font-family: 'Nanum Myeongjo', serif;
    font-size: 1.8rem;
    font-weight: 700;
    letter-spacing: 2px;
    color: #222222;
    margin: 0;
}

.main-nav {
    display: flex;
    gap: 1.5rem;
}

.main-nav a {
    text-decoration: none;
    color: #222222;
    font-weight: 500;
    padding: 10px 15px;
    border-radius: 8px;
    transition: background-color 0.3s ease;
}

.main-nav a:hover {
    background-color: #e9e9e9;
}



.main-nav .more:hover {
    background-color: #0356cc;
}

.user-nav {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.user-nav .nav-link {
    color: #222222;
    text-decoration: none;
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 500;
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 8px;
    transition: background-color 0.3s ease;
}

.user-nav .nav-link:hover {
    background-color: #f0f0f0;
}

.nav-divider {
    width: 1px;
    height: 16px;
    background-color: #E5E5E5;
}

.signup-btn {
    background-color: #222222;
    color: #FFFFFF;
    border: none;
    border-radius: 30px;
    padding: 10px 20px;
    font-weight: 700;
    font-size: 0.9rem;
    cursor: pointer;
    transition: opacity 0.3s ease;
    text-decoration: none;
    display: inline-block;
}

.signup-btn:hover {
    opacity: 0.8;
}

/* 화면 크기 50% 이하일 때 적용되는 스타일 */
@media (max-width: 1600px) {
    .main-header .content-wrapper {
        justify-content: space-between; /* 메뉴 좌우 정렬 */
        padding: 0 3%;
    }




}
</style>
