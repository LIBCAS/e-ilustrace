import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TIllustrationDetail } from '../../../../fe-shared/@types/illustration'

type Input = {
  id: string
}

const useIllustrationDetailQuery = ({ id }: Input) =>
  useQuery({
    queryKey: ['illustration-detail', id],
    queryFn: () => api().get(`record/${id}`).json<TIllustrationDetail>(),
  })

export default useIllustrationDetailQuery
