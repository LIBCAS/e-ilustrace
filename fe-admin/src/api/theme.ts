import { useMutation, useQuery } from '@tanstack/react-query'
import { api, queryClient } from './index'
import { TEnrichmentStates } from '../../../fe-shared/@types/illustration'
import { TTheme } from '../../../fe-shared/@types/theme'

type TAddNewThemeInput = {
  theme: string
}

export const useAddNewThemeMutation = () =>
  useMutation({
    mutationFn: ({ theme }: TAddNewThemeInput) =>
      api()
        .post(`theme`, {
          json: { name: theme },
        })
        .json(),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['theme-list'] })
    },
  })

type TUpdateThemeStateInput = {
  uuid: string
  state: TEnrichmentStates
}

export const useUpdateThemeStateMutation = () =>
  useMutation({
    mutationFn: ({ uuid, state }: TUpdateThemeStateInput) =>
      api()
        .put(`record/${uuid}/ill/theme-state`, {
          json: state,
        })
        .json(),

    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ['illustration-detail', variables.uuid],
      })
    },
  })

export const useThemeListQuery = () =>
  useQuery({
    queryKey: ['theme-list'],
    queryFn: () =>
      api()
        .post(`theme/list`, {
          json: {
            size: -1,
          },
        })
        .json<{
          items: TTheme[]
          count: number
          searchAfter: string[]
        }>(),
  })
