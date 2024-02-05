import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'
import { useNewExhibitionStore } from '../../store/useNewExhibitionStore'
import { TExhibitionDetail } from '../../../../fe-shared/@types/exhibition'

type TInput = {
  id?: string
}

const useSaveMyExhibitionMutation = () => {
  const { name, description, radio, items } = useNewExhibitionStore()

  return useMutation({
    mutationFn: ({ id }: TInput) => {
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
export default useSaveMyExhibitionMutation
