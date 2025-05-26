import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './index.css'
import 'primeicons/primeicons.css'

import App from './App.vue'
import router from './router'
import VueGoogleMaps from '@fawmi/vue-google-maps'
import PrimeVue from 'primevue/config'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(PrimeVue, { unstyled: true })
app.use(VueGoogleMaps, {
  load: {
    key: import.meta.env.VITE_GOOGLE_MAPS_API_KEY, // 여기에 본인의 키 입력
    libraries: "places", // 필요하면 추가
    loading: "async"
  },
})

app.mount('#app')
