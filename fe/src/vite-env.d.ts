/// <reference types="vite/client" />
/// <reference types="vite-plugin-svgr/client" />

// interface ImportMetaEnv {
//   readonly VITE_SENTRY_DNS: string
//   readonly VITE_SENTRY_AUTH_TOKEN: string
//   readonly VITE_SENTRY_ORG: string
//   readonly VITE_SENTRY_PROJECT: string
//   readonly VITE_SENTRY_URL: string
// }

declare module 'mirador' {
  const mirador: {
    viewer: (config: unknown, plugins?: object[]) => void
  }

  export default mirador
}

declare module 'mirador-image-tools' {
  export const miradorImageToolsPlugin: object[]
}
