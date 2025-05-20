import { useMutation, useQuery } from '@tanstack/react-query'
import { api, queryClient } from './index'
import { TMe } from '../../../fe-shared/@types/user'

type TChangePasswordInput = { oldPassword: string; newPassword: string }

export const useChangePasswordMutation = () =>
  useMutation({
    mutationFn: ({ oldPassword, newPassword }: TChangePasswordInput) => {
      return api().put('user/change-pw', {
        json: {
          oldPassword,
          newPassword,
        },
      })
    },
  })

type TResetPasswordInput = { key: string; newPassword: string }

export const useResetPasswordMutation = () =>
  useMutation({
    mutationFn: ({ key, newPassword }: TResetPasswordInput) => {
      return api().post('user/reset-password', {
        json: {
          key,
          newPassword,
        },
      })
    },
  })

type TSendPasswordRecoveryEmailInput = { email: string }

export const useSendPasswordRecoveryEmailMutation = () =>
  useMutation({
    mutationFn: ({ email }: TSendPasswordRecoveryEmailInput) => {
      return api().post(`user/request-password-reset/${email}`)
    },
  })

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

type TRegistrationConfirmationInput = {
  key: string | null
}

export const useRegistrationConfirmationMutation = () =>
  useMutation({
    mutationFn: ({ key }: TRegistrationConfirmationInput) =>
      api().post(`user/confirm-reg/${key}`).json<boolean>(),
  })

type TRegistrationInput = {
  email: string
  firstName: string
  lastName: string
  password: string
}

export const useRegistrationMutation = () =>
  useMutation({
    mutationFn: ({
      email,
      firstName,
      lastName,
      password,
    }: TRegistrationInput) =>
      api().post('user', {
        json: {
          email,
          firstName,
          lastName,
          password,
        },
      }),
  })

export const useMeQuery = () =>
  useQuery({
    queryKey: ['me'],
    queryFn: () => api().get('internal/me').json<TMe>(),
  })
