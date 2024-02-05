import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TExhibitionDetail } from '../../../../fe-shared/@types/exhibition'

const useExhibitionDetailQuery = (id?: string) =>
  useQuery({
    queryKey: ['exhibition-detail', id],
    queryFn: () => api().get(`exhibition/${id}`).json<TExhibitionDetail>(),
    ...{
      enabled: !!id,
    },
  })

export default useExhibitionDetailQuery
