import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TIllustrationList } from '../../../../fe-shared/@types/illustration'

interface Records {
  items: TIllustrationList[]
  count: number
  searchAfter: string[]
}

type Input = {
  sort: 'ASC' | 'DESC'
  size: number
  year: { from: number; to: number }
  page: number
  authors: string[]
  objects: string[]
  places: string[]
  iccStates: string[]
  themeStates: string[]
  category: string
  search: string
}

const useIllustrationListQuery = ({
  size,
  sort,
  year,
  page,
  authors,
  objects,
  iccStates,
  themeStates,
  places,
  category,
  search,
}: Input) => {
  const filters: {
    field?: string
    operation: string
    value?: string | number
    filters?: { field: string; operation: string; value: string | number }[]
  }[] = []

  if (search.length > 0) {
    filters.push({
      field: category,
      operation: 'CONTAINS',
      value: search.trim(),
    })
  }

  if (authors?.length) {
    filters.push({
      operation: 'OR',
      filters: [
        ...authors.map((a) => ({
          field: 'mainAuthor.author.id',
          operation: 'EQ',
          value: a,
        })),
        ...authors.map((a) => ({
          field: 'subjectPersons.id',
          operation: 'EQ',
          value: a,
        })),
      ],
    })
  }

  if (objects.length) {
    filters.push({
      operation: 'OR',
      filters: [
        ...objects.map((o) => ({
          field: 'subjectEntries.id',
          operation: 'EQ',
          value: o,
        })),
      ],
    })
    // objects.forEach((o) => {
    //   filters.push({
    //     field: 'subjectEntries.id',
    //     operation: 'EQ',
    //     value: o,
    //   })
    // })
  }
  if (places.length) {
    filters.push({
      operation: 'OR',
      filters: [
        ...places.map((o) => ({
          field: 'publishingPlaces.id',
          operation: 'EQ',
          value: o,
        })),
      ],
    })
    // places.forEach((p) => {
    //   filters.push({
    //     field: 'publishingPlaces.id',
    //     operation: 'EQ',
    //     value: p,
    //   })
    // })
  }
  if (iccStates.length) {
    filters.push({
      operation: 'OR',
      filters: [
        ...iccStates.map((s) => ({
          field: 'iconclassState.id',
          operation: 'EQ',
          value: s,
        })),
      ],
    })
    // iccStates.forEach((s) => {
    //   filters.push({
    //     field: 'iconclassState.id',
    //     operation: 'EQ',
    //     value: s,
    //   })
    // })
  }
  if (themeStates.length) {
    filters.push({
      operation: 'OR',
      filters: [
        ...themeStates.map((s) => ({
          field: 'themeState.id',
          operation: 'EQ',
          value: s,
        })),
      ],
    })
    // themeStates.forEach((s) => {
    //   filters.push({
    //     field: 'themeState.id',
    //     operation: 'EQ',
    //     value: s,
    //   })
    // })
  }

  return useQuery({
    queryKey: ['illustration-list', size, year, sort, page, { ...filters }],
    queryFn: () =>
      api()
        .post(`record/list`, {
          json: {
            filters: [
              ...filters,
              {
                field: 'type',
                operation: 'EQ',
                value: 'illustration',
              },
              {
                field: 'yearFrom',
                operation: 'GTE',
                value: year.from,
              },
              // {
              //   field: 'yearFrom',
              //   operation: 'LTE',
              //   value: year.to,
              // },
              {
                field: 'yearFrom',
                operation: 'LTE',
                value: year.to,
              },
              // {
              //   field: 'yearTo',
              //   operation: 'GTE',
              //   value: year.from,
              // },
            ],
            sort: [
              {
                order: sort,
                type: 'FIELD',
                field: 'title',
              },
            ],
            offset: size * page,
            size,
          },
        })
        .json<Records>(),
  })
}

export default useIllustrationListQuery
