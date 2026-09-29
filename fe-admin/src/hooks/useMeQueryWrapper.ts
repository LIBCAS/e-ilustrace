import { useEffect } from 'react'
import { toast } from 'react-toastify'
import { useTranslation } from 'react-i18next'
import { useMeQuery } from '../api/user'

const useMeQueryWrapper = () => {
  const { t } = useTranslation()
  const { data, isLoading, isError } = useMeQuery()

  useEffect(() => {
    if (isError) {
      toast.error(t('common.me_loading_error'))
    }
  }, [isError, t])

  return {
    me: data,
    meLoading: isLoading,
    meError: isError,
  }
}

export default useMeQueryWrapper
