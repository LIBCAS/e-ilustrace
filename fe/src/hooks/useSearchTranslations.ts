import { useTranslation } from 'react-i18next'
import { useMemo } from 'react'

const useSearchTranslations = () => {
  const { t } = useTranslation('search')

  const sortValuesForDropdown = useMemo(
    () => [
      { value: 'title_ASC', label: 'A - Z' },
      { value: 'title_DESC', label: 'Z - A' },
      { value: 'yearFrom_ASC', label: t('oldest') },
      { value: 'yearFrom_DESC', label: t('newest') },
    ],
    [t]
  )

  const searchCategories = useMemo(
    () => [
      { value: 'ALL', label: t('all'), operation: 'OR' },
      {
        value: 'mainAuthor.author.fullName',
        label: t('main_author'),
        operation: 'FTXF',
      },
      { value: 'title', label: t('title'), operation: 'FTXF' },
      {
        value: 'publishingPlaces.name',
        label: t('publishing_place'),
        operation: 'FTXF',
      },
      {
        value: 'coauthors.author.fullName',
        label: t('printer_publisher'),
        operation: 'FTXF',
      },
      {
        value: 'yearFrom',
        label: t('publishing_year'),
        operation: 'EQ',
      },
      {
        value: 'identifier',
        label: t('record_id'),
        operation: 'CONTAINS',
      },
    ],
    [t]
  )

  const itemsPerPageForDropdown = [
    { value: '10', label: '10' },
    { value: '25', label: '25' },
    { value: '50', label: '50' },
  ]

  return { sortValuesForDropdown, itemsPerPageForDropdown, searchCategories }
}

export default useSearchTranslations
