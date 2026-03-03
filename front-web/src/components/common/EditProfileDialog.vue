<template>
  <el-dialog
    v-model="visible"
    title="编辑资料"
    width="500px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :modal="false"
    :append-to-body="true"
    :z-index="700"
    class="edit-profile-dialog"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      class="profile-form"
    >
      <!-- 头像上传 -->
      <el-form-item label="头像">
        <div class="avatar-upload-container">
          <div class="avatar-preview" @click="selectAvatar">
            <img
              v-if="form.avatarUrl"
              :src="form.avatarUrl"
              alt="头像"
              class="avatar-image"
            />
            <div v-else class="avatar-placeholder">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path>
                <circle cx="12" cy="13" r="4"></circle>
              </svg>
              <span>点击上传头像</span>
            </div>
            <div class="avatar-overlay">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="17 8 12 3 7 8"></polyline>
                <line x1="12" y1="3" x2="12" y2="15"></line>
              </svg>
              <span>更换头像</span>
            </div>
          </div>
          <input
            ref="avatarInputRef"
            type="file"
            accept="image/jpeg,image/jpg,image/png"
            style="display: none"
            @change="handleAvatarChange"
          />
          <div class="avatar-tips">
            支持 jpg、png 格式，建议尺寸 200x200，最大 5MB
          </div>
        </div>
      </el-form-item>

      <!-- 昵称 -->
      <el-form-item label="昵称" prop="nickname">
        <el-input
          v-model="form.nickname"
          placeholder="请输入昵称"
          maxlength="20"
          show-word-limit
          clearable
        />
      </el-form-item>

      <!-- 账号 -->
      <el-form-item label="账号" prop="username">
        <el-input
          v-model="form.username"
          placeholder="请输入账号（4-20个字符）"
          maxlength="20"
          clearable
        />
        <div class="form-tip">账号唯一标识，只能包含字母、数字、下划线</div>
      </el-form-item>

      <!-- 个性签名 -->
      <el-form-item label="个性签名" prop="bio">
        <el-input
          v-model="form.bio"
          type="textarea"
          placeholder="介绍一下自己吧..."
          :rows="3"
          maxlength="200"
          show-word-limit
          clearable
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          提交修改
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules, type UploadFile } from 'element-plus'
import { uploadAvatar, updateProfile } from '@/api/user'
import { useUserStore } from '@/stores/user'

interface Props {
  modelValue: boolean
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const userStore = useUserStore()

// 控制弹窗显示
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formRef = ref<FormInstance>()
const avatarInputRef = ref<HTMLInputElement>()
const submitting = ref(false)
const uploadingAvatar = ref(false)

// 表单数据
const form = ref({
  nickname: '',
  username: '',
  avatarUrl: '',
  bio: ''
})

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 表单验证规则
const rules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 1, max: 20, message: '昵称长度为1-20个字符', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 4, max: 20, message: '账号长度为4-20个字符', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_]+$/,
      message: '账号只能包含字母、数字、下划线',
      trigger: 'blur'
    }
  ],
  bio: [
    { max: 200, message: '个性签名最多200个字符', trigger: 'blur' }
  ]
}

// 选择头像
const selectAvatar = () => {
  avatarInputRef.value?.click()
}

// 处理头像选择
const handleAvatarChange = async (e: Event) => {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/jpg', 'image/png']
  if (!validTypes.includes(file.type)) {
    ElMessage.error('只支持 jpg、png 格式的图片')
    return
  }

  // 验证文件大小（5MB）
  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过 5MB')
    return
  }

  try {
    uploadingAvatar.value = true
    const res = await uploadAvatar(file)
    form.value.avatarUrl = res.avatarUrl
    ElMessage.success('头像上传成功')
  } catch (error: any) {
    console.error('头像上传失败:', error)
    ElMessage.error(error.message || '头像上传失败，请稍后重试')
  } finally {
    uploadingAvatar.value = false
    // 清空 input value，允许重复选择同一文件
    target.value = ''
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    // 只传有值的字段
    const updateData: any = {}
    if (form.value.nickname) updateData.nickname = form.value.nickname
    if (form.value.username) updateData.username = form.value.username
    if (form.value.bio) updateData.bio = form.value.bio
    if (form.value.avatarUrl) updateData.avatarUrl = form.value.avatarUrl

    const updatedProfile = await updateProfile(updateData)

    // 更新 store 中的用户信息
    userStore.updateUserProfile(updatedProfile)

    ElMessage.success('资料更新成功')
    emit('success')
    handleClose()
  } catch (error: any) {
    console.error('更新资料失败:', error)
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    submitting.value = false
  }
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
  // 延迟重置表单，避免动画时闪烁
  setTimeout(() => {
    formRef.value?.resetFields()
  }, 300)
}

// 初始化表单数据
const initForm = () => {
  if (userStore.userProfile) {
    form.value = {
      nickname: userStore.userProfile.nickname || '',
      username: userStore.userProfile.username || '',
      avatarUrl: userStore.userProfile.avatarUrl || '',
      bio: userStore.userProfile.bio || ''
    }
  }
}

// 监听弹窗打开
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    initForm()
  }
})
</script>

<style scoped>
.profile-form {
  padding: 0 20px;
}

.avatar-upload-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.avatar-preview {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid #e0e0e0;
  transition: all 0.3s;
}

.avatar-preview:hover {
  border-color: #1890ff;
}

.avatar-preview:hover .avatar-overlay {
  opacity: 1;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
  font-size: 12px;
  gap: 4px;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #fff;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-tips {
  font-size: 12px;
  color: #999;
  line-height: 1.5;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  width: 100%;
}
</style>

<style>
/* 继承全局的非模态弹窗样式 */
.edit-profile-dialog .el-overlay {
  pointer-events: none !important;
  background-color: transparent !important;
}

.edit-profile-dialog .el-overlay-dialog {
  pointer-events: none !important;
  background-color: transparent !important;
}

.edit-profile-dialog .el-dialog__wrapper {
  pointer-events: none !important;
}

.edit-profile-dialog .el-dialog {
  pointer-events: auto !important;
}

.edit-profile-dialog .el-dialog__body,
.edit-profile-dialog .el-dialog__header,
.edit-profile-dialog .el-dialog__footer {
  pointer-events: auto !important;
}

.edit-profile-dialog .el-form,
.edit-profile-dialog .el-input,
.edit-profile-dialog .el-button,
.edit-profile-dialog .el-form-item {
  pointer-events: auto !important;
}
</style>
