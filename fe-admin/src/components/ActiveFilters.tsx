import { FC } from 'react'
import { useTranslation } from 'react-i18next'

import clone from 'lodash/clone'
import Close from '../assets/icons/close.svg?react'
import { useSearchStore } from '../store/useSearchStore'

const ActiveFilters: FC = () => {
  const { t } = useTranslation()
  const {
    year,
    filterPublishingPlace,
    filterObject,
    filterAuthor,
    setYear,
    setFilterPublishingPlace,
    setFilterObject,
    setFilterAuthor,
  } = useSearchStore()

  const allFilters = [
    ...filterPublishingPlace,
    ...filterObject,
    ...filterAuthor,
  ]

  return (
    <div className="flex flex-wrap gap-2">
      <div className="flex flex-wrap gap-2 border-r border-superlightgray pr-2">
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
                    (place) => place.label === filter.label
                  )
                ) {
                  const places = clone(filterPublishingPlace)
                  places.splice(index, 1)
                  setFilterPublishingPlace(places)
                } else if (
                  filterObject.find((object) => object.label === filter.label)
                ) {
                  const objects = clone(filterObject)
                  objects.splice(index, 1)
                  setFilterObject(objects)
                } else if (
                  filterAuthor.find((author) => author.label === filter.label)
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
              setYear({ from: 0, to: 1990 })
            }}
          />
        </div>
      </div>

      {/** Check if there are active filters */}
      {allFilters.length > 0 ? (
        <button
          type="button"
          className="flex cursor-pointer items-center gap-1 rounded-md border-2 border-superlightgray bg-superlightgray px-2 py-1"
          onClick={() => {
            // Clear all filters from SearchFilter
            setFilterAuthor([])
            setFilterObject([])
            setFilterPublishingPlace([])
            setYear({ from: 0, to: 1990 })
          }}
        >
          {t('filters.delete_all')}
          <Close className="ml-2 h-4 w-4 cursor-pointer" />
        </button>
      ) : null}
    </div>
  )
}

export default ActiveFilters
