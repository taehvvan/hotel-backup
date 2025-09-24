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

  const rawRoomId = localStorage.getItem('roomId');
  console.log("--- 최종 디버깅 ---");
  console.log("localStorage에서 가져온 raw roomId:", rawRoomId);
  console.log("Number(rawRoomId) 변환 결과:", Number(rawRoomId));
  console.log("-------------------");

  // [디버깅] 현재 localStorage 상태 확인
  console.log("현재 localStorage:", JSON.stringify(localStorage, null, 2));

  // 1. 토스페이먼츠에서 받은 결제 정보 추출
  const query = route.query;
  const orderId = Array.isArray(query.orderId) ? query.orderId[0] : query.orderId;
  const paymentKey = Array.isArray(query.paymentKey) ? query.paymentKey[0] : query.paymentKey;
  const amount = Array.isArray(query.amount) ? query.amount[0] : query.amount;

  if (!orderId || !paymentKey || !amount) return router.replace('/');

  // 2. 예약 정보 및 전화번호 localStorage에서 불러오기
  const reservationId = localStorage.getItem('reservationId');
  const roomId = localStorage.getItem('roomId');
  const hotelId = localStorage.getItem('hotelId');
  const userId = localStorage.getItem('userId');          // 회원용
  const phoneNumber = localStorage.getItem('savedPhoneNumber'); // 비회원용

  // [디버깅] 필수 값 확인
  console.log("reservationId:", reservationId, "roomId:", roomId, "hotelId:", hotelId, "phoneNumber:", phoneNumber);

  if (!reservationId || !roomId || !hotelId) {
    console.error("필수 예약 정보 누락!");
    return router.replace('/');
  }

  try {
    const token = localStorage.getItem('accessToken');
    const isMember = !!token;
    const headers = isMember ? { Authorization: `Bearer ${token}` } : {};

    // [디버깅] 토큰 존재 여부 확인
    console.log('회원 여부:', isMember, '토큰:', token);

    console.log('POST할 데이터:', {
      reId: Number(reservationId),
      rId: Number(roomId),
      hId: Number(hotelId),
      orderId,
      userId: isMember ? Number(userId) : null,
      phone: !isMember ? phoneNumber : null,
      paymentKey,
      amount,
      payMethod: 'TOSS_PAY'
    });
    // 3. 결제 완료 API 호출
    await axios.post('http://localhost:8888/api/payments/complete', {
      reId: parseInt(reservationId, 10),
      rId: parseInt(roomId, 10),
      hId: parseInt(hotelId, 10),
      orderId,
      userId: isMember ? parseInt(userId) : null,
      phone: !isMember ? phoneNumber : null,
      paymentKey,
      amount,
      payMethod: 'TOSS_PAY'
    }, { headers });

    // 4. localStorage 정리
    localStorage.removeItem('reservationId');
    localStorage.removeItem('roomId');
    localStorage.removeItem('hotelId');
    localStorage.removeItem('savedPhoneNumber');

    router.push({
      path: '/payment-success',
      query: { orderId },
      replace: true
    });
  } catch (err) {
    console.error("결제 완료 처리 중 오류:", err);
    router.replace(`/payment-success?orderId=${orderId}`);
  }
});
</script>
