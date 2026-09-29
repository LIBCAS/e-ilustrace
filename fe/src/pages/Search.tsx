import { FC, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'

import clsx from 'clsx'
import clone from 'lodash/clone'
import { v4 as uuidv4 } from 'uuid'
import { useSearchParams } from 'react-router-dom'
import PlusIcon from '../assets/icons/plus.svg?react'
import DeleteIcon from '../assets/icons/delete.svg?react'
import SearchIcon from '../assets/icons/search.svg?react'
import FilterIcon from '../assets/icons/filter.svg?react'

import Button from '../components/reusableComponents/Button'
import ViewButtons from '../components/reusableComponents/ViewButtons'
import TilesView from '../components/reusableComponents/TilesView'
import ListView from '../components/reusableComponents/ListView'
import SearchFilter from '../components/search/SearchFilter'
import Dropdown from '../components/reusableComponents/inputs/Dropdown'
import SelectionDialog from '../components/reusableComponents/SelectionDialog'
import ActiveFilters from '../components/search/ActiveFilters'

import useMobile from '../hooks/useMobile'
import {
  recordTypes,
  SortTypes,
  useSearchStore,
  viewTypes,
} from '../store/useSearchStore'
import TextInput from '../components/reusableComponents/inputs/TextInput'
import { TIllustrationList } from '../../../fe-shared/@types/illustration'
import i18next from '../lang'
import {
  TDropdownWithOperator,
  TFilterOperator,
  RecordType,
  View,
  TSortTypes,
} from '../../../fe-shared/@types/common'
import SelectionDialogButton from '../components/reusableComponents/SelectionDialogButton'

import useSearchTranslations from '../hooks/useSearchTranslations'
import generateSearchSearchParams from '../utils/generateSearchSearchParams'
import {
  useRecordsWithFacetsQueryList,
  useSearchYearsRangeQuery,
} from '../api/record'

type SearchInputProps = {
  search: { search: string; category: TDropdownWithOperator; uuid: string }
  index: number
  searchesCount: number
}

const SearchInput: FC<SearchInputProps> = ({
  search,
  index,
  searchesCount,
}) => {
  const { t } = useTranslation('search')
  const { searchWithCategory, setSearchWithCategory } = useSearchStore()
  const { searchCategories } = useSearchTranslations()

  const onUpdateSearch = (uuid: string, newValue: string) => {
    const currentSearchClone = clone(searchWithCategory)
    const foundObject = currentSearchClone.findIndex((s) => s.uuid === uuid)
    if (foundObject >= 0) {
      if (currentSearchClone[foundObject].category.value === 'yearFrom') {
        currentSearchClone[foundObject].search = newValue.replace(/\D/g, '')
      } else {
        currentSearchClone[foundObject].search = newValue
      }
      setSearchWithCategory(currentSearchClone)
    }
  }

  const onUpdateCategory = (uuid: string, newValue: TDropdownWithOperator) => {
    const currentSearchClone = clone(searchWithCategory)
    const foundObject = currentSearchClone.findIndex((s) => s.uuid === uuid)
    if (foundObject >= 0) {
      if (newValue.value === 'yearFrom') {
        currentSearchClone[foundObject].search = currentSearchClone[
          foundObject
        ].search.replace(/\D/g, '')
      }
      currentSearchClone[foundObject].category = newValue
      setSearchWithCategory(currentSearchClone)
    }
  }

  return (
    <div className="flex flex-col gap-2 border-superlightgray max-md:border-b max-md:pb-2 md:flex-row md:justify-stretch">
      <TextInput
        id={`search_${search.uuid}`}
        startIcon={<SearchIcon />}
        placeholder={t('search_expression')}
        value={search.search}
        className="outline-black"
        parentClassName="!w-auto md:!w-full"
        onChange={(newValue) => onUpdateSearch(search.uuid, newValue)}
      />
      <div className="flex shrink-0 flex-wrap justify-end gap-2 md:flex-nowrap">
        <div className="w-full sm:max-w-[220px] md:min-w-[160px]">
          <Dropdown
            placeholder={t('category')}
            shortenValues
            value={search.category}
            onChange={(newValue) => onUpdateCategory(search.uuid, newValue)}
            options={searchCategories}
          />
        </div>
        {index > 0 ? (
          <Button
            startIcon={<DeleteIcon className="h-4 w-4" />}
            onClick={() => {
              const currentSearchClone = clone(searchWithCategory)
              const foundObject = currentSearchClone.findIndex(
                (s) => s.uuid === search.uuid
              )
              if (foundObject >= 0) {
                currentSearchClone.splice(foundObject, 1)
                setSearchWithCategory(currentSearchClone)
              }
            }}
          >
            {t('delete')}
          </Button>
        ) : null}
        {index === searchesCount - 1 ? (
          <Button
            startIcon={<PlusIcon className="h-4 w-4" />}
            variant="submit"
            onClick={() => {
              const currentSearchClone = clone(searchWithCategory)
              currentSearchClone.push({
                search: '',
                category: {
                  value: 'ALL',
                  label: i18next.t('search:all'),
                  operation: 'FTXF',
                },
                uuid: uuidv4(),
              })
              setSearchWithCategory(currentSearchClone)
            }}
          >
            {t('add')}
          </Button>
        ) : null}
      </div>
    </div>
  )
}

const Search: FC = () => {
  const { t } = useTranslation('search')
  const {
    sort,
    itemsPerPage,
    type,
    view,
    year,
    currentPage,
    filterObject,
    filterAuthor,
    filterPublishingPlace,
    filterSubjectPlace,
    IIIFFormat,
    setCurrentPage,
    setItemsPerPage,
    searchWithCategory,
    setSort,
    setType,
    setView,
    setYear,
    setFilterAuthor,
    setFilterObject,
    setFilterPublishingPlace,
    setFilterSubjectPlace,
    setSearchWithCategory,
    setIIIFFormat,
  } = useSearchStore()
  const viewRef = useRef<HTMLDivElement>(null)

  const [searchParams, setSearchParams] = useSearchParams()
  const [searchParamsInitialized, setSearchParamsInitialized] = useState(false)
  const [showDialog, setShowDialog] = useState(false)
  const { isMobile, isTablet } = useMobile()
  const [filterOpen, setFilterOpen] = useState(!isTablet)
  const { sortValuesForDropdown, itemsPerPageForDropdown, searchCategories } =
    useSearchTranslations()

  useEffect(() => {
    if (!searchParamsInitialized) {
      const paramsSort = searchParams.get('sort')
      const paramsYear = searchParams.get('year')
      const paramsItemsPerPage = searchParams.get('itemsPerPage')
      const paramsView = searchParams.get('view')
      const paramsType = searchParams.get('type')
      const paramsFilterAuthor = searchParams.get('filterAuthor')
      const paramsFilterObject = searchParams.get('filterObject')
      const paramsFilterPublishingPlace = searchParams.get(
        'filterPublishingPlace'
      )
      const paramsFilterSubjectPlace = searchParams.get('filterSubjectPlace')
      const paramsSearchWithCategory = searchParams.get('searchWithCategory')
      const paramsIIIFFormat = searchParams.get('IIIFFormat')
      const paramsCurrentPage = searchParams.get('currentPage')

      if (paramsSort?.length) {
        const parsedSort = paramsSort.split('~')
        if (
          parsedSort.length === 2 &&
          SortTypes.includes(parsedSort[0] as TSortTypes)
        ) {
          const foundMatch = sortValuesForDropdown.find(
            (sv) => sv.value === parsedSort[0]
          )
          if (foundMatch?.value) {
            setSort({
              value: foundMatch.value as TSortTypes,
              label: foundMatch.label,
            })
          }
        }
      }

      if (paramsYear?.length) {
        const parsedYear = paramsYear.split('~')
        if (
          parsedYear.length === 2 &&
          !Number.isNaN(Number(parsedYear[0])) &&
          !Number.isNaN(Number(parsedYear[1]))
        ) {
          setYear({ from: Number(parsedYear[0]), to: Number(parsedYear[1]) })
        }
      }

      if (paramsItemsPerPage?.length) {
        const parsedIPP = paramsItemsPerPage.split('~')
        if (parsedIPP.length === 2) {
          setItemsPerPage({ value: parsedIPP[0], label: parsedIPP[1] })
        }
      }

      if (
        paramsType?.length &&
        recordTypes.includes(paramsType as RecordType) &&
        paramsView?.length &&
        viewTypes.includes(paramsView as View)
      ) {
        if (paramsType === 'BOOK') {
          setView('LIST')
        } else {
          setView(paramsView as View)
        }
        setType(paramsType as RecordType)
      }

      if (paramsFilterAuthor?.length) {
        const authors = paramsFilterAuthor.split(';')
        if (authors.length) {
          const splitAuthors = authors
            .map((a) => a.split('~'))
            .filter((sa) => sa.length === 2)
          setFilterAuthor(
            splitAuthors.map((a) => ({
              value: a[0],
              label: a[1],
            }))
          )
        }
      }

      if (paramsFilterObject?.length) {
        const objects = paramsFilterObject.split(';')
        if (objects.length) {
          const splitObjects = objects
            .map((o) => o.split('~'))
            .filter((so) => so.length === 2)
          setFilterObject(
            splitObjects.map((o) => ({
              value: o[0],
              label: o[1],
            }))
          )
        }
      }

      if (paramsFilterPublishingPlace?.length) {
        const publishingPlaces = paramsFilterPublishingPlace.split(';')
        if (publishingPlaces.length) {
          const splitPublishingPlaces = publishingPlaces
            .map((p) => p.split('~'))
            .filter((sp) => sp.length === 2)
          setFilterPublishingPlace(
            splitPublishingPlaces.map((p) => ({
              value: p[0],
              label: p[1],
            }))
          )
        }
      }

      if (paramsFilterSubjectPlace?.length) {
        const subjectPlaces = paramsFilterSubjectPlace.split(';')
        if (subjectPlaces.length) {
          const splitSubjectPlaces = subjectPlaces
            .map((p) => p.split('~'))
            .filter((sp) => sp.length === 2)
          setFilterSubjectPlace(
            splitSubjectPlaces.map((p) => ({
              value: p[0],
              label: p[1],
            }))
          )
        }
      }

      if (paramsSearchWithCategory?.length) {
        const pSearchWithCategory = paramsSearchWithCategory.split(';')
        if (pSearchWithCategory.length) {
          const splitSearchWithCategory = pSearchWithCategory
            .map((c) => c.split('~'))
            .filter(
              (sc) =>
                sc.length > 3 && searchCategories.some((c) => c.value === sc[1])
            )
          setSearchWithCategory(
            splitSearchWithCategory.map((s) => ({
              search: s[0],
              category: {
                value: s[1],
                label:
                  searchCategories.find((sc) => sc.value === s[1])?.label || '',
                operation: s[3] as TFilterOperator,
              },
              uuid: s[4]?.length ? s[4] : uuidv4(),
            }))
          )
        }
      }

      if (paramsIIIFFormat?.length) {
        setIIIFFormat(paramsIIIFFormat === 'true')
      }

      if (
        paramsCurrentPage?.length &&
        !Number.isNaN(Number(paramsCurrentPage))
      ) {
        setCurrentPage(Number(paramsCurrentPage))
      }

      setSearchParamsInitialized(true)
    }
  }, [
    searchCategories,
    searchParams,
    searchParamsInitialized,
    setCurrentPage,
    setFilterAuthor,
    setFilterObject,
    setFilterPublishingPlace,
    setFilterSubjectPlace,
    setIIIFFormat,
    setItemsPerPage,
    setSearchWithCategory,
    setSort,
    setType,
    setView,
    setYear,
    sortValuesForDropdown,
  ])

  useEffect(() => {
    setSearchParams(
      generateSearchSearchParams({
        sort,
        year,
        itemsPerPage,
        view,
        type,
        filterAuthor,
        filterObject,
        filterPublishingPlace,
        filterSubjectPlace,
        currentPage,
        searchWithCategory,
        IIIFFormat,
      })
    )
  }, [
    IIIFFormat,
    currentPage,
    filterAuthor,
    filterObject,
    filterPublishingPlace,
    filterSubjectPlace,
    itemsPerPage,
    searchWithCategory,
    setSearchParams,
    sort,
    type,
    view,
    year,
  ])

  const { data: yearsDataReady } = useSearchYearsRangeQuery(type)

  const { records } = useRecordsWithFacetsQueryList({
    type,
    size: Number(itemsPerPage.value),
    year,
    sort: sort?.value || 'title_ASC',
    page: currentPage,
    authors: { authors: filterAuthor.map((a) => a.value), operation: 'OR' },
    objects: { objects: filterObject.map((o) => o.value), operation: 'OR' },
    publishingPlaces: {
      publishingPlaces: filterPublishingPlace.map((p) => p.value),
      operation: 'OR',
    },
    subjectPlaces: {
      subjectPlaces: filterSubjectPlace.map((p) => p.value),
      operation: 'OR',
    },
    searchWithCategory: searchWithCategory.map((s) => ({
      search: s.search,
      category: s.category.value,
      operation: s.category.operation,
    })),
    searchWithCategoryOperation: 'AND',
    isIIIF: IIIFFormat,
    recordsEnabled: !!yearsDataReady /* dont call query twice on init */,
  })

  const paginate = (pageNumber: number) => {
    setCurrentPage(pageNumber)
    if (viewRef.current) {
      viewRef.current.scrollTo(0, 0)
    }
  }

  return (
    <section>
      <div className="mt-20 border-y border-superlightgray">
        <div className="mx-auto flex max-w-7xl justify-center border-superlightgray py-5 md:py-10">
          <div className="mx-auto flex w-full max-w-[850px] flex-col items-center gap-4 px-4 md:flex-row">
            {isMobile && (
              <h1 className="mr-auto text-3xl font-bold md:text-4xl">
                {t('header')}
              </h1>
            )}
            <div className="flex w-full flex-col gap-2 md:gap-4">
              {searchWithCategory.map((search, index, { length }) => (
                <SearchInput
                  key={search.uuid}
                  search={search}
                  index={index}
                  searchesCount={length}
                />
              ))}
            </div>
          </div>
        </div>
        {isTablet ? (
          <div className="mx-auto flex w-full max-w-[850px] justify-end px-4 pb-5 md:pb-10">
            <Button
              className="bg-superlightgray"
              startIcon={<FilterIcon />}
              variant="outlined"
              onClick={() => setFilterOpen(true)}
            >
              {t('filter')}
            </Button>
          </div>
        ) : null}
      </div>
      <div className="flex">
        {!isTablet ? (
          <aside className="border-collapse border-x-[1.5px] border-superlightgray xl:h-[calc(100vh-126px)]">
            <SearchFilter
              filterOpen={filterOpen}
              setFilterOpen={setFilterOpen}
            />
          </aside>
        ) : (
          <aside
            className={clsx(
              'fixed left-0 top-0 z-50 h-screen border-collapse border-x-[1.5px] border-superlightgray bg-white xl:h-[calc(100vh-126px)]',
              { block: filterOpen, hidden: !filterOpen }
            )}
          >
            <SearchFilter
              filterOpen={filterOpen}
              setFilterOpen={setFilterOpen}
            />
          </aside>
        )}
        <div className="flex h-[calc(100vh-126px)] w-full flex-col px-8">
          {!isMobile && (
            <div className="mx-auto mb-2 flex w-full items-start justify-between gap-1 border-b border-superlightgray py-8">
              <ActiveFilters />
              <div className="flex w-[49%] min-w-[410px] max-w-[450px] shrink-0 flex-wrap justify-end gap-4 border-l border-superlightgray">
                <div className="flex flex-col gap-2">
                  <div className="flex max-h-11">
                    <button
                      type="button"
                      className={`rounded-l-xl p-2 px-3 ${
                        type === 'BOOK' ? 'font-bold text-red' : 'text-gray'
                      } border-collapse border-2 border-superlightgray hover:bg-superlightgray`}
                      onClick={() => setType('BOOK')}
                    >
                      {t('books')}
                    </button>
                    <button
                      type="button"
                      className={`rounded-r-xl p-2 px-3 ${
                        type === 'ILLUSTRATION'
                          ? 'font-bold text-red'
                          : 'text-gray'
                      } border-collapse border-y-2 border-r-2 border-superlightgray hover:bg-superlightgray`}
                      onClick={() => setType('ILLUSTRATION')}
                    >
                      {t('illustrations')}
                    </button>
                  </div>
                  <ViewButtons view={view} setView={setView} type={type} />
                </div>
                <div className="flex flex-col gap-2">
                  <Dropdown
                    placeholder={t('category')}
                    shortenValues
                    value={itemsPerPage}
                    onChange={setItemsPerPage}
                    options={itemsPerPageForDropdown}
                  />
                  <Dropdown
                    placeholder={t('book_name')}
                    value={sort}
                    onChange={setSort}
                    options={sortValuesForDropdown}
                  />
                </div>
              </div>
            </div>
          )}
          <div
            ref={viewRef}
            className="wrapper pb-8 pt-4 md:overflow-auto md:px-8"
          >
            {view === 'TILES' && (
              <TilesView
                allowFastSwitch
                error={records.isError}
                loading={records.isLoading}
                currentPage={currentPage}
                illustrations={
                  (records.data?.items as TIllustrationList[]) || []
                }
                illustrationsPerPage={Number(itemsPerPage.value)}
                totalIllustrations={records.data?.count || 0}
                paginate={paginate}
                backPath="search"
              />
            )}
            {view === 'LIST' && (
              <ListView
                allowFastSwitch
                error={records.isError}
                loading={records.isLoading}
                currentPage={currentPage}
                illustrations={records.data?.items || []}
                illustrationsPerPage={Number(itemsPerPage.value)}
                totalIllustrations={records.data?.count || 0}
                paginate={paginate}
                backPath="search"
              />
            )}
          </div>
        </div>
      </div>
      <SelectionDialog showDialog={showDialog} setShowDialog={setShowDialog} />
      <SelectionDialogButton setShowDialog={setShowDialog} />
    </section>
  )
}

export default Search
