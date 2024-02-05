import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { RecordType } from '../../@types/types'
import { TSubjectPersonList } from '../../../../fe-shared/@types/subject'

interface Persons {
  items: TSubjectPersonList[]
  count: number
  searchAfter: string[]
}

/**
 * Field 600
 */
const useSubjectPersonListQuery = (type: RecordType) =>
  useQuery({
    queryKey: ['subject-person-list', type],
    queryFn: () =>
      api()
        .post('subject/person/list', {
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
            ],
            sort: [
              {
                order: 'ASC',
                type: 'FIELD',
                field: 'fullName',
              },
            ],
          },
        })
        .json<Persons>(),
  })

export default useSubjectPersonListQuery
