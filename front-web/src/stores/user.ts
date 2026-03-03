import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserProfileResponse } from '@/api/user'
import { getProfile } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(localStorage.getItem('access_token'))
  const isLoggedIn = ref<boolean>(!!token.value)
  const userProfile = ref<UserProfileResponse | null>(null)

  /**
   * 设置token
   */
  const setToken = (newToken: string) => {
    token.value = newToken
    isLoggedIn.value = true
    localStorage.setItem('access_token', newToken)
  }

  /**
   * 清除token
   */
  const clearToken = () => {
    token.value = null
    isLoggedIn.value = false
    userProfile.value = null
    localStorage.removeItem('access_token')
  }

  /**
   * 登出
   */
  const logout = () => {
    clearToken()
  }

  /**
   * 获取用户信息
   */
  const fetchUserProfile = async () => {
    try {
      const profile = await getProfile()
      userProfile.value = profile
      return profile
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  /**
   * 更新用户信息（通常用于资料编辑后）
   */
  const updateUserProfile = (profile: UserProfileResponse) => {
    userProfile.value = profile
  }

  return {
    token,
    isLoggedIn,
    userProfile,
    setToken,
    clearToken,
    logout,
    fetchUserProfile,
    updateUserProfile
  }
})
