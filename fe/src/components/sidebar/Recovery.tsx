import { FC, useState } from 'react'
import { useTranslation } from 'react-i18next'

import LoginButton from '../reusableComponents/LoginButton'
import { LoginPhase } from '../../@types/types'
import TextInput from '../reusableComponents/inputs/TextInput'

type Props = {
  // loginPhase: LoginPhase;
  setLoginPhase: (loginPhase: LoginPhase) => void
}

const Recovery: FC<Props> = ({ setLoginPhase }) => {
  const { t } = useTranslation('navigation')
  const [email, setEmail] = useState('')

  const handleSubmit = () => {
    setLoginPhase('RECOVERED')
    setEmail('')
  }

  return (
    <div className="flex h-full w-full flex-col items-start gap-8 p-8">
      <h2 className="text-2xl font-bold text-white">
        {t('password_recovery')}
      </h2>
      <TextInput
        id="recovery_email"
        placeholder={t('recovery_email')}
        className={`w-full outline-superlightgray ${
          email ? 'bg-white ' : 'bg-opacity-30'
        } font-bold placeholder-white`}
        onChange={(newValue) => setEmail(newValue)}
      />
      <LoginButton onClick={() => handleSubmit()}>
        {t('recovery_link')}
      </LoginButton>
    </div>
  )
}

export default Recovery
