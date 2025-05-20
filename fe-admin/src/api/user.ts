import { useMutation, useQuery } from '@tanstack/react-query'
import { api, queryClient } from './index'
import { TMe, TUserList, TUserRole } from '../../../fe-shared/@types/user'
import { TFilter } from '../../../fe-shared/@types/common'

type TLoginInput = { userName: string; password: string }

export const useLoginMutation = () =>
  useMutation({
    mutationFn: ({ userName, password }: TLoginInput) => {
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

export const useLogoutMutation = () =>
  useMutation({
    mutationFn: () => api().post('logout'),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['me'] })
    },
  })

type TChangeUserRoleInput = {
  userId: string
  role: TUserRole
}

export const useChangeUserRoleMutation = () =>
  useMutation({
    mutationFn: ({ userId, role }: TChangeUserRoleInput) =>
      api()
        .put(`user/change-role`, {
          json: { userId, role },
        })
        .json<boolean>(),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['user-list'] })
    },
  })

export const useMeQuery = () =>
  useQuery({
    queryKey: ['me'],
    queryFn: () => api().get('internal/me').json<TMe>(),
  })

interface Users {
  items: TUserList[]
  count: number
  searchAfter: string[]
}

export const useUserListQuery = (search: string) => {
  const filters: {
    field?: string
    operation: string
    value?: string | number
    filters?: TFilter[]
  }[] = []

  if (search.length > 0) {
    filters.push({
      operation: 'OR',
      filters: [
        {
          field: 'fullName',
          operation: 'FTXF',
          value: search.trim(),
        },
        {
          field: 'email',
          operation: 'CONTAINS',
          value: search.trim(),
        },
      ],
    })
  }

  return useQuery({
    queryKey: ['user-list', search],
    queryFn: () =>
      api()
        .post('user/list', {
          json: {
            size: -1,
            filters,
          },
        })
        .json<Users>(),
  })
}

export const useDeleteUserMutation = () =>
  useMutation({
    mutationFn: (id: string) => api().delete(`user/${id}`).json(),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['user-list'] })
    },
  })
