import i18next from 'i18next'
import { initReactI18next } from 'react-i18next'
import LanguageDetector from 'i18next-browser-languagedetector'

import translationEn from './en'
import translationCs from './cs'

const resources = { en: translationEn, cs: translationCs }

export const supportedLanguages = ['cs', 'en'] as const
export type TSupportedLanguages = (typeof supportedLanguages)[number]
export const defaultLang = 'cs'

i18next.use(LanguageDetector).use(initReactI18next)
if (!i18next.isInitialized) {
  i18next.init({
    resources,
    fallbackLng: defaultLang,
    supportedLngs: supportedLanguages,
    load: 'languageOnly',

    ns: Object.keys(resources.cs),
    fallbackNS: 'explore',

    interpolation: {
      escapeValue: false,
    },
    // react: {
    //   useSuspense: true
    // }
  })
}

export default i18next
