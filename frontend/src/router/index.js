import { createRouter, createWebHistory } from 'vue-router'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: DefaultLayout,
      children: [
        { path: '', name: 'main', component: () => import('@/views/MainView.vue') },
        { path: 'login', name: 'login', component: () => import('@/views/LoginView.vue') },
        { path: 'signup', name: 'signup', component: () => import('@/views/SignupView.vue') },
        { path: 'board', name: 'board', component: () => import('@/views/BoardView.vue') },
        {
          path: 'board/:boardId',
          name: 'boardDetail',
          component: () => import('@/views/BoardDetailView.vue'),
        },
        // 로그인이 필요한 페이지들
        {
          path: 'plan',
          children: [
            {
              path: 'dashboard',
              name: 'plan-dashboard',
              component: () => import('@/views/PlanDashboardView.vue'),
            },
            {
              path: 'create',
              name: 'plan-create',
              component: () => import('@/views/PlanView.vue'),
            },
          ],
        },
        {
          path: 'mypage',
          name: 'mypage',
          component: () => import('@/views/MyPageView.vue'),
          meta: { requiresAuth: true },
        },
      ],
    },
  ],

  scrollBehavior(to, from, savedPosition) {
    // 뒤로가기 등의 경우에는 savedPosition 사용
    if (savedPosition) {
      return savedPosition
    } else {
      // 기본은 항상 맨 위로 이동
      return { top: 0 }
    }
  },
})

// 라우터 가드 - 로그인이 필요한 페이지 접근 시 인증 확인
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // 로그인이 필요한 페이지인지 확인
  if (to.meta.requiresAuth) {
    // 인증 상태 확인
    authStore.checkAuthStatus()

    if (!authStore.isLoggedIn) {
      // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
      next({
        name: 'login',
        query: { redirect: to.fullPath }, // 로그인 후 원래 페이지로 돌아가기 위해 경로 저장
      })
      return
    }
  }

  // 로그인된 상태에서 로그인/회원가입 페이지 접근 시 메인으로 리다이렉트
  if ((to.name === 'login' || to.name === 'signup') && authStore.isLoggedIn) {
    next({ name: 'main' })
    return
  }

  next()
})

export default router
