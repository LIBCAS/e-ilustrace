import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { toast } from 'react-toastify'
import { Navigate } from 'react-router-dom'
import TextInput from '../components/reusableComponents/inputs/TextInput'
import Button from '../components/reusableComponents/Button'
import useLoginMutation from '../api/query/useLoginMutation'
import useMeQueryWrapper from '../hooks/useMeQueryWrapper'
import Loader from '../components/reusableComponents/Loader'

const Login = () => {
  const { t } = useTranslation()
  const [fields, setFields] = useState({
    email: '',
    password: '',
  })

  const { mutateAsync: doLogin, status } = useLoginMutation()
  const { me, meLoading } = useMeQueryWrapper()

  const handleSubmit = async () => {
    const response = await doLogin({
      userName: fields.email,
      password: fields.password,
    })

    if (
      response.redirected &&
      response.ok &&
      !response.url.includes('login?error')
    ) {
      toast.success(t('common.logged_successfully'))
    } else if (!response.ok && response.status === 503) {
      toast.error(t('common.error_occurred_somewhere'))
    } else {
      toast.error('common.bad_credentials')
    }
  }

  if (meLoading) {
    return (
      <div className="my-16 flex items-center justify-center">
        <Loader />
      </div>
    )
  }

  if (me) {
    return <Navigate to="/" />
  }

  return (
    <div className="min-h-[92vh] border-t border-superlightgray bg-white py-5">
      <div className="container">
        <div className="mx-auto mt-20 flex w-[300px] flex-col gap-5">
          <h1 className="text-center">{t('login.header')}</h1>
          <TextInput
            value={fields.email}
            placeholder="name@email.com"
            label={t('login.email')}
            id="email"
            onChange={(value) =>
              setFields((prevState) => ({ ...prevState, email: value }))
            }
          />
          <TextInput
            value={fields.password}
            placeholder="*********"
            label={t('login.password')}
            id="password"
            onChange={(value) =>
              setFields((prevState) => ({ ...prevState, password: value }))
            }
            type="password"
          />
          <Button
            variant="submit"
            disabled={!(fields.email.length && fields.password.length)}
            isLoading={status === 'pending'}
            onClick={() => handleSubmit()}
          >
            <span>{t('login.do_login')}</span>
          </Button>
        </div>
      </div>
    </div>
  )
}

export default Login
