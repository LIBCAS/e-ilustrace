import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TTheme } from '../../../../fe-shared/@types/theme'

const useThemeListQuery = () =>
  useQuery({
    queryKey: ['theme-list'],
    queryFn: () =>
      api()
        .post(`theme/list`, {
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
        .json<{
          items: TTheme[]
          count: number
          searchAfter: string[]
        }>(),
  })

export default useThemeListQuery
