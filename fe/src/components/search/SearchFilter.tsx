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
import useRecordAuthorListQuery from '../../api/query/useRecordAuthorListQuery'
import usePublishingPlaceListQuery from '../../api/query/usePublishingPlaceListQuery'
import useSubjectEntryListQuery from '../../api/query/useSubjectEntryListQuery'
import RangeSlider from '../reusableComponents/inputs/rangeSlider/RangeSlider'
import useSearchYearsRangeQuery from '../../api/query/useSearchYearsRangeQuery'
import useMobile from '../../hooks/useMobile'
import useSubjectPlaceListQuery from '../../api/query/useSubjectPlaceListQuery'
import useSubjectPersonListQuery from '../../api/query/useSubjectPersonListQuery'
import useSearchTranslations from '../../hooks/useSearchTranslations'
import { TDropdown } from '../../../../fe-shared/@types/common'
import useKeywordListQuery from '../../api/query/useKeywordListQuery'

type SearchFilterProps = {
  // onClose?: () => void
  filterOpen: boolean
  setFilterOpen: Dispatch<SetStateAction<boolean>>
}

const SearchFilter: FC<SearchFilterProps> = ({ filterOpen, setFilterOpen }) => {
  const { t } = useTranslation('search')
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
    setYearRange,
    filterAuthor,
    setFilterAuthor,
    filterObject,
    setFilterObject,
    filterPublishingPlace,
    setFilterPublishingPlace,
    // filterSubjectPlace,
    // setFilterSubjectPlace,
    type,
    setType,
    view,
    setView,
    itemsPerPage,
    setItemsPerPage,
  } = useSearchStore()

  const { data: authors, status: authorsStatus } =
    useRecordAuthorListQuery(type)
  const { data: publishingPlaces, status: publishingPlaceStatus } =
    usePublishingPlaceListQuery(type)
  const { data: subjectPlaces, status: subjectPlacesStatus } =
    useSubjectPlaceListQuery(type)
  const { data: subjectPerson, status: subjectPersonStatus } =
    useSubjectPersonListQuery(type)
  const { data: subjectEntry, status: subjectEntryStatus } =
    useSubjectEntryListQuery(type)
  const { data: yearsRange } = useSearchYearsRangeQuery(type)
  const { data: keywords, status: keywordsStatus } = useKeywordListQuery(type)

  useEffect(() => {
    if (yearsRange) {
      setYearRange({ from: yearsRange.yearFrom, to: yearsRange.yearTo })
    }
  }, [setYearRange, yearsRange])

  let subject: TDropdown[] = []

  const subjectLoading =
    subjectEntryStatus === 'pending' ||
    subjectPlacesStatus === 'pending' ||
    keywordsStatus === 'pending'

  if (subjectPlaces?.items) {
    subject = subjectPlaces.items.map((s) => ({ value: s.id, label: s.name }))
  }

  // if (subjectPerson?.items) {
  //   subject = subjectPerson.items.map((s) => ({
  //     value: s.id,
  //     label: s.fullName,
  //   }))
  // }

  if (subjectEntry?.items) {
    subject = [
      ...subject,
      ...subjectEntry.items.map((s) => ({ value: s.id, label: s.label })),
    ]
  }

  if (keywords?.items) {
    subject = [
      ...subject,
      ...keywords.items.map((k) => ({ value: k.id, label: k.fullName })),
    ]
  }

  subject = sortBy(subject, (obj) => `${deburr(obj.label)}`)

  return (
    <div
      className={`flex h-full w-screen flex-col ${
        filterOpen ? 'md:w-[370px]' : 'w-0 md:w-16'
      }`}
    >
      <div className="flex items-center justify-between px-6 pt-6 font-bold md:bg-superlightgray md:bg-opacity-30 md:p-4 ">
        <div className="flex w-full items-center justify-between border-b-[1.5px] border-superlightgray pb-2 md:border-none md:p-0">
          {filterOpen && (
            <h2 className="text-2xl font-bold md:text-base">
              {t('filter_results')}
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
              className=" cursor-pointer"
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
              <div className="flex max-h-11">
                <button
                  type="button"
                  className={`w-full rounded-l-xl p-2 px-3 ${
                    type === 'BOOK' ? 'font-bold text-red' : 'text-gray'
                  }  border-collapse border-2 border-superlightgray hover:bg-superlightgray`}
                  onClick={() => setType('BOOK')}
                >
                  {t('books')}
                </button>
                <button
                  type="button"
                  className={`w-full rounded-r-xl p-2 px-3 ${
                    type === 'ILLUSTRATION' ? 'font-bold text-red' : 'text-gray'
                  } border-collapse border-y-2 border-r-2 border-superlightgray hover:bg-superlightgray`}
                  onClick={() => setType('ILLUSTRATION')}
                >
                  {t('illustrations')}
                </button>
              </div>
              <div className="w-full">
                <ViewButtons view={view} setView={setView} type={type} />
              </div>
            </div>
          </div>
        )}
        <span className="mb-4 mt-2 font-bold">{t('year')}</span>
        <RangeSlider
          fromValue={year.from}
          toValue={year.to}
          fromValueChange={(newValue) =>
            setYear({
              ...year,
              from: newValue,
            })
          }
          toValueChange={(newValue) =>
            setYear({
              ...year,
              to: newValue,
            })
          }
          bottomLimit={yearsRange?.yearFrom || 0}
          topLimit={yearsRange?.yearTo || 1990}
          bothValuesChange={(values) =>
            setYear({
              from: values[0],
              to: values[1],
            })
          }
        />
        <div className="relative mt-5 flex flex-col md:mt-10">
          <span className="mb-2 font-bold">{t('author')}</span>
          <Dropdown
            placeholder={t('author_search')}
            isMulti
            isSearchable
            value={filterAuthor}
            isLoading={
              authorsStatus === 'pending' || subjectPersonStatus === 'pending'
            }
            onChange={setFilterAuthor}
            options={
              authors && subjectPerson?.items
                ? sortBy(
                    [
                      ...authors,
                      ...subjectPerson.items.map((i) => ({
                        ...i,
                        fullName: `${i.fullName} (${t('person')})`,
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
          <span className="mb-2 font-bold">{t('what')}</span>
          <Dropdown
            placeholder={t('object_search')}
            isMulti
            isSearchable
            value={filterObject}
            isLoading={subjectLoading}
            onChange={setFilterObject}
            options={subject}
          />
        </div>
        <div className="mt-5 flex flex-col md:mt-10">
          <span className="mb-2 font-bold">{t('place')}</span>
          <div className="flex flex-col gap-4">
            <Dropdown
              placeholder={t('place_search')}
              isMulti
              isSearchable
              value={filterPublishingPlace}
              onChange={setFilterPublishingPlace}
              isLoading={publishingPlaceStatus === 'pending'}
              options={
                publishingPlaces?.items.map((place) => ({
                  value: place.id,
                  label: place.name,
                })) || []
              }
            />
            {/* <Dropdown */}
            {/*  placeholder={t('publishing_place_search')} */}
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
            {/*  placeholder={t('place_search')} */}
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
            name={t('iiif_format')}
            showName
            checked={IIIFFormat}
            onChange={(newValue) => setIIIFFormat(newValue)}
          />
        </div>
      </div>
    </div>
  )
}

export default SearchFilter
