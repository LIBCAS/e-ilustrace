import { FC, useState } from 'react'
import { useTranslation } from 'react-i18next'

import { toast } from 'react-toastify'
import { LoginPhase } from '../../../../fe-shared/@types/common'
import TextInput from '../reusableComponents/inputs/TextInput'
import { useChangePasswordMutation } from '../../api/user'
import Button from '../reusableComponents/Button'

type Props = {
  // loginPhase: LoginPhase;
  setLoginPhase: (loginPhase: LoginPhase) => void
}

const initialState = { oldPassword: '', newPassword: '' }

const PasswordChange: FC<Props> = ({ setLoginPhase }) => {
  const { t } = useTranslation()
  const [passwords, setPasswords] = useState<{
    oldPassword: string
    newPassword: string
  }>(initialState)

  const { mutateAsync: changePassword, status } = useChangePasswordMutation()

  const handleSubmit = () => {
    changePassword(passwords)
      .then(() => {
        toast.success(t('navigation:password_changed'))
        setPasswords(initialState)
        setLoginPhase('MENU')
      })
      .catch(() => {
        toast.error(t('common:error_occurred_somewhere'))
      })
  }

  return (
    <div className="flex h-full w-full flex-col items-start gap-8 p-8">
      <h2 className="text-2xl font-bold text-white">
        {t('navigation:password_change')}
      </h2>
      <TextInput
        type="password"
        id="old_password"
        placeholder={t('navigation:old_password')}
        className={`w-full outline-superlightgray ${
          passwords.oldPassword ? 'bg-white' : 'bg-opacity-30'
        } font-bold placeholder-white`}
        value={passwords.oldPassword}
        onChange={(newValue) =>
          setPasswords((prevState) => ({ ...prevState, oldPassword: newValue }))
        }
      />
      <TextInput
        type="password"
        id="new_password"
        placeholder={t('navigation:new_password')}
        className={`w-full outline-superlightgray ${
          passwords.newPassword ? 'bg-white' : 'bg-opacity-30'
        } font-bold placeholder-white`}
        value={passwords.newPassword}
        onChange={(newValue) =>
          setPasswords((prevState) => ({ ...prevState, newPassword: newValue }))
        }
      />
      <Button
        onClick={() => handleSubmit()}
        className="mx-auto w-full border-white bg-white text-center !text-black"
        disabled={
          status === 'pending' ||
          !passwords.oldPassword.length ||
          !passwords.newPassword.length
        }
      >
        {t('navigation:do_password_change')}
      </Button>
    </div>
  )
}

export default PasswordChange
