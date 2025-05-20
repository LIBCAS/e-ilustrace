import { FC } from 'react'

import { Link } from 'react-router-dom'
import clsx from 'clsx'
import { PhotoIcon } from '@heroicons/react/24/outline'
import { useTranslation } from 'react-i18next'
import Loader from './Loader'
import { TIllustrationList } from '../../../../fe-shared/@types/illustration'
import Paginator from './Paginator'
import ShowError from './ShowError'
import constructRecordDetailUrl from '../../utils/constructRecordDetailUrl'
import BookMark from '../../assets/icons/bookmark.svg?react'
import useMeQueryWrapper from '../../hooks/useMeQueryWrapper'
import { useMySelectionQuery } from '../../api/my-selection'
import useAddToMySelectionMutationWrapper from '../../hooks/useAddToMySelectionMutationWrapper'
import useRemoveFromMySelectionMutationWrapper from '../../hooks/useRemoveFromMySelectionMutationWrapper'
import { TBookList } from '../../../../fe-shared/@types/book'
import { TSelectionItemDetail } from '../../../../fe-shared/@types/selection'

type Props = {
  clickType?: 'normal' | 'vise'
  allowFastSwitch?: boolean
  error?: boolean
  loading?: boolean
  currentPage: number
  illustrations: TIllustrationList[]
  illustrationsPerPage: number
  totalIllustrations: number
  paginate: (pageNumber: number) => void
  backPath: string
}

const BlankImage = () => {
  return <PhotoIcon className="h-[150px] w-[150px] text-lightgray" />
}

const TilesView: FC<Props> = ({
  clickType = 'normal',
  allowFastSwitch = false,
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
    <div className="mx-auto flex w-full flex-col items-center justify-center">
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
      <div className="mx-auto flex w-full flex-wrap justify-evenly">
        {illustrations.map((i) =>
          clickType === 'normal' ? (
            <Link
              to={
                allowFastSwitch
                  ? constructRecordDetailUrl(`${i.id}/switch`, backPath)
                  : constructRecordDetailUrl(i.id, backPath)
              }
              className="explore-item relative mb-8 flex w-full max-w-[250px] cursor-pointer flex-col items-center justify-start px-2 pb-2 pt-5 md:basis-1/5"
              key={i.id}
            >
              {i.illustrationScan ? (
                <img
                  className="max-h-[170px] justify-self-start transition-all duration-300"
                  src={`/api/eil/files/${i.illustrationScan.id}`}
                  alt={i.title}
                />
              ) : null}
              {i.pageScan && !i.illustrationScan ? (
                <img
                  className="max-h-[170px] justify-self-start transition-all duration-300"
                  src={`/api/eil/files/${i.pageScan.id}`}
                  alt={i.title}
                />
              ) : null}
              {!i.illustrationScan && !i.pageScan ? <BlankImage /> : null}

              <div
                className={clsx(
                  'explore-item-text flex flex-col items-center transition-all duration-300'
                )}
              >
                <span className={clsx('text-center text-sm')}>
                  {i.printEntry?.placesOfPublication.join(' ')}{' '}
                  {i.printEntry?.originators.join(' ')} {i.printEntry?.date}
                </span>
              </div>
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
                onClick={(event) => {
                  event.preventDefault()
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
            </Link>
          ) : (
            <div
              className="explore-item relative mb-8 flex w-full max-w-[250px] flex-col items-center justify-start px-2 pb-2 pt-5 md:basis-1/5"
              key={i.id}
            >
              <a
                href={
                  'viseFileId' in i && i.viseFileId
                    ? `/vise/Illustrations/file?file_id=${i.viseFileId}`
                    : `/vise/Illustrations/filelist`
                }
                target="_blank"
                rel="noreferrer"
              >
                {i.illustrationScan ? (
                  <img
                    className="max-h-[170px] justify-self-start transition-all duration-300"
                    src={`/api/eil/files/${i.illustrationScan.id}`}
                    alt={i.title}
                  />
                ) : null}
                {i.pageScan && !i.illustrationScan ? (
                  <img
                    className="max-h-[170px] justify-self-start transition-all duration-300"
                    src={`/api/eil/files/${i.pageScan.id}`}
                    alt={i.title}
                  />
                ) : null}
                {!i.illustrationScan && !i.pageScan ? <BlankImage /> : null}
              </a>
              <div
                className={clsx(
                  'explore-item-text flex flex-col items-center transition-all duration-300'
                )}
              >
                <Link
                  to={constructRecordDetailUrl(i.id, backPath)}
                  className={clsx('text-center')}
                >
                  {i.printEntry?.placesOfPublication.join(' ')}{' '}
                  {i.printEntry?.originators.join(' ')} {i.printEntry?.date}
                </Link>
              </div>
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
                onClick={(event) => {
                  event.preventDefault()
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
          )
        )}
      </div>
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
  )
}

export default TilesView
