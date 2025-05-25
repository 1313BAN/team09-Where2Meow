import { createRouter, createWebHistory } from 'vue-router'
import DefaultLayout from '@/layouts/DefaultLayout.vue'

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
          },
          {
            path: 'create',
            name: 'plan-create',
            component: () => import('@/views/PlanView.vue')
          }
          ]
        },
      ],
    },
  ],
})

export default router
