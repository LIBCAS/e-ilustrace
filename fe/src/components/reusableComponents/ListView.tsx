import { FC } from 'react'

import { Link } from 'react-router-dom'
import { PhotoIcon } from '@heroicons/react/24/outline'
import { useTranslation } from 'react-i18next'
import Loader from './Loader'
import BookMark from '../../assets/icons/bookmark.svg?react'
import { TBookList } from '../../../../fe-shared/@types/book'
import { TIllustrationList } from '../../../../fe-shared/@types/illustration'
import Paginator from './Paginator'
import ShowError from './ShowError'
import useMySelectionQuery from '../../api/query/useMySelectionQuery'
import useAddToMySelectionMutationWrapper from '../../hooks/useAddToMySelectionMutationWrapper'
import useRemoveFromMySelectionMutationWrapper from '../../hooks/useRemoveFromMySelectionMutationWrapper'
import useMeQueryWrapper from '../../hooks/useMeQueryWrapper'
import constructRecordDetailUrl from '../../utils/constructRecordDetailUrl'
import { TSelectionItemDetail } from '../../../../fe-shared/@types/selection'

type Props = {
  allowFastSwitch?: boolean
  clickType?: 'normal' | 'vise'
  error?: boolean
  loading?: boolean
  currentPage: number
  illustrations: TBookList[] | TIllustrationList[]
  illustrationsPerPage: number
  totalIllustrations: number
  paginate: (pageNumber: number) => void
  backPath: string
}

const BlankImage = ({ classNames }: { classNames: string }) => {
  return (
    <div className="">
      <PhotoIcon className={`text-lightgray ${classNames}`} />
    </div>
  )
}

