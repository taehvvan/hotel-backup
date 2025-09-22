<template>
  <div>결제 완료 처리 중... 잠시만 기다려주세요.</div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();

onMounted(async () => {

  // [디버깅 1] 페이지가 로드되는 시점의 localStorage 상태를 전부 확인합니다.
  console.log("현재 localStorage의 모든 데이터:", JSON.stringify(localStorage, null, 2));

  // 1. 토스페이먼츠로부터 받은 결제 정보 추출
  const query = route.query;
  const orderId = Array.isArray(query.orderId) ? query.orderId[0] : query.orderId;
  const paymentKey = Array.isArray(query.paymentKey) ? query.paymentKey[0] : query.paymentKey;
  const amount = Array.isArray(query.amount) ? query.amount[0] : query.amount;

  if (!orderId || !paymentKey || !amount) return router.replace('/');

  // 2. localStorage에서 예약 시 저장했던 임시 정보 불러오기
  const reservationId = localStorage.getItem('reservationId');
  const roomId = localStorage.getItem('roomId');
  const hotelId = localStorage.getItem('hotelId');

  // [디버깅 2] 각 아이템을 개별적으로 확인
  console.log(`localStorage에서 읽은 값 -> reservationId: ${reservationId}, roomId: ${roomId}, hotelId: ${hotelId}`);

  if (!reservationId || !roomId || !hotelId) {
    // [디버깅 3] 어떤 값이 누락되었는지 명확히 알려줍니다.
    console.error(`필수 정보 누락! reservationId: ${reservationId}, roomId: ${roomId}, hotelId: ${hotelId}`);
    return router.replace('/');
  }

  try {
    const token = localStorage.getItem('jwtToken');
    console.log('토큰:', token);
    if (!token) {
      console.error('토큰이 존재하지 않습니다.');
      return; // 토큰이 없으면 반환
    }

    localStorage.setItem('accessToken', token);

    await axios.post('http://localhost:8888/api/payments/complete', {
      reservationId,
      roomId,
      hotelId,
      orderId,
      paymentKey,
      amount,
      payMethod: 'TOSS_PAY'
    }, {
      headers: {
        Authorization: `Bearer ${token}`,  // 기존 토큰 사용
      },
  });

    // localStorage 정리
    localStorage.removeItem('reservationId');
    localStorage.removeItem('roomId');
    localStorage.removeItem('hotelId');

    router.push({
      path: '/payment-success',
      query: {
        orderId: orderId,
      },
      replace: true,
    });
  } catch (err) {
    console.error(err);
    router.replace(`/payment-success?orderId=${orderId}`);
  }
});
</script>
