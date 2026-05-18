import axios from 'axios'

const runtimeBase =
  (window as any).__APP_CONFIG__?.API_BASE_URL || import.meta.env.VITE_API_BASE_URL;

const http = axios.create({
  baseURL: runtimeBase,
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default http
