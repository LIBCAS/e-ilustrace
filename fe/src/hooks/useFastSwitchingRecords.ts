/* eslint-disable @typescript-eslint/ban-ts-comment */
import { useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { useSearchStore } from '../store/useSearchStore'
import useRecordListQuery from '../api/query/useRecordListQuery'
import { TBookList } from '../../../fe-shared/@types/book'
import { TIllustrationList } from '../../../fe-shared/@types/illustration'
import constructRecordDetailUrl from '../utils/constructRecordDetailUrl'

const useFastSwitchingRecords = () => {
  const params = useParams()
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const {
    sort,
    itemsPerPage,
    type,
    year,
    currentPage,
    filterObject,
    filterAuthor,
    filterPublishingPlace,
    setCurrentPage,
    searchWithCategory,
  } = useSearchStore()

  const {
    data: previousRecords,
    isFetching: previousRecordsLoading,
    isError: previousRecordsError,
  } = useRecordListQuery({
    type,
    size: Number(itemsPerPage.value),
    year,
    sort: sort?.value || 'title_ASC',
    page: currentPage - 1,
    authors: { authors: filterAuthor.map((a) => a.value), operation: 'OR' },
    objects: { objects: filterObject.map((o) => o.value), operation: 'OR' },
    publishingPlaces: {
      publishingPlaces: filterPublishingPlace.map((p) => p.value),
      operation: 'OR',
    },
    searchWithCategory: searchWithCategory.map((s) => ({
      search: s.search,
      category: s.category.value,
      operation: s.category.operation,
    })),
    enabled: currentPage > 0 && !!params.switch,
  })

  const {
    data: records,
    isFetching: recordsLoading,
    isError: recordsError,
  } = useRecordListQuery({
    type,
    size: Number(itemsPerPage.value),
    year,
    sort: sort?.value || 'title_ASC',
    page: currentPage,
    authors: { authors: filterAuthor.map((a) => a.value), operation: 'OR' },
    objects: { objects: filterObject.map((o) => o.value), operation: 'OR' },
    publishingPlaces: {
      publishingPlaces: filterPublishingPlace.map((p) => p.value),
      operation: 'OR',
    },
    searchWithCategory: searchWithCategory.map((s) => ({
      search: s.search,
      category: s.category.value,
      operation: s.category.operation,
    })),
    enabled: !!params.switch,
  })

  const {
    data: nextRecords,
    isFetching: nextRecordsLoading,
    isError: nextRecordsError,
  } = useRecordListQuery({
    type,
    size: Number(itemsPerPage.value),
    year,
    sort: sort?.value || 'title_ASC',
    page: currentPage + 1,
    authors: { authors: filterAuthor.map((a) => a.value), operation: 'OR' },
    objects: { objects: filterObject.map((o) => o.value), operation: 'OR' },
    publishingPlaces: {
      publishingPlaces: filterPublishingPlace.map((p) => p.value),
      operation: 'OR',
    },
    searchWithCategory: searchWithCategory.map((s) => ({
      search: s.search,
      category: s.category.value,
      operation: s.category.operation,
    })),
    enabled: !!params.switch,
  })

  const recordsArray: TBookList[] | TIllustrationList[] = []

  if (previousRecords?.items) {
    // @ts-ignore
    recordsArray.push(...previousRecords.items)
  }
  if (records?.items) {
    // @ts-ignore
    recordsArray.push(...records.items)
  }
  if (nextRecords?.items) {
    // @ts-ignore
    recordsArray.push(...nextRecords.items)
  }

  let position: 'first' | 'middle' | 'last' | boolean

  const currentRecordIndex = recordsArray.findIndex((r) => r.id === params.id)
  if (!!params.switch && currentRecordIndex >= 0) {
    if (currentRecordIndex === 0) {
      position = 'first'
    } else if (currentRecordIndex === recordsArray.length - 1) {
      position = 'last'
    } else {
      position = 'middle'
    }
  } else {
    position = false
  }

  const moveUp = () => {
    if (
      records &&
      nextRecords &&
      Number(itemsPerPage.value) * (currentPage + 1) < records.count &&
      currentRecordIndex === recordsArray.length - 1 - nextRecords.items.length
    ) {
      setCurrentPage(currentPage + 1)
    }
    navigate(
      constructRecordDetailUrl(
        `${recordsArray[currentRecordIndex + 1].id}/switch`,
        searchParams.has('back')
          ? searchParams.get('back')?.toString()
          : undefined
      )
    )
  }

  const moveDown = () => {
    if (
      previousRecords &&
      currentRecordIndex === previousRecords.items.length &&
      currentPage > 0
    ) {
      setCurrentPage(currentPage - 1)
    }
    navigate(
      constructRecordDetailUrl(
        `${recordsArray[currentRecordIndex - 1].id}/switch`,
        searchParams.has('back')
          ? searchParams.get('back')?.toString()
          : undefined
      )
    )
  }

  return {
    records: recordsArray,
    recordsLoading:
      previousRecordsLoading || recordsLoading || nextRecordsLoading,
    recordsError: previousRecordsError || recordsError || nextRecordsError,
    moveUp,
    moveDown,
    position,
  }
}

export default useFastSwitchingRecords
