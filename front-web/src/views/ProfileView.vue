<template>
  <div v-if="userStore.isLoggedIn" class="profile-view">
    <div class="profile-container">
      <!-- 左列：个人信息卡片 -->
      <div class="left-column">
        <div class="profile-card">
          <div class="avatar-section">
            <img :src="userStore.userProfile?.avatarUrl || defaultAvatar" alt="头像" class="avatar" />
          </div>

          <div class="user-info">
            <h2 class="nickname">{{ userStore.userProfile?.nickname || '未设置昵称' }}</h2>
            <p class="account">账号: {{ userStore.userProfile?.username || '' }}</p>
          </div>

          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-value">{{ userStore.userProfile?.followingCount || 0 }}</span>
              <span class="stat-label">关注</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userStore.userProfile?.followerCount || 0 }}</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userStore.userProfile?.totalLikesReceived || 0 }}</span>
              <span class="stat-label">获赞</span>
            </div>
          </div>

          <div class="bio-section">
            <h3 class="bio-title">个性签名</h3>
            <p class="bio-content">{{ userStore.userProfile?.bio || '这个人很懒，还没有填写个性签名~' }}</p>
          </div>

          <button class="edit-profile-btn" @click="showEditDialog = true">编辑资料</button>
        </div>
      </div>

      <!-- 右列：视频相关内容 -->
      <div class="right-column">
        <div class="video-tabs">
          <div
            v-for="tab in tabs"
            :key="tab.key"
            :class="['tab-item', { active: activeTab === tab.key }]"
            @click="activeTab = tab.key"
          >
            {{ tab.label }}
          </div>
        </div>

        <div class="video-content">
          <!-- 作品 -->
          <div v-if="activeTab === 'works'" class="video-grid">
            <div v-if="isLoading && videos.length === 0" class="empty-state">
              <p>加载中...</p>
            </div>
            <template v-else-if="videos.length > 0">
              <div
                v-for="video in videos"
                :key="video.videoId"
                class="video-item"
              >
                <div class="video-thumbnail">
                  <img
                    v-if="video.coverUrl"
                    :src="video.coverUrl"
                    :alt="video.title"
                    class="thumbnail-image"
                  />
                  <div v-else class="video-placeholder">
                    <video :src="video.videoUrl" class="video-preview" muted></video>
                  </div>
                  <div class="video-status-badge">
                    {{ getVideoStatusText(video) }}
                  </div>
                  <div class="video-stats-overlay">
                    <span class="video-stat-item">
                      <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="currentColor" stroke="none">
                        <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                      </svg>
                      {{ formatNumber(video.likeCount) }}
                    </span>
                    <span class="video-stat-item">
                      <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="currentColor" stroke="none">
                        <path d="M8 5v14l11-7z"/>
                      </svg>
                      {{ formatNumber(video.viewCount) }}
                    </span>
                  </div>
                </div>
                <p class="video-title">{{ getVideoDescription(video) }}</p>
              </div>
              <!-- 加载更多按钮 -->
              <div v-if="currentPage * pageSize < totalVideos" class="load-more-container">
                <button
                  class="load-more-btn"
                  :disabled="isLoading"
                  @click="loadMoreVideos"
                >
                  {{ isLoading ? '加载中...' : '加载更多' }}
                </button>
              </div>
            </template>
            <div v-else class="empty-state">
              <p>还没有发布作品</p>
              <button class="action-btn">去发布</button>
            </div>
          </div>

          <!-- 收藏 -->
          <div v-else-if="activeTab === 'favorites'" class="collection-grid">
            <div v-for="i in 6" :key="i" class="collection-item">
              <div class="collection-thumbnail">
                <div class="collection-videos-preview">
                  <div class="preview-video"></div>
                  <div class="preview-video"></div>
                  <div class="preview-video"></div>
                  <div class="preview-video"></div>
                </div>
              </div>
              <div class="collection-info">
                <p class="collection-title">收藏夹 {{ i }}</p>
                <p class="collection-count">{{ Math.floor(Math.random() * 50 + 1) }} 个视频</p>
              </div>
            </div>
            <div v-if="!hasFavorites" class="empty-state">
              <p>还没有收藏夹</p>
              <button class="action-btn">创建收藏夹</button>
            </div>
          </div>

          <!-- 喜欢 -->
          <div v-else-if="activeTab === 'likes'" class="video-grid">
            <div v-for="i in 5" :key="i" class="video-item">
              <div class="video-thumbnail">
                <div class="video-stats-overlay">
                  <span class="video-stat-item"><svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="currentColor" stroke="none"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>{{ formatNumber(Math.floor(Math.random() * 1000)) }}</span>
                </div>
              </div>
              <p class="video-title">喜欢的视频 {{ i }}</p>
            </div>
            <div v-if="!hasLikes" class="empty-state">
              <p>还没有喜欢的视频</p>
              <button class="action-btn">去发现</button>
            </div>
          </div>

          <!-- 浏览历史 -->
          <div v-else-if="activeTab === 'history'" class="video-grid">
            <div v-for="i in 8" :key="i" class="video-item">
              <div class="video-thumbnail">
                <div class="video-stats-overlay">
                  <span class="video-stat-item"><svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="currentColor" stroke="none"><path d="M8 5v14l11-7z"/></svg>{{ formatNumber(Math.floor(Math.random() * 10000)) }}</span>
                </div>
              </div>
              <p class="video-title">浏览过的视频 {{ i }}</p>
            </div>
            <div v-if="!hasHistory" class="empty-state">
              <p>还没有浏览记录</p>
              <button class="action-btn">去发现</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="empty-view"></div>

  <!-- 编辑资料弹窗 -->
  <EditProfileDialog v-model="showEditDialog" />
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onActivated } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import EditProfileDialog from '@/components/common/EditProfileDialog.vue'
import { getUserVideos, type Video } from '@/api/video'

