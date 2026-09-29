import { useRef, useDeferredValue, FC } from 'react'
import { useTranslation } from 'react-i18next'
import clone from 'lodash/clone'
import { v4 as uuidv4 } from 'uuid'
import { useRecordsWithFacetsQueryList } from '../../api/record'
import PlusIcon from '../../assets/icons/plus.svg?react'
import SearchIcon from '../../assets/icons/search.svg?react'
// import BookMark from '../assets/icons/bookmark.svg?react'
// import FilterIcon from '../assets/icons/filter.svg?react'
// import Button from '../components/reusableComponents/Button'
import DeleteIcon from '../../assets/icons/delete.svg?react'
import ListView from '../../components/reusableComponents/ListView'
import SearchFilter from './SearchFilter'
import Dropdown from '../../components/reusableComponents/inputs/Dropdown'
import ActiveFilters from './ActiveFilters'
import TextInput from '../../components/reusableComponents/inputs/TextInput'
import { TDropdownWithOperator } from '../../../../fe-shared/@types/common'
import { useExportStore } from '../../store/useExportStore'
import useSearchTranslations from '../../hooks/useSearchTranslations'
import Button from '../../components/reusableComponents/Button'
import i18next from '../../lang'
import ExportSelectionDialog from './ExportSelectionDialog'

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
  const { t } = useTranslation()
  const { searchWithCategory, setSearchWithCategory } = useExportStore()
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
        placeholder={t('search.search_expression')}
        value={search.search}
        className="outline-black"
        parentClassName="!w-auto md:!w-full"
        onChange={(newValue) => onUpdateSearch(search.uuid, newValue)}
      />
      <div className="flex shrink-0 flex-wrap justify-end gap-2 md:flex-nowrap">
        <div className="w-full sm:max-w-[220px] md:min-w-[160px]">
          <Dropdown
            placeholder={t('search.category')}
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
            {t('search.delete')}
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
                  label: i18next.t('search.all'),
                  operation: 'FTXF',
                },
                uuid: uuidv4(),
              })
              setSearchWithCategory(currentSearchClone)
            }}
          >
            {t('search.add')}
          </Button>
        ) : null}
      </div>
    </div>
  )
}

const Export = () => {
  const { t } = useTranslation()

  const {
    sort,
    year,
    itemsPerPage,
    type,
    currentPage,
    filterObject,
    filterAuthor,
    filterPublishingPlace,
    filterICCStates,
    filterThemeStates,
    IIIFFormat,
    setCurrentPage,
    setItemsPerPage,
    setType,
    searchWithCategory,
    setSort,
  } = useExportStore()
  const viewRef = useRef<HTMLDivElement>(null)

  const { records } = useRecordsWithFacetsQueryList({
    type,
    size: Number(itemsPerPage.value),
    sort: sort?.value || 'title_ASC',
    page: currentPage,
    year: useDeferredValue(year),
    authors: { authors: filterAuthor.map((a) => a.value), operation: 'OR' },
    objects: { objects: filterObject.map((o) => o.value), operation: 'OR' },
    publishingPlaces: {
      publishingPlaces: filterPublishingPlace.map((p) => p.value),
      operation: 'OR',
    },
    iccStates: filterICCStates.map((s) => s.value),
    themeStates: filterThemeStates.map((s) => s.value),
    searchWithCategory: searchWithCategory.map((s) => ({
      search: s.search,
      category: s.category.value,
      operation: s.category.operation,
    })),
    searchWithCategoryOperation: 'AND',
    facetsEnabled: false,
    isIIIF: IIIFFormat,
  })

  const paginate = (pageNumber: number) => {
    setCurrentPage(pageNumber)
    if (viewRef.current) {
      viewRef.current.scrollTo(0, 0)
    }
  }

  return (
    <div className="bg-white">
      <ExportSelectionDialog queryRecordsCount={records?.data?.count} />
      <div className="w-full border-y border-superlightgray">
        <div className="mx-auto flex max-w-7xl justify-center border-superlightgray py-5 md:py-10">
          <div className="mx-auto flex w-full max-w-[850px] flex-col items-center gap-4 px-4 md:flex-row">
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
      </div>
      <div className="flex">
        <aside className="border-collapse border-x-[1.5px] border-superlightgray md:h-[calc(100vh-126px)]">
          <SearchFilter />
        </aside>
        <div className="flex h-[calc(100vh-126px)] w-full flex-col px-8">
          <div className="mx-auto mb-2 flex w-full items-center justify-between border-b border-superlightgray py-8">
            <ActiveFilters />
            <div className="ml-auto flex gap-4">
              <div className="flex max-h-11">
                <button
                  type="button"
                  className={`rounded-l-xl p-2 px-3 ${
                    type === 'BOOK' ? 'font-bold text-red' : 'text-gray'
                  } border-collapse border-2 border-superlightgray hover:bg-superlightgray`}
                  onClick={() => setType('BOOK')}
                >
                  {t('search.books')}
                </button>
                <button
                  type="button"
                  className={`rounded-r-xl p-2 px-3 ${
                    type === 'ILLUSTRATION' ? 'font-bold text-red' : 'text-gray'
                  } border-collapse border-y-2 border-r-2 border-superlightgray hover:bg-superlightgray`}
                  onClick={() => setType('ILLUSTRATION')}
                >
                  {t('search.illustrations')}
                </button>
              </div>
              <Dropdown
                placeholder={t('search.category')}
                shortenValues
                value={itemsPerPage}
                onChange={setItemsPerPage}
                options={[
                  { value: '10', label: '10' },
                  { value: '25', label: '25' },
                  { value: '50', label: '50' },
                ]}
              />
              <Dropdown
                placeholder={t('search.book_name')}
                value={sort}
                onChange={setSort}
                options={[
                  { value: 'title_ASC', label: 'A - Z' },
                  { value: 'title_DESC', label: 'Z - A' },
                ]}
              />
            </div>
          </div>

          <div
            ref={viewRef}
            className="wrapper pb-28 pt-4 md:overflow-auto md:px-8 md:pt-8"
          >
            <ListView
              error={records.isError}
              loading={records.isLoading}
              currentPage={currentPage}
              illustrations={records.data?.items || []}
              illustrationsPerPage={Number(itemsPerPage.value)}
              totalIllustrations={records?.data?.count || 0}
              paginate={paginate}
              showExportBadges
            />
          </div>
        </div>
      </div>
    </div>
  )
}

export default Export
