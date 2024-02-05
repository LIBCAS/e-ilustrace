import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TIconClassCategoryDefault } from '../../../../fe-shared/@types/iconClass'

const useIconClassListQuery = () =>
  useQuery({
    queryKey: ['iconclass-list'],
    queryFn: () =>
      api()
        .post(`iconclass/list`, {
          json: {
            size: 10000,
          },
        })
        .json<{
          items: TIconClassCategoryDefault[]
          count: number
          searchAfter: string[]
        }>(),
  })

export default useIconClassListQuery
