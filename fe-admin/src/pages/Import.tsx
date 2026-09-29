import { useTranslation } from 'react-i18next'
import { toast } from 'react-toastify'
import Button from '../components/reusableComponents/Button'
import { useImportMutation } from '../api/import'

const Import = () => {
  const { t } = useTranslation()
  const { mutateAsync, status } = useImportMutation()

  const handleImport = () => {
    mutateAsync()
      .then(() => {
        toast.success(t('import.success'))
      })
      .catch(() => {})
  }

  return (
    <div className="min-h-[92vh] border-t border-superlightgray bg-white py-5">
      <div className="container">
        <h2>{t('import.header')}</h2>
        <Button
          className="mt-10"
          isLoading={status === 'pending'}
          onClick={() => handleImport()}
          disabled={status === 'pending'}
        >
          {t('import.run')}
        </Button>
      </div>
    </div>
  )
}

export default Import
