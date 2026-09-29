import { useTranslation } from 'react-i18next'
import { useMemo } from 'react'

const useIconClassDropdownTranslations = () => {
  const { t } = useTranslation('iconclass')

  const filterValuesForDropdown = useMemo(
    () => [
      {
        value: 'iconclass.code',
        label: t('icc_code'),
        operation: 'START_WITH',
      },
      {
        value: 'iconclass.name',
        label: t('icc_name'),
        operation: 'FTXF',
      },
      { value: 'title', label: t('title'), operation: 'FTXF' },
      {
        value: 'identifier',
        label: t('identifier'),
        operation: 'CONTAINS',
      },
    ],
    [t]
  )

  return { filterValuesForDropdown }
}

export default useIconClassDropdownTranslations
