import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'

type TInput = {
  items: string[]
}

const useRemoveFromMySelectionMutation = () =>
  useMutation({
    mutationFn: ({ items }: TInput) =>
      api().put('selection/delete-items', {
        json: {
          items: items.map((i) => ({ id: i })),
        },
      }),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-selection'] })
    },
  })

export default useRemoveFromMySelectionMutation
