import { useSearchParams } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import useRegistrationMutation from '../api/mutation/useRegistrationConfirmationMutation'
import Loader from '../components/reusableComponents/Loader'
import ShowError from '../components/reusableComponents/ShowError'
import ShowInfoMessage from '../components/reusableComponents/ShowInfoMessage'

const RegistrationConfirmation = () => {
  const { t } = useTranslation()
  const [searchParams] = useSearchParams()
  const { mutateAsync } = useRegistrationMutation()
  const [status, setStatus] = useState<'pending' | 'done' | 'error'>('pending')

  useEffect(() => {
    mutateAsync({ key: searchParams.get('key') })
      .then((data) => {
        if (data) {
          setStatus('done')
        } else {
          setStatus('error')
        }
      })
      .catch(() => {
        setStatus('error')
      })
  }, [mutateAsync, searchParams])

  return (
    <div className="mt-20 flex items-center justify-center border-y border-superlightgray py-20">
      {status === 'pending' ? <Loader /> : null}
      {status === 'error' ? (
        <ShowError error={t('common:error_occurred_somewhere')} />
      ) : null}
      {status === 'done' ? (
        <ShowInfoMessage
          message={t('navigation:registration_activation_successful')}
        />
      ) : null}
    </div>
  )
}

export default RegistrationConfirmation
