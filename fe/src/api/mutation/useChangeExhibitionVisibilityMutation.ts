import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'

type TInput = {
  id: string
}

const useChangeExhibitionVisibilityMutation = () =>
  useMutation({
    mutationFn: ({ id }: TInput) => api().put(`exhibition/${id}/publish`),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ['exhibition-detail', variables.id],
      })
    },
  })

export default useChangeExhibitionVisibilityMutation
