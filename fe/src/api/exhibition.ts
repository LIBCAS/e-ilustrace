import { useMutation, useQuery } from '@tanstack/react-query'
import { api, queryClient } from './index'
import { useNewExhibitionStore } from '../store/useNewExhibitionStore'
import {
  TExhibitionDetail,
  TExhibitionList,
} from '../../../fe-shared/@types/exhibition'

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
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['exhibition-list'] })
    },
  })

type TSaveMyExhibitionInput = {
  id?: string
}

export const useSaveMyExhibitionMutation = () => {
  const { name, description, radio, items } = useNewExhibitionStore()

  return useMutation({
    mutationFn: ({ id }: TSaveMyExhibitionInput) => {
      return id
        ? api()
            .put(`exhibition/${id}`, {
              json: {
                name,
                description,
                items: items.map((i) => ({
                  id: i.itemId,
                  description: i.description,
                  illustration: { id: i.id },
                  name: i.name,
                  year: i.year,
                  preface: i.preface,
                })),
                radio,
              },
            })
            .json<TExhibitionDetail>()
        : api()
            .post('exhibition', {
              json: {
                name,
                description,
                items: items.map((i) => ({
                  description: i.description,
                  illustration: { id: i.id },
                  name: i.name,
                  year: i.year,
                  preface: i.preface,
                })),
                radio,
              },
            })
            .json<TExhibitionDetail>()
    },
    onSuccess: (data, variables) => {
      if (variables.id) {
        queryClient.invalidateQueries({
          queryKey: ['exhibition-detail', variables.id],
        })
      }
      queryClient.invalidateQueries({ queryKey: ['exhibition-list'] })
    },
  })
}

export const useExhibitionDetailQuery = (id?: string) =>
  useQuery({
    queryKey: ['exhibition-detail', id],
    queryFn: () => api().get(`exhibition/${id}`).json<TExhibitionDetail>(),
    ...{
      enabled: !!id,
    },
  })

interface ExhibitionList {
  items: TExhibitionList[]
  count: number
  searchAfter: string[]
}

interface TExhibitionListProps {
  size: number
  page: number
}

export const useExhibitionListQuery = ({ size, page }: TExhibitionListProps) =>
  useQuery({
    queryKey: ['exhibition-list', size, page],
    queryFn: () => {
      return api()
        .post('exhibition/list', {
          json: {
            filters: [
              {
                field: 'published',
                operation: 'GTE',
                value: '0',
              },
            ],
            size,
            offset: size * page,
            sort: [
              {
                order: 'DESC',
                type: 'FIELD',
                field: 'created',
              },
            ],
          },
        })
        .json<ExhibitionList>()
    },
  })

export const useMineExhibitionListQuery = (enabled = true) =>
  useQuery({
    queryKey: ['exhibition-list', 'mine'],
    queryFn: () => {
      return api()
        .post('exhibition/list-mine', {
          json: {
            filters: [],
            size: -1,
            sort: [
              {
                order: 'DESC',
                type: 'FIELD',
                field: 'created',
              },
            ],
          },
        })
        .json<ExhibitionList>()
    },
    ...{ enabled },
  })
