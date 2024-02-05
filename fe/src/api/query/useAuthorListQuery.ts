import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TAuthorList } from '../../../../fe-shared/@types/author'
import { RecordType } from '../../@types/types'

interface Authors {
  items: TAuthorList[]
  count: number
  searchAfter: string[]
}

const useAuthorListQuery = (type: RecordType) =>
  useQuery({
    queryKey: ['author-list', type],
    queryFn: () =>
      api()
        .post('author/list', {
          json: {
            size: 10000,
            sort: [
              {
                order: 'ASC',
                type: 'FIELD',
                field: 'fullName',
              },
            ],
          },
        })
        .json<Authors>(),
  })

export default useAuthorListQuery
