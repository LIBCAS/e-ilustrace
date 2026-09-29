import { FC } from 'react'

import { Link } from 'react-router'
import { PhotoIcon } from '@heroicons/react/24/outline'
import { useTranslation } from 'react-i18next'
import cloneDeep from 'lodash/cloneDeep'
import Loader from './Loader'
import { TIllustrationList } from '../../../../fe-shared/@types/illustration'
import Paginator from './Paginator'
import ShowError from './ShowError'
import { useExportStore } from '../../store/useExportStore'
import BookMark from '../../assets/icons/bookmark.svg?react'
import { TBookList } from '../../../../fe-shared/@types/book'

type Props = {
  error?: boolean
  loading?: boolean
  currentPage: number
  illustrations: TIllustrationList[] | TBookList[]
  illustrationsPerPage: number
  totalIllustrations: number
  paginate: (pageNumber: number) => void
  showExportBadges?: boolean
}

const BlankImage = ({ classNames }: { classNames: string }) => {
  return (
    <div>
      <PhotoIcon className={`text-lightgray ${classNames}`} />
    </div>
  )
}

const ListView: FC<Props> = ({
  error = false,
  loading = false,
  currentPage,
  illustrationsPerPage,
  illustrations,
  totalIllustrations,
  paginate,
  showExportBadges = false,
}) => {
  const { t } = useTranslation()

  const exportRecords = useExportStore((state) => state.exportRecords)
  const setExportRecords = useExportStore((state) => state.setExportRecords)

  const handleDeletion = (record: TIllustrationList | TBookList) => {
    let exportRecordsClone = cloneDeep(exportRecords)
    exportRecordsClone = exportRecordsClone.filter((r) => r.id !== record.id)
    setExportRecords(exportRecordsClone)
  }

  const handleAddition = (record: TIllustrationList | TBookList) => {
    const exportRecordsClone = cloneDeep(exportRecords)
    exportRecordsClone.push(record)
    setExportRecords(exportRecordsClone)
  }

  if (loading) {
    return (
      <div className="flex">
        <Loader className="mx-auto self-center" />
      </div>
    )
  }

  if (error) {
    return <ShowError />
  }

  return (
    <div className="flex w-full flex-col">
      <div className="flex flex-col items-start justify-start">
        {illustrations.map((i) => (
          <Link
            className="relative flex w-full cursor-pointer items-center justify-start border-b border-superlightgray py-5 md:px-2"
            key={i.id}
            to={`../${t('urls.enrichment')}/${i.id}`}
          >
            {i.type === 'ILLUSTRATION' && i.illustrationScan ? (
              <div className="mr-4 w-[60px] shrink-0 md:mr-10 md:w-[90px] lg:w-[120px]">
                <img
                  src={`/api/eil/files/${i.illustrationScan.id}`}
                  alt={i.title}
                />
              </div>
            ) : null}
            {i.type === 'ILLUSTRATION' && i.pageScan && !i.illustrationScan ? (
              <div className="mr-4 w-[60px] shrink-0 md:mr-10 md:w-[90px] lg:w-[120px]">
                <img src={`/api/eil/files/${i.pageScan.id}`} alt={i.title} />
              </div>
            ) : null}
            {i.type === 'BOOK' && i.frontPageScan ? (
              <div className="mr-4 w-[60px] shrink-0 md:mr-10 md:w-[90px] lg:w-[120px]">
                <img
                  src={`/api/eil/files/${i.frontPageScan.id}`}
                  alt={i.title}
                />
              </div>
            ) : null}
            {(i.type === 'BOOK' && !i.frontPageScan) ||
            (i.type === 'ILLUSTRATION' &&
              !i.illustrationScan &&
              !i.pageScan) ? (
              <BlankImage classNames="shrink-0 w-[60px] md:w-[90px] lg:w-[120px] mr-4 md:mr-10" />
            ) : null}

            <div className="flex flex-col items-start transition-all duration-300">
              <span className="line-clamp-2 text-left font-bold">
                {i.title.trim().endsWith('/')
                  ? i.title.trim().slice(0, -1)
                  : i.title.trim()}
              </span>
              <span className="text-sm text-gray">
                identifikátor: {i.identifier}
              </span>
              <span className="text-sm text-gray">uuid: {i.id}</span>
            </div>
            {showExportBadges ? (
              <button
                aria-label="Bookmark"
                type="button"
                className={`absolute right-0 top-5 z-10 hover:text-red sm:right-5 ${
                  exportRecords.find((item) => item.id === i.id)
                    ? 'text-red'
                    : 'text-lightgray'
                }`}
                onClick={(event) => {
                  event.preventDefault()
                  const item = exportRecords.find((it) => it.id === i.id)
                  if (item) {
                    handleDeletion(item)
                  } else {
                    handleAddition(i)
                  }
                }}
              >
                <BookMark className="text-inherit transition-all duration-300" />
              </button>
            ) : null}
          </Link>
        ))}
        <div className="mx-auto mt-4 flex w-fit items-center">
          <Paginator
            itemsPerPage={illustrationsPerPage}
            contentLength={totalIllustrations}
            currentPage={currentPage}
            onChange={paginate}
          />
          <span className="ml-5 text-gray">
            {t('search.records_count')}
            {totalIllustrations}
          </span>
        </div>
      </div>
    </div>
  )
}

export default ListView
