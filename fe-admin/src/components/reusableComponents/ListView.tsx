import { FC } from 'react'

import { Link } from 'react-router-dom'
import { PhotoIcon } from '@heroicons/react/24/outline'
import { useTranslation } from 'react-i18next'
import Loader from './Loader'
import { TIllustrationList } from '../../../../fe-shared/@types/illustration'
import Paginator from './Paginator'
import ShowError from './ShowError'

type Props = {
  error?: boolean
  loading?: boolean
  currentPage: number
  illustrations: TIllustrationList[]
  illustrationsPerPage: number
  totalIllustrations: number
  paginate: (pageNumber: number) => void
}

const BlankImage = ({ classNames }: { classNames: string }) => {
  return (
    <div className="">
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
}) => {
  const { t } = useTranslation()

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
            {i.illustrationScan ? (
              <div className="mr-4 w-[60px] shrink-0 md:mr-10 md:w-[90px] lg:w-[120px]">
                <img
                  src={`/api/eil/files/${i.illustrationScan.id}`}
                  alt={i.title}
                />
              </div>
            ) : null}
            {i.pageScan && !i.illustrationScan ? (
              <div className="mr-4 w-[60px] shrink-0 md:mr-10 md:w-[90px] lg:w-[120px]">
                <img src={`/api/eil/files/${i.pageScan.id}`} alt={i.title} />
              </div>
            ) : null}
            {!i.illustrationScan && !i.pageScan ? (
              <BlankImage classNames="shrink-0 w-[60px] md:w-[90px] lg:w-[120px] mr-4 md:mr-10" />
            ) : null}

            <div className="flex flex-col items-start transition-all duration-300 ">
              <span className="line-clamp-2 text-left font-bold">
                {i.title}
              </span>
              <span className="text-sm text-gray ">
                identifikátor: {i.identifier}
              </span>
              <span className="text-sm text-gray ">uuid: {i.id}</span>
            </div>
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
