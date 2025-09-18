<template>
  <div>
    <h2>구글 로그인 처리 중...</h2>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

onMounted(() => {
  console.log("1. onMounted 훅 실행됨");

  const token = route.query.token;

  console.log("2. URL에서 파싱된 토큰:", token);

  if (token) {
    console.log("3. 토큰이 존재합니다. loginSuccess 액션 호출");
    
    authStore.loginSuccess(token);
    
    // ✅ Pinia 스토어의 상태가 변경되었는지 확인
    console.log("4. 로그인 상태:", authStore.isLoggedIn);
    console.log("5. 유저 이름:", authStore.userName);
    
    //alert('로그인 성공!');
    router.push({ name: 'MainPage' });
  } else {
    console.log("3. 토큰이 존재하지 않습니다.");
    const error = route.query.error;
    if (error) {
      console.error('로그인 실패:', error);
      alert('로그인에 실패했습니다.');
    }
    router.push({ name: 'Login' });
  }
});
</script>