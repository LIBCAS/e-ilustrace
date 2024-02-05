import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TSubjectEntryList } from '../../../../fe-shared/@types/subject'
import { RecordType } from '../../@types/types'

interface Objects {
  items: TSubjectEntryList[]
  count: number
  searchAfter: string[]
}

const useSubjectEntryListQuery = (type: RecordType) =>
  useQuery({
    queryKey: ['subject-entry-list', type],
    queryFn: () =>
      api()
        .post('subject/entry/list', {
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
                field: 'label',
              },
            ],
          },
        })
        .json<Objects>(),
  })

export default useSubjectEntryListQuery
