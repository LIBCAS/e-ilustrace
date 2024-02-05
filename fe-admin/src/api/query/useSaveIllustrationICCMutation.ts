import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'

type TInput = {
  uuid: string
  ICC: { id: string }[]
  themes: { id: string }[]
}

const useSaveIllustrationICCMutation = () =>
  useMutation({
    mutationFn: ({ uuid, ICC, themes }: TInput) =>
      api()
        .put(`record/${uuid}`, {
          json: { iconclass: ICC, type: 'ILLUSTRATION', themes },
        })
        .json(),

    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ['illustration-detail', variables.uuid],
      })
    },
  })

export default useSaveIllustrationICCMutation
