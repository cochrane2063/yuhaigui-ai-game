<template>
  <div class="login-page">
    <div class="login-bg" aria-hidden="true" />
    <div class="login-inner">
      <div class="login-card">
        <RouterLink class="back-home" to="/">← 返回首页</RouterLink>

        <p class="eyebrow">AI 海龟汤</p>
        <h1 class="title">登录</h1>
        <p class="subtitle">登录后可同步进度与房间记录（功能开发中）。</p>

        <a-alert
          v-if="loginError"
          class="login-error"
          type="error"
          :message="loginError"
          show-icon
        />

        <a-form
          class="login-form"
          layout="vertical"
          :model="formState"
          :rules="rules"
          @finish="handleLogin"
        >
          <a-form-item label="用户名或邮箱" name="account">
            <a-input
              v-model:value="formState.account"
              size="large"
              placeholder="name@example.com"
              autocomplete="username"
            />
          </a-form-item>

          <a-form-item label="密码" name="password">
            <a-input-password
              v-model:value="formState.password"
              size="large"
              placeholder="请输入密码"
              autocomplete="current-password"
            />
          </a-form-item>

          <div class="form-row">
            <a-checkbox v-model:checked="formState.remember">记住我</a-checkbox>
            <button type="button" class="text-link" @click.prevent>忘记密码？</button>
          </div>

          <a-form-item class="submit-wrap">
            <a-button type="primary" html-type="submit" size="large" block class="submit-btn">
              登录
            </a-button>
          </a-form-item>
        </a-form>

        <p class="footer-hint">
          还没有账号？
          <RouterLink class="text-link" to="/register">注册</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import http from '@/api/http'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginError = ref('')

const formState = reactive({
  account: '',
  password: '',
  remember: false,
})

const rules = {
  account: [{ required: true, message: '请输入用户名或邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = async () => {
  loginError.value = ''
  try {
    const { data } = await http.post('/auth/login', {
      username: formState.account,
      password: formState.password,
    })
    localStorage.setItem('token', data.token)
    if (data.username) localStorage.setItem('username', data.username)
    router.push('/')
  } catch (err) {
    loginError.value = err?.response?.data?.message || 'Login failed'
    const code = err?.response?.data?.code
    if (code === "INVALID_CREDENTIALS") {
      loginError.value = '用户名或密码错误'
    }
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(1.25rem, 4vw, 2.5rem);
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(1100px 560px at 20% 0%, rgba(13, 148, 136, 0.16), transparent 52%),
    radial-gradient(880px 440px at 90% 85%, rgba(59, 130, 246, 0.1), transparent 48%),
    linear-gradient(168deg, var(--color-background-soft) 0%, var(--color-background) 48%, var(--color-background-mute) 100%);
  z-index: 0;
}

.login-inner {
  position: relative;
  z-index: 1;
  width: min(100%, 420px);
}

.login-card {
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

.login-error {
  margin-bottom: 1rem;
}

.login-form :deep(.ant-form-item-label > label) {
  font-weight: 600;
  color: var(--color-heading);
}

.form-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  margin-bottom: 0.25rem;
  flex-wrap: wrap;
}

.text-link {
  border: none;
  background: none;
  padding: 0;
  font: inherit;
  font-size: 0.875rem;
  color: var(--app-accent);
  cursor: pointer;
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
