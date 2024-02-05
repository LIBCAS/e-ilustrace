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
                  {i.printEntry.placesOfPublication.join(' ')}{' '}
                  {i.printEntry.originators.join(' ')} {i.printEntry.date}
                </span>
              </div>
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
                  {i.printEntry.placesOfPublication.join(' ')}{' '}
                  {i.printEntry.originators.join(' ')} {i.printEntry.date}
                </Link>
              </div>
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
