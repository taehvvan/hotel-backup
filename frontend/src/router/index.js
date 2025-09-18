import { createRouter, createWebHistory } from 'vue-router';
import MainPage from '../components/MainPage.vue';
import Login from '../components/Login.vue';
import Register from '../components/Register.vue';
import AccommodationListPage from '../components/AccommodationListPage.vue';
import LandmarkListPage from '../components/LandmarkListPage.vue';
import HeritageListPage from '../components/HeritageListPage.vue';
import WishlistPage from '../components/WishlistPage.vue';
import BookingCheckPage from '../components/BookingCheckPage.vue';
import SearchResultPage from '../components/SearchResultPage.vue';
import HotelDetailPage from '../components/HotelDetailPage.vue'; 
import UserMypage from '../components/UserMypage.vue';
import TermsOfService from '../components/TermsOfService.vue';
import PrivacyPolicy from '../components/PrivacyPolicy.vue';
import CheckoutPage from '../components/CheckoutPage.vue';
import CheckoutPageGuest from '../components/CheckoutPageGuest.vue';
import HotelManagerPage from '../components/HotelManagerPage.vue';
import AdminDashboardPage from '../components/AdminDashboardPage.vue';
import LandmarkDetailPage from '../components/LandmarkDetailPage.vue';
import HeritageDetailPage from '../components/HeritageDetailPage.vue';
import RegisterSuccess from '../components/RegisterSuccess.vue';
import ManagerRegisterPage from '../components/ManagerRegisterPage.vue';
import ManagerLoginPage from '../components/ManagerLoginPage.vue'; // [추가] 호텔 매니저 로그인 페이지 import

const routes = [
  // --- 공용 페이지 ---
  { path: '/', name: 'MainPage', component: MainPage },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/register-success', name: 'RegisterSuccess', component: RegisterSuccess, props: true },
  { path: '/manager-register', name: 'ManagerRegister', component: ManagerRegisterPage },
  { path: '/manager-login', name: 'ManagerLogin', component: ManagerLoginPage }, // [추가]
  { path: '/search', name: 'SearchResult', component: SearchResultPage },
  { path: '/hotel/:id', name: 'HotelDetail', component: HotelDetailPage, props: true },
  { path: '/landmark/:id', name: 'LandmarkDetail', component: LandmarkDetailPage, props: true },
  { path: '/heritage/:id', name: 'HeritageDetail', component: HeritageDetailPage, props: true },
  { path: '/terms', name: 'TermsOfService', component: TermsOfService },
  { path: '/privacy', name: 'PrivacyPolicy', component: PrivacyPolicy },
  { path: '/accommodations', name: 'AccommodationList', component: AccommodationListPage },
  { path: '/landmarks', name: 'LandmarkList', component: LandmarkListPage },
  { path: '/heritage', name: 'HeritageList', component: HeritageListPage },

  // --- 일반 사용자 전용 페이지 (로그인 필요) ---
  { path: '/mypage', name: 'UserMypage', component: UserMypage, meta: { requiresAuth: true } },
  { path: '/wishlist', name: 'Wishlist', component: WishlistPage, meta: { requiresAuth: true } },
  { path: '/booking-check', name: 'BookingCheck', component: BookingCheckPage, meta: { requiresAuth: true } },
  {
    path: '/checkout', // 회원용 결제 페이지
    name: 'Checkout',
    component: CheckoutPage,
    meta: { requiresAuth: true },
    props: (route) => ({
      hotelName: route.query.hotelName,
      roomName: route.query.roomName,
      price: Number(route.query.price)
    })
  },

  // --- 비회원 전용 페이지 ---
  {
    path: '/checkout-guest', 
    name: 'CheckoutGuest',
    component: CheckoutPageGuest,
    props: (route) => ({
      hotelName: route.query.hotelName,
      roomName: route.query.roomName,
      basePrice: Number(route.query.basePrice)
    })
  },

  // --- 호텔 매니저 전용 페이지 ---
  {
    path: '/manager',
    name: 'HotelManager',
    component: HotelManagerPage,
    meta: { requiresAuth: true, requiresRole: 'manager' }
  },

  // --- 사이트 관리자 전용 페이지 ---
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboardPage,
    meta: { requiresAuth: true, requiresRole: 'admin' }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});



export default router;