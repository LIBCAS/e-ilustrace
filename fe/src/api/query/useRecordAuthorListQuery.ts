import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TAuthorList } from '../../../../fe-shared/@types/author'
import { RecordType } from '../../@types/types'

const useRecordAuthorListQuery = (type: RecordType) =>
  useQuery({
    queryKey: ['record-author-list', type],
    queryFn: () =>
      api()
        .post('record-author/list-authors', {
          json: {
            size: 10000,
            filters: [
              type === 'BOOK'
                ? {
                    field: 'fromBook',
                    operation: 'EQ',
                    value: true,
                  }
                : {
                    field: 'fromIllustration',
                    operation: 'EQ',
                    value: true,
                  },
              {
                operation: 'OR',
                filters: [
                  {
                    field: 'roles',
                    operation: 'EQ',
                    value: 'AUTHOR',
                  },
                  {
                    field: 'roles',
                    operation: 'EQ',
                    value: 'PRINTER',
                  },
                  {
                    field: 'roles',
                    operation: 'EQ',
                    value: 'PUBLISHER',
                  },
                  {
                    field: 'roles',
                    operation: 'EQ',
                    value: 'ILLUSTRATOR',
                  },
                  {
                    field: 'roles',
                    operation: 'EQ',
                    value: 'WOODCARVER',
                  },
                ],
              },
            ],
            sort: [
              {
                order: 'ASC',
                type: 'FIELD',
                field: 'author.fullName',
              },
            ],
          },
        })
        .json<TAuthorList[]>(),
  })

export default useRecordAuthorListQuery
