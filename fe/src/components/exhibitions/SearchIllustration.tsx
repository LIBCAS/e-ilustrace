import { Link } from 'react-router'
import { useTranslation } from 'react-i18next'

import { FC, useDeferredValue, useState } from 'react'
import Search from '../../assets/icons/search.svg?react'

import { useRecordListQuery } from '../../api/record'
import TextInput from '../reusableComponents/inputs/TextInput'
import Loader from '../reusableComponents/Loader'
import ShowError from '../reusableComponents/ShowError'
import ShowInfoMessage from '../reusableComponents/ShowInfoMessage'
import ListOfSelectableIllustrations from './ListOfSelectableIllustrations'
import { TIllustrationList } from '../../../../fe-shared/@types/illustration'
import constructSearchUrl from '../../utils/constructSearchUrl'

const SearchIllustration: FC = () => {
  const { t } = useTranslation()
  const [search, setSearch] = useState('')
  const { data, isFetching, isError } = useRecordListQuery({
    type: 'ILLUSTRATION',
    size: 30,
    page: 0,
    searchWithCategory: [
      {
        search: useDeferredValue(search),
        category: 'title',
        operation: 'FTXF',
      },
      {
        search: useDeferredValue(search),
        category: 'identifier',
        operation: 'CONTAINS',
      },
    ],
    enabled: !!search.length,
  })

  return (
    <div className="mt-12 w-full border-t border-t-superlightgray py-8">
      <div className="flex justify-between">
        <h2 className="my-4 text-xl font-bold">
          {t('exhibitions:search_illustration')}
        </h2>
      </div>
      <div className="w-5/6">
        <TextInput
          id="ill-search"
          value={search}
          onChange={(newValue) => setSearch(newValue)}
          className="bg-superlightgray/50 focus:border-black"
          startIcon={<Search />}
          placeholder={t('exhibitions:search_placeholder')}
        />
      </div>
      <div>
        {isFetching ? (
          <div className="my-10 flex items-center justify-center">
            <Loader />
          </div>
        ) : null}
        {!isFetching && isError ? <ShowError /> : null}
        {!isFetching && !isError && !data?.items && search.length ? (
          <ShowInfoMessage message={t('common:no_record_found')} />
        ) : null}
        {!isFetching && !isError && data?.items ? (
          <ListOfSelectableIllustrations
            illustrations={data.items as TIllustrationList[]}
          />
        ) : null}
      </div>
      <p className="mt-6 text-gray">
        {t('exhibitions:did_not_found')}{' '}
        <span className="text-red underline">
          <Link to={constructSearchUrl()} target="_blank">
            {t('exhibitions:go_to_complex_search')}
          </Link>
        </span>
        {t('exhibitions:and_use_my_selection')}
      </p>
    </div>
  )
}

export default SearchIllustration
