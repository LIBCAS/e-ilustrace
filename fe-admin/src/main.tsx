import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import { BrowserRouter } from 'react-router-dom'
import { ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'
import { QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { init as SentryInit, BrowserTracing } from '@sentry/react'
import { queryClient } from './api'
import ScrollToTop from './components/ScrollToTop'
import App from './App'

// Localization
import './lang'

const { MODE, VITE_SENTRY_DNS } = import.meta.env

// Setup Sentry for errors reporting in production
SentryInit({
  dsn: VITE_SENTRY_DNS,
  tracePropagationTargets: ['localhost', 'e-ilustrace.cz', /^\//],
  integrations: [new BrowserTracing()],
  environment: MODE,
  // We recommend adjusting this value in production, or using tracesSampler
  // for finer control
  tracesSampleRate: MODE === 'development' ? 0 : 0.5,
  beforeSend(event) {
    return MODE === 'development' ? null : event
  },
})

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement)
root.render(
  <React.StrictMode>
    <BrowserRouter basename={MODE === 'development' ? undefined : '/admin'}>
      <QueryClientProvider client={queryClient}>
        <ScrollToTop />
        <App />
        <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>
      <ToastContainer position="bottom-right" />
    </BrowserRouter>
  </React.StrictMode>
)
