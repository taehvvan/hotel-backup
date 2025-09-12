import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // 1. 방금 만든 라우터 파일을 불러옵니다.
import './assets/main.css'

const app = createApp(App)

app.use(router) // 2. 앱에 라우터를 사용하도록 등록합니다.

app.mount('#app')