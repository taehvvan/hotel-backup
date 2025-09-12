<template>
    <div class="booking-check-container">
      <div class="form-wrapper">
        <header class="page-header">
          <h1>예약 확인</h1>
          <p>예약 번호와 예약 시 입력한 전화번호를 입력해주세요.</p>
        </header>
  
        <form @submit.prevent="handleBookingCheck" class="check-form">
          <div class="input-group">
            <label for="booking-number">예약 번호</label>
            <input type="text" id="booking-number" v-model="bookingNumber" placeholder="예약 번호를 입력하세요">
          </div>
          <div class="input-group">
            <label for="phone-number">전화번호</label>
            <input type="tel" id="phone-number" v-model="phoneNumber" placeholder="'-' 없이 숫자만 입력하세요">
          </div>
          <button type="submit" class="btn-check">예약 조회하기</button>
        </form>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const bookingNumber = ref('');
  const phoneNumber = ref('');
  
  const handleBookingCheck = () => {
    // 실제 예약 확인 로직 (백엔드와 통신)
    console.log('Checking booking for:', {
      bookingNumber: bookingNumber.value,
      phoneNumber: phoneNumber.value
    });
    alert(`${bookingNumber.value} 예약 조회를 시작합니다.`);
  };
  </script>
  
  <style scoped>
  .booking-check-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 80vh; /* 헤더 높이를 제외한 영역을 채우도록 */
    background-color: #f8f9fa;
    padding: 40px 20px;
    box-sizing: border-box;
  }
  
  .form-wrapper {
    width: 100%;
    max-width: 450px;
    background-color: #ffffff;
    padding: 40px;
    border-radius: 16px;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
    text-align: center;
  }
  
  .page-header {
    margin-bottom: 30px;
  }
  
  .page-header h1 {
    font-size: 2rem;
    font-weight: 700;
    margin: 0 0 10px 0;
    color: #222;
  }
  
  .page-header p {
    font-size: 1rem;
    color: #666;
    margin: 0;
  }
  
  .input-group {
    margin-bottom: 20px;
    text-align: left;
  }
  
  .input-group label {
    display: block;
    font-size: 0.9rem;
    font-weight: 600;
    margin-bottom: 8px;
  }
  
  .input-group input {
    width: 100%;
    padding: 12px 15px;
    font-size: 1rem;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-sizing: border-box;
    transition: border-color 0.3s, box-shadow 0.3s;
  }
  
  .input-group input:focus {
    outline: none;
    border-color: #3366FF;
    box-shadow: 0 0 0 3px rgba(51, 102, 255, 0.1);
  }
  
  .btn-check {
    width: 100%;
    padding: 14px;
    margin-top: 10px;
    font-size: 1rem;
    font-weight: 700;
    color: #fff;
    background-color: #333;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  .btn-check:hover {
    background-color: #000;
  }
  </style>