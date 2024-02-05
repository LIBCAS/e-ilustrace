import { useMutation } from '@tanstack/react-query'
import { api } from '../index'

type TInput = { password: string }

const useChangePasswordMutation = () =>
  useMutation({
    mutationFn: ({ password }: TInput) => {
      return api().put('user/change-pw', {
        json: {
          password,
        },
      })
    },
  })

export default useChangePasswordMutation
