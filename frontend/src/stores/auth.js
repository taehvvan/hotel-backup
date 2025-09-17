import { defineStore } from 'pinia';
import axios from 'axios';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    userName: '',
  }),
  actions: {
    async loginSuccess(token) {
      localStorage.setItem('jwtToken', token);
      await this.checkLoginStatus(); // 로그인 후 상태 즉시 업데이트
    },
    async checkLoginStatus() {
      const token = localStorage.getItem('jwtToken');
      if (token) {
        try {
          const response = await axios.get('http://localhost:8888/api/auth/me', {
            headers: { Authorization: `Bearer ${token}` }
          });
          this.isLoggedIn = true;
          this.userName = response.data.name;
        } catch (err) {
          this.isLoggedIn = false;
          localStorage.removeItem('jwtToken');
        }
      } else {
        this.isLoggedIn = false;
      }
    },
    logout() {
      localStorage.removeItem('jwtToken');
      this.isLoggedIn = false;
      this.userName = '';
    }
  }
});