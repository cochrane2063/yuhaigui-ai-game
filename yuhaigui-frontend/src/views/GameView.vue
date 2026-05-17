<template>
  <div class="game-page">
    <aside class="room-sidebar">
      <header class="sidebar-header">
        <RouterLink class="back-home" to="/">← 首页</RouterLink>
        <a-button type="primary" block class="new-room-btn" @click="createRoom">
          新建房间
        </a-button>
      </header>

      <div class="sidebar-body">
        <div v-if="loadingRooms" class="sidebar-status">加载中…</div>
      <div v-else-if="rooms.length === 0" class="sidebar-status">暂无房间，点击上方新建</div>

      <ul v-else class="room-list">
        <li
          v-for="room in rooms"
          :key="room.roomId"
          class="room-item"
          :class="{ active: selectedRoomId === room.roomId }"
          @click="selectRoom(room.roomId)"
        >
          <span class="room-item-title">房间 {{ room.roomId }}</span>
          <span class="room-item-preview">{{ roomPreview(room) }}</span>
          </li>
        </ul>
      </div>

      <footer v-if="displayUsername" class="sidebar-user">
        <span class="sidebar-user-label">用户</span>
        <span class="sidebar-user-name">{{ displayUsername }}</span>
      </footer>
    </aside>

    <main class="chat-panel">
      <ChatRoomView
        v-if="selectedRoomId"
        :key="selectedRoomId"
        :room-id="String(selectedRoomId)"
        :initial-messages="selectedMessages"
        embedded
        @messages-changed="onMessagesChanged"
      />
      <div v-else class="chat-empty">
        <p class="chat-empty-title">选择或创建一个房间</p>
        <p class="chat-empty-desc">在左侧新建房间，或从列表中打开已有对局。</p>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '@/api/http'
import ChatRoomView from '@/views/ChatRoomView.vue'
import { parseMessageList, serializeApiMessage } from '@/utils/chatMessage'
import { getStoredUsername } from '@/utils/auth'

const props = defineProps({
  roomId: { type: String, default: '' },
})

const route = useRoute()
const router = useRouter()

const rooms = ref([])
const loadingRooms = ref(true)
const selectedRoomId = ref(null)
const displayUsername = computed(
  () => rooms.value[0]?.username || getStoredUsername(),
)

const selectedMessages = computed(() => {
  const room = rooms.value.find((r) => r.roomId === selectedRoomId.value)
  return room ? parseMessageList(room.messageList) : []
})

function roomPreview(room) {
  const parsed = parseMessageList(room.messageList)
  const last = parsed[parsed.length - 1]
  if (!last?.content) return '暂无消息'
  const text = last.content.trim()
  return text.length > 28 ? `${text.slice(0, 28)}…` : text
}

function ensureRoomInList(roomId) {
  if (!rooms.value.some((r) => r.roomId === roomId)) {
    rooms.value = [{ roomId, messageList: [] }, ...rooms.value]
  }
}

function selectRoom(roomId) {
  selectedRoomId.value = roomId
  ensureRoomInList(roomId)
  if (String(route.params.roomId) !== String(roomId)) {
    router.replace({ name: 'game', params: { roomId } })
  }
}

function createRoom() {
  const roomId = Date.now()
  ensureRoomInList(roomId)
  selectRoom(roomId)
}

function onMessagesChanged({ roomId, messages }) {
  const room = rooms.value.find((r) => r.roomId === Number(roomId))
  if (!room) return
  room.messageList = messages.map((m) => {
    const payload = serializeApiMessage(m)
    return [payload.type, payload]
  })
}

async function loadRooms() {
  loadingRooms.value = true
  try {
    const { data } = await http.get('/chat/rooms')
    rooms.value = [...data].sort((a, b) => b.roomId - a.roomId)
  } catch (err) {
    console.error('加载房间列表失败:', err)
    rooms.value = []
  } finally {
    loadingRooms.value = false
  }
}

function initSelection() {
  const routeRoomId = props.roomId ? Number(props.roomId) : null
  if (routeRoomId) {
    ensureRoomInList(routeRoomId)
    selectedRoomId.value = routeRoomId
    return
  }
  if (rooms.value.length > 0) {
    selectRoom(rooms.value[0].roomId)
  }
}

watch(
  () => props.roomId,
  (id) => {
    if (!id || loadingRooms.value) return
    const roomId = Number(id)
    if (roomId !== selectedRoomId.value) {
      ensureRoomInList(roomId)
      selectedRoomId.value = roomId
    }
  },
)

onMounted(async () => {
  await loadRooms()
  initSelection()
})
</script>

<style scoped>
.game-page {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(180deg, var(--color-background-soft) 0%, var(--color-background) 32%);
}

.room-sidebar {
  width: min(100%, 280px);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--app-surface-border);
  background: color-mix(in srgb, var(--color-background) 92%, transparent);
  backdrop-filter: blur(10px);
}

.sidebar-header {
  padding: 1rem;
  border-bottom: 1px solid var(--app-surface-border);
}

.back-home {
  display: inline-block;
  font-size: 0.875rem;
  color: var(--color-text);
  opacity: 0.75;
  margin-bottom: 0.75rem;
  transition: opacity 0.15s ease, color 0.15s ease;
}

.back-home:hover {
  opacity: 1;
  color: var(--app-accent);
}

.new-room-btn {
  font-weight: 600;
}

.sidebar-body {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-status {
  padding: 1.25rem 1rem;
  font-size: 0.875rem;
  color: var(--color-text);
  opacity: 0.75;
}

.room-list {
  list-style: none;
  margin: 0;
  padding: 0.5rem;
  overflow-y: auto;
  flex: 1;
  min-height: 0;
}

.sidebar-user {
  margin-top: auto;
  padding: 0.875rem 1rem;
  border-top: 1px solid var(--app-surface-border);
  display: flex;
  flex-direction: column;
  gap: 0.15rem;
}

.sidebar-user-label {
  font-size: 0.6875rem;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--app-accent);
  opacity: 0.9;
}

.sidebar-user-name {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-heading);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.room-item {
  padding: 0.75rem 0.875rem;
  border-radius: var(--app-radius-md);
  cursor: pointer;
  border: 1px solid transparent;
  transition: background 0.15s ease, border-color 0.15s ease;
}

.room-item:hover {
  background: var(--app-accent-soft);
}

.room-item.active {
  background: var(--app-accent-soft);
  border-color: color-mix(in srgb, var(--app-accent) 35%, transparent);
}

.room-item-title {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-heading);
  font-family: ui-monospace, 'Cascadia Code', 'Segoe UI Mono', monospace;
  margin-bottom: 0.25rem;
}

.room-item-preview {
  display: block;
  font-size: 0.8125rem;
  color: var(--color-text);
  opacity: 0.78;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-panel {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  text-align: center;
}

.chat-empty-title {
  font-family: var(--app-font-display);
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 0.35rem;
}

.chat-empty-desc {
  font-size: 0.9375rem;
  color: var(--color-text);
  opacity: 0.85;
  max-width: 32ch;
}

@media (max-width: 768px) {
  .game-page {
    flex-direction: column;
  }

  .room-sidebar {
    width: 100%;
    max-height: 40vh;
    border-right: none;
    border-bottom: 1px solid var(--app-surface-border);
  }
}
</style>
