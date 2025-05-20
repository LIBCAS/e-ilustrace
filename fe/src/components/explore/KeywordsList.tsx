import React, { Dispatch, FC, SetStateAction, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'

import clone from 'lodash/clone'
import { useSearchParams } from 'react-router-dom'
import sortBy from 'lodash/sortBy'
import { deburr } from 'lodash'
import clsx from 'clsx'
import Checkbox from '../reusableComponents/inputs/Checkbox'

import MenuClose from '../../assets/icons/menu_close.svg?react'
import MenuOpen from '../../assets/icons/menu_open.svg?react'
import { useThemeListQuery, useThemesCountQuery } from '../../api/theme'
import { useExploreStore } from '../../store/useExploreStore'
import getThemeTranslation from '../../utils/getThemeTranslation'
import Dropdown from '../reusableComponents/inputs/Dropdown'
import { useRecordsWithFacetsQueryList } from '../../api/record'

type KeywordItemProps = {
  id: string
  name: string
}

type KeywordsListProps = {
  filterOpen: boolean
  setFilterOpen: Dispatch<SetStateAction<boolean>>
}

const KeywordItem: FC<KeywordItemProps> = ({ id, name }) => {
  const { themes, setThemes, setPage } = useExploreStore()
  const { i18n } = useTranslation()

  const { data: themesCount } = useThemesCountQuery()

  const handleCheckChange = () => {
    const checkedThemesClone = clone(themes)
    const index = themes.findIndex((th) => th === name)
    if (index >= 0) {
      checkedThemesClone.splice(index, 1)
    } else {
      checkedThemesClone.push(name)
    }
    setThemes(checkedThemesClone)
    setPage(0)
  }

  return (
    <li className="flex items-center">
      <Checkbox
        id={id}
        name={i18n.resolvedLanguage === 'cs' ? name : getThemeTranslation(name)}
        showName
        checked={!!themes.find((th) => th === name)}
        onChange={() => {
          handleCheckChange()
        }}
      />
      <span className="ml-1 font-bold">
        ({themesCount?.find((th) => th.id === id)?.count || 0})
      </span>
    </li>
  )
}

const KeywordsList: FC<KeywordsListProps> = ({ filterOpen, setFilterOpen }) => {
  const {
    themes,
    setThemes,
    page,
    setPage,
    illustrationsPerPage,
    filterObject,
    filterAuthor,
    setFilterAuthor,
    setFilterObject,
  } = useExploreStore()
  const [searchParams, setSearchParams] = useSearchParams()
  const [searchParamsInitialized, setSearchParamsInitialized] = useState(false)
  const { t } = useTranslation()
  const { data: themesData } = useThemeListQuery()

  const { facets } = useRecordsWithFacetsQueryList({
    type: 'ILLUSTRATION',
    size: illustrationsPerPage,
    page,
    themes: { themes, operation: 'AND' },
    objects: { objects: filterObject.map((o) => o.value), operation: 'OR' },
    authors: {
      authors: filterAuthor.map((p) => p.value),
      operation: 'OR',
    },
    facetsEnabled: true,
    recordsEnabled: false,
  })

  useEffect(() => {
    if (!searchParamsInitialized) {
      const paramsThemes = searchParams.get('themes')
      const paramsObjects = searchParams.get('objects')
      const paramsAuthors = searchParams.get('authors')
      const paramsPage = Number(searchParams.get('page'))
      if (
        paramsThemes?.length ||
        paramsObjects?.length ||
        paramsAuthors?.length ||
        paramsPage >= 0
      ) {
        setThemes(paramsThemes?.length ? paramsThemes.split(',') : [])
        if (paramsObjects?.length) {
          const objects = paramsObjects.split(';')
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
        if (paramsAuthors?.length) {
          const authors = paramsAuthors.split(';')
          if (authors.length) {
            const splitAuthors = authors
              .map((p) => p.split('~'))
              .filter((sp) => sp.length === 2)
            setFilterAuthor(
              splitAuthors.map((p) => ({
                value: p[0],
                label: p[1],
              }))
            )
          }
        }
        if (paramsPage >= 0) {
          setPage(paramsPage)
        } else {
          setPage(0)
        }
        setSearchParamsInitialized(true)
      }
    }
  }, [
    searchParams,
    searchParamsInitialized,
    setFilterObject,
    setFilterAuthor,
    setPage,
    setThemes,
  ])

  useEffect(() => {
    setSearchParams({
      themes: themes.join(','),
      objects: filterObject.map((fo) => `${fo.value}~${fo.label}`).join(';'),
      authors: filterAuthor.map((fo) => `${fo.value}~${fo.label}`).join(';'),
      page: page.toString(),
    })
  }, [filterObject, filterAuthor, page, setSearchParams, themes])

  return (
    <div
      className={`flex h-[calc(100vh-5rem)] max-h-[calc(100vh-5rem)] flex-col ${
        filterOpen ? 'w-full min-w-[370px]' : 'w-0 md:w-16'
      }`}
    >
      <div className="flex items-center justify-between bg-superlightgray bg-opacity-30 p-4 font-bold">
        {filterOpen && <p className="font-bold">{t('explore:keywords')}</p>}
        {filterOpen ? (
          <MenuClose
            className="cursor-pointer"
            onClick={() => setFilterOpen(false)}
          />
        ) : (
          <MenuOpen
            className="cursor-pointer"
            onClick={() => setFilterOpen(true)}
          />
        )}
      </div>
      <div
        className={clsx('relative mt-5 flex flex-col px-3', {
          hidden: !filterOpen,
        })}
      >
        <span className="mb-2 font-bold">{t('search:author')}</span>
        <div className="flex flex-col gap-4">
          <Dropdown
            placeholder={t('search:author')}
            isMulti
            isSearchable
            value={filterAuthor}
            onChange={setFilterAuthor}
            isLoading={facets.status === 'pending'}
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
      </div>
      <div
        className={clsx('relative my-5 flex flex-col px-3', {
          hidden: !filterOpen,
        })}
      >
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
                  ],
                  (obj) => `${deburr(obj.label)}`
                )
              : []
          }
        />
      </div>
      <div className="overflow-y-auto py-4 pr-4">
        <ul className={`${filterOpen ? '' : 'hidden'} px-6`}>
          {themesData?.items.map((i) => (
            <KeywordItem id={i.id} key={i.id} name={i.name} />
          ))}
        </ul>
      </div>
    </div>
  )
}

export default KeywordsList
