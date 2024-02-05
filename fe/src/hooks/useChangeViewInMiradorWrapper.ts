import { useTranslation } from 'react-i18next'
import { toast } from 'react-toastify'
import useChangeViewInMirador from '../api/mutation/useChangeViewInMirador'
import useMeQueryWrapper from './useMeQueryWrapper'
import { TSelectionItemDetail } from '../../../fe-shared/@types/selection'

const useChangeViewInMiradorWrapper = () => {
  const { mutateAsync, status } = useChangeViewInMirador()
  const { me } = useMeQueryWrapper()
  const { t } = useTranslation()

  const handleChange = ({ record }: { record: TSelectionItemDetail }) => {
    if (me) {
      toast.promise(mutateAsync({ record }), {
        pending: t('common:pending_view_change_in_my_selection'),
        success: t('common:changed_view_in_my_selection_successfully'),
        error: t('common:error_occurred_somewhere'),
      })
    }
  }

  return {
    doViewChange: handleChange,
    viewChangeStatus: { status },
  }
}

export default useChangeViewInMiradorWrapper
