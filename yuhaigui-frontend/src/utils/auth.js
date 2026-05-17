export function getUsernameFromToken() {
  const token = localStorage.getItem('token')
  if (!token) return ''

  try {
    const payload = token.split('.')[1]
    if (!payload) return ''
    const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
    const data = JSON.parse(json)
    return data.sub ?? ''
  } catch {
    return ''
  }
}

export function getStoredUsername() {
  return localStorage.getItem('username') || getUsernameFromToken()
}
