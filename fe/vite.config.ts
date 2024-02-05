/// <reference types="vite/client" />
import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react-swc'
import svgr from 'vite-plugin-svgr'
import eslint from 'vite-plugin-eslint'
import { sentryVitePlugin } from '@sentry/vite-plugin'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = { ...process.env, ...loadEnv(mode, process.cwd()) }

  // console.log('Printing env variables')
  // console.log(env)
  // console.log('End of printing env variables')

  return {
    plugins: [
      react(),
      svgr(),
      eslint(),
      sentryVitePlugin({
        disable: mode === 'development',
        url: env.VITE_SENTRY_URL,
        authToken: env.VITE_SENTRY_AUTH_TOKEN,
        org: env.VITE_SENTRY_ORG,
        project: env.VITE_SENTRY_PROJECT,
        release: {
          create: !!env.VITE_SENTRY_DEPLOY_ENV,
          deploy: {
            env: env.VITE_SENTRY_DEPLOY_ENV || 'Not specified',
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
      port: 3000,
      host: true,
      proxy: {
        '/api': {
          target: 'https://test.e-ilustrace.cz',
          changeOrigin: true,
          secure: true,
        },
        '/iiif': {
          target: 'https://test.e-ilustrace.cz',
          changeOrigin: true,
          secure: true,
        },
      },
    },
    build: {
      sourcemap: true,
    },
  }
})
