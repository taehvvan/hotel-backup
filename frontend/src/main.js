import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router'; // 1. 방금 만든 라우터 파일을 불러옵니다.
import './assets/main.css';
import { useAuthStore } from './stores/auth';

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router); // 2. 앱에 라우터를 사용하도록 등록합니다.

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  await authStore.checkLoginStatus(); // 페이지 이동 전에 로그인 상태 확인
  next();
});
app.mount('#app')