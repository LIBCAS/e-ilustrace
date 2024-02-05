import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TUser } from '../../../../fe-shared/@types/user'

const useMeQuery = () =>
  useQuery({
    queryKey: ['me'],
    queryFn: () => api().get('internal/me').json<TUser>(),
  })

export default useMeQuery
