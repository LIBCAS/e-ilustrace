import { FC, useState } from 'react'
import { useTranslation } from 'react-i18next'

import { toast } from 'react-toastify'
import { z } from 'zod'
import { LoginPhase } from '../../../../fe-shared/@types/common'
import TextInput from '../reusableComponents/inputs/TextInput'
import { useSendPasswordRecoveryEmailMutation } from '../../api/user'
import Button from '../reusableComponents/Button'

type Props = {
  // loginPhase: LoginPhase;
  setLoginPhase: (loginPhase: LoginPhase) => void
}

const Recovery: FC<Props> = ({ setLoginPhase }) => {
  const { t } = useTranslation()
  const [email, setEmail] = useState('')

  const { mutateAsync: sendEmail, status } =
    useSendPasswordRecoveryEmailMutation()

  const handleSubmit = () => {
    sendEmail({ email })
      .then(() => {
        setLoginPhase('RECOVERED')
        setEmail('')
      })
      .catch(() => {
        toast.error(t('common:error_occurred_somewhere'))
      })
  }

  return (
    <div className="flex h-full w-full flex-col items-start gap-8 p-8">
      <h2 className="text-2xl font-bold text-white">
        {t('navigation:password_recovery')}
      </h2>
      <TextInput
        id="recovery_email"
        placeholder={t('navigation:recovery_email')}
        className={`w-full outline-superlightgray ${
          email ? 'bg-white' : 'bg-opacity-30'
        } font-bold placeholder-white`}
        value={email}
        onChange={(newValue) => setEmail(newValue)}
      />
      <Button
        onClick={() => handleSubmit()}
        className="mx-auto w-full border-white bg-white text-center !text-black"
        disabled={
          status === 'pending' || !z.string().email().safeParse(email).success
        }
      >
        {t('navigation:recovery_link')}
      </Button>
    </div>
  )
}

export default Recovery
