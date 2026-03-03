<template>
  <div class="layout">
    <Header @openAuth="handleOpenAuth" />
    <div class="layout-container">
      <Sidebar @requireAuth="handleRequireAuth" />
      <main class="main-content">
        <router-view @requireAuth="handleRequireAuth" />
      </main>
    </div>
    <AuthDialog v-model="authDialogVisible" :show-cancel="!isForceLogin" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Header from './Header.vue'
import Sidebar from './Sidebar.vue'
import AuthDialog from '../common/AuthDialog.vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

// 登录弹窗显示状态
const authDialogVisible = ref(false)

// 是否为强制登录模式（访问需要登录的页面时为 true，主动点击登录按钮时为 false）
const isForceLogin = ref(false)

// 定义需要登录的路由
const authRequiredPaths = ['/following', '/profile', '/create']

// 检查当前路由是否需要登录
const checkAuthRequired = () => {
  if (authRequiredPaths.includes(route.path) && !userStore.isLoggedIn) {
    authDialogVisible.value = true
    isForceLogin.value = true
  }
}

// 处理需要登录的事件（来自 Sidebar 或 FollowingView，访问需要登录的页面）
const handleRequireAuth = () => {
  authDialogVisible.value = true
  isForceLogin.value = true  // 强制登录模式，无关闭按钮
}

// 处理 Header 的登录按钮点击（用户主动登录）
const handleOpenAuth = () => {
  // 如果弹窗已经显示（可能是强制登录模式），则不改变状态
  if (!authDialogVisible.value) {
    authDialogVisible.value = true
    isForceLogin.value = false  // 非强制模式，有关闭按钮
  }
  // 如果弹窗已经在强制登录模式下显示，则不做任何操作，保持强制模式
}

// 监听路由变化,如果切换到不需要登录的页面,关闭登录弹窗并重置强制模式
watch(() => route.path, (newPath) => {
  const publicPaths = ['/', '/hot']
  if (publicPaths.includes(newPath)) {
    authDialogVisible.value = false
    isForceLogin.value = false  // 重置强制登录模式
  }
})

// 组件挂载时检查当前路由是否需要登录（处理页面刷新的情况）
onMounted(() => {
  checkAuthRequired()
})
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-container {
  display: flex;
  flex: 1;
}

.main-content {
  margin-left: 180px;
  margin-top: 64px;
  flex: 1;
  height: calc(100vh - 64px);
  overflow-y: auto;
}

/* 滚动条样式 */
.main-content::-webkit-scrollbar {
  width: 8px;
}

.main-content::-webkit-scrollbar-thumb {
  background: #3a3a4a;
  border-radius: 4px;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: #4a4a5a;
}
</style>
