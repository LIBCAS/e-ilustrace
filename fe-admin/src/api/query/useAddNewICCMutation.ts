import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'

type TInput = {
  ICC: string
}

const useAddNewICCMutation = () =>
  useMutation({
    mutationFn: ({ ICC }: TInput) =>
      api()
        .post(`iconclass`, {
          json: { code: ICC },
        })
        .json(),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['iconclass-list'] })
    },
  })

export default useAddNewICCMutation
