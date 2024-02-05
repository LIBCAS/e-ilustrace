import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'
import { TSelectionItemDetail } from '../../../../fe-shared/@types/selection'

type TInput = {
  record: TSelectionItemDetail
}

const useChangeViewInMirador = () =>
  useMutation({
    mutationFn: ({ record }: TInput) => {
      return api().put('selection/mirador', {
        json: { items: [record] },
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-selection'] })
    },
  })

export default useChangeViewInMirador
