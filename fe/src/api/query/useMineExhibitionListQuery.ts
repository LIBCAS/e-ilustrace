import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TExhibitionList } from '../../../../fe-shared/@types/exhibition'

interface ExhibitionList {
  items: TExhibitionList[]
  count: number
  searchAfter: string[]
}

const useMineExhibitionListQuery = (enabled = true) =>
  useQuery({
    queryKey: ['exhibition-list', 'mine'],
    queryFn: () => {
      return api()
        .post('exhibition/list-mine', {
          json: {
            filters: [],
            size: 10000,
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
    ...{ enabled },
  })

export default useMineExhibitionListQuery
