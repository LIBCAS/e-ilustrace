import { useTranslation } from 'react-i18next'
import { toast } from 'react-toastify'
import useAddToMySelectionMutation from '../api/mutation/useAddToMySelectionMutation'
import useMeQueryWrapper from './useMeQueryWrapper'

const useAddToMySelectionMutationWrapper = () => {
  const { mutateAsync, status } = useAddToMySelectionMutation()
  const { me } = useMeQueryWrapper()
  const { t } = useTranslation()

  const handleAddition = ({
    illustrations,
    books,
  }: {
    illustrations: string[]
    books: string[]
  }) => {
    if (me) {
      toast.promise(mutateAsync({ illustrations, books }), {
        pending: t('common:pending_adding_to_my_selection'),
        success: t('common:added_to_my_selection_successfully'),
        error: t('common:error_occurred_somewhere'),
      })
    }
  }

  return {
    doAdd: handleAddition,
    addingStatus: status,
  }
}

export default useAddToMySelectionMutationWrapper
