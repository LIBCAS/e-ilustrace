import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactCompiler from 'eslint-plugin-react-compiler'
import reactRefresh from 'eslint-plugin-react-refresh'
import betterTailwindcss from 'eslint-plugin-better-tailwindcss'
import tseslint from 'typescript-eslint'
import { defineConfig, globalIgnores } from 'eslint/config'

const tailwindUnknownIgnorePatterns = [
  '^wrapper$',
  '^sidebar-item$',
  '^button$',
  '^icon$',
  '^check-1$',
  '^explore-item$',
  '^explore-item-text$',
  '^storyline$',
  '^exhibition-text$',
  '^wysiwyg-editor-content$',
  '^slider-handles$',
  '^slider-tracks$',
  '^disabled$',
]

export default defineConfig([
  globalIgnores(['dist', 'node_modules', '@']),
  {
    files: ['**/*.{ts,tsx}'],
    plugins: {
      'react-compiler': reactCompiler,
    },
    extends: [
      js.configs.recommended,
      ...tseslint.configs.recommended,
      reactHooks.configs.flat.recommended,
      reactRefresh.configs.vite,
      betterTailwindcss.configs['correctness-warn'],
    ],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      globals: globals.browser,
    },
    settings: {
      'better-tailwindcss': {
        cwd: '.',
        entryPoint: './src/index.css',
        detectComponentClasses: true,
      },
    },
    rules: {
      'react-compiler/react-compiler': 'error',
      'better-tailwindcss/no-unknown-classes': [
        'warn',
        { ignore: tailwindUnknownIgnorePatterns },
      ],
    },
  },
])
