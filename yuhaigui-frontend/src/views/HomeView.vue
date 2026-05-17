<template>
  <div class="home">
    <div class="home-bg" aria-hidden="true" />
    <div class="home-inner">
      <div class="hero-card">
        <p class="eyebrow">推理 · 问答 · 故事还原</p>
        <h1 class="title">AI 海龟汤</h1>
        <p class="subtitle">
          通过「是 / 否 / 无关」式提问，逐步拼凑真相。主持人由 AI 担任，随时开局。
        </p>
        <a-button type="primary" size="large" class="cta" @click="openGameView">
          开始游戏
        </a-button>
        <p class="login-hint">
          <RouterLink v-if="!isLoggedIn" class="login-link" to="/login">登录</RouterLink>
          <button v-else type="button" class="login-link" @click="logout">退出登录</button>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isLoggedIn = ref(!!localStorage.getItem('token'))

const openGameView = () => {
  router.push({ name: 'game' })
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  isLoggedIn.value = false
}
</script>

<style scoped>
.home {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(1.25rem, 4vw, 2.5rem);
  overflow: hidden;
}

.home-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(1200px 600px at 15% 10%, rgba(13, 148, 136, 0.18), transparent 55%),
    radial-gradient(900px 480px at 85% 75%, rgba(59, 130, 246, 0.12), transparent 50%),
    linear-gradient(165deg, var(--color-background-soft) 0%, var(--color-background) 45%, var(--color-background-mute) 100%);
  z-index: 0;
}

.home-inner {
  position: relative;
  z-index: 1;
  width: min(100%, 520px);
}

.hero-card {
  padding: clamp(1.75rem, 5vw, 2.75rem);
  border-radius: var(--app-radius-lg);
  background: var(--app-surface);
  border: 1px solid var(--app-surface-border);
  box-shadow: var(--app-shadow);
  backdrop-filter: blur(14px);
  text-align: center;
}

.eyebrow {
  font-size: 0.8125rem;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--app-accent);
  margin-bottom: 0.75rem;
}

.title {
  font-family: var(--app-font-display);
  font-size: clamp(2rem, 6vw, 2.75rem);
  font-weight: 700;
  color: var(--color-heading);
  line-height: 1.15;
  margin-bottom: 1rem;
}

.subtitle {
  font-size: 1rem;
  color: var(--color-text);
  opacity: 0.88;
  max-width: 38ch;
  margin: 0 auto 1.75rem;
  line-height: 1.65;
}

.cta {
  min-width: 10rem;
  height: 48px;
  font-size: 1rem;
  font-weight: 600;
  box-shadow: 0 10px 28px rgba(13, 148, 136, 0.28);
}

.login-hint {
  margin-top: 1.25rem;
  font-size: 0.875rem;
  color: var(--color-text);
  opacity: 0.78;
}

.login-link {
  color: var(--app-accent);
  font-weight: 600;
  text-decoration: none;
}

button.login-link {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  font: inherit;
  font-size: inherit;
}

.login-link:hover {
  text-decoration: underline;
  text-underline-offset: 3px;
}
</style>
