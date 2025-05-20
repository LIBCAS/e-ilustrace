import { useTranslation } from 'react-i18next'

import { useDeferredValue } from 'react'
import { deburr, sortBy } from 'lodash'
import Checkbox from './reusableComponents/inputs/Checkbox'
import Dropdown from './reusableComponents/inputs/Dropdown'

import { useSearchStore } from '../store/useSearchStore'
import { useEnrichmentStates } from '../utils/helperHooks'
import NumberRangeInput from './reusableComponents/inputs/NumberRangeInput'
import { useRecordsWithFacetsQueryList } from '../api/record'

const SearchFilter = () => {
  const { t } = useTranslation()
  const {
    IIIFFormat,
    setIIIFFormat,
    filterAuthor,
    setFilterAuthor,
    year,
    setYear,
    filterObject,
    setFilterObject,
    filterPublishingPlace,
    setFilterPublishingPlace,
    filterThemeStates,
    filterICCStates,
    setFilterICCStates,
    setFilterThemeStates,
    itemsPerPage,
    sort,
    currentPage,
    currentSearch,
    category,
  } = useSearchStore()

  const { facets } = useRecordsWithFacetsQueryList({
    type: 'ILLUSTRATION',
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
    searchWithCategory: [
      {
        search: currentSearch,
        category: category.value,
        operation: 'CONTAINS',
      },
    ],
    facetsEnabled: true,
    recordsEnabled: false,
  })

  const { states } = useEnrichmentStates()

  return (
    <div className="flex h-full w-screen flex-col md:w-[370px]">
      <div className="flex items-center justify-between px-6 pt-6 font-bold md:bg-superlightgray md:bg-opacity-30 md:p-4">
        <div className="flex w-full items-center justify-between border-b-[1.5px] border-superlightgray pb-2 md:border-none md:p-0">
          <h2 className="text-2xl font-bold md:text-base">
            {t('search.filter_results')}
          </h2>
        </div>
      </div>
      <div className="mt-2 flex h-full flex-col overflow-y-scroll px-6">
        <span className="mb-4 mt-2 font-bold">{t('search.year')}</span>
        <NumberRangeInput
          fromValue={year.from.toString()}
          toValue={year.to.toString()}
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
        />
        <div className="relative mt-5 flex flex-col md:mt-10">
          <span className="mb-2 font-bold">{t('search.icc_state')}</span>
          <Dropdown
            placeholder={t('search.state_search')}
            isMulti
            isSearchable
            value={filterICCStates}
            onChange={setFilterICCStates}
            options={states}
          />
        </div>
        <div className="relative mt-5 flex flex-col md:mt-10">
          <span className="mb-2 font-bold">{t('search.theme_state')}</span>
          <Dropdown
            placeholder={t('search.state_search')}
            isMulti
            isSearchable
            value={filterThemeStates}
            onChange={setFilterThemeStates}
            options={states}
          />
        </div>
        <div className="relative mt-5 flex flex-col md:mt-10">
          <span className="mb-2 font-bold">{t('search.author')}</span>
          <Dropdown
            placeholder={t('search.author_search')}
            isMulti
            isSearchable
            value={filterAuthor}
            onChange={setFilterAuthor}
            loading={facets.status === 'pending'}
            options={
              facets.data
                ? sortBy(
                    [
                      ...facets.data.authors,
                      ...facets.data.subjectPersons.map((i) => ({
                        ...i,
                        fullName: `${i.fullName} (${t('search.person')})`,
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
          <span className="mb-2 font-bold">{t('search.what')}</span>
          <Dropdown
            placeholder={t('search.object_search')}
            isMulti
            isSearchable
            value={filterObject}
            onChange={setFilterObject}
            loading={facets.status === 'pending'}
            options={
              facets.data
                ? sortBy(
                    [
                      ...facets.data.subjectPlaces.map((s) => ({
                        value: s.id,
                        label: `${s.name} (${t('search.theme')})`,
                      })),
                      ...facets.data.subjectEntries.map((s) => ({
                        value: s.id,
                        label: `${s.label} (${t('search.theme')})`,
                      })),
                      ...facets.data.keywords.map((k) => ({
                        value: k.id,
                        label: `${k.label} (${t('search.keyword')})`,
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
          <span className="mb-2 font-bold">{t('search.place')}</span>
          <Dropdown
            placeholder={t('search.place_search')}
            isMulti
            isSearchable
            value={filterPublishingPlace}
            onChange={setFilterPublishingPlace}
            loading={facets.status === 'pending'}
            options={
              facets.data
                ? facets.data.publishingPlaces.map((place) => ({
                    value: place.id,
                    label: place.name,
                  }))
                : []
            }
          />
        </div>
        <div className="mt-5 pb-5 md:mt-10">
          <Checkbox
            id="filter_iiif_format"
            name={t('search.iiif_format')}
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
