import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'

const useImportMutation = () =>
  useMutation({
    mutationFn: () => api().post(`record/ill/import`).json(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['record-list'] })
    },
  })

export default useImportMutation
