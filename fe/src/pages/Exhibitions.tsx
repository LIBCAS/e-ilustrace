import { FC, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { Trans, useTranslation } from 'react-i18next'
import { PhotoIcon } from '@heroicons/react/24/outline'
import dayjs from 'dayjs'
import PlusIcon from '../assets/icons/plus.svg?react'
import useMeQueryWrapper from '../hooks/useMeQueryWrapper'
import Loader from '../components/reusableComponents/Loader'
import ShowError from '../components/reusableComponents/ShowError'
import useMineExhibitionListQuery from '../api/query/useMineExhibitionListQuery'
import ShowInfoMessage from '../components/reusableComponents/ShowInfoMessage'
import useExhibitionListQuery from '../api/query/useExhibitionListQuery'
import Paginator from '../components/reusableComponents/Paginator'
import { useSidebarStore } from '../store/useSidebarStore'

const BlankImage = ({ classNames }: { classNames: string }) => {
  return <PhotoIcon className={`text-lightgray ${classNames}`} />
}

const Exhibitions: FC = () => {
  const { t } = useTranslation('exhibitions')
  const { me, meLoading, meError } = useMeQueryWrapper()
  const { setSidebarOpen, setLoginPhase } = useSidebarStore()
  const viewRef = useRef<HTMLDivElement>(null)
  const [page, setPage] = useState(0)
  const itemsPerPage = 12
  const {
    data: mineExhibitions,
    isLoading: mineExhibitionsLoading,
    isError: mineExhibitionsError,
  } = useMineExhibitionListQuery(!!me)
  const {
    data: publicExhibitions,
    isLoading: publicExhibitionsLoading,
    isError: publicExhibitionsError,
  } = useExhibitionListQuery({ size: itemsPerPage, page })

  const paginate = (pageNumber: number) => {
    setPage(pageNumber)
    if (viewRef.current) {
      viewRef.current.scrollTo(0, 0)
    }
  }

  return (
    <section>
      <div className="border-[1.5px] border-superlightgray py-10">
        <h1 className="text-center text-4xl font-bold">{t('exhibitions')}</h1>
      </div>
      <div className="mx-auto mt-4 flex max-w-7xl flex-wrap px-8">
        <h2 className="text-center text-2xl font-bold">
          {t('my_exhibitions')}
        </h2>
        <div className="flex w-full flex-col">
          {meLoading ? (
            <div className="my-16 flex w-full items-center justify-center">
              <Loader />
            </div>
          ) : null}
          {meError && !meLoading ? (
            <div className="flex w-full items-center justify-center">
              <ShowError />
            </div>
          ) : null}
          {!me && !meLoading && !meError ? (
            <ShowInfoMessage
              message={
                <Trans
                  i18nKey="common:login_required"
                  components={{
                    button: (
                      <button
                        type="button"
                        className="text-red hover:brightness-110"
                        aria-hidden
                        onClick={() => {
                          setLoginPhase('LOGIN')
                          setSidebarOpen(true)
                        }}
                      />
                    ),
                  }}
                />
              }
            />
          ) : null}
          {me ? (
            <>
              {mineExhibitionsLoading ? (
                <div className="my-16 flex w-full items-center justify-center">
                  <Loader />
                </div>
              ) : null}
              {mineExhibitionsError && !mineExhibitionsLoading ? (
                <div className="flex w-full items-center justify-center">
                  <ShowError />
                </div>
              ) : null}
              {!mineExhibitionsLoading && !mineExhibitionsError ? (
                <>
                  <p className="my-5 text-gray">
                    {t('exhibitions_shown', { count: mineExhibitions?.count })}
                  </p>
                  <div className="flex flex-wrap justify-center gap-12">
                    <Link
                      to="add"
                      className="flex h-[300px] cursor-pointer items-center gap-4 rounded-xl border-2 border-lightgray px-10 py-14 font-bold"
                    >
                      <PlusIcon className="text-red" />
                      {t('add_exhibition')}
                    </Link>
                    {mineExhibitions?.items.map((e) => {
                      const image =
                        e.items.find((i) => i.illustration.illustrationScan?.id)
                          ?.illustration.illustrationScan?.id ||
                        e.items.find((i) => i.illustration.pageScan?.id)
                          ?.illustration.pageScan?.id

                      return (
                        <Link
                          to={e.id}
                          className="flex cursor-pointer flex-col items-center justify-start"
                          key={e.id}
                        >
                          {image ? (
                            <img
                              className="h-[300px] max-w-full rounded-xl"
                              src={`/api/eil/files/${image}`}
                              alt={e.name}
                            />
                          ) : (
                            <BlankImage classNames="h-[300px]" />
                          )}
                          <span className="text-center font-bold text-black">
                            {e.name}
                          </span>
                          <span className="block text-sm text-gray">
                            {t('created')}
                            {dayjs(e.created).format('DD. MM. YYYY')}
                          </span>
                        </Link>
                      )
                    })}
                  </div>
                </>
              ) : null}
            </>
          ) : null}
        </div>
      </div>
      <div className="mx-auto mt-4 flex max-w-7xl flex-wrap px-8">
        <h2 className="mb-6 text-center text-2xl font-bold">
          {t('public_exhibitions')}
        </h2>
        <div ref={viewRef} className="flex w-full flex-col">
          {publicExhibitionsLoading ? (
            <div className="my-16 flex w-full items-center justify-center">
              <Loader />
            </div>
          ) : null}
          {publicExhibitionsError && !publicExhibitionsLoading ? (
            <div className="flex w-full items-center justify-center">
              <ShowError />
            </div>
          ) : null}
          <div className="flex flex-wrap justify-center gap-12">
            {publicExhibitions?.items.map((e) => {
              let image

              const prefaceIll = e.items.find(
                (i) =>
                  i.preface &&
                  (i.illustration.illustrationScan?.id ||
                    i.illustration.pageScan?.id)
              )

              if (prefaceIll) {
                image =
                  prefaceIll.illustration.illustrationScan?.id ||
                  prefaceIll.illustration.pageScan?.id
              } else {
                image =
                  e.items.find((i) => i.illustration.illustrationScan?.id)
                    ?.illustration.illustrationScan?.id ||
                  e.items.find((i) => i.illustration.pageScan?.id)?.illustration
                    .pageScan?.id
              }

              return (
                <Link
                  to={e.id}
                  className="flex cursor-pointer flex-col items-center justify-start"
                  key={`public-${e.id}`}
                >
                  {image ? (
                    <img
                      className="h-[300px] max-w-full rounded-xl"
                      src={`/api/eil/files/${image}`}
                      alt={e.name}
                    />
                  ) : (
                    <BlankImage classNames="h-[300px]" />
                  )}
                  <span className="text-center font-bold text-black">
                    {e.name}
                  </span>
                  <span className="block text-sm text-gray">
                    {t('author')}
                    {e.user.fullName}
                  </span>
                  <span className="block text-sm text-gray">
                    {t('created')}
                    {dayjs(e.created).format('DD. MM. YYYY')}
                  </span>
                </Link>
              )
            })}
          </div>
        </div>
      </div>
      <div className="mx-auto mb-8 mt-8 flex w-fit flex-col items-center gap-y-2 md:flex-row">
        <Paginator
          itemsPerPage={itemsPerPage}
          contentLength={publicExhibitions?.count || 0}
          currentPage={page}
          onChange={paginate}
        />
        <span className="ml-5 text-gray">
          {t('search:records_count')}
          {publicExhibitions?.count || 0}
        </span>
      </div>
    </section>
  )
}

export default Exhibitions
