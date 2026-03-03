<template>
  <div v-if="userStore.isLoggedIn" class="following-view">
    <h1>关注</h1>
    <p>查看你关注的内容</p>
  </div>
  <div v-else class="empty-view"></div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 检查登录状态的 emit
interface Emits {
  (e: 'requireAuth'): void
}

const emit = defineEmits<Emits>()

// 组件挂载时检查登录状态
onMounted(() => {
  if (!userStore.isLoggedIn) {
    emit('requireAuth')
  }
})
</script>

<style scoped>
.following-view {
  padding: 24px;
}

.empty-view {
  min-height: 100%;
}

h1 {
  font-size: 28px;
  margin-bottom: 16px;
}

p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.85);
}
</style>
