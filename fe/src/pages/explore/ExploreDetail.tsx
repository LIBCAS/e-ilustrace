import { Dispatch, FC, SetStateAction, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useSearchParams } from 'react-router-dom'
import LeftArrow from '../../assets/icons/left_arrow.svg?react'
import KeyIcon from '../../assets/icons/key.svg?react'

import Button from '../../components/reusableComponents/Button'
import SelectionDialog from '../../components/reusableComponents/SelectionDialog'
import Gallery from '../../components/reusableComponents/Gallery'
import useMobile from '../../hooks/useMobile'
import Paginator from '../../components/reusableComponents/Paginator'
import { TIllustrationList } from '../../../../fe-shared/@types/illustration'
import { useExploreStore } from '../../store/useExploreStore'
import SelectionDialogButton from '../../components/reusableComponents/SelectionDialogButton'
import getThemeTranslation from '../../utils/getThemeTranslation'
import { useRecordsWithFacetsQueryList } from '../../api/record'

type TExploreDetailProps = {
  setFilterOpen: Dispatch<SetStateAction<boolean>>
}

const ExploreDetail: FC<TExploreDetailProps> = ({ setFilterOpen }) => {
  const [showDialog, setShowDialog] = useState(false)
  const { isMobile } = useMobile()
  const { t, i18n } = useTranslation('explore')
  const {
    themes,
    page,
    setPage,
    resetParams,
    illustrationsPerPage,
    filterAuthor,
    filterObject,
  } = useExploreStore()
  const [searchParams] = useSearchParams()

  const { records } = useRecordsWithFacetsQueryList({
    type: 'ILLUSTRATION',
    size: illustrationsPerPage,
    page,
    themes: { themes, operation: 'AND' },
    objects: { objects: filterObject.map((o) => o.value), operation: 'OR' },
    authors: {
      authors: filterAuthor.map((p) => p.value),
      operation: 'OR',
    },
  })

  return (
    <section className="relative pb-16">
      <div className="flex flex-col items-end gap-4 border-b-[1.5px] border-superlightgray pb-6 md:flex-row md:items-center md:justify-between md:border-none md:pb-0">
        <div className="flex w-full items-center gap-4">
          <LeftArrow
            className="flex-shrink-0 cursor-pointer"
            onClick={() => resetParams()}
          />
          <h1 className="text-lg font-bold uppercase tracking-wider">
            {[
              ...filterObject.map((o) => o.label),
              ...filterAuthor.map((p) => p.label),
              ...themes.map((theme) =>
                i18n.resolvedLanguage === 'cs'
                  ? theme
                  : getThemeTranslation(theme)
              ),
            ].join(', ')}
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
        items={(records.data?.items as TIllustrationList[]) || []}
        error={records.isError}
        loading={records.isLoading}
        backPath={`explore?${searchParams.toString()}`}
      />
      <SelectionDialog showDialog={showDialog} setShowDialog={setShowDialog} />
      <SelectionDialogButton setShowDialog={setShowDialog} />
      <div className="mx-auto mt-4 flex w-fit flex-col items-center gap-y-2 md:flex-row">
        {records.data?.count ? (
          <Paginator
            itemsPerPage={illustrationsPerPage}
            contentLength={records.data.count}
            currentPage={page}
            onChange={setPage}
          />
        ) : null}
        <span className="ml-5 text-gray">
          {t('search:records_count')}
          {records.data?.count || 0}
        </span>
      </div>
    </section>
  )
}

export default ExploreDetail
