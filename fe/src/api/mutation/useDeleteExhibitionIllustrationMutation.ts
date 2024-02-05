import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'

type TInput = {
  id: string
}

const useDeleteExhibitionMutation = () =>
  useMutation({
    mutationFn: ({ id }: TInput) => api().delete(`exhibition-item/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['exhibition-list'] })
    },
  })

export default useDeleteExhibitionMutation
