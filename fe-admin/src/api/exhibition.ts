import { useMutation, useQuery } from '@tanstack/react-query'
import { api, queryClient } from './index'
import { TExhibitionDetail } from '../../../fe-shared/@types/exhibition'

type TChangeExhibitionVisibilityInput = {
  id: string
}

export const useChangeExhibitionVisibilityMutation = () =>
  useMutation({
    mutationFn: ({ id }: TChangeExhibitionVisibilityInput) =>
      api().put(`exhibition/${id}/publish`),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ['exhibition-detail', variables.id],
      })
    },
  })

type TDeleteExhibitionInput = {
  id: string
}

export const useDeleteExhibitionMutation = () =>
  useMutation({
    mutationFn: ({ id }: TDeleteExhibitionInput) =>
      api().delete(`exhibition/${id}`),
  })

export const useExhibitionDetailQuery = (id?: string) =>
  useQuery({
    queryKey: ['exhibition-detail', id],
    queryFn: () => api().get(`exhibition/${id}`).json<TExhibitionDetail>(),
    ...{
      enabled: !!id,
    },
  })
