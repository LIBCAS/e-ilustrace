import { useRef, useDeferredValue } from 'react'
import { useTranslation } from 'react-i18next'
import useIllustrationListQuery from '../api/query/useIllustrationListQuery'
import SearchIcon from '../assets/icons/search.svg?react'
// import BookMark from '../assets/icons/bookmark.svg?react'
// import FilterIcon from '../assets/icons/filter.svg?react'
// import Button from '../components/reusableComponents/Button'
import ListView from '../components/reusableComponents/ListView'
import SearchFilter from '../components/SearchFilter'
import Dropdown from '../components/reusableComponents/inputs/Dropdown'
import ActiveFilters from '../components/ActiveFilters'
import { useSearchStore } from '../store/useSearchStore'
import TextInput from '../components/reusableComponents/inputs/TextInput'

const Records = () => {
  const { t } = useTranslation()

  const {
    sort,
    year,
    itemsPerPage,
    currentPage,
    filterObject,
    filterAuthor,
    filterPlace,
    filterICCStates,
    filterThemeStates,
    setCurrentPage,
    setItemsPerPage,
    currentSearch,
    setCurrentSearch,
    category,
    setSort,
    setCategory,
  } = useSearchStore()
  const viewRef = useRef<HTMLDivElement>(null)

  const {
    data: records,
    isLoading: recordsLoading,
    isError: recordsError,
  } = useIllustrationListQuery({
    size: Number(itemsPerPage.value),
    sort: sort?.value || 'ASC',
    page: currentPage,
    year: useDeferredValue(year),
    authors: filterAuthor.map((a) => a.value),
    objects: filterObject.map((o) => o.value),
    places: filterPlace.map((p) => p.value),
    iccStates: filterICCStates.map((s) => s.value),
    themeStates: filterThemeStates.map((s) => s.value),
    category: category.value,
    search: useDeferredValue(currentSearch),
  })

  const paginate = (pageNumber: number) => {
    setCurrentPage(pageNumber)
    if (viewRef.current) {
      viewRef.current.scrollTo(0, 0)
    }
  }

  return (
    <div className="bg-white">
      <div className="w-full border-y border-superlightgray">
        <div className="mx-auto flex max-w-7xl justify-center border-superlightgray py-5 md:py-10">
          <div className="flex w-11/12 flex-col items-center gap-4 sm:w-3/4 md:w-2/3 md:flex-row">
            <TextInput
              id="vise"
              startIcon={<SearchIcon />}
              placeholder={t('search.search_expression')}
              value={currentSearch}
              className="outline-black"
              onChange={(newValue) => setCurrentSearch(newValue)}
            />
            <div className="h-min max-h-min w-full md:w-1/2">
              <Dropdown
                placeholder={t('search.category')}
                shortenValues
                value={category}
                onChange={setCategory}
                options={[
                  { value: 'identifier', label: t('search.record_id') },
                  { value: 'id', label: 'UUID' },
                ]}
              />
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
                  { value: 'ASC', label: 'A - Z' },
                  { value: 'DESC', label: 'Z - A' },
                ]}
              />
            </div>
          </div>

          <div
            ref={viewRef}
            className="wrapper pb-28 pt-4 md:overflow-auto md:px-8 md:pt-8"
          >
            <ListView
              error={recordsError}
              loading={recordsLoading}
              currentPage={currentPage}
              illustrations={records?.items || []}
              illustrationsPerPage={Number(itemsPerPage.value)}
              totalIllustrations={records?.count || 0}
              paginate={paginate}
            />
          </div>
        </div>
      </div>
    </div>
  )
}

export default Records
