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
        // 로그인 요청
        const response = await axios.post('http://localhost:8888/api/auth/login', {
          email,
          password
        });

        const token = response.data.accessToken || response.data.token;
        localStorage.setItem('jwtToken', token);
        console.log('로그인 성공, JWT:', token);

        // 사용자 정보 가져오기 및 role 기반 이동
        await this.fetchUserInfo(token);

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

    async fetchUserInfo(token) {
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
      }
    },

    async checkLoginStatus() {
      const token = localStorage.getItem('jwtToken');
      if (token) {
        await this.fetchUserInfo(token); // store 상태 갱신
      } else {
        this.isLoggedIn = false;
        this.userName = '';
        this.userRole = '';
      }
    },

    logout() {
      localStorage.removeItem('jwtToken');
      this.isLoggedIn = false;
      this.userName = '';
      this.userRole = '';
      router.push('/');
    }
  }
});
