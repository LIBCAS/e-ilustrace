import { FC } from 'react'
import { PhotoIcon } from '@heroicons/react/24/outline'
import { useTranslation } from 'react-i18next'
import clone from 'lodash/clone'
import PlusIcon from '../../assets/icons/plus.svg?react'
import DeleteIcon from '../../assets/icons/delete.svg?react'
import { useNewExhibitionStore } from '../../store/useNewExhibitionStore'
import { TIllustrationEssential } from '../../../../fe-shared/@types/selection'
import { TIllustrationList } from '../../../../fe-shared/@types/illustration'

const BlankImage = ({ classNames }: { classNames: string }) => {
  return <PhotoIcon className={`text-lightgray ${classNames}`} />
}

type TInput = {
  illustrations: TIllustrationEssential[] | TIllustrationList[]
}

const ListOfSelectableIllustrations: FC<TInput> = ({ illustrations }) => {
  const { t } = useTranslation()
  const { items: selectedIllustrations, setItems: setSelectedIllustrations } =
    useNewExhibitionStore()

  const handleAddition = (id: string) => {
    setSelectedIllustrations([
      ...selectedIllustrations,
      { id, description: '', name: '', year: '', preface: false },
    ])
  }

  const handleDeletion = (id: string) => {
    const index = selectedIllustrations.findIndex((i) => i.id === id)
    if (index >= 0) {
      const illClone = clone(selectedIllustrations)
      illClone.splice(index, 1)
      setSelectedIllustrations(illClone)
    }
  }

  return (
    <div className="my-6 flex h-full max-h-[350px] w-full flex-col overflow-y-scroll px-6">
      {illustrations.map((i) => (
        <div
          key={`selectable-ill-${i.id}`}
          className="my-3 flex h-16 items-center justify-start gap-2 border-b-[1.5px] border-superlightgray pb-5"
        >
          <div className="mr-4 h-16 w-[100px] shrink-0">
            {i.illustrationScan ? (
              <img
                className="h-full max-w-full"
                src={`/api/eil/files/${i.illustrationScan.id}`}
                alt={i.title}
              />
            ) : null}
            {i.pageScan && !i.illustrationScan ? (
              <img
                className="h-full max-w-full"
                src={`/api/eil/files/${i.pageScan.id}`}
                alt={i.title}
              />
            ) : null}
            {!i.illustrationScan && !i.pageScan ? (
              <BlankImage classNames="h-full max-w-full" />
            ) : null}
          </div>
          <div>
            <span className="font-semibold">{i.title}</span>
            {i.yearFrom ? (
              <span className="ml-2 text-gray">
                {t('exhibitions:year_title')} {i.yearFrom}
              </span>
            ) : null}
          </div>
          {selectedIllustrations.find((si) => si.id === i.id) ? (
            <button
              onClick={() => handleDeletion(i.id)}
              className="ml-auto flex w-fit shrink-0 items-center gap-4 font-semibold"
              type="button"
            >
              <DeleteIcon className="text-red" />
              {t('exhibitions:remove_from_exhibition')}
            </button>
          ) : (
            <button
              onClick={() => handleAddition(i.id)}
              className="ml-auto flex w-fit shrink-0 items-center gap-4 font-semibold"
              type="button"
            >
              <PlusIcon className="text-red" />
              {t('exhibitions:add_to_exhibition')}
            </button>
          )}
        </div>
      ))}
    </div>
  )
}

export default ListOfSelectableIllustrations
