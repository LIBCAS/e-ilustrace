import { useTranslation } from 'react-i18next'
import { toast } from 'react-toastify'
import useMeQueryWrapper from './useMeQueryWrapper'
import useRemoveFromMySelectionMutation from '../api/mutation/useRemoveFromMySelectionMutation'

const useRemoveFromMySelectionMutationWrapper = () => {
  const { mutateAsync, status } = useRemoveFromMySelectionMutation()
  const { me } = useMeQueryWrapper()
  const { t } = useTranslation()

  const handleDeletion = ({ items }: { items: string[] }) => {
    if (me) {
      toast.promise(mutateAsync({ items }), {
        pending: t('common:pending_remove_from_my_selection'),
        success: t('common:removed_from_my_selection_successfully'),
        error: t('common:error_occurred_somewhere'),
      })
    }
  }

  return {
    doRemove: handleDeletion,
    removingStatus: status,
  }
}

export default useRemoveFromMySelectionMutationWrapper
