import { useMutation } from '@tanstack/react-query'
import { api, queryClient } from '../index'
import { TIllustrationDetail } from '../../../../fe-shared/@types/illustration'
import { TBookDetail } from '../../../../fe-shared/@types/book'

type TInput = { file: File }
const useUploadMutation = () =>
  useMutation({
    mutationFn: ({ file }: TInput) => {
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

export default useUploadMutation
