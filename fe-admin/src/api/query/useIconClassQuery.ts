import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TIllustrationDetail } from '../../../../fe-shared/@types/illustration'

type Input = {
  id: string
}

const useIconClassQuery = ({ id }: Input) =>
  useQuery({
    queryKey: ['iconclass', id],
    queryFn: () => api().get(`iconclass/${id}`).json<TIllustrationDetail>(),
  })

export default useIconClassQuery
