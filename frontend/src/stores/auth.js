import { defineStore } from 'pinia';
import axios from 'axios';
import router from '@/router';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    userName: '',
    userRole: '',
  }),
  actions: {
    async login(email, password) {
      try {
        const response = await axios.post('http://localhost:8888/api/auth/login', {
          email,
          password
        });

        localStorage.setItem('accessToken', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);
        console.log('로그인 성공, Access Token:', response.data.accessToken);

        // 토큰을 저장한 후 인자 없이 fetchUserInfo 호출
        await this.fetchUserInfo();

      } catch (error) {
        if (error.response) {
          console.error('로그인 실패:', error.response.data);
          alert(`로그인 실패: ${JSON.stringify(error.response.data)}`);
        } else {
          console.error('요청 실패:', error);
          alert('로그인 요청 실패. 네트워크 상태를 확인해주세요.');
        }
      }
    },

    async fetchUserInfo() {
      // localStorage에서 직접 토큰을 가져오도록 수정
      const token = localStorage.getItem('accessToken');
      if (!token) {
        console.error('액세스 토큰이 없습니다. 로그인 상태를 초기화합니다.');
        this.logout();
        return;
      }

      try {
        const response = await axios.get('http://localhost:8888/api/auth/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        
        this.isLoggedIn = true;
        this.userName = response.data.name;
        this.userRole = response.data.role;
    
        console.log('사용자 role:', this.userRole);
    
        // role에 따라 페이지 이동
        if (this.userRole === 'ROLE_ADMIN') {
          router.push('/admin');
        } else if (this.userRole === 'ROLE_MANAGER') {
          router.push('/manager');
        } else {
          router.push('/');
        }
      } catch (err) {
        console.error('사용자 정보 조회 실패:', err);
        // 토큰이 유효하지 않으면 로그아웃 처리
        this.logout();
      }
    },

    async checkLoginStatus() {
      const token = localStorage.getItem('accessToken');
      if (token) {
        try {
          // fetchUserInfo가 토큰을 직접 가져오도록 수정되었으므로 인자 제거
          await this.fetchUserInfo();
        } catch (err) {
          console.error('액세스 토큰이 만료되었거나 유효하지 않습니다.');
          this.logout();
        }
      } else {
        this.isLoggedIn = false;
        this.userName = '';
        this.userRole = '';
      }
    },

    logout() {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      this.isLoggedIn = false;
      this.userName = '';
      this.userRole = '';
      router.push('/');
    }
  }
});