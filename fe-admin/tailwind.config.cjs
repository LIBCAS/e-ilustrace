/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    colors: {
      white: '#FFFFFF',
      superlightgray: '#E9ECEF',
      lightgray: '#CED4DA',
      gray: '#6C757D',
      footergray: '#F2F4F5',
      darkgray: '#343A40',
      black: '#121416',
      red: '#E2293F',
      green: '#22BE73',
      warning: '#FCB62E',
      blue: '#2298EE',
    },
    screens: {
      '2xs': '360px',
      xs: '480px',
      sm: '640px',
      md: '768px',
      lg: '1024px',
      xl: '1280px',
      '2xl': '1536px',
    },
    extend: {
      boxShadow: {
        '3xl': '0 35px 60px -15px rgba(0, 0, 0, 0.3)',
      },
    },
  },
  plugins: [],
}
