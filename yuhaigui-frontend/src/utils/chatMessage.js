/**
 * @typedef {Object} ChatUiMessage
 * @property {boolean} isAI
 * @property {string} content - Shown in the UI
 * @property {string|null} thinking - Stored for future use, not displayed
 * @property {unknown} [toolExecutionRequests]
 * @property {unknown} [attributes]
 */

const MESSAGE_WRAPPER_RE = /(?:Ai|User|System)Message\s*\{/

/**
 * @param {string} value
 * @returns {string}
 */
function unescapeText(value) {
  return value.replace(/\\"/g, '"').replace(/\\n/g, '\n').replace(/\\\\/g, '\\')
}

/**
 * @param {string} raw
 * @returns {boolean}
 */
function isSerializedLangChainMessage(raw) {
  return typeof raw === 'string' && MESSAGE_WRAPPER_RE.test(raw)
}

/**
 * Extract display text from LangChain4j toString() or JSON object strings.
 * @param {string} raw
 * @returns {string}
 */
export function extractDisplayText(raw) {
  if (raw == null) return ''
  if (typeof raw !== 'string') return ''

  const trimmed = raw.trim()
  if (!trimmed) return ''

  if (trimmed.startsWith('{')) {
    try {
      const obj = JSON.parse(trimmed)
      return extractDisplayTextFromObject(obj)
    } catch {
      // fall through
    }
  }

  if (!isSerializedLangChainMessage(trimmed)) {
    return trimmed
  }

  const patterns = [
    /\btext\s*=\s*"((?:\\.|[^"\\])*)"\s*,\s*thinking\b/,
    /\btext\s*=\s*"((?:\\.|[^"\\])*)"\s*,\s*toolExecutionRequests\b/,
    /\btext\s*=\s*"((?:\\.|[^"\\])*)"\s*\}/,
    /TextContent\s*\{\s*text\s*=\s*"((?:\\.|[^"\\])*)"/,
    /\btext\s*=\s*"((?:\\.|[^"\\])*)"/,
  ]

  for (const pattern of patterns) {
    const match = trimmed.match(pattern)
    if (match?.[1] != null) {
      return unescapeText(match[1])
    }
  }

  return ''
}

/**
 * @param {unknown} obj
 * @returns {string}
 */
function extractDisplayTextFromObject(obj) {
  if (!obj || typeof obj !== 'object') return ''

  let candidate = ''
  if (typeof obj.text === 'string') {
    candidate = obj.text
  } else if (Array.isArray(obj.contents)) {
    for (const part of obj.contents) {
      if (typeof part?.text === 'string') {
        candidate = part.text
        break
      }
    }
  }

  if (!candidate) return ''
  return extractDisplayText(candidate)
}

/**
 * @param {string} raw
 * @returns {string|null}
 */
function extractThinkingFromSerialized(raw) {
  if (!raw || typeof raw !== 'string') return null
  if (/\bthinking\s*=\s*null\b/.test(raw)) return null
  const match = raw.match(/\bthinking\s*=\s*"((?:\\.|[^"\\])*)"/)
  return match ? unescapeText(match[1]) : null
}

/**
 * @param {unknown} item
 * @returns {string}
 */
function inferMessageType(item) {
  if (item?.type) return String(item.type).toUpperCase()
  if (item?.thinking != null || item?.toolExecutionRequests != null) return 'AI'
  return 'USER'
}

/**
 * @param {unknown} type
 * @param {unknown} payload
 * @returns {ChatUiMessage|null}
 */
function fromPayload(type, payload) {
  if (type === 'SYSTEM') return null

  if (payload && typeof payload === 'object') {
    const rawText = typeof payload.text === 'string' ? payload.text : ''
    return {
      isAI: type === 'AI',
      content: extractDisplayTextFromObject(payload),
      thinking: payload.thinking ?? extractThinkingFromSerialized(rawText) ?? null,
      toolExecutionRequests: payload.toolExecutionRequests,
      attributes: payload.attributes,
    }
  }

  if (typeof payload === 'string') {
    return {
      isAI: type === 'AI',
      content: extractDisplayText(payload),
      thinking: extractThinkingFromSerialized(payload),
      toolExecutionRequests: undefined,
      attributes: undefined,
    }
  }

  return null
}

/**
 * @param {unknown} item
 * @returns {ChatUiMessage|null}
 */
export function parseApiMessage(item) {
  if (!item) return null

  if (Array.isArray(item)) {
    const [type, payload] = item
    return fromPayload(String(type).toUpperCase(), payload)
  }

  if (typeof item === 'object') {
    return fromPayload(inferMessageType(item), item)
  }

  if (typeof item === 'string') {
    const type = isSerializedLangChainMessage(item) && item.includes('toolExecutionRequests') ? 'AI' : 'USER'
    return fromPayload(type, item)
  }

  return null
}

/**
 * @param {unknown[]} messageList
 * @returns {ChatUiMessage[]}
 */
export function parseMessageList(messageList) {
  if (!messageList?.length) return []
  return messageList.map(parseApiMessage).filter(Boolean)
}

/**
 * @param {Partial<ChatUiMessage>} message
 * @returns {ChatUiMessage}
 */
export function normalizeUiMessage(message) {
  const rawContent = message.content ?? ''
  const content =
    typeof rawContent === 'string'
      ? extractDisplayText(rawContent)
      : extractDisplayTextFromObject(rawContent)

  let thinking = message.thinking ?? null
  if (thinking == null && typeof rawContent === 'string' && isSerializedLangChainMessage(rawContent)) {
    thinking = extractThinkingFromSerialized(rawContent)
  }

  return {
    isAI: !!message.isAI,
    content,
    thinking,
    toolExecutionRequests: message.toolExecutionRequests,
    attributes: message.attributes,
  }
}

/**
 * @param {ChatUiMessage} message
 * @returns {{ type: string, text: string, thinking: string|null }}
 */
export function serializeApiMessage(message) {
  const normalized = normalizeUiMessage(message)
  return {
    type: normalized.isAI ? 'AI' : 'USER',
    text: normalized.content,
    thinking: normalized.thinking ?? null,
  }
}

/**
 * @param {boolean} isAI
 * @param {string} content
 * @param {string|null} [thinking]
 * @returns {ChatUiMessage}
 */
export function createUiMessage(isAI, content, thinking = null) {
  return normalizeUiMessage({ isAI, content, thinking })
}
