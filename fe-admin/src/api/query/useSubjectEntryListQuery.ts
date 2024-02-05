import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { TSubjectEntryList } from '../../../../fe-shared/@types/subject'

interface Objects {
  items: TSubjectEntryList[]
  count: number
  searchAfter: string[]
}

const useSubjectEntryListQuery = () =>
  useQuery({
    queryKey: ['subject-entry-list'],
    queryFn: () =>
      api()
        .post('subject/entry/list', {
          json: {
            size: 10000,
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
