<template>
    <div class="login-container">
      <div class="login-wrapper">
        <div class="login-header">
          <router-link to="/" class="logo">쉼, 한국</router-link>
          <h2>호텔 매니저 로그인</h2>
        </div>
  
        <div class="input-group">
          <input type="email" id="email" v-model="email" placeholder="이메일 주소" required>
        </div>
        <div class="input-group">
          <input type="password" id="password" v-model="password" placeholder="비밀번호" required>
        </div>
        <button type="button" class="btn-login-email" @click="handleManagerLogin">매니저 로그인</button>
  
        <div class="extra-links">
          <a href="#">비밀번호 찾기</a>
          <span class="divider">|</span>
          <router-link to="/manager-register">매니저 가입</router-link>
        </div>
        
        <div class="separator"></div>
  
        <div class="user-login-link">
          <router-link to="/login">일반 회원이신가요?</router-link>
        </div>
  
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const email = ref('');
  const password = ref('');
  
  const handleManagerLogin = () => {
    // 실제 앱에서는 관리자 로그인 API를 호출합니다.
    alert(`${email.value} 계정으로 매니저 로그인을 시도합니다.`);
  };
  </script>
  
  <style scoped>
  /* Login.vue와 동일한 스타일을 사용합니다. */
  .login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background-color: #f8f9fa;
    padding: 20px;
    box-sizing: border-box;
  }
  
  .login-wrapper {
    width: 100%;
    max-width: 380px;
    text-align: center;
  }
  
  .login-header { margin-bottom: 30px; }
  .login-header .logo { font-family: 'Nanum Myeongjo', serif; font-size: 2.5rem; font-weight: 700; color: #333; text-decoration: none; }
  .login-header h2 { margin: 10px 0 0 0; font-size: 1.2rem; color: #666; font-weight: 500; }
  
  .input-group { margin-bottom: 15px; }
  .input-group input {
    width: 100%;
    padding: 14px 20px;
    font-size: 1rem;
    border: 1px solid #DCDCDC;
    border-radius: 8px;
    box-sizing: border-box;
  }
  .input-group input:focus { outline: none; border-color: #A0A0A0; }
  
  .btn-login-email {
    width: 100%;
    padding: 14px;
    margin-top: 10px;
    font-size: 1rem;
    font-weight: 700;
    color: #fff;
    background-color: #333;
    border: none;
    border-radius: 8px;
    cursor: pointer;
  }
  
  .extra-links { margin-top: 20px; font-size: 0.9rem; }
  .extra-links a { color: #555; text-decoration: none; }
  .extra-links .divider { margin: 0 10px; color: #ccc; }
  
  .separator {
    display: flex;
    align-items: center;
    text-align: center;
    color: #aaa;
    margin: 40px 0 20px 0;
  }
  .separator::before,
  .separator::after {
    content: '';
    flex: 1;
    border-bottom: 1px solid #eee;
  }
  .separator:not(:empty)::before { margin-right: .25em; }
  .separator:not(:empty)::after { margin-left: .25em; }
  
  .user-login-link a {
    font-size: 0.95rem;
    font-weight: 500;
    color: #555;
    text-decoration: none;
  }
  </style>