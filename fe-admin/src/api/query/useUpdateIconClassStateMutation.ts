import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'
import { TEnrichmentStates } from '../../../../fe-shared/@types/illustration'

type TInput = {
  uuid: string
  state: TEnrichmentStates
}

const useUpdateIconClassStateMutation = () =>
  useMutation({
    mutationFn: ({ uuid, state }: TInput) =>
      api()
        .put(`record/${uuid}/ill/iconclass-state`, {
          json: state,
        })
        .json(),

    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ['illustration-detail', variables.uuid],
      })
    },
  })

export default useUpdateIconClassStateMutation
