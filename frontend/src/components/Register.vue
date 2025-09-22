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

        <div class="input-group email-check-group">
          <input type="email" id="email" v-model="email" placeholder="이메일 주소" required :disabled="isEmailVerified">
          <button type="button" class="btn-check-duplicate" @click="checkEmailDuplicate" :disabled="email.length === 0">
            {{ isEmailVerified ? '인증 완료' : '중복 확인' }}
          </button>
        </div>
        <p v-if="emailStatus" class="status-message">{{ emailStatus }}</p>

        <div v-if="!isEmailDuplicate && !isEmailVerified" class="input-group centered-btn-group">
          <button type="button" class="btn-send-code" @click="sendVerificationCode">
            {{ codeSent ? '재전송' : '인증번호 전송' }}
          </button>
        </div>
        <p v-if="verificationStatus" class="status-message">{{ verificationStatus }}</p>

        <div v-if="codeSent && !isEmailVerified" class="input-group verification-group">
          <input type="text" id="verificationCode" v-model="verificationCode" placeholder="인증번호" required>
          <button type="button" class="btn-verify-code" @click="verifyCode" :disabled="!verificationCode.length">
            인증 확인
          </button>
        </div>

        <div class="input-group">
          <input type="password" id="password" v-model="password" placeholder="비밀번호" required>
        </div>
        <div class="input-group">
          <input type="password" id="passwordConfirm" v-model="passwordConfirm" placeholder="비밀번호 확인" required>
        </div>
        
        <button type="submit" class="btn-register" :disabled="!isEmailVerified">회원가입</button>

      </form>

      <div class="extra-links">
        <span>이미 계정이 있으신가요?</span>
        <router-link to="/login">로그인</router-link>
      </div>

      <div class="separator"><span>SNS 계정으로 간편 가입</span></div>

      <div class="social-login">
        <button type="button" class="btn-social google" @click="handleGoogleLogin">
          <img src="https://img.icons8.com/color/48/google-logo.png" alt="Google"/>
          Google 계정으로 가입
        </button>
        <button type="button" class="btn-social kakao" @click="handleKakaoLogin">
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

// --- 새로운 상태 변수들 ---

const name = ref('');
const email = ref('');
const password = ref('');
const passwordConfirm = ref('');
const verificationCode = ref('');
const phone = ref('');
const birth = ref('');
const social = ref('');

const isEmailDuplicate = ref(true); // 이메일 중복 상태 (초기값: 중복)
const isEmailVerified = ref(false); // 이메일 인증 완료 상태
const codeSent = ref(false); // 인증번호 전송 상태

const emailStatus = ref(''); // 이메일 상태 메시지
const verificationStatus = ref(''); // 인증 상태 메시지

// --- 새로운 API 호출 함수들 ---
const checkEmailDuplicate = async () => {
  const trimmedEmail = email.value.trim();
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!emailRegex.test(trimmedEmail)) {
    emailStatus.value = '올바른 이메일 주소를 입력해주세요.';

    return;
  }

  try {
    const response = await axios.post('/api/check-email', { email: trimmedEmail });
    isEmailDuplicate.value = response.data.isDuplicate;

    if (response.data.isDuplicate) {
      emailStatus.value = '이미 사용 중인 이메일입니다.';
    } else {
      emailStatus.value = '사용 가능한 이메일입니다.';
    }
  } catch (error) {
    emailStatus.value = '이메일 중복 확인에 실패했습니다.';
  }
};

const sendVerificationCode = async () => {
  try {
    await axios.post('/api/send-code', { email: email.value.trim() });
    codeSent.value = true;
    verificationStatus.value = '인증번호가 전송되었습니다. 3분 이내에 입력해주세요.';
  } catch (error) {
    verificationStatus.value = '인증번호 전송에 실패했습니다. 다시 시도해주세요.';
  }
};

const verifyCode = async () => {
  try {
    const response = await axios.post('/api/verify-code', {
      email: email.value.trim(),
      code: verificationCode.value.trim()
    });

    if (response.data.message === '이메일 인증이 완료되었습니다.') {
      isEmailVerified.value = true;
      verificationStatus.value = '인증이 완료되었습니다.';
    } else {
      verificationStatus.value = '인증번호가 일치하지 않습니다.';
    }
  } catch (error) {
    verificationStatus.value = error.response?.data?.message || '인증번호 확인에 실패했습니다.';
  }
};

// --- 최종 회원가입 처리 함수 ---
const handleRegister = async () => {
  // 1. 공백 제거
  const trimmedName = name.value.trim();
  const trimmedEmail = email.value.trim();
  const trimmedPassword = password.value.trim();
  const trimmedPasswordConfirm = passwordConfirm.value.trim();

  // 2. 입력값 유효성 검사 (정규식 사용)
  const nameRegex = /^[가-힣a-zA-Z\s]{2,}$/;
  if (!nameRegex.test(trimmedName)) {
    alert('이름은 두 글자 이상의 한글 또는 영문만 가능합니다.');
    return;
  }
  
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
  if (!passwordRegex.test(trimmedPassword)) {
    alert('비밀번호는 최소 8자 이상이며, 영문, 숫자, 특수문자를 포함해야 합니다.');
    return;
  }

  // 3. 비밀번호 일치 확인
  if (trimmedPassword !== trimmedPasswordConfirm) {
    alert('비밀번호가 일치하지 않습니다.');
    return;
  }

  // 이메일 인증이 완료되지 않았으면 최종 회원가입을 막습니다.
  if (!isEmailVerified.value) {
    alert('이메일 인증을 먼저 완료해주세요.');
    return;
  }

  try {
    // 요청할 데이터 준비
    const data = {
      name: trimmedName,
      email: trimmedEmail,
      password: trimmedPassword,
      // phone, birth, social은 HTML에 없으므로 임시로 빈 값 처리
      phone: '',
      birth: '',
      social: '',
      role: 'ROLE_USER'
    };

    // 백엔드에 요청 보내기
    const response = await axios.post('/api/register', data);

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

const handleKakaoLogin = () => {
  alert('카카오 회원가입을 시도합니다.');
  window.location.href = '/api/kakao/login';
};

const handleGoogleLogin = () => {
  alert('구글 회원가입을 시도합니다.');
  window.location.href = '/api/google/login';
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

.input-group {
  margin-bottom: 15px;
}

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

/* 폼 그룹들을 Flexbox로 통일하여 정렬 */
.email-check-group,
.verification-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.email-check-group input,
.verification-group input {
  flex-grow: 1;
}

/* 새로 추가: 중앙 정렬된 버튼 그룹 */
.centered-btn-group {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
}

/* 모든 버튼의 기본 스타일을 통일 */
.btn-check-duplicate,
.btn-send-code,
.btn-verify-code {
  width: 110px;
  padding: 14px 10px;
  border-radius: 8px;
  border: 1px solid #DCDCDC;
  background-color: #fff;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  flex-shrink: 0;
}

/* 상태 메시지 스타일 */
.status-message {
  font-size: 0.85rem;
  margin-top: -10px;
  margin-bottom: 10px;
  text-align: left;
  color: #555;
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

/* 비활성화 상태 스타일 */
.btn-check-duplicate:disabled,
.btn-send-code:disabled,
.btn-verify-code:disabled,
.btn-register:disabled {
  background-color: #f0f0f0;
  cursor: not-allowed;
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