const ListView: FC<Props> = ({
  allowFastSwitch = false,
  clickType = 'normal',
  error = false,
  loading = false,
  currentPage,
  illustrationsPerPage,
  illustrations,
  totalIllustrations,
  paginate,
  backPath,
}) => {
  const { t } = useTranslation('search')
  const { me } = useMeQueryWrapper()
  const { data: selection } = useMySelectionQuery(!!me)
  const { doAdd } = useAddToMySelectionMutationWrapper()
  const { doRemove } = useRemoveFromMySelectionMutationWrapper()

  const handleAddition = (record: TBookList | TIllustrationList) => {
    if (record.type === 'BOOK') {
      doAdd({ illustrations: [], books: [record.id] })
    } else {
      doAdd({ illustrations: [record.id], books: [] })
    }
  }

  const handleDeletion = (record: TSelectionItemDetail) => {
    doRemove({ items: [record.id] })
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
        {totalIllustrations > 0 ? (
          <div className="mx-auto mb-4 flex w-fit flex-col items-center gap-y-2 md:flex-row">
            <Paginator
              itemsPerPage={illustrationsPerPage}
              contentLength={totalIllustrations}
              currentPage={currentPage}
              onChange={paginate}
            />
            <span className="ml-5 text-gray">
              {t('records_count')}
              {totalIllustrations}
            </span>
          </div>
        ) : null}
        {illustrations.map((i) => (
          <div
            className="relative flex w-full items-center justify-start border-b border-superlightgray py-5 md:px-2"
            key={i.id}
          >
            {clickType === 'normal' ? (
              <Link
                to={
                  allowFastSwitch
                    ? constructRecordDetailUrl(`${i.id}/switch`, backPath)
                    : constructRecordDetailUrl(i.id, backPath)
                }
                className="flex"
              >
                {i.type === 'ILLUSTRATION' && i.illustrationScan ? (
                  <div className="mr-4 w-[60px] shrink-0 md:mr-10 md:w-[90px] lg:w-[120px]">
                    <img
                      src={`/api/eil/files/${i.illustrationScan.id}`}
                      alt={i.title}
                    />
                  </div>
                ) : null}
                {i.type === 'ILLUSTRATION' &&
                i.pageScan &&
                !i.illustrationScan ? (
                  <div className="mr-4 w-[60px] shrink-0 md:mr-10 md:w-[90px] lg:w-[120px]">
                    <img
                      src={`/api/eil/files/${i.pageScan.id}`}
                      alt={i.title}
                    />
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

                <div className="flex w-[65%] flex-col items-start leading-7 md:w-[70%]">
                  <span className="text-left text-red max-md:text-sm">
                    {i.identifier}
                  </span>
                  <span className="line-clamp-2 text-left font-bold max-md:text-sm">
                    {i.title.trim().endsWith('/')
                      ? i.title.trim().slice(0, -1)
                      : i.title.trim()}
                  </span>
                  <span className="text-md text-gray">
                    {i.mainAuthor ? i.mainAuthor.author.fullName : null}
                  </span>
                  {'publishingEntry' in i &&
                    i.publishingEntry?.placesOfPublication && (
                      <span className="text-sm text-gray">
                        {i.publishingEntry.placesOfPublication.join(' ')}{' '}
                        {'publishingEntry' in i &&
                          i.publishingEntry?.originators &&
                          i.publishingEntry.originators.join(' ')}{' '}
                        {i.yearFrom}
                        {i.yearTo ? `-${i.yearTo}` : null}
                      </span>
                    )}
                  {'printEntry' in i && i.printEntry?.placesOfPublication && (
                    <span className="text-sm text-gray ">
                      {i.printEntry.placesOfPublication.join(' ')}{' '}
                      {i.printEntry.originators.join(' ')} {i.printEntry.date}
                    </span>
                  )}
                  {'illustrations' in i && (
                    <span className="mt-4 text-sm text-gray">
                      {t('number_of_illustrations_in_publication')}:{' '}
                      {i.illustrations.length}
                    </span>
                  )}
                </div>
              </Link>
            ) : (
              <div className="flex">
                <a
                  href={
                    'viseFileId' in i
                      ? `/vise/Illustrations/file?file_id=${i.viseFileId}`
                      : `/vise/Illustrations/filelist/`
                  }
                  target="_blank"
                  rel="noreferrer"
                >
                  {i.type === 'ILLUSTRATION' && i.illustrationScan ? (
                    <div className="mr-4 w-[60px] shrink-0 md:mr-10 md:w-[90px] lg:w-[120px]">
                      <img
                        src={`/api/eil/files/${i.illustrationScan.id}`}
                        alt={i.title}
                      />
                    </div>
                  ) : null}
                  {i.type === 'ILLUSTRATION' &&
                  i.pageScan &&
                  !i.illustrationScan ? (
                    <div className="mr-4 w-[60px] shrink-0 md:mr-10 md:w-[90px] lg:w-[120px]">
                      <img
                        src={`/api/eil/files/${i.pageScan.id}`}
                        alt={i.title}
                      />
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
                </a>
                <Link
                  to={constructRecordDetailUrl(i.id, backPath)}
                  className="flex w-[65%] flex-col items-start leading-7 md:w-[70%]"
                >
                  <span className="text-left text-red max-md:text-sm">
                    {i.identifier}
                  </span>
                  <span className="line-clamp-2 text-left font-bold max-md:text-sm">
                    {i.title.trim().endsWith('/')
                      ? i.title.trim().slice(0, -1)
                      : i.title.trim()}
                  </span>
                  <span className="text-md text-gray">
                    {i.mainAuthor ? i.mainAuthor.author.fullName : null}
                  </span>
                  {'publishingEntry' in i &&
                    i.publishingEntry?.placesOfPublication && (
                      <span className="text-sm text-gray">
                        {i.publishingEntry.placesOfPublication.join(' ')}{' '}
                        {'publishingEntry' in i &&
                          i.publishingEntry?.originators &&
                          i.publishingEntry.originators.join(' ')}{' '}
                        {i.yearFrom}
                        {i.yearTo ? `-${i.yearTo}` : null}
                      </span>
                    )}
                  {'printEntry' in i && i.printEntry?.placesOfPublication && (
                    <span className="text-sm text-gray ">
                      {i.printEntry.placesOfPublication.join(' ')}{' '}
                      {i.printEntry.originators.join(' ')} {i.printEntry.date}
                    </span>
                  )}
                  {'illustrations' in i && (
                    <span className="mt-4 text-xs text-gray">
                      {t('number_of_illustrations_in_publication')} -{' '}
                      {i.illustrations.length}
                    </span>
                  )}
                </Link>
              </div>
            )}

            <button
              aria-label="Bookmark"
              disabled={!me}
              type="button"
              className={`absolute right-0 top-5 z-10 sm:right-5 ${
                me ? 'hover:text-red' : ''
              } ${
                selection?.items?.find(
                  (item) =>
                    item.book?.id === i.id || item.illustration?.id === i.id
                )
                  ? 'text-red'
                  : 'text-lightgray'
              }`}
              onClick={() => {
                const item = selection?.items?.find(
                  (it) => it.book?.id === i.id || it.illustration?.id === i.id
                )
                if (item) {
                  handleDeletion(item)
                } else {
                  handleAddition(i)
                }
              }}
            >
              <BookMark className="text-inherit transition-all duration-300" />
            </button>
          </div>
        ))}
        <div className="mx-auto mt-4 flex w-fit flex-col items-center gap-y-2 md:flex-row">
          <Paginator
            itemsPerPage={illustrationsPerPage}
            contentLength={totalIllustrations}
            currentPage={currentPage}
            onChange={paginate}
          />
          <span className="ml-5 text-gray">
            {t('records_count')}
            {totalIllustrations}
          </span>
        </div>
      </div>
    </div>
  )
}

export default ListView
