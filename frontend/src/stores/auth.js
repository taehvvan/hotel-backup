import { defineStore } from 'pinia';
import axios from 'axios';
import router from '@/router';

export const useAuthStore = defineStore('auth', {
  // state는 앱 로드 시 항상 false로 시작하고, restoreLoginState()를 통해 갱신됩니다.
  state: () => ({
    isLoggedIn: false,
    userName: '',
    userRole: '',
    user: null,
    refreshToken: ''
  }),

  actions: {
    /**
     * 사용자가 이메일/비밀번호로 로그인합니다.
     */
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
        const response = await axios.get('/api/auth/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        
        // 1. Pinia 스토어 상태 업데이트
        this.isLoggedIn = true;
        this.userName = response.data.name;
        this.userRole = response.data.role;
        this.user = response.data; // 서버에서 받은 사용자 객체 전체를 저장
        this.refreshToken = refreshToken;

        // 2. 사용자 정보를 localStorage에도 저장하여 상태 유지 (★★★★★ 핵심)
        localStorage.setItem('userName', this.userName);
        localStorage.setItem('userRole', this.userRole);
        localStorage.setItem('refreshToken', refreshToken);

        console.log('사용자정보저장완료');

        localStorage.setItem('refreshToken', this.refreshToken);

        // axios 기본 헤더에 인증 토큰 설정
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        console.log(`사용자 정보 설정 완료: ${this.userName} (${this.userRole})`);
      
        console.log('사용자 정보 설정 완료:', this.userName);
      
        // 3. 로그인 직후에만 역할 기반 리디렉션 실행
        if (shouldRedirect) {
          if (this.userRole === 'ROLE_ADMIN') {
            router.push('/admin');
          } else if (this.userRole === 'ROLE_MANAGER') {
            router.push('/manager');
          } else {
            router.push('/');
          }
        }
      } catch (err) {
        console.error('사용자 정보 조회 실패:', err);
        // 토큰이 유효하지 않으면 로그아웃 처리
        this.logout();
      }
    },

    /*
    restoreLoginState() {
      const token = localStorage.getItem('jwtToken');
      const userName = localStorage.getItem('userName');
      const userRole = localStorage.getItem('userRole');

      if (token && userName && userRole) {
        this.isLoggedIn = true;
        this.userName = userName;
        this.userRole = userRole;
        // 향후 모든 axios 요청에 인증 헤더를 기본으로 포함
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        console.log('localStorage에서 로그인 상태를 성공적으로 복원했습니다.');
      }
    },
    */

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

    /**
     * 사용자를 로그아웃시킵니다.
     */
    logout() {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      this.isLoggedIn = false;
      this.userName = '';
      this.userRole = '';
      
      // axios 헤더에서 토큰 제거
      delete axios.defaults.headers.common['Authorization'];
      
      router.push('/');
      console.log('로그아웃 완료');
    }
  }
});