import React, { Dispatch, FC, SetStateAction, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'

import clone from 'lodash/clone'
import { useSearchParams } from 'react-router-dom'
import Checkbox from '../reusableComponents/inputs/Checkbox'

import SearchIcon from '../../assets/icons/search.svg?react'
import MenuClose from '../../assets/icons/menu_close.svg?react'
import MenuOpen from '../../assets/icons/menu_open.svg?react'
import useThemeListQuery from '../../api/query/useThemeListQuery'
import TextInput from '../reusableComponents/inputs/TextInput'
import { useExploreStore } from '../../store/useExploreStore'
import useThemesCountQuery from '../../api/query/useThemesCountQuery'

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
        name={name}
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
  const { themes, setThemes, search, setSearch, page, setPage } =
    useExploreStore()
  const [searchParams, setSearchParams] = useSearchParams()
  const [searchParamsInitialized, setSearchParamsInitialized] = useState(false)
  const { t } = useTranslation('explore')
  const { data } = useThemeListQuery()

  useEffect(() => {
    if (!searchParamsInitialized) {
      const paramsThemes = searchParams.get('themes')
      const paramsSearch = searchParams.get('search')
      const paramsPage = Number(searchParams.get('page'))
      if (paramsThemes?.length || paramsSearch?.length || paramsPage >= 0) {
        setThemes(paramsThemes?.length ? paramsThemes.split(',') : [])
        setSearch(paramsSearch || '')
        if (paramsPage >= 0) {
          setPage(paramsPage)
        } else {
          setPage(0)
        }
        setSearchParamsInitialized(true)
      }
    }
  }, [searchParams, searchParamsInitialized, setPage, setSearch, setThemes])

  useEffect(() => {
    setSearchParams({ themes: themes.join(','), search, page: page.toString() })
  }, [page, search, setSearchParams, themes])

  return (
    <div
      className={`h-[calc(100vh-5rem)] max-h-[calc(100vh-5rem)] overflow-y-hidden ${
        filterOpen ? 'w-full min-w-[370px]' : 'w-0 md:w-16'
      }`}
    >
      <div className="flex items-center justify-between bg-superlightgray bg-opacity-30 p-4 font-bold">
        {filterOpen && <p className="font-bold">{t('keywords')}</p>}
        {filterOpen ? (
          <MenuClose
            className=" cursor-pointer"
            onClick={() => setFilterOpen(false)}
          />
        ) : (
          <MenuOpen
            className=" cursor-pointer"
            onClick={() => setFilterOpen(true)}
          />
        )}
      </div>
      {/* <div */}
      {/*  className={`${ */}
      {/*    filterOpen ? '' : 'hidden' */}
      {/*  } mt-8 border-b-[1.5px] border-superlightgray px-6 pb-6`} */}
      {/* > */}
      {/*  <TextInput */}
      {/*    id="vise" */}
      {/*    startIcon={<SearchIcon />} */}
      {/*    placeholder={t('search:search_expression')} */}
      {/*    className="outline-black" */}
      {/*    value={search} */}
      {/*    onChange={(newValue) => { */}
      {/*      setSearch(newValue) */}
      {/*      setPage(0) */}
      {/*    }} */}
      {/*  /> */}
      {/* </div> */}
      <div className="h-[calc(100%-80px)] py-4 pr-4">
        <ul
          className={`${
            filterOpen ? '' : 'hidden'
          } h-[calc(100%-80px)] overflow-y-scroll px-6`}
        >
          {data?.items.map((i) => (
            <KeywordItem id={i.id} key={i.id} name={i.name} />
          ))}
        </ul>
      </div>
    </div>
  )
}

export default KeywordsList
