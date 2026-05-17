<template>
  <a-layout class="chat-page" :class="{ embedded }">
    <header class="top-bar" :class="{ 'top-bar-embedded': embedded }">
      <RouterLink v-if="!embedded" class="back" to="/">← 返回首页</RouterLink>
      <div class="room-pill">
        <span class="room-label">房间</span>
        <code class="room-code">{{ activeRoomId }}</code>
      </div>
    </header>

    <a-layout-content class="chat-body">
      <div class="message-scroll" ref="messageListRef">
        <div v-if="messages.length === 0" class="empty-hint">
          <p class="empty-title">还没有消息</p>
          <p class="empty-desc">点击「开始」或直接在下方输入你的第一个问题。</p>
        </div>

        <div v-for="(msg, index) in messages" :key="index" class="message-row" :class="msg.isAI ? 'from-ai' : 'from-user'">
          <a-comment v-if="msg.isAI" class="comment ai">
            <template #avatar>
              <a-avatar :size="40" class="avatar-ai">AI</a-avatar>
            </template>
            <template #content>
              <div class="bubble ai">{{ msg.content }}</div>
            </template>
          </a-comment>

          <a-comment v-else class="comment user">
            <template #avatar>
              <a-avatar :size="40" class="avatar-user">我</a-avatar>
            </template>
            <template #content>
              <div class="bubble user">{{ msg.content }}</div>
            </template>
          </a-comment>
        </div>
      </div>
    </a-layout-content>

    <a-layout-footer class="composer">
      <div class="composer-actions">
        <a-button type="primary" :disabled="isGameStarted" @click="handleStart">开始</a-button>
        <a-button danger :disabled="isGameEnded" @click="handleEnd">结束</a-button>
      </div>
      <div class="composer-input">
        <a-input
          v-model:value="inputMessage"
          size="large"
          placeholder="输入你的问题…"
          :disabled="isGameEnded"
          @pressEnter="sendMessage"
        />
        <a-button type="primary" size="large" :disabled="isGameEnded" @click="sendMessage">发送</a-button>
      </div>
    </a-layout-footer>
  </a-layout>
</template>

