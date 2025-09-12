<template>
    <div class="register-container">
      <div class="register-wrapper">
        <div class="register-header">
          <router-link to="/" class="logo">쉼, 한국</router-link>
          <h2>호텔 매니저 신청</h2>
        </div>
  
        <form @submit.prevent="handleManagerRegister" class="register-form">
          <div class="form-section-group">
            <p class="group-title">계정 정보</p>
            <div class="input-group">
              <input type="text" v-model="formData.name" placeholder="이름" required>
            </div>
            <div class="input-group">
              <input type="email" v-model="formData.email" placeholder="이메일 주소" required>
            </div>
            <div class="input-group">
              <input type="password" v-model="formData.password" placeholder="비밀번호" required>
            </div>
          </div>
  
          <div class="form-section-group">
            <p class="group-title">사업자 정보</p>
            <div class="input-group">
              <input type="text" v-model="formData.companyName" placeholder="상호명" required>
            </div>
            <div class="input-group">
              <input type="text" v-model="formData.businessNumber" placeholder="사업자 등록번호" required>
            </div>
          </div>
          
          <button type="submit" class="btn-register">매니저 신청하기</button>
        </form>
      
        <div class="extra-links">
          <span>이미 계정이 있으신가요?</span>
          <router-link to="/login">로그인</router-link>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  
  const router = useRouter();
  
  const formData = ref({
    name: '',
    email: '',
    password: '',
    companyName: '',
    businessNumber: '',
  });
  
  const handleManagerRegister = () => {
    console.log('Manager Application Data:', formData.value);
    alert('호텔 매니저 신청이 완료되었습니다. 관리자 승인 후 활동하실 수 있습니다.');
    router.push('/');
  };
  </script>
  
  <style scoped>
  @import url('https://fonts.googleapis.com/css2?family=Nanum+Myeongjo:wght@700&display=swap');
  
  .register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background-color: #f8f9fa;
    padding: 20px;
    box-sizing: border-box;
  }
  
  .register-wrapper {
    width: 100%;
    max-width: 420px;
    text-align: center;
    background-color: #fff;
    padding: 50px;
    border-radius: 12px;
    box-shadow: 0 5px 20px rgba(0,0,0,0.05);
  }
  
  .register-header { margin-bottom: 30px; }
  .register-header .logo {
    font-family: 'Nanum Myeongjo', serif;
    font-size: 2.5rem;
    font-weight: 700;
    color: #333;
    text-decoration: none;
  }
  .register-header h2 {
    margin: 10px 0 0 0;
    font-size: 1.2rem;
    color: #666;
    font-weight: 500;
  }
  
  .form-section-group {
    margin-bottom: 25px;
    text-align: left;
  }
  .group-title {
    font-size: 1rem;
    font-weight: 600;
    color: #333;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
  }
  
  .input-group { margin-bottom: 15px; }
  .input-group input {
    width: 100%;
    padding: 14px 20px;
    font-size: 1rem;
    border: 1px solid #DCDCDC;
    border-radius: 8px;
    box-sizing: border-box;
  }
  .input-group input:focus {
    outline: none;
    border-color: #A0A0A0;
  }
  
  .btn-register {
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
  
  .extra-links {
    margin-top: 20px;
    font-size: 0.9rem;
  }
  .extra-links a {
    color: #0A2A66;
    text-decoration: none;
    font-weight: 600;
    margin-left: 5px;
  }
  .extra-links a:hover {
    text-decoration: underline;
  }
  </style>