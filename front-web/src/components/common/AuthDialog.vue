<template>
  <el-dialog
    v-model="visible"
    :title="isLogin ? '登录' : '注册'"
    width="420px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :show-close="showCancel"
    :modal="false"
    :append-to-body="true"
    :z-index="700"
    class="auth-non-modal-dialog"
    @close="handleClose"
  >
    <!-- 登录表单 -->
    <el-form
      v-if="isLogin"
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      label-position="top"
      class="auth-form"
    >
      <el-form-item label="邮箱" prop="email">
        <el-input
          v-model="loginForm.email"
          placeholder="请输入邮箱"
          clearable
        />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="请输入密码"
          show-password
          clearable
          @keyup.enter="handleLogin"
        />
      </el-form-item>

      <el-form-item label="验证码" prop="captchaText">
        <div class="captcha-container">
          <el-input
            v-model="loginForm.captchaText"
            placeholder="请输入验证码"
            clearable
            @keyup.enter="handleLogin"
          />
          <div class="captcha-image" @click="refreshCaptcha">
            <img
              v-if="captchaImage"
              :src="captchaImage"
              alt="验证码"
            />
            <span v-else class="loading-text">加载中...</span>
          </div>
        </div>
      </el-form-item>
    </el-form>

    <!-- 注册表单 -->
    <el-form
      v-else
      ref="registerFormRef"
      :model="registerForm"
      :rules="registerRules"
      label-position="top"
      class="auth-form"
    >
      <el-form-item label="邮箱" prop="email">
        <el-input
          v-model="registerForm.email"
          placeholder="请输入邮箱"
          clearable
        />
      </el-form-item>

      <el-form-item label="图形验证码" prop="captchaText">
        <div class="captcha-container">
          <el-input
              v-model="registerForm.captchaText"
              placeholder="请输入验证码"
              clearable
          />
          <div class="captcha-image" @click="refreshCaptcha">
            <img
                v-if="captchaImage"
                :src="captchaImage"
                alt="验证码"
            />
            <span v-else class="loading-text">加载中...</span>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="邮箱验证码" prop="emailCode">
        <div class="code-container">
          <el-input
            v-model="registerForm.emailCode"
            placeholder="请输入邮箱验证码"
            clearable
          />
          <el-button
            :disabled="emailCodeCountdown > 0"
            :loading="sendingEmailCode"
            @click="handleSendEmailCode"
          >
            {{ emailCodeButtonText }}
          </el-button>
        </div>
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          placeholder="请输入密码（至少8位）"
          show-password
          clearable
        />
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          placeholder="请再次输入密码"
          show-password
          clearable
          @keyup.enter="handleRegister"
        />
      </el-form-item>

    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button v-if="!isLogin" @click="switchMode">返回登录</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          {{ isLogin ? '登录' : '注册' }}
        </el-button>
        <el-button v-if="isLogin" link @click="switchMode">
          没有账号？去注册
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { getCaptcha, login, sendEmailCode, register } from '@/api/auth'
import { useUserStore } from '@/stores/user'

interface Props {
  modelValue: boolean
  showCancel?: boolean
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
}

const props = withDefaults(defineProps<Props>(), {
  showCancel: true
})
const emit = defineEmits<Emits>()

const userStore = useUserStore()

// 控制弹窗显示
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 登录/注册模式切换
const isLogin = ref(true)

// 加载状态
const loading = ref(false)
const sendingEmailCode = ref(false)

// 图形验证码
const captchaId = ref('')
const captchaImage = ref('')
const loginFormRef = ref<FormInstance>()
const registerFormRef = ref<FormInstance>()

// 登录表单
const loginForm = ref({
  email: '',
  password: '',
  captchaText: ''
})

// 注册表单
const registerForm = ref({
  email: '',
  emailCode: '',
  password: '',
  confirmPassword: '',
  captchaText: ''
})

// 邮箱验证码倒计时
const emailCodeCountdown = ref(0)
const emailCodeButtonText = computed(() => {
  return emailCodeCountdown.value > 0
    ? `${emailCodeCountdown.value}s后重新发送`
    : '发送验证码'
})

