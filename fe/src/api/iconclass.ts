import { useQuery } from '@tanstack/react-query'
import { api } from './index'
import { TIconClassCategoryDefault } from '../../../fe-shared/@types/iconClass'

// eslint-disable-next-line import/prefer-default-export
export const useIconClassListQuery = () =>
  useQuery({
    queryKey: ['iconclass-list'],
    queryFn: () =>
      api()
        .post(`iconclass/list`, {
          json: {
            size: -1,
            sort: [
              {
                order: 'ASC',
                type: 'FIELD',
                field: 'code',
              },
            ],
          },
        })
        .json<{
          items: TIconClassCategoryDefault[]
          count: number
          searchAfter: string[]
        }>(),
  })
