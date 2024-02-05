import { useTranslation } from 'react-i18next'
import { Dispatch, FC, SetStateAction } from 'react'
import Button from './Button'
import useMeQueryWrapper from '../../hooks/useMeQueryWrapper'
import BookMark from '../../assets/icons/bookmark.svg?react'

type TSelectionDialogButtonProps = {
  setShowDialog: Dispatch<SetStateAction<boolean>>
}

const SelectionDialogButton: FC<TSelectionDialogButtonProps> = ({
  setShowDialog,
}) => {
  const { me } = useMeQueryWrapper()
  const { t } = useTranslation('search')

  return (
    <Button
      disabled={!me}
      className="fixed bottom-5 right-5 z-10 rounded-3xl shadow-[0px_7px_30px_-5px_rgba(0,0,0,0.75)] hover:shadow-[0px_7px_30px_-5px_rgba(0,0,0,0.75)] md:bottom-10 md:right-10 xl:bottom-16 xl:right-16"
      startIcon={<BookMark />}
      onClick={() => {
        setShowDialog(true)
      }}
    >
      {t('dialog:my_selection')}
    </Button>
  )
}

export default SelectionDialogButton
