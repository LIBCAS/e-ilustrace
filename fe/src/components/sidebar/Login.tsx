import { FC, useState } from 'react'
import { useTranslation } from 'react-i18next'

import { toast } from 'react-toastify'
import { LoginPhase } from '../../@types/types'
import TextInput from '../reusableComponents/inputs/TextInput'
import Button from '../reusableComponents/Button'
import useLoginMutation from '../../api/mutation/useLoginMutation'

type Props = {
  setLoginPhase: (loginPhase: LoginPhase) => void
}

const Login: FC<Props> = ({ setLoginPhase }) => {
  const { t } = useTranslation()
  const [fields, setFields] = useState({ email: '', password: '' })
  const { mutateAsync: doLogin, status } = useLoginMutation()

  const handleLogin = async () => {
    const response = await doLogin({
      userName: fields.email,
      password: fields.password,
    })

    if (
      response.redirected &&
      response.ok &&
      !response.url.includes('login?error')
    ) {
      toast.success(t('common:logged_successfully'))
      setLoginPhase('MENU')
    } else if (!response.ok && response.status === 503) {
      toast.error(t('common:error_occurred_somewhere'))
    } else {
      toast.error('common:bad_credentials')
    }
  }

  return (
    // <Form onSubmit={(values) => console.log(values)}>
    <div className="mt-8 flex w-full flex-col items-start gap-8 p-8">
      <TextInput
        id="email"
        color="red"
        value={fields.email}
        placeholder={t('navigation:email')}
        onChange={(newValue) =>
          setFields((prevState) => ({ ...prevState, email: newValue }))
        }
      />
      <TextInput
        id="password"
        color="red"
        value={fields.password}
        placeholder={t('navigation:password')}
        onChange={(newValue) =>
          setFields((prevState) => ({ ...prevState, password: newValue }))
        }
        type="password"
      />

      {/* <Field */}
      {/*  name="loginEmail" */}
      {/*  render={({ input }) => ( */}
      {/*    <Input */}
      {/*      {...input} */}
      {/*      id="loginEmail" */}
      {/*      placeholder={t('email')} */}
      {/*      value={input.value} */}
      {/*      className={`w-full outline-superlightgray transition ${ */}
      {/*        input.value ? 'bg-white ' : 'bg-opacity-30' */}
      {/*      } font-bold placeholder-white`} */}
      {/*    /> */}
      {/*  )} */}
      {/* /> */}
      {/* <Field */}
      {/*  name="loginPassword" */}
      {/*  render={({ input }) => ( */}
      {/*    <Input */}
      {/*      {...input} */}
      {/*      password */}
      {/*      id="loginPassword" */}
      {/*      type="password" */}
      {/*      placeholder={t('password')} */}
      {/*      value={input.value} */}
      {/*      className={`w-full outline-superlightgray transition ${ */}
      {/*        input.value ? 'bg-white ' : 'bg-opacity-30' */}
      {/*      } font-bold placeholder-white`} */}
      {/*    /> */}
      {/*  )} */}
      {/* /> */}
      {/* <Checkbox */}
      {/*  id="remember_login" */}
      {/*  name={t('remember_login')} */}
      {/*  className="text-white" */}
      {/*  showName */}
      {/* /> */}
      <Button
        className="w-full border-white bg-white text-center !text-black"
        disabled={status === 'pending'}
        onClick={() => handleLogin()}
      >
        {t('navigation:login')}
      </Button>
      <button
        type="button"
        className="mx-auto cursor-pointer font-bold text-white underline"
        onClick={() => setLoginPhase('RECOVERY')}
      >
        {t('navigation:forgot_password')}
      </button>
    </div>
    // </Form>
  )
}

export default Login
