import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'
import { TUserRole } from '../../../../fe-shared/@types/user'

type TInput = {
  userId: string
  role: TUserRole
}

const useChangeUserRoleMutation = () =>
  useMutation({
    mutationFn: ({ userId, role }: TInput) =>
      api()
        .put(`user/change-role`, {
          json: { userId, role },
        })
        .json<boolean>(),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['user-list'] })
    },
  })

export default useChangeUserRoleMutation
