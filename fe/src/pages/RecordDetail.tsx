import React, { FC, Suspense, useState } from 'react'
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom'

import { useTranslation } from 'react-i18next'
import { sortBy } from 'lodash'
import LeftArrow from '../assets/icons/navigate_back.svg?react'
import BookMarkOutlined from '../assets/icons/bookmark_outlined.svg?react'
import BookMark from '../assets/icons/bookmark.svg?react'

import Button from '../components/reusableComponents/Button'
import useMobile from '../hooks/useMobile'
import { useRecordQuery } from '../api/record'
import Loader from '../components/reusableComponents/Loader'
import BookSection from '../components/recordDetail/BookSection'
import IllustrationSection from '../components/recordDetail/IllustrationSection'
import ShowError from '../components/reusableComponents/ShowError'
import useAddToMySelectionMutationWrapper from '../hooks/useAddToMySelectionMutationWrapper'
import useRemoveFromMySelectionMutationWrapper from '../hooks/useRemoveFromMySelectionMutationWrapper'
import { useMySelectionQuery } from '../api/my-selection'
import useMeQueryWrapper from '../hooks/useMeQueryWrapper'
import useFastSwitchingRecords from '../hooks/useFastSwitchingRecords'
import constructRecordDetailUrl from '../utils/constructRecordDetailUrl'
import { TIllustrationDetail } from '../../../fe-shared/@types/illustration'
import { TBookDetail } from '../../../fe-shared/@types/book'
import SelectionDialog from '../components/reusableComponents/SelectionDialog'
import SelectionDialogButton from '../components/reusableComponents/SelectionDialogButton'

const MiradorContainer = React.lazy(
  () => import('../components/reusableComponents/MiradorContainer')
)

const canUseMirador = (record: TIllustrationDetail | TBookDetail) => {
  return record.type === 'ILLUSTRATION'
    ? !!record.illustrationScan || !!record.pageScan
    : record.illustrations.some((i) => i.pageScan)
}

