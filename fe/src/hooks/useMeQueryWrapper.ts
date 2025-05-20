import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { useTranslation } from 'react-i18next'
import { useMeQuery } from '../api/user'

const useMeQueryWrapper = () => {
  const { t } = useTranslation()
  const [wasLoggedIn, setWasLoggedIn] = useState<boolean | undefined>(undefined)
  const { data, isLoading, isError } = useMeQuery()

  useEffect(() => {
    if (isError) {
      toast.error(t('common:me_loading_error'), {
        toastId: 'meError',
      })
    }
  }, [isError, t])

  useEffect(() => {
    if (!data && wasLoggedIn) {
      toast.info(t('common:you_were_logged_out'), {
        toastId: 'logged_out',
      })
      setWasLoggedIn(false)
    }
  }, [data, t, wasLoggedIn])

  useEffect(() => {
    if (!isLoading && !isError && data) {
      setWasLoggedIn(true)
    }
  }, [data, isError, isLoading])

  return {
    me: data,
    meLoading: isLoading,
    meError: isError,
  }
}

export default useMeQueryWrapper
