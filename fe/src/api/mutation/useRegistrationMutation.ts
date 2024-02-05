import { useMutation } from '@tanstack/react-query'
import { api } from '../index'

type TInput = {
  email: string
  firstName: string
  lastName: string
  password: string
}

const useRegistrationMutation = () =>
  useMutation({
    mutationFn: ({ email, firstName, lastName, password }: TInput) =>
      api().post('user', {
        json: {
          email,
          firstName,
          lastName,
          password,
        },
      }),
  })

export default useRegistrationMutation
