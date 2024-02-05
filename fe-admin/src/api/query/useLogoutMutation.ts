import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'

const useLogoutMutation = () =>
  useMutation({
    mutationFn: () => api().post('logout'),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['me'] })
    },
  })

export default useLogoutMutation
