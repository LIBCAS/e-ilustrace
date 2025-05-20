import { FC } from 'react'
import { useTranslation } from 'react-i18next'

import clone from 'lodash/clone'
import Close from '../../assets/icons/close.svg?react'
import { useSearchStore } from '../../store/useSearchStore'
import { useSearchYearsRangeQuery } from '../../api/record'

const ActiveFilters: FC = () => {
  const { t } = useTranslation('filters')
  const {
    year,
    type,
    filterPublishingPlace,
    filterSubjectPlace,
    filterObject,
    filterAuthor,
    setYear,
    setFilterPublishingPlace,
    setFilterSubjectPlace,
    setFilterObject,
    setFilterAuthor,
  } = useSearchStore()

  const { data: searchYears } = useSearchYearsRangeQuery(type)

  const allFilters = [
    ...filterPublishingPlace,
    ...filterSubjectPlace,
    ...filterObject,
    ...filterAuthor,
  ]

  return (
    <div className="flex w-[49%] flex-wrap gap-2">
      <div className="flex flex-wrap gap-2">
        {allFilters.map((filter, index) => (
          <div
            key={filter.label}
            className="flex items-center gap-1 rounded-md border-2 border-superlightgray px-2 py-1"
          >
            {filter.label}
            <Close
              key={`close-${filter.label}`}
              className="ml-2 h-4 w-4 cursor-pointer"
              onClick={() => {
                if (
                  filterPublishingPlace.find(
                    (place) => place.value === filter.value
                  )
                ) {
                  const places = clone(filterPublishingPlace)
                  places.splice(index, 1)
                  setFilterPublishingPlace(places)
                } else if (
                  filterSubjectPlace.find(
                    (place) => place.value === filter.value
                  )
                ) {
                  const places = clone(filterSubjectPlace)
                  places.splice(index, 1)
                  setFilterSubjectPlace(places)
                } else if (
                  filterObject.find((object) => object.value === filter.value)
                ) {
                  const objects = clone(filterObject)
                  objects.splice(index, 1)
                  setFilterObject(objects)
                } else if (
                  filterAuthor.find((author) => author.value === filter.value)
                ) {
                  const authors = clone(filterAuthor)
                  authors.splice(index, 1)
                  setFilterAuthor(authors)
                }
              }}
            />
          </div>
        ))}
        <div className="flex items-center gap-1 rounded-md border-2 border-superlightgray px-2 py-1">
          {year.from} - {year.to}
          <Close
            className="ml-2 h-4 w-4 cursor-pointer"
            onClick={() => {
              setYear(
                searchYears
                  ? { from: searchYears.yearFrom, to: searchYears.yearTo }
                  : { from: 0, to: 1990 }
              )
            }}
          />
        </div>
      </div>

      {/** Check if there are active filters */}
      {allFilters.length > 0 ? (
        <button
          type="button"
          className="flex items-center gap-1 rounded-md border-2 border-superlightgray bg-superlightgray px-2 py-1"
          onClick={() => {
            // Clear all filters from SearchFilter
            setFilterAuthor([])
            setFilterObject([])
            setFilterPublishingPlace([])
            setFilterSubjectPlace([])
            setYear(
              searchYears
                ? { from: searchYears.yearFrom, to: searchYears.yearTo }
                : { from: 0, to: 1990 }
            )
          }}
        >
          {t('delete_all')}
          <Close className="ml-2 h-4 w-4 cursor-pointer" />
        </button>
      ) : null}
    </div>
  )
}

export default ActiveFilters
