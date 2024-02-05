import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TExhibitionList } from '../../../../fe-shared/@types/exhibition'

interface ExhibitionList {
  items: TExhibitionList[]
  count: number
  searchAfter: string[]
}

interface TProps {
  size: number
  page: number
}

const useExhibitionListQuery = ({ size, page }: TProps) =>
  useQuery({
    queryKey: ['exhibition-list', size, page],
    queryFn: () => {
      return api()
        .post('exhibition/list', {
          json: {
            filters: [
              {
                field: 'published',
                operation: 'GTE',
                value: '0',
              },
            ],
            size,
            offset: size * page,
            sort: [
              {
                order: 'DESC',
                type: 'FIELD',
                field: 'created',
              },
            ],
          },
        })
        .json<ExhibitionList>()
    },
  })

export default useExhibitionListQuery
