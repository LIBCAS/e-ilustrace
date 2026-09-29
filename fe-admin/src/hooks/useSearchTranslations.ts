import { useTranslation } from 'react-i18next'
import { useMemo } from 'react'

const useSearchTranslations = () => {
  const { t } = useTranslation()

  const sortValuesForDropdown = useMemo(
    () => [
      { value: 'title_ASC', label: 'A - Z' },
      { value: 'title_DESC', label: 'Z - A' },
      { value: 'yearFrom_ASC', label: t('search.oldest') },
      { value: 'yearFrom_DESC', label: t('search.newest') },
    ],
    [t]
  )

  const searchCategories = useMemo(
    () => [
      { value: 'ALL', label: t('search.all'), operation: 'OR' },
      {
        value: 'mainAuthor.author.fullName',
        label: t('search.main_author'),
        operation: 'FTXF',
      },
      { value: 'title', label: t('search.title'), operation: 'FTXF' },
      {
        value: 'publishingPlaces.name',
        label: t('search.publishing_place'),
        operation: 'FTXF',
      },
      {
        value: 'coauthors.author.fullName',
        label: t('search.printer_publisher'),
        operation: 'FTXF',
      },
      {
        value: 'yearFrom',
        label: t('search.publishing_year'),
        operation: 'EQ',
      },
      {
        value: 'identifier',
        label: t('search.record_id'),
        operation: 'CONTAINS',
      },
      {
        value: 'iconclass.code',
        label: t('search.iconclass'),
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
