import { useMutation, useQuery } from '@tanstack/react-query'
import { api, queryClient } from './index'
import { TIconClassCategoryDefault } from '../../../fe-shared/@types/iconClass'
import {
  TEnrichmentStates,
  TIllustrationDetail,
} from '../../../fe-shared/@types/illustration'

type TAddNewICCInput = {
  ICC: string
}

export const useAddNewICCMutation = () =>
  useMutation({
    mutationFn: ({ ICC }: TAddNewICCInput) =>
      api()
        .post(`iconclass`, {
          json: { code: ICC },
        })
        .json(),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['iconclass-list'] })
    },
  })

type TUpdateIconClassStateInput = {
  uuid: string
  state: TEnrichmentStates
}

export const useUpdateIconClassStateMutation = () =>
  useMutation({
    mutationFn: ({ uuid, state }: TUpdateIconClassStateInput) =>
      api()
        .put(`record/${uuid}/ill/iconclass-state`, {
          json: state,
        })
        .json(),

    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ['illustration-detail', variables.uuid],
      })
    },
  })

export const useIconClassListQuery = () =>
  useQuery({
    queryKey: ['iconclass-list'],
    queryFn: () =>
      api()
        .post(`iconclass/list`, {
          json: {
            size: -1,
          },
        })
        .json<{
          items: TIconClassCategoryDefault[]
          count: number
          searchAfter: string[]
        }>(),
  })

type TIconClassInput = {
  id: string
}

export const useIconClassQuery = ({ id }: TIconClassInput) =>
  useQuery({
    queryKey: ['iconclass', id],
    queryFn: () => api().get(`iconclass/${id}`).json<TIllustrationDetail>(),
  })
