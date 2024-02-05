import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TPublishingPlaceList } from '../../../../fe-shared/@types/place'
import { RecordType } from '../../@types/types'

interface Places {
  items: TPublishingPlaceList[]
  count: number
  searchAfter: string[]
}

const usePublishingPlaceListQuery = (type: RecordType) =>
  useQuery({
    queryKey: ['publishing-place-list', type],
    queryFn: () =>
      api()
        .post('publishing-place/list', {
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

export default usePublishingPlaceListQuery
