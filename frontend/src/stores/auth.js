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

        const token = response.data.accessToken || response.data.token;
        localStorage.setItem('jwtToken', token);
        console.log('로그인 성공, JWT 저장 완료');

        // 사용자 정보를 가져오고, 역할에 따라 페이지를 이동시킵니다.
        await this.fetchAndSetUser(token, true);

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

    async fetchAndSetUser(token, shouldRedirect = false) {
      try {
        const response = await axios.get('http://localhost:8888/api/auth/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        
        // 1. Pinia 스토어 상태 업데이트
        this.isLoggedIn = true;
        this.userName = response.data.name;
        this.userRole = response.data.role;
        this.user = response.data; // 서버에서 받은 사용자 객체 전체를 저장

        // 2. 사용자 정보를 localStorage에도 저장하여 상태 유지 (★★★★★ 핵심)
        localStorage.setItem('userName', this.userName);
        localStorage.setItem('userRole', this.userRole);

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
        console.error('사용자 정보 조회 실패 (토큰 만료 가능성):', err);
        this.logout(); // 토큰이 유효하지 않으면 로그아웃 처리
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
      const token = localStorage.getItem('jwtToken');
      // 스토어는 로그아웃 상태인데 토큰이 남아있는 경우 (예: 새로고침 직후)에만 실행
      if (token && !this.isLoggedIn) {
        console.log('localStorage에 토큰이 있어 재검증을 시도합니다...');
        try {
          // fetchAndSetUser를 호출하여 실제 서버에 유효성을 검증합니다.
          await this.fetchAndSetUser(token, false); // 페이지 이동은 하지 않음
        } catch (error) {
          // fetchAndSetUser 내부에서 이미 logout 처리를 하지만, 추가적인 에러 로깅
          console.error("토큰 재검증 실패:", error.message);
        }
      }
    },

    /**
     * 사용자를 로그아웃시킵니다.
     */
    logout() {
      // localStorage에서 모든 인증 정보 제거
      localStorage.removeItem('jwtToken');
      localStorage.removeItem('userName');
      localStorage.removeItem('userRole');

      // Pinia 스토어 상태 초기화
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
