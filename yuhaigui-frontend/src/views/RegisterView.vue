<template>
  <div class="register-page">
    <div class="register-bg" aria-hidden="true" />
    <div class="register-inner">
      <div class="register-card">
        <RouterLink class="back-home" to="/">← 返回首页</RouterLink>

        <p class="eyebrow">AI 海龟汤</p>
        <h1 class="title">注册</h1>
        <p class="subtitle">创建账号后即可登录并同步进度（功能开发中）。</p>

        <a-alert
          v-if="registerError"
          class="register-error"
          type="error"
          :message="registerError"
          show-icon
        />

        <a-form
          class="register-form"
          layout="vertical"
          :model="formState"
          :rules="rules"
          @finish="handleRegister"
        >
          <a-form-item label="用户名" name="username">
            <a-input
              v-model:value="formState.username"
              size="large"
              placeholder="请输入用户名"
              autocomplete="username"
            />
          </a-form-item>

          <a-form-item label="密码" name="password">
            <a-input-password
              v-model:value="formState.password"
              size="large"
              placeholder="至少 6 位"
              autocomplete="new-password"
            />
          </a-form-item>

          <a-form-item label="确认密码" name="confirmPassword">
            <a-input-password
              v-model:value="formState.confirmPassword"
              size="large"
              placeholder="再次输入密码"
              autocomplete="new-password"
            />
          </a-form-item>

          <a-form-item class="submit-wrap">
            <a-button type="primary" html-type="submit" size="large" block class="submit-btn">
              注册
            </a-button>
          </a-form-item>
        </a-form>

        <p class="footer-hint">
          已有账号？
          <RouterLink class="text-link" to="/login">登录</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '@/api/http'

const router = useRouter()
const registerError = ref('')

const formState = reactive({
  username: '',
  password: '',
  confirmPassword: '',
})

const validateConfirmPassword = async (_rule, value) => {
  if (!value) {
    return Promise.reject('请再次输入密码')
  }
  if (value !== formState.password) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 32, message: '用户名长度为 2–32 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }],
}

const handleRegister = async () => {
  registerError.value = ''
  try {
    const { data } = await http.post('/auth/register', {
      username: formState.username,
      password: formState.password,
    })
    localStorage.setItem('token', data.token)
    if (data.username) localStorage.setItem('username', data.username)
    router.push('/')
  } catch (err) {
    registerError.value = err?.response?.data?.message || 'Register failed'
    const code = err?.response?.data?.code
    if (code === "USERNAME_EXISTS") {
      registerError.value = '用户名已存在'
    }
  }
}
</script>

<style scoped>
.register-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(1.25rem, 4vw, 2.5rem);
  overflow: hidden;
}

.register-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(1100px 560px at 20% 0%, rgba(13, 148, 136, 0.16), transparent 52%),
    radial-gradient(880px 440px at 90% 85%, rgba(59, 130, 246, 0.1), transparent 48%),
    linear-gradient(168deg, var(--color-background-soft) 0%, var(--color-background) 48%, var(--color-background-mute) 100%);
  z-index: 0;
}

.register-inner {
  position: relative;
  z-index: 1;
  width: min(100%, 420px);
}

.register-card {
  position: relative;
  padding: clamp(1.5rem, 4vw, 2.25rem);
  border-radius: var(--app-radius-lg);
  background: var(--app-surface);
  border: 1px solid var(--app-surface-border);
  box-shadow: var(--app-shadow);
  backdrop-filter: blur(14px);
}

.back-home {
  display: inline-block;
  font-size: 0.875rem;
  color: var(--color-text);
  opacity: 0.75;
  text-decoration: none;
  margin-bottom: 1.25rem;
  transition: opacity 0.15s ease, color 0.15s ease;
}

.back-home:hover {
  opacity: 1;
  color: var(--app-accent);
}

.eyebrow {
  font-size: 0.8125rem;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--app-accent);
  margin-bottom: 0.5rem;
}

.title {
  font-family: var(--app-font-display);
  font-size: clamp(1.5rem, 4vw, 1.875rem);
  font-weight: 700;
  color: var(--color-heading);
  line-height: 1.2;
  margin-bottom: 0.5rem;
}

.subtitle {
  font-size: 0.9375rem;
  color: var(--color-text);
  opacity: 0.85;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.register-error {
  margin-bottom: 1rem;
}

.register-form :deep(.ant-form-item-label > label) {
  font-weight: 600;
  color: var(--color-heading);
}

.text-link {
  font-size: 0.875rem;
  color: var(--app-accent);
  font-weight: 600;
  text-decoration: underline;
  text-underline-offset: 3px;
}

.text-link:hover {
  color: var(--app-accent-hover);
}

.submit-wrap {
  margin-bottom: 0;
  margin-top: 0.5rem;
}

.submit-btn {
  height: 48px;
  font-size: 1rem;
  font-weight: 600;
  box-shadow: 0 10px 28px rgba(13, 148, 136, 0.22);
}

.footer-hint {
  text-align: center;
  font-size: 0.875rem;
  color: var(--color-text);
  opacity: 0.82;
  margin-top: 1.25rem;
  margin-bottom: 0;
}
</style>
