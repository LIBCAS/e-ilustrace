import { FC } from 'react'
import { useTranslation } from 'react-i18next'
import { FaceFrownIcon } from '@heroicons/react/24/outline'

type TShowErrorProps = {
  error?: string
}

const ShowError: FC<TShowErrorProps> = ({ error = undefined }) => {
  const { t } = useTranslation()

  return (
    <div className="my-16 text-center">
      <FaceFrownIcon className="mx-auto mb-2 h-16" />
      <span>{error || t('common.error_occurred_when_loading_data')}</span>
    </div>
  )
}

export default ShowError
