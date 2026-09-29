import { FC, useRef, useState } from 'react'
import { Link } from 'react-router'
import { Trans, useTranslation } from 'react-i18next'
import { PhotoIcon } from '@heroicons/react/24/outline'
import dayjs from 'dayjs'
import PlusIcon from '../assets/icons/plus.svg?react'
import useMeQueryWrapper from '../hooks/useMeQueryWrapper'
import Loader from '../components/reusableComponents/Loader'
import ShowError from '../components/reusableComponents/ShowError'
import {
  useMineExhibitionListQuery,
  useExhibitionListQuery,
} from '../api/exhibition'
import Paginator from '../components/reusableComponents/Paginator'
import { useSidebarStore } from '../store/useSidebarStore'
import Button from '../components/reusableComponents/Button'
import { TExhibitionList } from '../../../fe-shared/@types/exhibition'
import { TMe } from '../../../fe-shared/@types/user'

const BlankImage = ({ classNames }: { classNames: string }) => {
  return <PhotoIcon className={`text-lightgray ${classNames}`} />
}

type TPublicExhibitionsSectionProps = {
  meId?: string
  publicExhibitionsLoading: boolean
  publicExhibitionsError: boolean
  publicExhibitions:
    | {
        items: TExhibitionList[]
        count: number
      }
    | undefined
  itemsPerPage: number
  page: number
  paginate: (pageNumber: number) => void
  setLoginPhase: (phase: 'LOGIN') => void
  setSidebarOpen: (open: boolean) => void
}

const PublicExhibitionsSection = ({
  meId = undefined,
  publicExhibitionsLoading,
  publicExhibitionsError,
  publicExhibitions,
  itemsPerPage,
  page,
  paginate,
  setLoginPhase,
  setSidebarOpen,
}: TPublicExhibitionsSectionProps) => {
  const { t, i18n } = useTranslation('exhibitions')
  const viewRef = useRef<HTMLDivElement>(null)

  const handlePaginate = (pageNumber: number) => {
    paginate(pageNumber)
    if (viewRef.current) {
      viewRef.current.scrollTo(0, 0)
    }
  }

  return (
    <>
      <div className="mx-auto mt-4 flex max-w-7xl flex-wrap px-8">
        <h2 className="mb-6 text-center text-2xl font-bold">
          {t('public_exhibitions')}
        </h2>
        <span className="mb-10">
          {meId ? (
            <Trans
              i18nKey="exhibitions:help_logged_in"
              components={{
                button2: (
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
          ) : (
            <Trans
              i18nKey="exhibitions:help_public"
              components={{
                button1: (
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
                button2: (
                  <a
                    href={
                      i18n.resolvedLanguage === 'cs'
                        ? 'https://e-ilustrace.cz/napoveda/#vystavy'
                        : 'https://e-ilustrace.cz/en/help/'
                    }
                    className="text-red hover:brightness-110"
                    target="_blank"
                    rel="noreferrer"
                  />
                ),
              }}
            />
          )}
        </span>
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
                  {/* <span className="block text-sm text-gray"> */}
                  {/*  {t('created')} */}
                  {/*  {dayjs(e.created).format('DD. MM. YYYY')} */}
                  {/* </span> */}
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
          onChange={handlePaginate}
        />
        <span className="ml-5 text-gray">
          {t('search:records_count')}
          {publicExhibitions?.count || 0}
        </span>
      </div>
    </>
  )
}

type TMyExhibitionsSectionProps = {
  me?: TMe | null
  meLoading: boolean
  meError: boolean
  mineExhibitionsLoading: boolean
  mineExhibitionsError: boolean
  mineExhibitions:
    | {
        items: TExhibitionList[]
        count: number
      }
    | undefined
  setLoginPhase: (phase: 'LOGIN') => void
  setSidebarOpen: (open: boolean) => void
}

const MyExhibitionsSection = ({
  me = null,
  meLoading,
  meError,
  mineExhibitionsLoading,
  mineExhibitionsError,
  mineExhibitions,
  setLoginPhase,
  setSidebarOpen,
}: TMyExhibitionsSectionProps) => {
  const { t } = useTranslation('exhibitions')

  return (
    <div className="mx-auto mb-8 mt-4 flex max-w-7xl flex-wrap px-8">
      <h2 className="text-center text-2xl font-bold">{t('my_exhibitions')}</h2>
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
          <div className="mb-10 mt-16 flex flex-col items-center gap-4">
            <Button
              variant="submit"
              onClick={() => {
                setLoginPhase('LOGIN')
                setSidebarOpen(true)
              }}
            >
              {t('add_edit_exhibition')}
            </Button>
            <span>{t('login_required')}</span>
          </div>
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
  )
}

const Exhibitions: FC = () => {
  const { t } = useTranslation('exhibitions')
  const { me, meLoading, meError } = useMeQueryWrapper()
  const { setSidebarOpen, setLoginPhase } = useSidebarStore()
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

  const paginate = (pageNumber: number) => setPage(pageNumber)

  if (meLoading) {
    return (
      <section>
        <div className="border-[1.5px] border-superlightgray py-10">
          <h1 className="text-center text-4xl font-bold">{t('exhibitions')}</h1>
        </div>
        <div className="mx-auto mt-4 flex max-w-7xl flex-wrap px-8">
          <div className="my-16 flex w-full items-center justify-center">
            <Loader />
          </div>
        </div>
      </section>
    )
  }

  return (
    <section>
      <div className="border-[1.5px] border-superlightgray py-10">
        <h1 className="text-center text-4xl font-bold">{t('exhibitions')}</h1>
      </div>
      {me ? (
        <>
          <MyExhibitionsSection
            me={me}
            meLoading={meLoading}
            meError={meError}
            mineExhibitionsLoading={mineExhibitionsLoading}
            mineExhibitionsError={mineExhibitionsError}
            mineExhibitions={mineExhibitions}
            setLoginPhase={setLoginPhase}
            setSidebarOpen={setSidebarOpen}
          />
          <PublicExhibitionsSection
            meId={me.id}
            publicExhibitionsLoading={publicExhibitionsLoading}
            publicExhibitionsError={publicExhibitionsError}
            publicExhibitions={publicExhibitions}
            itemsPerPage={itemsPerPage}
            page={page}
            paginate={paginate}
            setLoginPhase={setLoginPhase}
            setSidebarOpen={setSidebarOpen}
          />
        </>
      ) : (
        <>
          <PublicExhibitionsSection
            publicExhibitionsLoading={publicExhibitionsLoading}
            publicExhibitionsError={publicExhibitionsError}
            publicExhibitions={publicExhibitions}
            itemsPerPage={itemsPerPage}
            page={page}
            paginate={paginate}
            setLoginPhase={setLoginPhase}
            setSidebarOpen={setSidebarOpen}
          />
          <MyExhibitionsSection
            me={me}
            meLoading={meLoading}
            meError={meError}
            mineExhibitionsLoading={mineExhibitionsLoading}
            mineExhibitionsError={mineExhibitionsError}
            mineExhibitions={mineExhibitions}
            setLoginPhase={setLoginPhase}
            setSidebarOpen={setSidebarOpen}
          />
        </>
      )}
    </section>
  )
}

export default Exhibitions
