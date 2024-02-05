/// <reference types="vite/client" />
/// <reference types="vite-plugin-svgr/client" />

interface ImportMetaEnv {
  readonly VITE_SENTRY_DNS: string
  readonly VITE_SENTRY_AUTH_TOKEN: string
  readonly VITE_SENTRY_ORG: string
  readonly VITE_SENTRY_PROJECT: string
  readonly VITE_SENTRY_URL: string
}
