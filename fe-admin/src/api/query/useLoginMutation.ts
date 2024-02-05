import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'

type TInput = { userName: string; password: string }

const useLoginMutation = () =>
  useMutation({
    mutationFn: ({ userName, password }: TInput) => {
      const formData = new FormData()
      formData.set('username', userName)
      formData.set('password', password)
      // formData.set('captcha', '1')

      return api().post('auth', { body: formData })
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['me'] })
    },
  })

export default useLoginMutation
