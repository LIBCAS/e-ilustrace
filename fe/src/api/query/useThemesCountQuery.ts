import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TThemesCount } from '../../../../fe-shared/@types/theme'
import { useExploreStore } from '../../store/useExploreStore'

type TFilter = {
  field?: string
  operation: string
  value?: string | number
  filters?: TFilter[]
}

const useThemesCountQuery = () => {
  const { search, themes } = useExploreStore()

  const filters: {
    field?: string
    operation: string
    value?: string | number
    filters?: TFilter[]
  }[] = []

  if (themes.some((t) => t.length)) {
    filters.push({
      operation: 'AND',
      filters: [
        ...themes
          .filter((t) => t.length)
          .map((o) => ({
            field: 'themes.name',
            operation: 'EQ',
            value: o,
          })),
      ],
    })
  }

  if (search.length) {
    filters.push({
      operation: 'OR',
      filters: [
        {
          field: 'title',
          operation: 'FTXF',
          value: search.trim(),
        },
        {
          field: 'subjectPersons.fullName',
          operation: 'FTXF',
          value: search.trim(),
        },
        {
          field: 'subjectEntries.label',
          operation: 'FTXF',
          value: search.trim(),
        },
        {
          field: 'subjectPlaces.name',
          operation: 'FTXF',
          value: search.trim(),
        },
        {
          field: 'keywords',
          operation: 'FTXF',
          value: search.trim(),
        },
      ],
    })
    filters.push({
      field: 'type',
      operation: 'EQ',
      value: 'illustration',
    })
  }

  return useQuery({
    queryKey: ['themes-count', filters],
    queryFn: () =>
      api()
        .post(`theme/count-by-params`, {
          json: {
            filters: [...filters],
            size: 100,
          },
        })
        .json<TThemesCount[]>(),
  })
}

export default useThemesCountQuery
