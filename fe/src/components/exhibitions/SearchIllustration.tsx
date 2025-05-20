import { Link } from 'react-router-dom'
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
import Button from '../reusableComponents/Button'
import CloseIcon from '../../assets/icons/close.svg?react'
import constructSearchUrl from '../../utils/constructSearchUrl'

type TProps = {
  close: () => void
}

const SearchIllustration: FC<TProps> = ({ close }) => {
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
        {/* <ActionButtons /> */}
        <Button
          iconButton
          variant="text"
          className="self-end justify-self-end border-none bg-white font-bold uppercase text-black hover:text-black hover:shadow-none"
          onClick={() => close()}
        >
          <CloseIcon />
        </Button>
      </div>
      <div className="w-5/6">
        <TextInput
          id="ill-search"
          value={search}
          onChange={(newValue) => setSearch(newValue)}
          className="bg-opacity-50 outline-black"
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
