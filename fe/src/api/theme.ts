import { useQuery } from '@tanstack/react-query'
import { api } from './index'
import { TTheme, TThemesCount } from '../../../fe-shared/@types/theme'
import { useExploreStore } from '../store/useExploreStore'

export const useThemeListQuery = () =>
  useQuery({
    queryKey: ['theme-list'],
    queryFn: () =>
      api()
        .post(`theme/list`, {
          json: {
            size: -1,
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

type TFilter = {
  field?: string
  operation: string
  value?: string | number
  filters?: TFilter[]
}

export const useThemesCountQuery = () => {
  const { themes, filterAuthor, filterObject } = useExploreStore()

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

  if (filterObject.length) {
    filters.push({
      operation: 'OR',
      filters: [
        ...filterObject.map((o) => ({
          field: 'subjectEntries.id',
          operation: 'EQ',
          value: o.value,
        })),
        ...filterObject.map((a) => ({
          field: 'subjectPlaces.id',
          operation: 'EQ',
          value: a.value,
        })),
        ...filterObject.map((o) => ({
          field: 'keywords',
          operation: 'EQ',
          value: o.value,
        })),
      ],
    })
  }

  if (filterAuthor.length) {
    filters.push({
      operation: 'OR',
      filters: [
        ...filterAuthor.map((a) => ({
          field: 'mainAuthor.author.id',
          operation: 'EQ',
          value: a.value,
        })),
        ...filterAuthor.map((a) => ({
          field: 'subjectPersons.id',
          operation: 'EQ',
          value: a.value,
        })),
        ...filterAuthor.map((a) => ({
          field: 'coauthors.author.id',
          operation: 'EQ',
          value: a.value,
        })),
      ],
    })
  }

  filters.push({
    field: 'type',
    operation: 'EQ',
    value: 'illustration',
  })

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
