import { keepPreviousData, useQuery } from '@tanstack/react-query'
import {
  RecordType,
  TFilter,
  TFilterOperator,
  TRecordYears,
  TSortTypes,
} from '../../../fe-shared/@types/common'
import { api } from './index'
import { TBookDetail, TBookList } from '../../../fe-shared/@types/book'
import {
  TIllustrationDetail,
  TIllustrationList,
} from '../../../fe-shared/@types/illustration'
import { TRecordFacets } from '../../../fe-shared/@types/facets'

export const useSearchYearsRangeQuery = (type: RecordType) =>
  useQuery({
    queryKey: ['search-years-range', type],
    queryFn: () =>
      api().post(`record/years-range`, { json: { type } }).json<TRecordYears>(),
  })

interface Records {
  items: TBookList[] | TIllustrationList[]
  count: number
  searchAfter: string[]
}

interface TRecordListInput {
  type: RecordType
  sort?: TSortTypes
  year?: { from: number; to: number }
  size: number
  page: number
  authors?: { authors: string[]; operation: TFilterOperator }
  objects?: { objects: string[]; operation: TFilterOperator }
  publishingPlaces?: { publishingPlaces: string[]; operation: TFilterOperator }
  subjectPlaces?: { subjectPlaces: string[]; operation: TFilterOperator }
  themes?: { themes: string[]; operation: TFilterOperator }
  icc?: { icc: string[]; operation: TFilterOperator }
  searchWithCategory?: {
    search: string
    category: string | 'ALL'
    operation: TFilterOperator
  }[]
  searchWithCategoryOperation?: 'OR' | 'AND'
  isIIIF?: boolean
  enabled?: boolean
  usePlaceholderData?: boolean
}

