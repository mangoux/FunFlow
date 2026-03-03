import { http } from './index'

/**
 * 创建视频
 */
export interface CreateVideoRequest {
  video: File
  cover?: File
  title?: string
  tags: string[]
  isPublic: number
}

/**
 * 创建视频作品
 */
export const createVideo = (data: CreateVideoRequest): Promise<any> => {
  const formData = new FormData()

  formData.append('video', data.video)
  if (data.cover) {
    formData.append('cover', data.cover)
  }
  if (data.title) {
    formData.append('title', data.title)
  }
  formData.append('tags', JSON.stringify(data.tags))
  formData.append('isPublic', data.isPublic.toString())

  return http.post('/api/videos', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000
  })
}

/**
 * 视频标签信息
 */
export interface VideoTag {
  tagName: string
}

/**
 * 视频信息
 */
export interface Video {
  videoId: number
  title: string
  coverUrl: string | null
  videoUrl: string
  tags: string[]
  viewCount: number
  likeCount: number
  status: number
  isPublic: number
  createTime: string
}

/**
 * 获取用户作品列表响应
 */
export interface GetUserVideosResponse {
  userId: number
  total: number
  page: number
  pageSize: number
  videos: Video[]
}

/**
 * 获取用户作品列表请求参数
 */
export interface GetUserVideosParams {
  page?: number
  pageSize?: number
}

/**
 * 获取用户作品列表
 */
export const getUserVideos = (params?: GetUserVideosParams): Promise<GetUserVideosResponse> => {
  return http.get('/api/videos', { params })
}
