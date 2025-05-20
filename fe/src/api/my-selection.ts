import { useMutation, useQuery } from '@tanstack/react-query'
import { api, queryClient } from './index'

import {
  TSelectionDetail,
  TSelectionItemDetail,
} from '../../../fe-shared/@types/selection'

type TMySelectionInput = {
  illustrations: string[]
  books: string[]
}

export const useAddToMySelectionMutation = () =>
  useMutation({
    mutationFn: ({ illustrations, books }: TMySelectionInput) => {
      return api().post('selection/add-items', {
        json: {
          illustrations: illustrations.map((i) => ({ id: i })),
          books: books.map((b) => ({ id: b })),
        },
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-selection'] })
    },
  })

type TRemoveFromMySelectionInput = {
  items: string[]
}

export const useRemoveFromMySelectionMutation = () =>
  useMutation({
    mutationFn: ({ items }: TRemoveFromMySelectionInput) =>
      api().put('selection/delete-items', {
        json: {
          items: items.map((i) => ({ id: i })),
        },
      }),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-selection'] })
    },
  })

type TChangeViewInMiradorInput = {
  record: TSelectionItemDetail
}

export const useChangeViewInMiradorMutation = () =>
  useMutation({
    mutationFn: ({ record }: TChangeViewInMiradorInput) => {
      return api().put('selection/mirador', {
        json: { items: [record] },
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-selection'] })
    },
  })

export const useMySelectionQuery = (enabled = true) =>
  useQuery({
    queryKey: ['my-selection'],
    queryFn: () => api().get(`selection/mine`).json<TSelectionDetail | null>(),
    ...{ enabled },
  })