export const useRecordListQuery = ({
  type,
  size,
  year,
  sort = 'title_ASC',
  page,
  authors,
  objects,
  publishingPlaces,
  subjectPlaces,
  themes,
  icc,
  searchWithCategory,
  searchWithCategoryOperation = 'OR',
  isIIIF = false,
  enabled = true,
  usePlaceholderData = false,
}: TRecordListInput) => {
  const filters: {
    field?: string
    operation: string
    value?: string | number
    filters?: TFilter[]
  }[] = []

  if (
    searchWithCategory?.length &&
    searchWithCategory.some((s) => s.search.length)
  ) {
    filters.push({
      operation: searchWithCategoryOperation,
      filters: [
        ...searchWithCategory
          .filter((s) => s.search.length)
          .map((s) =>
            s.category === 'ALL'
              ? {
                  operation: 'OR',
                  filters: [
                    {
                      field: 'title',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'identifier',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'id',
                      operation: 'CONTAINS',
                      value: s.search.trim(),
                    },
                    {
                      field: 'mainAuthor.author.fullName',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'subjectPersons.fullName',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'coauthors.author.fullName',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'subjectEntries.label',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'keywords',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'publishingPlaces.name',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'subjectPlaces.name',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    // {
                    //   field: 'yearFrom',
                    //   operation: 'EQ',
                    //   value: Number(s.search.trim()),
                    // },
                  ],
                }
              : {
                  field: s.category,
                  operation: s.operation,
                  value:
                    s.category === 'yearFrom'
                      ? Number(s.search.trim())
                      : s.search.trim(),
                }
          ),
      ],
    })
  }

  if (authors?.authors.length) {
    filters.push({
      operation: authors.operation,
      filters: [
        ...authors.authors.map((a) => ({
          field: 'mainAuthor.author.id',
          operation: 'EQ',
          value: a,
        })),
        ...authors.authors.map((a) => ({
          field: 'subjectPersons.id',
          operation: 'EQ',
          value: a,
        })),
        ...authors.authors.map((a) => ({
          field: 'coauthors.author.id',
          operation: 'EQ',
          value: a,
        })),
      ],
    })
  }

  if (objects?.objects.length) {
    filters.push({
      operation: objects.operation,
      filters: [
        ...objects.objects.map((o) => ({
          field: 'subjectEntries.id',
          operation: 'EQ',
          value: o,
        })),
        ...objects.objects.map((a) => ({
          field: 'subjectPlaces.id',
          operation: 'EQ',
          value: a,
        })),
        ...objects.objects.map((o) => ({
          field: 'keywords',
          operation: 'EQ',
          value: o,
        })),
        ...objects.objects.map((o) => ({
          field: 'genres.id',
          operation: 'EQ',
          value: o,
        })),
      ],
    })
  }

  if (publishingPlaces?.publishingPlaces.length) {
    filters.push({
      operation: publishingPlaces.operation,
      filters: [
        ...publishingPlaces.publishingPlaces.map((o) => ({
          field: 'publishingPlaces.id',
          operation: 'EQ',
          value: o,
        })),
      ],
    })
  }

  if (subjectPlaces?.subjectPlaces.length) {
    filters.push({
      operation: subjectPlaces.operation,
      filters: [
        ...subjectPlaces.subjectPlaces.map((o) => ({
          field: 'subjectPlaces.id',
          operation: 'EQ',
          value: o,
        })),
      ],
    })
  }

  if (themes?.themes.length && themes.themes.some((t) => t.length)) {
    filters.push({
      operation: themes.operation,
      filters: [
        ...themes.themes.map((o) => ({
          field: 'themes.name',
          operation: 'EQ',
          value: o,
        })),
      ],
    })
  }

  if (icc?.icc.length) {
    filters.push({
      operation: icc.operation,
      filters: [
        ...icc.icc.map((i) => ({
          field: 'iconclass.code',
          operation: 'START_WITH',
          value: i,
        })),
      ],
    })
  }

  if (isIIIF) {
    filters.push({
      operation: 'OR',
      filters: [
        {
          field: 'isIiif',
          operation: 'EQ',
          value: 'true',
        },
      ],
    })
  }

  if (year) {
    filters.push(
      ...[
        {
          field: 'yearFrom',
          operation: 'GTE',
          value: year.from,
        },
        {
          field: 'yearFrom',
          operation: 'LTE',
          value: year.to,
        },
      ]
    )
  }

  const sortType = sort?.split('_')

  return useQuery({
    queryKey: ['record-list', type, size, year, sort, page, { ...filters }],
    queryFn: () =>
      api()
        .post(`record/list`, {
          json: {
            filters: [
              ...filters,
              {
                field: 'type',
                operation: 'EQ',
                value: type.toLowerCase(),
              },
            ],
            sort: [
              {
                order: sortType[1],
                type: 'FIELD',
                field: sortType[0],
              },
            ],
            offset: size * page,
            size,
          },
        })
        .json<Records>(),
    enabled,
    placeholderData: usePlaceholderData ? keepPreviousData : undefined,
  })
}

interface TRecordsWithFacetsQueryInput extends TRecordListInput {
  recordsEnabled?: boolean
  facetsEnabled?: boolean
}

export const useRecordsWithFacetsQueryList = ({
  type,
  size,
  year,
  sort = 'title_ASC',
  page,
  authors,
  objects,
  publishingPlaces,
  subjectPlaces,
  themes,
  icc,
  searchWithCategory,
  searchWithCategoryOperation = 'OR',
  isIIIF = false,
  recordsEnabled = true,
  facetsEnabled = false,
  usePlaceholderData = false,
}: TRecordsWithFacetsQueryInput) => {
  const filters: {
    field?: string
    operation: string
    value?: string | number
    filters?: TFilter[]
  }[] = []

  if (
    searchWithCategory?.length &&
    searchWithCategory.some((s) => s.search.length)
  ) {
    filters.push({
      operation: searchWithCategoryOperation,
      filters: [
        ...searchWithCategory
          .filter((s) => s.search.length)
          .map((s) =>
            s.category === 'ALL'
              ? {
                  operation: 'OR',
                  filters: [
                    {
                      field: 'title',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'identifier',
                      operation: 'CONTAINS',
                      value: s.search.trim(),
                    },
                    {
                      field: 'id',
                      operation: 'CONTAINS',
                      value: s.search.trim(),
                    },
                    {
                      field: 'mainAuthor.author.fullName',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'subjectPersons.fullName',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'coauthors.author.fullName',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'subjectEntries.label',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'keywords',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'publishingPlaces.name',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    {
                      field: 'subjectPlaces.name',
                      operation: 'FTXF',
                      value: s.search.trim(),
                    },
                    // {
                    //   field: 'yearFrom',
                    //   operation: 'EQ',
                    //   value: Number(s.search.trim()),
                    // },
                  ],
                }
              : {
                  field: s.category,
                  operation: s.operation,
                  value:
                    s.category === 'yearFrom'
                      ? Number(s.search.trim())
                      : s.search.trim(),
                }
          ),
      ],
    })
  }

  if (authors?.authors.length) {
    filters.push({
      operation: authors.operation,
      filters: [
        ...authors.authors.map((a) => ({
          field: 'mainAuthor.author.id',
          operation: 'EQ',
          value: a,
        })),
        ...authors.authors.map((a) => ({
          field: 'subjectPersons.id',
          operation: 'EQ',
          value: a,
        })),
        ...authors.authors.map((a) => ({
          field: 'coauthors.author.id',
          operation: 'EQ',
          value: a,
        })),
      ],
    })
  }

  if (objects?.objects.length) {
    filters.push({
      operation: objects.operation,
      filters: [
        ...objects.objects.map((o) => ({
          field: 'subjectEntries.id',
          operation: 'EQ',
          value: o,
        })),
        ...objects.objects.map((a) => ({
          field: 'subjectPlaces.id',
          operation: 'EQ',
          value: a,
        })),
        ...objects.objects.map((o) => ({
          field: 'keywords',
          operation: 'EQ',
          value: o,
        })),
        ...objects.objects.map((o) => ({
          field: 'genres.id',
          operation: 'EQ',
          value: o,
        })),
      ],
    })
  }

  if (publishingPlaces?.publishingPlaces.length) {
    filters.push({
      operation: publishingPlaces.operation,
      filters: [
        ...publishingPlaces.publishingPlaces.map((o) => ({
          field: 'publishingPlaces.id',
          operation: 'EQ',
          value: o,
        })),
      ],
    })
  }

  if (subjectPlaces?.subjectPlaces.length) {
    filters.push({
      operation: subjectPlaces.operation,
      filters: [
        ...subjectPlaces.subjectPlaces.map((o) => ({
          field: 'subjectPlaces.id',
          operation: 'EQ',
          value: o,
        })),
      ],
    })
  }

  if (themes?.themes.length && themes.themes.some((t) => t.length)) {
    filters.push({
      operation: themes.operation,
      filters: [
        ...themes.themes.map((o) => ({
          field: 'themes.name',
          operation: 'EQ',
          value: o,
        })),
      ],
    })
  }

  if (icc?.icc.length) {
    filters.push({
      operation: icc.operation,
      filters: [
        ...icc.icc.map((i) => ({
          field: 'iconclass.code',
          operation: 'START_WITH',
          value: i,
        })),
      ],
    })
  }

  if (isIIIF) {
    filters.push({
      operation: 'OR',
      filters: [
        {
          field: 'isIiif',
          operation: 'EQ',
          value: 'true',
        },
      ],
    })
  }

  if (year) {
    filters.push(
      ...[
        {
          field: 'yearFrom',
          operation: 'GTE',
          value: year.from,
        },
        {
          field: 'yearFrom',
          operation: 'LTE',
          value: year.to,
        },
      ]
    )
  }

  const sortType = sort?.split('_')

  return {
    records: useQuery({
      queryKey: ['record-list', type, size, year, sort, page, { ...filters }],
      queryFn: () =>
        api()
          .post(`record/list`, {
            json: {
              filters: [
                ...filters,
                {
                  field: 'type',
                  operation: 'EQ',
                  value: type.toLowerCase(),
                },
              ],
              sort: [
                {
                  order: sortType[1],
                  type: 'FIELD',
                  field: sortType[0],
                },
              ],
              offset: size * page,
              size,
            },
          })
          .json<Records>(),
      enabled: recordsEnabled,
      placeholderData: usePlaceholderData ? keepPreviousData : undefined,
    }),
    facets: useQuery({
      queryKey: ['facet-list', type, year, { ...filters }],
      queryFn: () =>
        api()
          .post(`record/list-facets/${type}`, {
            json: {
              size: -1,
              filters,
            },
          })
          .json<TRecordFacets>(),
      enabled: facetsEnabled,
    }),
  }
}

type TRecordInput = {
  id: string
}

export const useRecordQuery = ({ id }: TRecordInput) =>
  useQuery({
    queryKey: ['record-detail', id],
    queryFn: () =>
      api().get(`record/${id}`).json<TIllustrationDetail | TBookDetail>(),
  })
