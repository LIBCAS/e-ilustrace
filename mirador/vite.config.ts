/// <reference types="vite/client" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import eslint from 'vite-plugin-eslint'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  return {
    plugins: [react(), eslint()],
    build: {
      sourcemap: true,
    },
    server: {
      port: 3000,
      host: true,
    },
    base: mode === 'development' ? '/' : '/mirador/',
  }
})
