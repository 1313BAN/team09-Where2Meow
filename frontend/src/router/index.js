import { createRouter, createWebHistory } from 'vue-router'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import PlanLayout from '@/layouts/PlanLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: DefaultLayout,
      children: [
        { path: '', name: 'main', component: () => import('@/views/MainView.vue') },
        { path: 'login', name: 'login', component: () => import('@/views/LoginView.vue') },
        { 
          path: 'plan', 
          children: [{
            path: 'dashboard',
            name: 'plan-dashboard',
            component: () => import('@/views/PlanDashboardView.vue')
          }]
        },
      ],
    },
    // PlanLayout을 사용하는 별도 라우트
    {
      path: '/plan/create',
      component: PlanLayout,
      children: [
        {
          path: '',
          name: 'plan-create',
          component: () => import('@/views/PlanView.vue')
        }
      ]
    }
  ],
})

export default router
