import { useNavigate, useParams } from 'react-router-dom'
import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { toast } from 'react-toastify'
import { useResetPasswordMutation } from '../api/user'
import TextInput from '../components/reusableComponents/inputs/TextInput'
import Button from '../components/reusableComponents/Button'

const RegistrationConfirmation = () => {
  const { t } = useTranslation()
  const params = useParams()
  const navigate = useNavigate()
  const [newPassword, setNewPassword] = useState('')
  const { mutateAsync, status } = useResetPasswordMutation()

  const handleSubmit = () => {
    mutateAsync({ key: params.token || '', newPassword })
      .then(() => {
        toast.success(t('navigation:password_changed'))
        navigate('/')
      })
      .catch(() => {
        toast.error(t('common:error_occurred_somewhere'))
      })
  }

  return (
    <div className="mt-20 flex items-center justify-center border-y border-superlightgray py-20">
      <div className="mx-auto flex max-w-[350px] flex-col gap-6">
        <h1 className="text-center text-3xl font-bold md:text-4xl">
          {t('navigation:password_reset')}
        </h1>
        <TextInput
          id="new-password"
          placeholder={t('navigation:new_password')}
          value={newPassword}
          onChange={(newValue) => setNewPassword(newValue)}
        />
        <Button
          onClick={() => handleSubmit()}
          className="mx-auto w-full"
          disabled={
            status === 'pending' || !newPassword.length || !params.token?.length
          }
        >
          {t('navigation:do_password_change')}
        </Button>
      </div>
    </div>
  )
}

export default RegistrationConfirmation
