/// <reference types="vite/client" />
import { defineConfig } from 'vite'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  return {
    plugins: [react(), babel({ presets: [reactCompilerPreset()] })],
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
