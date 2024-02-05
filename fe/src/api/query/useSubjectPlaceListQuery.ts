import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { RecordType } from '../../@types/types'
import { TSubjectPlaceList } from '../../../../fe-shared/@types/subject'

interface Places {
  items: TSubjectPlaceList[]
  count: number
  searchAfter: string[]
}

const useSubjectPlaceListQuery = (type: RecordType) =>
  useQuery({
    queryKey: ['subject-place-list', type],
    queryFn: () =>
      api()
        .post('subject/place/list', {
          json: {
            size: 10000,
            filters: [
              type === 'BOOK'
                ? {
                    field: 'fromBook',
                    operation: 'EQ',
                    value: true,
                  }
                : {
                    field: 'fromIllustration',
                    operation: 'EQ',
                    value: true,
                  },
            ],
            sort: [
              {
                order: 'ASC',
                type: 'FIELD',
                field: 'name',
              },
            ],
          },
        })
        .json<Places>(),
  })

export default useSubjectPlaceListQuery
