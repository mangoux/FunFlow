import { http } from './index'

/**
 * 用户个人资料响应类型
 */
export interface UserProfileResponse {
  userId: number
  username: string
  nickname: string
  avatarUrl: string
  bio: string
  followingCount: number
  followerCount: number
  totalLikesReceived: number
}

/**
 * 获取当前登录用户的个人资料
 */
export const getProfile = () => {
  return http.get<UserProfileResponse>('/api/user/profile')
}

/**
 * 头像上传响应类型
 */
export interface AvatarUploadResponse {
  avatarUrl: string
}

/**
 * 上传用户头像
 */
export const uploadAvatar = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return http.post<AvatarUploadResponse>('/api/user/profile/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 用户资料更新请求类型
 */
export interface UpdateProfileRequest {
  username?: string
  nickname?: string
  avatarUrl?: string
  bio?: string
}

/**
 * 更新用户资料
 */
export const updateProfile = (data: UpdateProfileRequest) => {
  return http.put<UserProfileResponse>('/api/user/profile', data)
}
