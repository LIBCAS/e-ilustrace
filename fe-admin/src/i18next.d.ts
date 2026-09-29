import { resources, defaultNS } from './lang'

declare module 'i18next' {
   
  interface CustomTypeOptions {
    defaultNS: typeof defaultNS
    resources: (typeof resources)['cs']
  }
}
