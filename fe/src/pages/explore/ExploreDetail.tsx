import { Dispatch, FC, SetStateAction, useDeferredValue, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useSearchParams } from 'react-router-dom'
import LeftArrow from '../../assets/icons/left_arrow.svg?react'
import KeyIcon from '../../assets/icons/key.svg?react'

import Button from '../../components/reusableComponents/Button'
import SelectionDialog from '../../components/reusableComponents/SelectionDialog'
import Gallery from '../../components/reusableComponents/Gallery'
import useMobile from '../../hooks/useMobile'
import useRecordListQuery from '../../api/query/useRecordListQuery'
import Paginator from '../../components/reusableComponents/Paginator'
import { TIllustrationList } from '../../../../fe-shared/@types/illustration'
import { useExploreStore } from '../../store/useExploreStore'
import SelectionDialogButton from '../../components/reusableComponents/SelectionDialogButton'

type TExploreDetailProps = {
  setFilterOpen: Dispatch<SetStateAction<boolean>>
}

const ExploreDetail: FC<TExploreDetailProps> = ({ setFilterOpen }) => {
  const [showDialog, setShowDialog] = useState(false)
  const { isMobile } = useMobile()
  const { t } = useTranslation('explore')
  const { themes, search, page, setPage, resetParams, illustrationsPerPage } =
    useExploreStore()
  const [searchParams] = useSearchParams()

  const {
    data: illustrations,
    isFetching: illustrationsLoading,
    isError: illustrationsError,
  } = useRecordListQuery({
    type: 'ILLUSTRATION',
    size: illustrationsPerPage,
    page,
    searchWithCategory: [
      {
        search: useDeferredValue(search),
        category: 'title',
        operation: 'FTXF',
      },
      {
        search: useDeferredValue(search),
        category: 'subjectPersons.fullName',
        operation: 'FTXF',
      },
      {
        search: useDeferredValue(search),
        category: 'subjectEntries.label',
        operation: 'FTXF',
      },
      {
        search: useDeferredValue(search),
        category: 'subjectPlaces.name',
        operation: 'FTXF',
      },
      {
        search: useDeferredValue(search),
        category: 'keywords',
        operation: 'FTXF',
      },
    ],
    themes: { themes, operation: 'AND' },
  })

  return (
    <section className="relative pb-16">
      <div className="flex flex-col items-end gap-4 border-b-[1.5px] border-superlightgray pb-6 md:flex-row md:items-center md:justify-between md:border-none md:pb-0">
        <div className="flex w-full items-center gap-4">
          <LeftArrow
            className="flex-shrink-0 cursor-pointer"
            onClick={() => resetParams()}
          />
          <h1 className="text-xl font-bold uppercase tracking-wider">
            {/* {t(name as string)} */}
            {themes.join(', ')}
          </h1>
        </div>
        <div className="flex flex-wrap justify-end gap-4">
          {isMobile && (
            <Button
              className="bg-superlightgray"
              variant="outlined"
              startIcon={<KeyIcon />}
              onClick={() => setFilterOpen(true)}
            >
              {t('keywords')}
            </Button>
          )}
        </div>
      </div>
      <Gallery
        items={(illustrations?.items as TIllustrationList[]) || []}
        error={illustrationsError}
        loading={illustrationsLoading}
        backPath={`explore?${searchParams.toString()}`}
      />
      <SelectionDialog showDialog={showDialog} setShowDialog={setShowDialog} />
      <SelectionDialogButton setShowDialog={setShowDialog} />
      <div className="mx-auto mt-4 flex w-fit flex-col items-center gap-y-2 md:flex-row">
        {illustrations?.count ? (
          <Paginator
            itemsPerPage={illustrationsPerPage}
            contentLength={illustrations.count}
            currentPage={page}
            onChange={setPage}
          />
        ) : null}
        <span className="ml-5 text-gray">
          {t('search:records_count')}
          {illustrations?.count || 0}
        </span>
      </div>
    </section>
  )
}

export default ExploreDetail
