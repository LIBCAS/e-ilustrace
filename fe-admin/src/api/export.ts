import { useMutation } from '@tanstack/react-query'
import {
  TFilter,
  // TFilterOperator,
  // TSortTypes,
} from '../../../fe-shared/@types/common'
import { api } from './index'
// import { TIllustrationList } from '../../../fe-shared/@types/illustration'
import { useExportStore } from '../store/useExportStore'

// interface Props {
//   sort?: TSortTypes
//   year?: { from: number; to: number }
//   size: number
//   page: number
//   authors?: { authors: string[]; operation: TFilterOperator }
//   objects?: { objects: string[]; operation: TFilterOperator }
//   publishingPlaces?: { publishingPlaces: string[]; operation: TFilterOperator }
//   subjectPlaces?: { subjectPlaces: string[]; operation: TFilterOperator }
//   themes?: { themes: string[]; operation: TFilterOperator }
//   icc?: { icc: string[]; operation: TFilterOperator }
//   searchWithCategory?: {
//     search: string
//     category: string | 'ALL'
//     operation: TFilterOperator
//   }[]
//   searchWithCategoryOperation?: 'OR' | 'AND'
//   isIIIF?: boolean
//   iccStates: string[]
//   themeStates: string[]
//   exportRecords: TIllustrationList[]
// }

// eslint-disable-next-line import/prefer-default-export
export const useExportRecordMutation = () => {
  const filters: {
    field?: string
    operation: string
    value?: string | number
    filters?: TFilter[]
  }[] = []

  const records: {
    field?: string
    operation: string
    value?: string | number
    filters?: TFilter[]
  }[] = []

  const {
    year,
    sort,
    type: recordType,
    filterAuthor,
    filterObject,
    filterPublishingPlace,
    filterThemeStates,
    filterICCStates,
    searchWithCategory,
    IIIFFormat,
    exportRecords,
  } = useExportStore()

  if (
    searchWithCategory?.length &&
    searchWithCategory.some((s) => s.search.length)
  ) {
    filters.push({
      operation: 'AND',
      filters: [
        ...searchWithCategory
          .filter((s) => s.search.length)
          .map((s) =>
            s.category.value === 'ALL'
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
                  field: s.category.value,
                  operation: s.category.operation,
                  value:
                    s.category.value === 'yearFrom'
                      ? Number(s.search.trim())
                      : s.search.trim(),
                }
          ),
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
        ...filterObject.map((o) => ({
          field: 'genres.id',
          operation: 'EQ',
          value: o.value,
        })),
      ],
    })
  }

  if (filterPublishingPlace.length) {
    filters.push({
      operation: 'OR',
      filters: [
        ...filterPublishingPlace.map((o) => ({
          field: 'publishingPlaces.id',
          operation: 'EQ',
          value: o.value,
        })),
      ],
    })
  }

  // if (subjectPlaces?.subjectPlaces.length) {
  //   filters.push({
  //     operation: subjectPlaces.operation,
  //     filters: [
  //       ...subjectPlaces.subjectPlaces.map((o) => ({
  //         field: 'subjectPlaces.id',
  //         operation: 'EQ',
  //         value: o,
  //       })),
  //     ],
  //   })
  // }

  // if (themes?.themes.length && themes.themes.some((t) => t.length)) {
  //   filters.push({
  //     operation: themes.operation,
  //     filters: [
  //       ...themes.themes.map((o) => ({
  //         field: 'themes.name',
  //         operation: 'EQ',
  //         value: o,
  //       })),
  //     ],
  //   })
  // }

  // if (icc.length) {
  //   filters.push({
  //     operation: icc.operation,
  //     filters: [
  //       ...icc.icc.map((i) => ({
  //         field: 'iconclass.code',
  //         operation: 'START_WITH',
  //         value: i,
  //       })),
  //     ],
  //   })
  // }

  if (IIIFFormat) {
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

  if (filterThemeStates.length) {
    filters.push({
      operation: 'OR',
      filters: [
        ...filterThemeStates.map((s) => ({
          field: 'themeState.id',
          operation: 'EQ',
          value: s.value,
        })),
      ],
    })
  }

  if (filterICCStates.length) {
    filters.push({
      operation: 'OR',
      filters: [
        ...filterICCStates.map((s) => ({
          field: 'iconclassState.id',
          operation: 'EQ',
          value: s.value,
        })),
      ],
    })
  }

  if (exportRecords.length) {
    records.push({
      operation: 'OR',
      filters: [
        ...exportRecords.map((s) => ({
          field: 'id',
          operation: 'EQ',
          value: s.id,
        })),
      ],
    })
  }

  const sortType = sort ? sort.value.split('_') : 'title_ASC'.split('_')

  return useMutation({
    mutationFn: (type: 'filtered' | 'selected') =>
      api()
        .post(`record/export`, {
          json: {
            filters:
              type === 'filtered'
                ? [
                    ...filters,
                    {
                      field: 'type',
                      operation: 'EQ',
                      value: recordType.toLowerCase(),
                    },
                  ]
                : records,
            sort: [
              {
                order: sortType[1],
                type: 'FIELD',
                field: sortType[0],
              },
            ],
          },
        })
        .blob(),
  })
}
