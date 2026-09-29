module.exports = {
  settings: {
    react: {
      version: 'detect',
    },
  },
  env: {
    browser: true,
    es2022: true,
  },
  extends: [
    'airbnb',
    'airbnb-typescript',
    'airbnb/hooks',
    'plugin:react/recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:prettier/recommended',
  ],
  overrides: [],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
    project: ['./tsconfig.json'],
    tsconfigRootDir: __dirname,
  },
  ignorePatterns: ['vite.config.ts'],
  plugins: ['react', 'risxss', '@typescript-eslint', 'prettier'],
  rules: {
    'react/react-in-jsx-scope': 0,
    'react/function-component-definition': 0,
    'react/require-default-props': ['error', { functions: 'defaultArguments' }],
    'prettier/prettier': 'error',
    'import/no-extraneous-dependencies': 0,
    'no-continue': 0,
    'risxss/catch-potential-xss-react': 'error',
    'react/no-danger': 0,
    '@typescript-eslint/no-unused-vars': 1,
  },
}
