import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TPublishingPlaceList } from '../../../../fe-shared/@types/place'

interface Places {
  items: TPublishingPlaceList[]
  count: number
  searchAfter: string[]
}

const usePublishingPlaceListQuery = () =>
  useQuery({
    queryKey: ['publishing-place-list'],
    queryFn: () =>
      api()
        .post('publishing-place/list', {
          json: {
            size: 10000,
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
