import { useQuery } from '@tanstack/react-query'
import { api } from '../index'
import { RecordType } from '../../@types/types'
import { TRecordYears } from '../../../../fe-shared/@types/common'

const useSearchYearsRangeQuery = (type: RecordType) =>
  useQuery({
    queryKey: ['search-years-range', type],
    queryFn: () =>
      api().post(`record/years-range`, { json: { type } }).json<TRecordYears>(),
  })

export default useSearchYearsRangeQuery
