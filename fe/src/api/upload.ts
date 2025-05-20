import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from './index'
import { TIllustrationDetail } from '../../../fe-shared/@types/illustration'
import { TBookDetail } from '../../../fe-shared/@types/book'

type TUploadInput = { file: File }
// eslint-disable-next-line import/prefer-default-export
export const useUploadMutation = () =>
  useMutation({
    mutationFn: ({ file }: TUploadInput) => {
      const formData = new FormData()
      formData.append('file', file)
      return api()
        .post(`import/${file.name}`, {
          body: formData,
        })
        .json<TIllustrationDetail | TBookDetail>()
    },
    onSuccess: () => {
      queryClient.refetchQueries({ queryKey: ['record-detail', 'record-list'] })
    },
  })