// 表单验证规则
const loginRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  captchaText: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.value.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  emailCode: [
    { required: true, message: '请输入邮箱验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码至少8位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  captchaText: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

// 获取图形验证码
const fetchCaptcha = async () => {
  try {
    const res = await getCaptcha()
    captchaId.value = res.captchaId
    captchaImage.value = res.imageData
  } catch (error) {
    console.error('获取验证码失败:', error)
    ElMessage.error('获取验证码失败，请稍后重试')
  }
}

// 刷新验证码
const refreshCaptcha = () => {
  captchaImage.value = ''
  fetchCaptcha()
}

// 发送邮箱验证码
const handleSendEmailCode = async () => {
  // 验证邮箱和图形验证码
  if (!registerForm.value.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  if (!registerForm.value.captchaText) {
    ElMessage.warning('请先输入图形验证码')
    return
  }

  try {
    sendingEmailCode.value = true
    await sendEmailCode({
      email: registerForm.value.email,
      captchaId: captchaId.value,
      captchaText: registerForm.value.captchaText
    })
    ElMessage.success('验证码已发送到您的邮箱，请注意查收')

    // 开始倒计时
    emailCodeCountdown.value = 60
    const timer = setInterval(() => {
      emailCodeCountdown.value--
      if (emailCodeCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error: any) {
    console.error('发送验证码失败:', error)
    ElMessage.error(error.message || '发送验证码失败，请稍后重试')
    // 刷新验证码
    refreshCaptcha()
  } finally {
    sendingEmailCode.value = false
  }
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loading.value = true

    const res = await login({
      email: loginForm.value.email,
      password: loginForm.value.password,
      captchaId: captchaId.value,
      captchaText: loginForm.value.captchaText
    })

    ElMessage.success('登录成功')
    userStore.setToken(res.accessToken)
    handleClose()
  } catch (error: any) {
    console.error('登录失败:', error)
    if (error.message) {
      ElMessage.error(error.message)
    }
    // 刷新验证码
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()
    loading.value = true

    await register({
      email: registerForm.value.email,
      emailCode: registerForm.value.emailCode,
      password: registerForm.value.password
    })

    ElMessage.success('注册成功，请登录')
    // 切换到登录模式
    switchMode()
  } catch (error: any) {
    console.error('注册失败:', error)
    if (error.message) {
      ElMessage.error(error.message)
    }
    // 刷新验证码
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = () => {
  if (isLogin.value) {
    handleLogin()
  } else {
    handleRegister()
  }
}

// 切换登录/注册模式
const switchMode = () => {
  isLogin.value = !isLogin.value
  // 清空表单
  if (isLogin.value) {
    loginForm.value = {
      email: '',
      password: '',
      captchaText: ''
    }
  } else {
    registerForm.value = {
      email: '',
      emailCode: '',
      password: '',
      confirmPassword: '',
      captchaText: ''
    }
  }
  // 刷新验证码
  refreshCaptcha()
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
  // 清空表单
  loginForm.value = {
    email: '',
    password: '',
    captchaText: ''
  }
  registerForm.value = {
    email: '',
    emailCode: '',
    password: '',
    confirmPassword: '',
    captchaText: ''
  }
}

// 监听弹窗打开
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    // 弹窗打开时获取验证码
    fetchCaptcha()
  }
})
</script>

<style scoped>
.auth-form {
  padding: 0 20px;
}

.captcha-container,
.code-container {
  display: flex;
  gap: 12px;
  width: 100%;
}

.captcha-container :deep(.el-input),
.code-container :deep(.el-input) {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  border-radius: 4px;
  cursor: pointer;
  overflow: hidden;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.loading-text {
  font-size: 12px;
  color: #999;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  width: 100%;
}

.dialog-footer :deep(.el-button--link) {
  margin-left: auto;
}
</style>

<style>
/* 全局样式 - 覆盖 Element Plus 的 Dialog overlay 行为 */
/* 让 overlay 层透明且不阻挡点击，这样用户可以在弹窗打开时点击导航 */

/* Element Plus Dialog 的 overlay 容器 - 让它不阻挡点击 */
.el-overlay {
  pointer-events: none !important;
  background-color: transparent !important;
}

.el-overlay-dialog {
  pointer-events: none !important;
  background-color: transparent !important;
}

/* 弹窗的包装器也不阻挡点击 */
.el-dialog__wrapper {
  pointer-events: none !important;
}

/* 只有弹窗本身和其内容可以交互 */
.el-dialog {
  pointer-events: auto !important;
}

.el-dialog__body {
  pointer-events: auto !important;
}

.el-dialog__header {
  pointer-events: auto !important;
}

.el-dialog__footer {
  pointer-events: auto !important;
}

/* 确保弹窗内的所有交互元素都可以点击 */
.el-dialog .el-form,
.el-dialog .el-input,
.el-dialog .el-button,
.el-dialog .el-form-item {
  pointer-events: auto !important;
}
</style>
