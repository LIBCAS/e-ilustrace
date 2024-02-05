import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { FC } from 'react'
import Loader from '../reusableComponents/Loader'
import ShowError from '../reusableComponents/ShowError'
import ListOfSelectableIllustrations from './ListOfSelectableIllustrations'
import useMySelectionQuery from '../../api/query/useMySelectionQuery'
import Button from '../reusableComponents/Button'
import CloseIcon from '../../assets/icons/close.svg?react'
import { TIllustrationEssential } from '../../../../fe-shared/@types/selection'

// import ActionButtons from './ActionButtons'

type TProps = {
  close: () => void
}

const FromMySelection: FC<TProps> = ({ close }) => {
  const { t } = useTranslation('exhibitions')
  const { data, isFetching, isError } = useMySelectionQuery()

  return (
    <div className="mt-12 flex w-full flex-col border-t border-t-superlightgray py-8">
      <div className="flex items-center justify-between">
        <h2 className="my-4 text-xl font-bold">{t('add_from_selection')}</h2>
        {/* <ActionButtons /> */}
        <Button
          iconButton
          variant="text"
          className="self-end justify-self-end border-none bg-white font-bold uppercase text-black hover:text-black hover:shadow-none"
          onClick={() => close()}
        >
          <CloseIcon />
        </Button>
      </div>
      <div>
        {isFetching ? (
          <div className="my-10 flex items-center justify-center">
            <Loader />
          </div>
        ) : null}
        {!isFetching && isError ? <ShowError /> : null}
        {!isFetching && !isError && !data ? (
          <p className="text-gray">
            {t('empty_selection')}{' '}
            <span className="text-red underline">
              <Link to="/explore">{t('go_to_artwork_search')}</Link>
            </span>
            {t('objects_and_more')}
          </p>
        ) : null}
        {!isFetching && !isError && data ? (
          <ListOfSelectableIllustrations
            illustrations={
              (data?.items
                ?.filter((i) => i.illustration?.id)
                .map((i) => i.illustration) as TIllustrationEssential[]) || []
            }
          />
        ) : null}
      </div>
    </div>
  )
}

export default FromMySelection
