import { http } from './index'

// 图形验证码响应类型
export interface CaptchaResponse {
  captchaId: string
  imageData: string
}

// 登录请求类型
export interface LoginRequest {
  email: string
  password: string
  captchaId: string
  captchaText: string
}

// 登录响应类型
export interface LoginResponse {
  accessToken: string
}

// 发送邮箱验证码请求类型
export interface SendEmailCodeRequest {
  email: string
  captchaId: string
  captchaText: string
}

// 注册请求类型
export interface RegisterRequest {
  email: string
  emailCode: string
  password: string
}

/**
 * 获取图形验证码
 */
export const getCaptcha = () => {
  return http.get<CaptchaResponse>('/api/auth/captcha')
}

/**
 * 用户登录
 */
export const login = (data: LoginRequest) => {
  return http.post<LoginResponse>('/api/auth/login', data)
}

/**
 * 发送邮箱验证码
 */
export const sendEmailCode = (data: SendEmailCodeRequest) => {
  return http.post('/api/auth/send-email-code', data)
}

/**
 * 用户注册
 */
export const register = (data: RegisterRequest) => {
  return http.post('/api/auth/register', data)
}
