import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TIllustrationDetail } from '../../../../fe-shared/@types/illustration'
import { TBookDetail } from '../../../../fe-shared/@types/book'

type Input = {
  id: string
}

const useRecordQuery = ({ id }: Input) =>
  useQuery({
    queryKey: ['record-detail', id],
    queryFn: () =>
      api().get(`record/${id}`).json<TIllustrationDetail | TBookDetail>(),
  })

export default useRecordQuery
