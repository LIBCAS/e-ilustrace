import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TSelectionDetail } from '../../../../fe-shared/@types/selection'

const useMySelectionQuery = (enabled = true) =>
  useQuery({
    queryKey: ['my-selection'],
    queryFn: () => api().get(`selection/mine`).json<TSelectionDetail | null>(),
    ...{ enabled },
  })

export default useMySelectionQuery
