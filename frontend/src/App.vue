<template>
  <HeaderComponent />
  
  <router-view></router-view>

  <FooterComponent />
</template>

<script setup>
import HeaderComponent from './components/HeaderComponent.vue';
import FooterComponent from './components/FooterComponent.vue';
</script>

<style>
@import '@/assets/main.css';
</style>