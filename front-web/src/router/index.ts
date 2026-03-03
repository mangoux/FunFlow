import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/layout/Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: Layout,
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/HomeView.vue')
        },
        {
          path: 'hot',
          name: 'hot',
          component: () => import('@/views/HotView.vue')
        },
        {
          path: 'following',
          name: 'following',
          component: () => import('@/views/FollowingView.vue')
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/ProfileView.vue')
        },
        {
          path: 'create',
          name: 'create',
          component: () => import('@/views/CreateView.vue')
        }
      ]
    }
  ]
})

export default router
