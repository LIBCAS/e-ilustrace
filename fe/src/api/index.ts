import ky from 'ky'
import { QueryClient } from '@tanstack/react-query'
// import { toast } from 'react-toastify'

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 0,
      refetchOnWindowFocus: false,
    },
  },
})

// type ErrorType = {
//   timestamp: string // '2023-04-11T11:55:02.425270'
//   status: number // 400
//   code: string // 'FIELD_NOT_MAPPED'
//   path: string // 'POST /api/eil/record/list'
//   user: null
//   message: string // "Field 'type' is not present in index mapping."
//   details: { key: string } // { key: 'type' }
//   debugInfo: {
//     type: string
//     // type: 'cs.inqool.eas.common.domain.index.field.IndexFieldLeafNode'
//   }
// }

// const processError = (error: ErrorType) => {
//   const { message } = error
//   return `${error.status}: ${message}`
//   return message
// }

export const api = () =>
  ky.extend({
    timeout: 60000,
    retry: 0,
    prefixUrl: '/api/eil',
    hooks: {
      afterResponse: [
        async (_request, _options, response) => {
          if (response.ok) return

          const error = await response.json()
          // toast.error(processError(error))
          throw error
        },
      ],
    },
  })
