import { FC, useDeferredValue, useState } from 'react'

import { useTranslation } from 'react-i18next'
import SearchIcon from '../assets/icons/search.svg?react'
import ImageSearch from '../assets/icons/image_search.svg?react'
import VisualGroup from '../assets/icons/visual_group.svg?react'

import Button from '../components/reusableComponents/Button'
import ViewButtons from '../components/reusableComponents/ViewButtons'
import { View, TDropdownWithOperator } from '../../../fe-shared/@types/common'
import TilesView from '../components/reusableComponents/TilesView'
import ListView from '../components/reusableComponents/ListView'
import { TIllustrationList } from '../../../fe-shared/@types/illustration'
import { useRecordListQuery } from '../api/record'
import TextInput from '../components/reusableComponents/inputs/TextInput'
import Dropdown from '../components/reusableComponents/inputs/Dropdown'

const Vise: FC = () => {
  const { t } = useTranslation()
  const [view, setView] = useState<View>('TILES')
  const [currentPage, setCurrentPage] = useState(0)
  const [illustrationsPerPage] = useState(10)
  const [search, setSearch] = useState('')
  const [category, setCategory] = useState<TDropdownWithOperator>({
    value: 'ALL',
    label: t('search:all'),
    operation: 'OR',
  })

  const {
    data: illustrations,
    isLoading: illustrationsLoading,
    isError: illustrationsError,
  } = useRecordListQuery({
    type: 'ILLUSTRATION',
    size: 20,
    page: currentPage,
    searchWithCategory: [
      {
        search: useDeferredValue(search),
        category: category.value,
        operation: category.operation,
      },
    ],
  })

  // Change page
  const paginate = (pageNumber: number) => setCurrentPage(pageNumber)

  return (
    <section>
      <div className="mt-20 border-y border-superlightgray">
        <div className="mx-auto flex max-w-7xl justify-center border-superlightgray py-3 md:py-10">
          <div className="flex w-full max-w-[900px] flex-col items-end gap-4 px-4 md:flex-row md:items-center">
            <TextInput
              id="vise"
              value={search}
              onChange={(newValue) => setSearch(newValue)}
              startIcon={<SearchIcon />}
              placeholder={t('search:search_expression')}
              className="outline-black"
            />
            <div className="w-[200px] md:min-w-[250px]">
              <Dropdown
                placeholder={t('category')}
                shortenValues
                value={category}
                onChange={(newValue) => setCategory(newValue)}
                options={[
                  { value: 'ALL', label: t('search:all'), operation: 'OR' },
                  {
                    value: 'mainAuthor.author.fullName',
                    label: t('search:main_author'),
                    operation: 'FTXF',
                  },
                  {
                    value: 'title',
                    label: t('search:title'),
                    operation: 'FTXF',
                  },
                  {
                    value: 'publishingPlaces.name',
                    label: t('search:publishing_place'),
                    operation: 'FTXF',
                  },
                  {
                    value: 'coauthors.author.fullName',
                    label: t('search:printer_publisher'),
                    operation: 'FTXF',
                  },
                  {
                    value: 'yearFrom',
                    label: t('search:publishing_year'),
                    operation: 'EQ',
                  },
                  {
                    value: 'identifier',
                    label: t('search:record_id'),
                    operation: 'CONTAINS',
                  },
                ]}
              />
            </div>
          </div>
        </div>
      </div>
      <div className="wrapper mx-auto flex justify-between border-b border-superlightgray px-8 py-8">
        <h1 className="text-left text-3xl font-bold">VISE</h1>
        <div className="flex gap-1">
          <Button
            iconButton
            variant="outlined"
            href="/vise/Illustrations/external_search"
            target="_blank"
          >
            <ImageSearch />
          </Button>
          <Button
            iconButton
            variant="outlined"
            href="/vise/Illustrations/visual_group"
            target="_blank"
          >
            <VisualGroup />
          </Button>
          <ViewButtons view={view} setView={setView} />
        </div>
      </div>
      <div className="wrapper px-8 py-8">
        {view === 'TILES' && (
          <TilesView
            clickType="vise"
            error={illustrationsError}
            loading={illustrationsLoading}
            currentPage={currentPage}
            illustrations={(illustrations?.items as TIllustrationList[]) || []}
            illustrationsPerPage={illustrationsPerPage}
            totalIllustrations={illustrations?.count || 0}
            paginate={paginate}
            backPath="vise"
          />
        )}
        {view === 'LIST' && (
          <ListView
            clickType="vise"
            error={illustrationsError}
            loading={illustrationsLoading}
            currentPage={currentPage}
            illustrations={illustrations?.items || []}
            illustrationsPerPage={illustrationsPerPage}
            totalIllustrations={illustrations?.count || 0}
            paginate={paginate}
            backPath="vise"
          />
        )}
      </div>
    </section>
  )
}

export default Vise