const RecordDetail: FC = () => {
  const navigate = useNavigate()
  const params = useParams()
  const [searchParams] = useSearchParams()
  const { t } = useTranslation('detail')
  const [showMore, setShowMore] = useState(false)
  const [showDialog, setShowDialog] = useState(false)
  const { isMobile } = useMobile()
  const {
    data: record,
    isLoading,
    isError,
  } = useRecordQuery({ id: params?.id ? params.id : '' })

  const { me } = useMeQueryWrapper()

  const { recordsLoading, recordsError, moveUp, moveDown, position } =
    useFastSwitchingRecords()

  const { data: selection } = useMySelectionQuery(!!me)
  const { doAdd } = useAddToMySelectionMutationWrapper()
  const { doRemove } = useRemoveFromMySelectionMutationWrapper()

  const handleAddition = () => {
    if (record) {
      if (record.type === 'BOOK') {
        doAdd({ illustrations: [], books: [record.id] })
      } else {
        doAdd({ illustrations: [record.id], books: [] })
      }
    }
  }

  const handleDeletion = () => {
    if (selection?.items && record?.id) {
      const item =
        selection.items.find((i) => i.book?.id === record.id) ||
        selection.items.find((i) => i.illustration?.id === record.id)
      doRemove({
        items: item ? [item.id] : [],
      })
    }
  }

  const isInMySelection =
    selection?.items
      ?.filter((i) => i.illustration?.id)
      ?.find((ill) => ill.id === record?.id) ||
    selection?.items
      ?.filter((i) => i.book?.id)
      ?.find((i) => i.book?.id === record?.id)

  return (
    <section>
      <div className="border-[1.5px] border-superlightgray py-4">
        <div className="lg:mr-8">
          <div className="mx-auto flex max-w-7xl flex-wrap items-center md:flex-nowrap">
            <div
              className={`w-full ${
                isMobile ? 'border-b-[1.5px] border-superlightgray pb-4' : ''
              } flex items-center`}
            >
              <LeftArrow
                className="cursor-pointer text-red"
                onClick={() => {
                  navigate(
                    searchParams.has('back')
                      ? `/${searchParams.get('back')}`
                      : '/search'
                  )
                }}
              />
              <h1 className="line-clamp-2 w-3/4 text-left text-3xl font-bold md:w-full">
                {record?.title.trim().endsWith('/')
                  ? record?.title.trim().slice(0, -1)
                  : record?.title.trim()}
              </h1>
            </div>
            <div className="flex w-full px-4 md:px-0 lg:w-1/4">
              <Button
                disabled={!me}
                className="mx-auto mt-4 flex-grow md:mx-0 md:ml-auto md:flex-grow-0"
                variant={isMobile ? 'secondary' : 'text'}
                startIcon={
                  isInMySelection ? (
                    <BookMark className="text-red" />
                  ) : (
                    <BookMarkOutlined />
                  )
                }
                onClick={() => {
                  if (isInMySelection) {
                    handleDeletion()
                  } else {
                    handleAddition()
                  }
                }}
              >
                {isInMySelection ? (
                  <span className="text-red">
                    {t('remove_from_my_selection')}
                  </span>
                ) : (
                  t('add_into_my_selection')
                )}
              </Button>
            </div>
          </div>
          <div className="mx-auto mt-2 flex max-w-7xl items-center justify-between">
            {position && !recordsLoading && !recordsError ? (
              <>
                {position !== 'first' ? (
                  <button type="button" onClick={() => moveDown()}>
                    <span className="flex cursor-pointer items-center">
                      <LeftArrow className="cursor-pointer text-red" />
                      {t('previous_record')}
                    </span>
                  </button>
                ) : (
                  <div />
                )}
                {position !== 'last' ? (
                  <button type="button" onClick={() => moveUp()}>
                    <span className="flex cursor-pointer items-center">
                      {t('next_record')}
                      <LeftArrow className="rotate-180 cursor-pointer text-red" />
                    </span>
                  </button>
                ) : (
                  <div />
                )}
              </>
            ) : null}
          </div>
        </div>
      </div>
      <div className="mx-auto flex max-w-7xl flex-col justify-between px-4 py-8 md:flex-row 2xl:px-0">
        {isLoading ? (
          <Loader className="mx-auto flex min-h-[50vh] items-center justify-center" />
        ) : null}
        {isError ? (
          <div className="flex w-full items-center justify-center">
            <ShowError />
          </div>
        ) : null}
        {record?.type === 'BOOK' ? (
          <BookSection
            record={record}
            showMore={showMore}
            setShowMore={setShowMore}
          />
        ) : null}
        {record?.type === 'ILLUSTRATION' ? (
          <IllustrationSection
            record={record}
            showMore={showMore}
            setShowMore={setShowMore}
          />
        ) : null}
      </div>
      <div className="relative mx-auto mb-6 flex max-w-7xl px-4 2xl:px-0">
        {record && canUseMirador(record) ? (
          <>
            {/* <span className="text-lg font-bold">MIRADOR</span> */}
            <Suspense fallback={<Loader className="" />}>
              <MiradorContainer
                config={{
                  id: 'mirador',
                  // window: {
                  // allowClose: false,
                  // },
                  windows: [
                    {
                      defaultView: 'gallery',
                      imageToolsEnabled: true,
                      imageToolsOpen: true,
                      // loadedManifest: `/api/eil/record/${record.id}/manifest.json`,
                      loadedManifest: `/api/eil/record/${record.id}/manifest.json`,
                      // thumbnailNavigationPosition: 'far-bottom',
                    },
                  ],
                }}
              />
            </Suspense>
          </>
        ) : null}
      </div>
      {record && 'illustrations' in record && record.illustrations.length ? (
        <div className="mx-auto mb-6 flex max-w-7xl px-4 2xl:px-0">
          <span>
            {sortBy(record.illustrations, (obj) => `${obj.identifier}`).map(
              (l) => (
                <Link
                  to={constructRecordDetailUrl(l.id)}
                  key={l.id}
                  className="block text-red hover:underline"
                >
                  {`${l.identifier} (${l.title})`}
                </Link>
              )
            )}
          </span>
        </div>
      ) : null}
      {record && 'book' in record && record.book?.illustrations.length ? (
        <div className="mx-auto mb-6 flex max-w-7xl">
          <span>
            {sortBy(
              record.book.illustrations.filter(
                (i) => i.identifier !== record.identifier
              ),
              (obj) => `${obj.identifier}`
            ).map((i) => (
              <Link
                to={constructRecordDetailUrl(i.id)}
                key={i.id}
                className="block text-red hover:underline"
              >
                {`${i.identifier} (${i.title})`}
              </Link>
            ))}
          </span>
        </div>
      ) : null}
      <SelectionDialog showDialog={showDialog} setShowDialog={setShowDialog} />
      <SelectionDialogButton setShowDialog={setShowDialog} />
    </section>
  )
}

export default RecordDetail