const route = useRoute()
const userStore = useUserStore()

// 默认头像（使用 Element Plus 的默认头像，与 Header 保持一致）
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 检查登录状态的 emit
interface Emits {
  (e: 'requireAuth'): void
}

const emit = defineEmits<Emits>()

// 编辑资料弹窗
const showEditDialog = ref(false)

// 标签页配置
const tabs = [
  { key: 'works', label: '作品' },
  { key: 'favorites', label: '收藏' },
  { key: 'likes', label: '喜欢' },
  { key: 'history', label: '历史' }
]

const activeTab = ref('works')

// 作品列表数据
const videos = ref<Video[]>([])
const totalVideos = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const isLoading = ref(false)

// 数据状态（后续可以接入真实数据）
const hasWorks = ref(false)
const hasFavorites = ref(false)
const hasLikes = ref(false)
const hasHistory = ref(false)

// 获取用户信息
const fetchUserInfo = async () => {
  if (userStore.isLoggedIn) {
    try {
      console.log('正在获取用户信息...')
      await userStore.fetchUserProfile()
      console.log('用户信息获取成功:', userStore.userProfile)
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
}

// 获取用户作品列表
const fetchUserVideos = async () => {
  if (isLoading.value) return

  try {
    isLoading.value = true
    const response = await getUserVideos({
      page: currentPage.value,
      pageSize: pageSize.value
    })

    videos.value = response.videos
    totalVideos.value = response.total
    hasWorks.value = response.videos.length > 0

    console.log('获取作品列表成功:', response)
  } catch (error) {
    console.error('获取作品列表失败:', error)
    videos.value = []
    totalVideos.value = 0
    hasWorks.value = false
  } finally {
    isLoading.value = false
  }
}

// 获取视频状态文本
const getVideoStatusText = (video: Video): string => {
  const { status, isPublic } = video

  if (status === 0) return '审核中'
  if (status === 2) return '已下架'
  if (status === 1) {
    return isPublic === 1 ? '公开' : '私密'
  }

  return ''
}

// 获取视频描述（标题 + 标签）
const getVideoDescription = (video: Video): string => {
  const { title, tags } = video

  if (!title || title.trim() === '') {
    return tags.map(tag => `#${tag}`).join('')
  }

  return `${title} ${tags.map(tag => `#${tag}`).join('')}`
}

// 加载更多作品
const loadMoreVideos = () => {
  if (currentPage.value * pageSize.value < totalVideos.value) {
    currentPage.value++
    fetchUserVideos()
  }
}

// 格式化数字显示（如：1234 -> 1.2K）
const formatNumber = (num: number): string => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 组件挂载时检查登录状态并获取用户信息
onMounted(() => {
  console.log('ProfileView onMounted')
  if (!userStore.isLoggedIn) {
    emit('requireAuth')
  } else {
    fetchUserInfo()
    fetchUserVideos()
  }
})

// 组件被激活时（使用 keep-alive 时）或每次进入时获取用户信息
onActivated(() => {
  console.log('ProfileView onActivated')
  if (userStore.isLoggedIn) {
    fetchUserInfo()
    fetchUserVideos()
  }
})

// 监听路由变化，当进入 profile 页面时刷新用户信息
watch(() => route.path, (newPath, oldPath) => {
  console.log('路由变化:', oldPath, '->', newPath)
  if (newPath === '/profile' && userStore.isLoggedIn) {
    fetchUserInfo()
    fetchUserVideos()
  }
})
</script>

<style scoped>
.profile-view {
  min-height: calc(100vh - 64px);
  background: #161823;
  padding: 32px 24px;
}

.profile-container {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 32px;
}

/* 左列样式 */
.left-column {
  position: sticky;
  top: 24px;
  height: fit-content;
}

.profile-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.avatar {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(255, 255, 255, 0.15);
  margin-bottom: 12px;
  transition: transform 0.3s ease;
}

.avatar:hover {
  transform: scale(1.05);
  border-color: rgba(255, 255, 255, 0.25);
}

.user-info {
  text-align: center;
  margin-bottom: 18px;
}

.nickname {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 6px;
}

.account {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 18px;
  padding: 14px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
}

.stat-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
}

.bio-section {
  margin-bottom: 18px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.bio-title {
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 6px;
}

.bio-content {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.5;
  min-height: 36px;
}

.edit-profile-btn {
  width: 100%;
  padding: 10px;
  background: #1890ff;
  border: none;
  border-radius: 8px;
  color: #fff;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.edit-profile-btn:hover {
  background: #40a9ff;
  transform: translateY(-2px);
}

/* 右列样式 */
.right-column {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.video-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 16px;
}

.tab-item {
  padding: 10px 24px;
  background: transparent;
  border: none;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-item:hover {
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.05);
}

.tab-item.active {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.1);
}

.video-content {
  min-height: 400px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.video-item {
  background: transparent;
  border-radius: 12px;
  overflow: visible;
  transition: all 0.3s ease;
  cursor: pointer;
}

.video-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.video-thumbnail {
  width: 100%;
  aspect-ratio: 3 / 4;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 8px;
}

.thumbnail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.video-placeholder {
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-status-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 4px 10px;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  border-radius: 12px;
  color: #ffffff;
  font-size: 12px;
  font-weight: 500;
  backdrop-filter: blur(8px);
}

.load-more-container {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

.load-more-btn {
  padding: 12px 32px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: #ffffff;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.load-more-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
}

.load-more-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.video-stats-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 8px 12px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.6), transparent);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.video-stat-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.video-stat-item svg {
  flex-shrink: 0;
  width: 18px;
  height: 18px;
  display: inline-block;
  vertical-align: middle;
  opacity: 0.9;
}

.video-title {
  font-size: 14px;
  color: #ffffff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 0 4px;
}

.collection-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.collection-item {
  background: transparent;
  border-radius: 12px;
  overflow: visible;
  transition: all 0.3s ease;
  cursor: pointer;
}

.collection-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.collection-thumbnail {
  width: 100%;
  aspect-ratio: 3 / 4;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 8px;
  position: relative;
}

.collection-videos-preview {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 2px;
  width: 100%;
  height: 100%;
}

.preview-video {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  width: 100%;
  height: 100%;
}

.preview-video:nth-child(2) {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.preview-video:nth-child(3) {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.preview-video:nth-child(4) {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.collection-info {
  padding: 0 4px;
}

.collection-title {
  font-size: 14px;
  color: #ffffff;
  font-weight: 500;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.collection-count {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.video-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-item {
  display: flex;
  gap: 16px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.history-item:hover {
  background: rgba(255, 255, 255, 0.06);
  transform: translateX(4px);
}

.history-thumbnail {
  width: 160px;
  height: 100px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border-radius: 8px;
  flex-shrink: 0;
  position: relative;
}

.history-thumbnail::after {
  content: '▶';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 24px;
  color: rgba(255, 255, 255, 0.9);
}

.history-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

.history-title {
  font-size: 16px;
  color: #ffffff;
  font-weight: 500;
}

.history-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: rgba(255, 255, 255, 0.5);
}

.empty-state p {
  font-size: 16px;
  margin-bottom: 16px;
}

.action-btn {
  padding: 10px 24px;
  background: #1890ff;
  border: none;
  border-radius: 4px;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background: #40a9ff;
  transform: translateY(-2px);
}

.empty-view {
  min-height: 100%;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .profile-container {
    grid-template-columns: 1fr;
  }

  .left-column {
    position: static;
  }

  .video-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  }
}

@media (max-width: 768px) {
  .profile-view {
    padding: 16px;
  }

  .profile-container {
    gap: 16px;
  }

  .video-tabs {
    overflow-x: auto;
    white-space: nowrap;
  }

  .tab-item {
    padding: 8px 16px;
    font-size: 14px;
  }

  .stats-grid {
    gap: 12px;
  }

  .stat-value {
    font-size: 20px;
  }
}
</style>
