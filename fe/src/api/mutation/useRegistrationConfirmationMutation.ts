import { useMutation } from '@tanstack/react-query'
import { api } from '../index'

type TInput = {
  key: string | null
}

const useRegistrationMutation = () =>
  useMutation({
    mutationFn: ({ key }: TInput) =>
      api().post(`user/confirm-reg/${key}`).json<boolean>(),
  })

export default useRegistrationMutation
