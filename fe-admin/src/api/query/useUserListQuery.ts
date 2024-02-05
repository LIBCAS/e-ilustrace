import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TUserList } from '../../../../fe-shared/@types/user'

interface Users {
  items: TUserList[]
  count: number
  searchAfter: string[]
}

const useUserListQuery = () =>
  useQuery({
    queryKey: ['user-list'],
    queryFn: () =>
      api()
        .post('user/list', {
          json: {
            size: 10000,
          },
        })
        .json<Users>(),
  })

export default useUserListQuery
