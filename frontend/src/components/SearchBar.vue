<template>
    <section class="search-section" ref="searchSectionRef">
      <div class="content-wrapper">
        <form class="main-search-bar" @submit.prevent="search">
          <!-- 목적지 입력 -->
          <div class="search-input-group destination">
            <span class="icon-location"></span>
            <input type="text" placeholder="어디로 떠날까요?" v-model="destination" @focus="closeAllPopups" />
          </div>
  
          <!-- 날짜 선택 -->
          <div class="search-input-group dates" @click.stop="toggleCalendar">
            <span class="icon-calendar"></span>
            <span class="date-text">{{ checkInText }}</span>
            <span class="date-separator">-</span>
            <span class="date-text">{{ checkOutText }}</span>
            <span class="nights-badge" v-if="nights">{{ nights }}박</span>
          </div>
  
          <!-- 게스트 선택 -->
          <div class="search-input-group guests" @click.stop="toggleGuestSelector" ref="guestBtn">
            <span class="icon-guests"></span>
            <span>객실 {{ rooms }}개, 성인 {{ adults }}명, 어린이 {{ children }}명</span>
          </div>
  
          <button type="submit" class="search-button">
            <span class="icon-search"></span> 검색
          </button>
        </form>
  
        <!-- 달력 팝업 -->
        <div v-if="isCalendarOpen" class="calendar-popup">
          <div v-for="(month, index) in months" :key="index" class="calendar-month">
            <!-- 월 이름 -->
            <div class="month-name">{{ month.name }}</div>
            <div class="days-header">
              <span v-for="day in weekDays" :key="day">{{ day }}</span>
            </div>
            <div class="days-grid">
              <!-- 빈 칸 -->
              <span v-for="empty in month.emptyDays" :key="'e'+empty" class="day empty"></span>
              <!-- 실제 날짜 -->
              <span 
                v-for="day in month.days" 
                :key="day" 
                class="day"
                :class="{
                  selected: isSelectedDate(month.year, month.month, day),
                  inRange: isInRange(month.year, month.month, day),
                  disabled: isDisabled(month.year, month.month, day)
                }"
                @click="!isDisabled(month.year, month.month, day) && selectDate(month.year, month.month, day)"
              >
                {{ day }}
              </span>
            </div>
          </div>
        </div>
  
        <!-- 게스트 팝업 -->
        <div v-if="isGuestSelectorOpen" class="guest-selector-popup" :style="{ top: guestBtnTop + 'px', left: guestBtnLeft + 'px' }">
          <div class="guest-row" v-for="guest in guestTypes" :key="guest.type">
            <span class="label">{{ guest.label }}</span>
            <div class="counter">
              <button type="button" @click="decrement(guest.type)" :disabled="guest.value <= guest.min">-</button>
              <span>{{ guest.value }}</span>
              <button type="button" @click="increment(guest.type)">{{ guest.value < guest.max ? '+' : '' }}</button>
            </div>
          </div>
          <div class="guest-actions">
            <button type="button" class="confirm-btn" @click="closeAllPopups">확인</button>
          </div>
        </div>
      </div>
    </section>
  </template>
  
  <script setup>
  import { ref, reactive, computed, nextTick, onMounted, onUnmounted } from 'vue';
  
  const searchSectionRef = ref(null);
  const guestBtn = ref(null);
  const isCalendarOpen = ref(false);
  const isGuestSelectorOpen = ref(false);
  
  const destination = ref('');
  const checkIn = ref(null);
  const checkOut = ref(null);
  
  const rooms = ref(1);
  const adults = ref(2);
  const children = ref(0);
  
  const guestTypes = reactive([
    { type: 'rooms', label: '객실', value: rooms.value, min: 1, max: 5 },
    { type: 'adults', label: '성인', value: adults.value, min: 1, max: 10 },
    { type: 'children', label: '어린이', value: children.value, min: 0, max: 10 }
  ]);
  
  const guestBtnTop = ref(0);
  const guestBtnLeft = ref(0);
  
  const closeAllPopups = () => {
    isCalendarOpen.value = false;
    isGuestSelectorOpen.value = false;
  };
  
  const toggleCalendar = () => {
    isGuestSelectorOpen.value = false;
    isCalendarOpen.value = !isCalendarOpen.value;
  };
  
  const toggleGuestSelector = () => {
    isCalendarOpen.value = false;
    isGuestSelectorOpen.value = !isGuestSelectorOpen.value;
    nextTick(() => {
      const rect = guestBtn.value.getBoundingClientRect();
      guestBtnTop.value = rect.bottom + window.scrollY + 5;
      guestBtnLeft.value = rect.left + window.scrollX;
    });
  };
  
  const handleClickOutside = (event) => {
    if (searchSectionRef.value && !searchSectionRef.value.contains(event.target)) {
      closeAllPopups();
    }
  };
  
  onMounted(() => {
    document.addEventListener('click', handleClickOutside);
  });
  
  onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
  });
  
  // ------------------- 날짜 처리 -------------------
  const weekDays = ['일','월','화','수','목','금','토'];
  const today = new Date();
  today.setHours(0,0,0,0);
  
  // 달력 생성 (현재 달 기준 2개월)
  const generateMonths = (numMonths = 2) => {
    const monthsArray = [];
    for (let i = 0; i < numMonths; i++) {
      const monthDate = new Date(today.getFullYear(), today.getMonth() + i, 1);
      const year = monthDate.getFullYear();
      const month = monthDate.getMonth() + 1;
      const monthName = `${year}년 ${month}월`;
      const daysInMonth = new Date(year, month, 0).getDate();
      const firstDay = new Date(year, month - 1, 1).getDay();
      monthsArray.push({
        year,
        month,
        name: monthName,
        days: Array.from({ length: daysInMonth }, (_, i) => i + 1),
        emptyDays: Array.from({ length: firstDay }, (_, i) => i)
      });
    }
    return monthsArray;
  };
  
  const months = reactive(generateMonths(2));
  
  // 날짜 선택
  const selectDate = (year, month, day) => {
    const date = new Date(year, month-1, day);
    date.setHours(0,0,0,0);
    if(!checkIn.value || (checkIn.value && checkOut.value)) {
      checkIn.value = date;
      checkOut.value = null;
    } else {
      if(date > checkIn.value) checkOut.value = date;
      else { checkOut.value = checkIn.value; checkIn.value = date; }
    }
  };
  
  // 오늘 이전 날짜는 선택 불가
  const isDisabled = (year, month, day) => {
    const date = new Date(year, month-1, day);
    date.setHours(0,0,0,0);
    return date < today;
  };
  
  // 체크인/체크아웃 표시
  const checkInText = computed(() => checkIn.value ? `${checkIn.value.getMonth()+1}/${checkIn.value.getDate()}` : '날짜 선택');
  const checkOutText = computed(() => checkOut.value ? `${checkOut.value.getMonth()+1}/${checkOut.value.getDate()}` : '날짜 선택');
  
  // 선택 범위 박
  const nights = computed(() => checkIn.value && checkOut.value ? Math.floor((checkOut.value - checkIn.value)/(1000*60*60*24)) : 0);
  
  const isSelectedDate = (year, month, day) => {
    const date = new Date(year, month-1, day);
    date.setHours(0,0,0,0);
    return (checkIn.value && date.getTime() === checkIn.value.getTime()) || (checkOut.value && date.getTime() === checkOut.value.getTime());
  };
  
  const isInRange = (year, month, day) => {
    if(!checkIn.value || !checkOut.value) return false;
    const date = new Date(year, month-1, day);
    date.setHours(0,0,0,0);
    return date > checkIn.value && date < checkOut.value;
  };
  
  // ------------------- 게스트 증가/감소 -------------------
  const increment = (type) => {
    const guest = guestTypes.find(g => g.type === type);
    if(guest.value < guest.max) guest.value++;
    if(type === 'rooms') rooms.value = guest.value;
    if(type === 'adults') adults.value = guest.value;
    if(type === 'children') children.value = guest.value;
  };
  
  const decrement = (type) => {
    const guest = guestTypes.find(g => g.type === type);
    if(guest.value > guest.min) guest.value--;
    if(type === 'rooms') rooms.value = guest.value;
    if(type === 'adults') adults.value = guest.value;
    if(type === 'children') children.value = guest.value;
  };
  
  // ------------------- 검색 -------------------
  const search = () => {
    console.log('검색 조건:', { destination: destination.value, checkIn: checkIn.value, checkOut: checkOut.value, rooms: rooms.value, adults: adults.value, children: children.value });
  };
  </script>
  
  <style scoped>
  .search-section { padding: 40px 0; position: relative; z-index: 10; }
  .content-wrapper { max-width: 1400px; margin: 0 auto; padding: 0 20px; }
  .main-search-bar { display: flex; align-items: center; background-color: #fff; border: 1px solid #E5E5E5; border-radius: 12px; height: 72px; box-shadow: 0 8px 16px rgba(0,0,0,0.05); }
  .search-input-group { display: flex; align-items: center; flex-grow: 1; height: 100%; padding: 0 20px; cursor: pointer; }
  .search-input-group:not(:last-of-type) { border-right: 1px solid #E5E5E5; }
  .search-input-group input { border: none; font-size: 1.1rem; width: 100%; font-weight: 500; }
  .search-input-group input:focus { outline: none; }
  .date-text, .guests span { font-weight: 500; font-size: 1.1rem; color: #222; }
  .date-separator { margin: 0 10px; color: #888; }
  .nights-badge { background-color: #e6f0ff; color: #3366FF; border-radius: 20px; padding: 4px 8px; margin-left: 10px; font-size: 0.8rem; }
  .search-button { background-color: #3366FF; color: #fff; border: none; border-radius: 8px; height: 56px; display: flex; align-items: center; gap: 8px; font-size: 1.1rem; font-weight: 700; padding: 0 32px; cursor: pointer; }
  .search-button:hover { background-color: #0045D6; }
  
  .calendar-popup { position: absolute; top: 80px; left: 50%; transform: translateX(-50%); width: 700px; padding: 20px; display: flex; gap: 20px; background-color: #fff; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); border: 1px solid #E5E5E5; }
  .calendar-month { flex: 1; }
  .month-name { font-size: 1.1rem; font-weight: 700; text-align: center; margin-bottom: 15px; color: #3366FF; }
  .days-header { display: grid; grid-template-columns: repeat(7, 1fr); text-align: center; font-size: 0.9rem; color: #888; }
  .days-grid { display: grid; grid-template-columns: repeat(7, 1fr); gap: 5px; margin-top: 10px; }
  .day { height: 40px; display: flex; align-items: center; justify-content: center; border-radius: 50%; cursor: pointer; }
  .day:not(.empty):hover { background-color: #cce0ff; }
  .day.selected { background-color: #3366FF; color: #fff; }
  .day.inRange { background-color: #e6f0ff; }
  .day.disabled { color: #ccc; cursor: not-allowed; }
  
  .guest-selector-popup { position: absolute; width: 350px; background-color: #fff; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); border: 1px solid #E5E5E5; z-index: 50; }
  .guest-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
  .counter { display: flex; align-items: center; gap: 15px; }
  .counter button { width: 32px; height: 32px; border-radius: 50%; border: 1px solid #E5E5E5; background-color: #fff; font-size: 1.5rem; cursor: pointer; color: #222; }
  .counter button:disabled { color: #E5E5E5; cursor: not-allowed; }
  .counter span { font-size: 1.1rem; width: 20px; text-align: center; }
  .confirm-btn { background-color: #3366FF; color: #fff; border: none; padding: 8px 20px; border-radius: 8px; font-weight: 500; cursor: pointer; }
  </style>
  