<script setup>
import { ref, nextTick, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/api/http'
import { createUiMessage, normalizeUiMessage } from '@/utils/chatMessage'

const props = defineProps({
  roomId: { type: [String, Number], default: '' },
  embedded: { type: Boolean, default: false },
  initialMessages: { type: Array, default: () => [] },
})

const emit = defineEmits(['messages-changed'])

const route = useRoute()
const activeRoomId = computed(() => String(props.roomId || route.params.roomId || ''))
const messages = ref([])
const inputMessage = ref('')
const messageListRef = ref(null)
const isGameStarted = ref(false)
const isGameEnded = ref(false)

function syncMessagesChanged() {
  emit('messages-changed', {
    roomId: activeRoomId.value,
    messages: messages.value,
  })
}

function applyRoomState(initialMessages) {
  messages.value = initialMessages.map(normalizeUiMessage)
  isGameStarted.value = messages.value.length > 0
  isGameEnded.value = messages.value.some((m) => m.content?.includes('游戏已结束'))
  inputMessage.value = ''
}

watch(
  () => [activeRoomId.value, props.initialMessages],
  () => {
    applyRoomState(props.initialMessages)
  },
  { immediate: true, deep: true },
)

const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

const doChat = async (roomId, message) => {
  const response = await http.post(
    `/chat/${roomId}/send`,
    null,
    { params: { message: message } },
  )
  return response.data
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  try {
    messages.value.push(createUiMessage(false, inputMessage.value))
    syncMessagesChanged()
    scrollToBottom()
    const question = inputMessage.value
    inputMessage.value = ''

    const response = await doChat(activeRoomId.value, question)

    messages.value.push(createUiMessage(true, response))

    if (response.includes('游戏已结束')) {
      isGameEnded.value = true
    }

    syncMessagesChanged()
    scrollToBottom()
  } catch (error) {
    console.error('发送消息失败:', error)
  }
}

const handleStart = async () => {
  isGameStarted.value = true
  const startMessage = '开始'
  try {
    messages.value.push(createUiMessage(false, startMessage))

    const response = await doChat(activeRoomId.value, startMessage)

    messages.value.push(createUiMessage(true, response))

    syncMessagesChanged()
    scrollToBottom()
  } catch (error) {
    console.error('开始游戏失败:', error)
  }
}

const handleEnd = async () => {
  isGameEnded.value = true
  const endMessage = '结束'
  try {
    messages.value.push(createUiMessage(false, endMessage))

    const response = await doChat(activeRoomId.value, endMessage)

    messages.value.push(createUiMessage(true, response))

    syncMessagesChanged()
    scrollToBottom()
  } catch (error) {
    console.error('结束游戏失败:', error)
  }

}
</script>

<style scoped>
.chat-page {
  min-height: 100vh;
  background: linear-gradient(180deg, var(--color-background-soft) 0%, var(--color-background) 32%);
}

.chat-page.embedded {
  min-height: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-page.embedded :deep(.ant-layout) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.top-bar-embedded {
  justify-content: center;
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
  padding: 0.875rem clamp(1rem, 3vw, 1.5rem);
  border-bottom: 1px solid var(--app-surface-border);
  background: color-mix(in srgb, var(--color-background) 88%, transparent);
  backdrop-filter: blur(10px);
}

.back {
  font-size: 0.9375rem;
  font-weight: 500;
}

.room-pill {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.35rem 0.65rem 0.35rem 0.75rem;
  border-radius: 999px;
  border: 1px solid var(--app-surface-border);
  background: var(--app-accent-soft);
}

.room-label {
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.06em;
  color: var(--app-accent);
  text-transform: uppercase;
}

.room-code {
  font-family: ui-monospace, 'Cascadia Code', 'Segoe UI Mono', monospace;
  font-size: 0.8125rem;
  font-weight: 600;
  color: var(--color-heading);
  background: transparent;
}

.chat-page.embedded .chat-body {
  flex: 1;
  min-height: 0;
}

.chat-body {
  flex: 1;
  padding: clamp(0.75rem, 2vw, 1.25rem) clamp(1rem, 3vw, 1.5rem);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.message-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-bottom: 0.5rem;
  scroll-behavior: smooth;
}

.empty-hint {
  text-align: center;
  padding: 3rem 1.25rem;
  border-radius: var(--app-radius-md);
  border: 1px dashed var(--color-border);
  background: var(--app-accent-soft);
  margin-bottom: 1rem;
}

.empty-title {
  font-family: var(--app-font-display);
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 0.35rem;
}

.empty-desc {
  font-size: 0.9375rem;
  color: var(--color-text);
  opacity: 0.85;
}

.message-row {
  margin-bottom: 1.25rem;
}

.comment {
  max-width: min(100%, 720px);
  margin: 0 auto !important;
}

.comment :deep(.ant-comment-inner) {
  gap: 0.75rem;
}

.comment :deep(.ant-comment-avatar) {
  margin-inline-end: 0;
}

.comment :deep(.ant-comment-content) {
  min-width: 0;
}

.from-ai .comment :deep(.ant-comment-inner) {
  flex-direction: row;
}

.from-user .comment :deep(.ant-comment-inner) {
  flex-direction: row-reverse;
}

.from-user .comment :deep(.ant-comment-content) {
  text-align: right;
}

.avatar-ai {
  background: linear-gradient(145deg, #0d9488, #0f766e) !important;
  color: #fff !important;
  font-weight: 700;
  font-size: 0.875rem;
}

.avatar-user {
  background: linear-gradient(145deg, #334155, #1e293b) !important;
  color: #f8fafc !important;
  font-weight: 700;
  font-size: 0.875rem;
}

.bubble {
  display: inline-block;
  max-width: min(100%, 520px);
  padding: 0.75rem 1rem;
  border-radius: 16px;
  font-size: 0.9375rem;
  line-height: 1.55;
  word-break: break-word;
  text-align: start;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.06);
}

.bubble.ai {
  background: var(--color-background-mute);
  color: var(--color-heading);
  border: 1px solid var(--app-surface-border);
  border-bottom-left-radius: 4px;
}

.bubble.user {
  background: linear-gradient(145deg, #0d9488, #0f766e);
  color: #fff;
  border-bottom-right-radius: 4px;
  box-shadow: 0 8px 22px rgba(13, 148, 136, 0.25);
}

.composer {
  padding: 1rem clamp(1rem, 3vw, 1.5rem) calc(1rem + env(safe-area-inset-bottom, 0px));
  background: var(--color-background);
  border-top: 1px solid var(--app-surface-border);
  box-shadow: 0 -12px 40px rgba(15, 23, 42, 0.06);
}

.composer-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.composer-input {
  display: flex;
  gap: 0.5rem;
  align-items: stretch;
}

.composer-input :deep(.ant-input) {
  flex: 1;
  min-width: 0;
}

@media (max-width: 480px) {
  .composer-input {
    flex-direction: column;
  }

  .composer-input :deep(.ant-btn) {
    width: 100%;
  }
}
</style>
