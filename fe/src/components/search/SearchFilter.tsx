import { Dispatch, FC, SetStateAction, useEffect } from 'react'
import { useTranslation } from 'react-i18next'

import sortBy from 'lodash/sortBy'
import { deburr } from 'lodash'
import MenuClose from '../../assets/icons/menu_close.svg?react'
import MenuOpen from '../../assets/icons/menu_open.svg?react'
import Close from '../../assets/icons/close.svg?react'

import Checkbox from '../reusableComponents/inputs/Checkbox'
import Dropdown from '../reusableComponents/inputs/Dropdown'
import ViewButtons from '../reusableComponents/ViewButtons'

import { useSearchStore } from '../../store/useSearchStore'
import RangeSlider from '../reusableComponents/inputs/rangeSlider/RangeSlider'
import {
  useSearchYearsRangeQuery,
  useRecordsWithFacetsQueryList,
} from '../../api/record'
import useMobile from '../../hooks/useMobile'
import useSearchTranslations from '../../hooks/useSearchTranslations'
import Button from '../reusableComponents/Button'

type SearchFilterProps = {
  // onClose?: () => void
  filterOpen: boolean
  setFilterOpen: Dispatch<SetStateAction<boolean>>
}

const SearchFilter: FC<SearchFilterProps> = ({ filterOpen, setFilterOpen }) => {
  const { t, i18n } = useTranslation()
  const { isMobile } = useMobile()
  const { sortValuesForDropdown, itemsPerPageForDropdown } =
    useSearchTranslations()
  const {
    IIIFFormat,
    setIIIFFormat,
    sort,
    setSort,
    year,
    setYear,
    yearRangeSet,
    setYearRangeSet,
    setYearRange,
    filterAuthor,
    setFilterAuthor,
    filterObject,
    setFilterObject,
    filterPublishingPlace,
    setFilterPublishingPlace,
    filterSubjectPlace,
    // setFilterSubjectPlace,
    type,
    setType,
    view,
    setView,
    itemsPerPage,
    setItemsPerPage,
    currentPage,
    searchWithCategory,
  } = useSearchStore()

  const { data: yearsRange } = useSearchYearsRangeQuery(type)

  const { facets } = useRecordsWithFacetsQueryList({
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
    recordsEnabled: false,
    facetsEnabled: !!yearsRange /* dont call query twice on init */,
  })

  useEffect(() => {
    if (yearsRange) {
      if (type === 'ILLUSTRATION' && !yearRangeSet.illustrations) {
        setYearRange({ from: yearsRange.yearFrom, to: yearsRange.yearTo })
        setYearRangeSet({ ...yearRangeSet, illustrations: true })
      }
      if (type === 'BOOK' && !yearRangeSet.books) {
        setYearRange({ from: yearsRange.yearFrom, to: yearsRange.yearTo })
        setYearRangeSet({ ...yearRangeSet, books: true })
      }
    }
  }, [setYearRange, setYearRangeSet, type, yearRangeSet, yearsRange])

  useEffect(() => {
    if (type === 'BOOK') {
      setFilterAuthor(
        filterAuthor.filter((a) => !a.label.includes(t('search:person')))
      )
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [type, t, setFilterAuthor])

  return (
    <div
      className={`flex h-full w-screen flex-col ${
        filterOpen ? 'md:w-[370px]' : 'w-0 md:w-16'
      }`}
    >
      <div className="flex items-center justify-between px-6 pt-6 font-bold md:bg-superlightgray md:bg-opacity-30 md:p-4">
        <div className="flex w-full items-center justify-between border-b-[1.5px] border-superlightgray pb-2 md:border-none md:p-0">
          {filterOpen && (
            <h2 className="text-2xl font-bold md:text-base">
              {t('search:filter_results')}
            </h2>
          )}
          {filterOpen && !isMobile ? (
            <MenuClose
              className="cursor-pointer"
              onClick={() => setFilterOpen(false)}
            />
          ) : null}
          {filterOpen && isMobile ? (
            <Close
              className="cursor-pointer"
              onClick={() => setFilterOpen(false)}
            />
          ) : null}
          {!filterOpen ? (
            <MenuOpen
              className="cursor-pointer"
              onClick={() => setFilterOpen(true)}
            />
          ) : null}
        </div>
      </div>
      <div
        className={`flex h-full flex-col ${
          filterOpen ? '' : 'hidden'
        } overflow-y-scroll px-6`}
      >
        {isMobile && (
          <div className="mx-auto mb-2 flex w-full flex-col items-stretch justify-between gap-2 border-b border-superlightgray py-4 md:py-8">
            {/* <ActiveFilters /> */}
            <div className="flex w-full flex-col gap-3 md:gap-4">
              <Dropdown
                placeholder={t('search:category')}
                shortenValues
                value={itemsPerPage}
                onChange={setItemsPerPage}
                options={itemsPerPageForDropdown}
              />
              <Dropdown
                placeholder={t('search:book_name')}
                value={sort}
                onChange={setSort}
                options={sortValuesForDropdown}
              />
              <div className="flex max-h-11">
                <button
                  type="button"
                  className={`w-full rounded-l-xl p-2 px-3 ${
                    type === 'BOOK' ? 'font-bold text-red' : 'text-gray'
                  } border-collapse border-2 border-superlightgray hover:bg-superlightgray`}
                  onClick={() => setType('BOOK')}
                >
                  {t('search:books')}
                </button>
                <button
                  type="button"
                  className={`w-full rounded-r-xl p-2 px-3 ${
                    type === 'ILLUSTRATION' ? 'font-bold text-red' : 'text-gray'
                  } border-collapse border-y-2 border-r-2 border-superlightgray hover:bg-superlightgray`}
                  onClick={() => setType('ILLUSTRATION')}
                >
                  {t('search:illustrations')}
                </button>
              </div>
              <div className="w-full">
                <ViewButtons view={view} setView={setView} type={type} />
              </div>
            </div>
          </div>
        )}
        <span className="mb-4 mt-2 font-bold">{t('search:year')}</span>
        <RangeSlider
          fromValue={year.from}
          toValue={year.to}
          bottomLimit={yearsRange?.yearFrom || 0}
          topLimit={yearsRange?.yearTo || 1990}
          bothValuesChange={(values) =>
            setYear({
              from: values[0],
              to: values[1],
            })
          }
        />
        <div className="relative mt-5 flex flex-col">
          <span className="mb-2 font-bold">{t('search:author')}</span>
          <Dropdown
            placeholder={t('search:author_search')}
            isMulti
            isSearchable
            value={filterAuthor}
            isLoading={facets.status === 'pending'}
            onChange={setFilterAuthor}
            options={
              facets.data
                ? sortBy(
                    [
                      ...facets.data.authors,
                      ...facets.data.subjectPersons.map((i) => ({
                        ...i,
                        fullName: `${i.fullName} (${t('search:person')})`,
                      })),
                    ],
                    (obj) => `${obj.fullName}`
                  ).map((author) => ({
                    value: author.id,
                    label: author.fullName,
                  }))
                : []
            }
          />
        </div>
        <div className="relative mt-5 flex flex-col md:mt-10">
          <span className="mb-2 font-bold">{t('search:what')}</span>
          <Dropdown
            placeholder={t('search:object_search')}
            isMulti
            isSearchable
            value={filterObject}
            isLoading={facets.status === 'pending'}
            onChange={setFilterObject}
            options={
              facets.data
                ? sortBy(
                    [
                      ...facets.data.subjectPlaces.map((s) => ({
                        value: s.id,
                        label: `${s.name} (${t('search:theme')})`,
                      })),
                      ...facets.data.subjectEntries.map((s) => ({
                        value: s.id,
                        label: `${s.label} (${t('search:theme')})`,
                      })),
                      ...facets.data.keywords.map((k) => ({
                        value: k.id,
                        label: `${k.label} (${t('search:keyword')})`,
                      })),
                      ...facets.data.genres.map((g) => ({
                        value: g.id,
                        label: `${g.name}`,
                      })),
                    ],
                    (obj) => `${deburr(obj.label)}`
                  )
                : []
            }
          />
        </div>
        <div className="mt-5 flex flex-col md:mt-10">
          <span className="mb-2 font-bold">{t('search:place')}</span>
          <div className="flex flex-col gap-4">
            <Dropdown
              placeholder={t('search:place_search')}
              isMulti
              isSearchable
              value={filterPublishingPlace}
              onChange={setFilterPublishingPlace}
              isLoading={facets.status === 'pending'}
              options={
                facets.data
                  ? facets.data.publishingPlaces.map((place) => ({
                      value: place.id,
                      label: place.name,
                    }))
                  : []
              }
            />
            {/* <Dropdown */}
            {/*  placeholder={t('search:publishing_place_search')} */}
            {/*  isMulti */}
            {/*  isSearchable */}
            {/*  value={filterPublishingPlace} */}
            {/*  onChange={setFilterPublishingPlace} */}
            {/*  options={ */}
            {/*    publishingPlaces?.items.map((place) => ({ */}
            {/*      value: place.id, */}
            {/*      label: place.name, */}
            {/*    })) || [] */}
            {/*  } */}
            {/* /> */}
            {/* <Dropdown */}
            {/*  placeholder={t('search:place_search')} */}
            {/*  isMulti */}
            {/*  isSearchable */}
            {/*  value={filterSubjectPlace} */}
            {/*  onChange={setFilterSubjectPlace} */}
            {/*  options={ */}
            {/*    subjectPlaces?.items.map((object) => ({ */}
            {/*      value: object.id, */}
            {/*      label: object.name, */}
            {/*    })) || [] */}
            {/*  } */}
            {/* /> */}
          </div>
        </div>
        <div className="mt-5 pb-5 md:mt-10">
          <Checkbox
            id="filter_iiif_format"
            name={t('search:iiif_format')}
            showName
            checked={IIIFFormat}
            onChange={(newValue) => setIIIFFormat(newValue)}
          />
        </div>
        <div className="mb-5 mt-3">
          <Button
            variant="secondary"
            href={
              i18n.resolvedLanguage === 'cs'
                ? 'https://e-ilustrace.cz/napoveda/'
                : 'https://e-ilustrace.cz/en/help/'
            }
            target="_blank"
          >
            {t('navigation:help')}
          </Button>
        </div>
      </div>
    </div>
  )
}

export default SearchFilter
