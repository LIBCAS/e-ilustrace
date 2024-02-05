import React, { Suspense } from 'react'
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
import Loader from './components/reusableComponents/Loader'

const { MODE, VITE_SENTRY_DNS } = import.meta.env

// Setup Sentry for errors reporting in production
SentryInit({
  enabled: MODE !== 'development',
  dsn: VITE_SENTRY_DNS,
  tracePropagationTargets: ['e-ilustrace.cz', /^\//],
  integrations: [new BrowserTracing()],
  environment: MODE,
  tracesSampleRate: 0.5,
})

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement)
root.render(
  <React.StrictMode>
    <BrowserRouter>
      <QueryClientProvider client={queryClient}>
        <ScrollToTop />
        <Suspense
          fallback={
            <Loader className="mx-auto flex min-h-[50vh] items-center justify-center" />
          }
        >
          <App />
        </Suspense>
        <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>
      <ToastContainer position="bottom-right" autoClose={5000} />
    </BrowserRouter>
  </React.StrictMode>
)
