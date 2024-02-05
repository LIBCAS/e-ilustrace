import React from 'react'
import { useTranslation } from 'react-i18next'

const Recovered = () => {
  const { t } = useTranslation('navigation')

  return (
    <div className="flex h-full w-full flex-col items-start gap-8 p-8">
      <h2 className="text-2xl font-bold text-white"> {t('recovered')}</h2>
      <p className="text-white">{t('recovered_text')}</p>
    </div>
  )
}

export default Recovered
