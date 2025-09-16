<template>
    <footer class="main-footer">
      <div class="content-wrapper">
        <div class="footer-content">
          <div class="footer-info">
            <h3 class="footer-logo">쉼, 한국</h3>
            <p>
              (주)쉼 코리아 | 대표: 홍길동 | 사업자등록번호: 123-45-67890<br>
              주소: 서울특별시 중구 청계천로 40 (한국관광공사)<br>
              고객센터: 1588-0000 | 이메일: contact@shimkorea.com
            </p>
            <p class="copyright">&copy; 2025 Shim, Korea. All rights reserved.</p>
          </div>
          <div class="footer-links">
            <div class="legal-links">
              <router-link to="/terms">이용약관</router-link>
        <router-link to="/privacy"><strong>개인정보 처리방침</strong></router-link>
            </div>
            <div class="social-links">
              <a href="#" aria-label="Instagram"><span class="icon-instagram"></span></a>
              <a href="#" aria-label="Facebook"><span class="icon-facebook"></span></a>
              <a href="#" aria-label="Blog"><span class="icon-blog"></span></a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </template>
  
  <script setup>
  // 이 컴포넌트는 특별한 로직이 필요 없습니다.
  </script>
  
  <style scoped>
  .main-footer {
    background-color: #F8F8F8;
    padding: 50px 0;
    border-top: 1px solid #E5E5E5;
    color: #666;
    font-size: 0.9rem;
  }
  .content-wrapper {
    max-width: 1280px;
    margin: 0 auto;
    padding: 0 20px;
  }
  .footer-content {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    text-align: left;
  }
  .footer-logo {
    font-size: 1.5rem;
    font-weight: 700;
    color: #333;
    margin: 0 0 15px 0;
  }
  .footer-info p {
    margin: 5px 0;
    line-height: 1.7;
  }
  .copyright {
    margin-top: 20px;
    font-size: 0.85rem;
    color: #999;
  }
  .footer-links {
    text-align: right;
  }
  .legal-links {
    margin-bottom: 20px;
  }
  .legal-links a {
    text-decoration: none;
    color: #555;
    margin-left: 20px;
  }
  .legal-links a:hover {
    text-decoration: underline;
  }
  .social-links {
    display: flex;
    gap: 15px;
    justify-content: flex-end;
  }
  .social-links a {
    display: inline-block;
    width: 40px;
    height: 40px;
    border: 1px solid #ddd;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .social-links a:hover {
    background-color: #eee;
  }
  .social-links span {
    width: 20px;
    height: 20px;
    background-color: #555;
    display: inline-block;
    -webkit-mask-size: contain;
    mask-size: contain;
    -webkit-mask-repeat: no-repeat;
    mask-repeat: no-repeat;
    -webkit-mask-position: center;
    mask-position: center;
  }
  .icon-instagram {
    -webkit-mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect><path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path><line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line></svg>');
    mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect><path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path><line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line></svg>');
  }
  .icon-facebook {
    -webkit-mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path></svg>');
    mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path></svg>');
  }
  .icon-blog {
    -webkit-mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2z"></path><path d="M12 12a5 5 0 1 1-5-5 5 5 0 0 1 5 5z"></path><path d="M12 12a2 2 0 1 0 2 2 2 2 0 0 0-2-2z"></path></svg>');
    mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2z"></path><path d="M12 12a5 5 0 1 1-5-5 5 5 0 0 1 5 5z"></path><path d="M12 12a2 2 0 1 0 2 2 2 2 0 0 0-2-2z"></path></svg>');
  }
  </style>