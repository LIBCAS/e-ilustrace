import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'

type TInput = {
  illustrations: string[]
  books: string[]
}

const useAddToMySelectionMutation = () =>
  useMutation({
    mutationFn: ({ illustrations, books }: TInput) => {
      return api().post('selection/add-items', {
        json: {
          illustrations: illustrations.map((i) => ({ id: i })),
          books: books.map((b) => ({ id: b })),
        },
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-selection'] })
    },
  })

export default useAddToMySelectionMutation
