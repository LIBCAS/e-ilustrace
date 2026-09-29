import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from './index'

// eslint-disable-next-line import/prefer-default-export
export const useImportMutation = () =>
  useMutation({
    mutationFn: () => api().post(`record/ill/import`).json(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['record-list'] })
    },
  })
