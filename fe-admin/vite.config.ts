/// <reference types="vite/client" />
import { defineConfig, loadEnv } from 'vite'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import babel from '@rolldown/plugin-babel'
import svgr from 'vite-plugin-svgr'
import eslint from 'vite-plugin-eslint2'
import { sentryVitePlugin } from '@sentry/vite-plugin'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [
      react(),
      babel({ presets: [reactCompilerPreset()] }),
      tailwindcss(),
      svgr(),
      eslint(),
      sentryVitePlugin({
        disable: mode === 'development',
        url: env.VITE_SENTRY_URL,
        authToken: env.VITE_SENTRY_AUTH_TOKEN,
        org: env.VITE_SENTRY_ORG,
        project: env.VITE_SENTRY_PROJECT,
        release: {
          create: !!env.SENTRY_DEPLOY_ENV,
          deploy: {
            env: env.SENTRY_DEPLOY_ENV || 'Not specified',
          },
          setCommits: {
            auto: true,
            ignoreMissing: true,
          },
        },
        // telemetry: false,
        // debug: true,
      }),
    ],
    server: {
      port: 3001,
      host: true,
      proxy: {
        '/api': {
          target: 'https://test.e-ilustrace.cz',
          changeOrigin: true,
          secure: true,
        },
      },
    },
    optimizeDeps: {
      needsInterop: ['react-paginate'],
    },
    base: mode === 'development' ? '/' : '/admin/',
    build: {
      sourcemap: true,
    },
  }
})
