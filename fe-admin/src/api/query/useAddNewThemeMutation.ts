import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'

type TInput = {
  theme: string
}

const useAddNewThemeMutation = () =>
  useMutation({
    mutationFn: ({ theme }: TInput) =>
      api()
        .post(`theme`, {
          json: { name: theme },
        })
        .json(),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['theme-list'] })
    },
  })

export default useAddNewThemeMutation
