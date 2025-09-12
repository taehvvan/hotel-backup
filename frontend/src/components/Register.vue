<template>
  <div class="register-container">
    <div class="register-wrapper">
      <div class="register-header">
        <router-link to="/" class="logo">쉼, 한국</router-link>
        <h2>회원가입</h2>
      </div>

      <form @submit.prevent="handleRegister" class="register-form">
        <div class="input-group">
          <input type="text" id="name" v-model="name" placeholder="이름" required>
        </div>
        <div class="input-group">
          <input type="email" id="email" v-model="email" placeholder="이메일 주소" required>
        </div>
        <div class="input-group">
          <input type="password" id="password" v-model="password" placeholder="비밀번호" required>
        </div>
        <div class="input-group">
          <input type="password" id="passwordConfirm" v-model="passwordConfirm" placeholder="비밀번호 확인" required>
        </div>
        <button type="submit" class="btn-register">회원가입</button>
      </form>

      <div class="extra-links">
        <span>이미 계정이 있으신가요?</span>
        <router-link to="/login">로그인</router-link>
      </div>

      <div class="separator"><span>SNS 계정으로 간편 가입</span></div>

      <div class="social-login">
        <button type="button" class="btn-social google">
          <img src="https://img.icons8.com/color/48/google-logo.png" alt="Google"/>
          Google 계정으로 가입
        </button>
        <button type="button" class="btn-social kakao">
          <img src="https://www.kakaocorp.com/page/favicon.ico" alt="Kakao"/>
          카카오 계정으로 가입
        </button>
      </div>

      <div class="separator"></div>

      <div class="manager-signup">
        <router-link to="/manager-register">호텔 관리자이신가요?</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();

// 상태 변수 선언
const name = ref('');
const email = ref('');
const password = ref('');
const passwordConfirm = ref('');
const phone = ref('');
const birth = ref('');
const social = ref('');

// 회원가입 처리 함수
const handleRegister = async () => {
  if (password.value !== passwordConfirm.value) {
    alert('비밀번호가 일치하지 않습니다.');
    return;
  }

  try {
    // 요청할 데이터 준비
    const data = {
      name: name.value,
      email: email.value,
      password: password.value,
      passwordConfirm: passwordConfirm.value,
      phone: phone.value,
      birth: birth.value,
      social: social.value,
      role: 'ROLE_USER' // 기본 역할: ROLE_USER
    };

    // 백엔드에 요청 보내기
    const response = await axios.post('http://localhost:8888/api/register', data);

    // 성공시 처리
    if (response.data.success) {
      alert('회원가입이 완료되었습니다!');
      router.push({ name: 'RegisterSuccess', query: { name: name.value } });
    } else {
      alert('회원가입에 실패했습니다.');
    }
  } catch (error) {
    const message = error.response?.data?.message || error.message;
    alert('회원가입 요청 실패: ' + message);
  }
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
  max-width: 400px;
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

.separator {
  display: flex;
  align-items: center;
  text-align: center;
  color: #aaa;
  margin: 30px 0;
}
.separator::before, .separator::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid #eee;
}
.separator span { padding: 0 15px; font-size: 0.9rem; }

.social-login {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.btn-social {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background-color: #fff;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
}
.btn-social img { width: 24px; height: 24px; }
.btn-social.kakao { background-color: #FEE500; border-color: #FEE500; }

.manager-signup { text-align: center; }
.manager-signup a {
  color: #555;
  font-weight: 500;
  text-decoration: none;
  font-size: 0.95rem;
}
.manager-signup a:hover {
  text-decoration: underline;
}
</style>