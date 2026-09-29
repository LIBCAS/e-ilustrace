import {
  Dispatch,
  FC,
  SetStateAction,
  useDeferredValue,
  useEffect,
  useState,
} from 'react'
import { useTranslation } from 'react-i18next'

import { useSearchParams } from 'react-router-dom'
import SearchIcon from '../../assets/icons/search.svg?react'
import PaletteIcon from '../../assets/icons/palette.svg?react'

import SelectionDialog from '../reusableComponents/SelectionDialog'
import Gallery from '../reusableComponents/Gallery'
import TextInput from '../reusableComponents/inputs/TextInput'
import Button from '../reusableComponents/Button'
import { useRecordListQuery } from '../../api/record'
import Paginator from '../reusableComponents/Paginator'
import { TIllustrationList } from '../../../../fe-shared/@types/illustration'
import { useIconClassStore } from '../../store/useIconClassStore'
import Dropdown from '../reusableComponents/inputs/Dropdown'
import SelectionDialogButton from '../reusableComponents/SelectionDialogButton'
import generateIconClassSearchParams from '../../utils/generateIconClassSearchParams'
import { TFilterOperator } from '../../../../fe-shared/@types/common'
import useIconClassDropdownTranslations from '../../hooks/useIconClassDropdownTranslations'

type Props = {
  isMobile: boolean
  setFilterOpen: Dispatch<SetStateAction<boolean>>
}

const IconClassSearch: FC<Props> = ({ isMobile, setFilterOpen }) => {
  const { t } = useTranslation('iconclass')
  const [searchParams, setSearchParams] = useSearchParams()
  const [searchParamsInitialized, setSearchParamsInitialized] = useState(false)
  const [showDialog, setShowDialog] = useState(false)
  const { filterValuesForDropdown } = useIconClassDropdownTranslations()
  const illustrationsPerPage = 20
  const {
    search,
    setSearch,
    page,
    setPage,
    icc,
    setIcc,
    category,
    setCategory,
  } = useIconClassStore()

  const {
    data: illustrations,
    isLoading: illustrationsLoading,
    isError: illustrationsError,
  } = useRecordListQuery({
    type: 'ILLUSTRATION',
    size: illustrationsPerPage,
    icc: { icc, operation: 'AND' },
    page,
    searchWithCategory: [
      {
        search: useDeferredValue(search),
        category: category.value,
        operation: category.operation,
      },
    ],
  })

  useEffect(() => {
    if (!searchParamsInitialized) {
      const paramsPage = searchParams.get('page')
      const paramsSearch = searchParams.get('search')
      const paramsCategory = searchParams.get('category')
      const paramsIcc = searchParams.get('icc')

      if (paramsPage?.length && !Number.isNaN(Number(paramsPage))) {
        setPage(Number(paramsPage))
      }

      if (paramsSearch?.length) {
        setSearch(paramsSearch)
      }

      if (paramsCategory?.length) {
        const parsedCategory = paramsCategory.split('~')
        if (parsedCategory.length === 3) {
          const foundMatch = filterValuesForDropdown.find(
            (sv) => sv.value === parsedCategory[0]
          )
          if (foundMatch?.value) {
            setCategory({
              value: foundMatch?.value,
              label: foundMatch?.label,
              operation: parsedCategory[2] as TFilterOperator,
            })
          }
        }
      }

      if (paramsIcc?.length) {
        const splitIcc = paramsIcc.split(';')
        if (splitIcc.length) {
          setIcc(splitIcc)
        }
      }

      setSearchParamsInitialized(true)
    }
  }, [
    searchParams,
    searchParamsInitialized,
    setCategory,
    setIcc,
    setPage,
    setSearch,
    filterValuesForDropdown,
  ])

  useEffect(() => {
    setSearchParams(
      generateIconClassSearchParams({ page, search, category, icc })
    )
  }, [category, icc, page, search, setSearchParams])

  const paginate = (pageNumber: number) => setPage(pageNumber)

  return (
    <section className="w-full pb-16">
      <div className="flex w-full flex-col flex-wrap items-center justify-between gap-2 border-b-[1.5px] border-superlightgray pb-3 text-left md:flex-row">
        <div className="mr-auto flex w-full items-center justify-between border-b-[1.5px] border-superlightgray pb-6 md:w-auto md:border-none md:pb-0">
          <h1 className="text-xl font-bold uppercase tracking-wider">
            ICONCLASS
          </h1>
          {isMobile && (
            <Button
              className="bg-superlightgray"
              variant="outlined"
              startIcon={<PaletteIcon />}
              onClick={() => setFilterOpen(true)}
            >
              {t('themes')}
            </Button>
          )}
        </div>
        <div className="flex w-full flex-col items-end justify-center gap-4 py-4 md:w-auto md:flex-row md:items-center md:py-0">
          <TextInput
            id="iconclass"
            startIcon={<SearchIcon />}
            placeholder={t('search_illustration')}
            value={search}
            className="h-full outline-black"
            onChange={(newValue) => setSearch(newValue)}
          />
          <div className="min-w-[200px] lg:min-w-[250px]">
            <Dropdown
              placeholder={t('category')}
              // shortenValues
              value={category}
              onChange={setCategory}
              options={filterValuesForDropdown}
            />
          </div>
        </div>
      </div>
      <Gallery
        loading={illustrationsLoading}
        error={illustrationsError}
        items={(illustrations?.items as TIllustrationList[]) || []}
        backPath="iconclass"
      />
      <SelectionDialog showDialog={showDialog} setShowDialog={setShowDialog} />
      <SelectionDialogButton setShowDialog={setShowDialog} />
      <div className="mx-auto mt-4 flex w-fit flex-col items-center gap-y-2 md:flex-row">
        {illustrations?.count ? (
          <Paginator
            itemsPerPage={illustrationsPerPage}
            contentLength={illustrations?.count || 0}
            currentPage={page}
            onChange={paginate}
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

export default IconClassSearch
