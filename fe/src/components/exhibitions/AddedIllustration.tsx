import { useTranslation } from 'react-i18next'
import { FC } from 'react'
import { PhotoIcon } from '@heroicons/react/24/outline'
import clone from 'lodash/clone'
import Switch from 'react-switch'
import { Link } from 'react-router-dom'
import InfoIcon from '../../assets/icons/info.svg?react'
import Delete from '../../assets/icons/delete.svg?react'
import DownArrow from '../../assets/icons/down.svg?react'
import UpArrow from '../../assets/icons/up.svg?react'
import Button from '../reusableComponents/Button'
import WYSIWYGEditor from '../reusableComponents/inputs/WYSIWYGEditor'
import {
  TIllustration,
  useNewExhibitionStore,
} from '../../store/useNewExhibitionStore'
import { useRecordQuery } from '../../api/record'
import Loader from '../reusableComponents/Loader'
import ShowError from '../reusableComponents/ShowError'
import ShowInfoMessage from '../reusableComponents/ShowInfoMessage'
import TextInput from '../reusableComponents/inputs/TextInput'
import constructRecordDetailUrl from '../../utils/constructRecordDetailUrl'

const BlankImage = ({ classNames }: { classNames: string }) => {
  return <PhotoIcon className={`text-lightgray ${classNames}`} />
}

type TProps = {
  addedIllustration: TIllustration
  index: number
}
const AddedIllustration: FC<TProps> = ({ addedIllustration, index }) => {
  const { t } = useTranslation()

  const {
    data: illustrationFromBE,
    isLoading,
    isError,
  } = useRecordQuery({ id: addedIllustration.id })

  const { name, items, setItems } = useNewExhibitionStore()

  const handleNameChange = (newName: string) => {
    const illClone = clone(items)
    illClone[index].name = newName
    setItems(illClone)
  }

  const handleYearChange = (newYear: string) => {
    const illClone = clone(items)
    // illClone[index].year = newYear.replace(/\D/g, '')
    illClone[index].year = newYear
    setItems(illClone)
  }

  const handleDescChange = (newDesc: string) => {
    const illClone = clone(items)
    illClone[index].description = newDesc
    setItems(illClone)
  }

  const handleDeletion = () => {
    const illClone = clone(items)
    illClone.splice(index, 1)
    setItems(illClone)
  }

  const handleMoveUp = () => {
    const illClone = clone(items)
    ;[illClone[index - 1], illClone[index]] = [
      illClone[index],
      illClone[index - 1],
    ]
    setItems(illClone)
  }

  const handleMoveDown = () => {
    const illClone = clone(items)
    ;[illClone[index], illClone[index + 1]] = [
      illClone[index + 1],
      illClone[index],
    ]
    setItems(illClone)
  }

  const handlePrefaceChange = () => {
    const illClone = clone(items)
    setItems(
      illClone.map((i) => {
        if (i.id === addedIllustration.id) {
          return { ...i, preface: !addedIllustration.preface }
        }
        return { ...i, preface: false }
      })
    )
  }

  if (isLoading) {
    return (
      <div className="my-5 flex items-center justify-center">
        <Loader />
      </div>
    )
  }

  if (isError) {
    return <ShowError />
  }

  if (!illustrationFromBE) {
    return <ShowInfoMessage message={t('common:illustration_not_found')} />
  }

  return (
    <div className="mt-12 flex w-full flex-col border-t border-t-superlightgray py-8">
      <div className="ml-auto">
        <div className="flex gap-1">
          <div className="mr-2 flex items-center gap-2">
            <Switch
              checked={addedIllustration.preface}
              onChange={() => handlePrefaceChange()}
              height={22}
              width={51}
            />
            <span>{t('exhibitions:default_illustration')}</span>
          </div>
          <Button
            className="flex h-10 w-10 items-center justify-center px-2 py-2"
            onClick={() => handleDeletion()}
          >
            <Delete />
          </Button>
          <Button
            variant="text"
            disabled={index === 0 || items.length === 1}
            className="flex h-10 w-10 items-center justify-center border-lightgray bg-lightgray px-2 py-2 text-gray"
            onClick={() => handleMoveUp()}
          >
            <UpArrow />
          </Button>
          <Button
            variant="text"
            disabled={index === items.length - 1 || items.length === 1}
            className="flex h-10 w-10 items-baseline justify-center border-lightgray bg-lightgray px-2 py-2 text-gray"
            onClick={() => handleMoveDown()}
          >
            <DownArrow />
          </Button>
        </div>
      </div>

      <div className="flex flex-row gap-8">
        <div className="basis-1/5">
          {illustrationFromBE.type === 'ILLUSTRATION' &&
          illustrationFromBE.illustrationScan ? (
            <img
              className="justify-self-start rounded-xl transition-all duration-300"
              src={`/api/eil/files/${illustrationFromBE.illustrationScan.id}`}
              alt={illustrationFromBE.title}
            />
          ) : null}
          {illustrationFromBE.type === 'ILLUSTRATION' &&
          illustrationFromBE.pageScan &&
          !illustrationFromBE.illustrationScan ? (
            <img
              className="justify-self-start rounded-xl transition-all duration-300"
              src={`/api/eil/files/${illustrationFromBE.pageScan.id}`}
              alt={illustrationFromBE.title}
            />
          ) : null}
          {illustrationFromBE.type === 'ILLUSTRATION' &&
          !illustrationFromBE.illustrationScan &&
          !illustrationFromBE.pageScan ? (
            <BlankImage classNames="justify-self-start rounded-xl transition-all duration-300" />
          ) : null}
          <p className="mt-2">
            <Link
              target="_blank"
              to={constructRecordDetailUrl(illustrationFromBE.id)}
              className="font-bold text-black hover:text-red"
            >
              {illustrationFromBE.title}
            </Link>
            <span className="text-gray">
              {' | '}
              {illustrationFromBE.yearFrom} - {illustrationFromBE.yearTo}
            </span>
          </p>
          <span className="block text-sm text-gray">
            {illustrationFromBE.mainAuthor?.author.fullName}
          </span>
          <span className="block text-sm text-gray">
            {illustrationFromBE.identifier}
          </span>
        </div>
        <div className="flex basis-4/5 flex-col">
          <div className="flex gap-4">
            <div className="flex basis-2/3 flex-col">
              <TextInput
                id={`${name}.headline`}
                value={addedIllustration.name}
                className="bg-opacity-50 outline-black"
                label={t('exhibitions:headline')}
                onChange={(newValue) => handleNameChange(newValue)}
              />
              <div className="mt-2 flex gap-2 text-sm text-gray">
                <InfoIcon />
                {t('exhibitions:custom_title')}
              </div>
            </div>
            <div className="flex basis-2/3 flex-col">
              <TextInput
                id={`${name}.year`}
                value={addedIllustration.year}
                className="bg-opacity-50 outline-black"
                label={t('exhibitions:year')}
                onChange={(newValue) => handleYearChange(newValue)}
              />
              <div className="mt-2 flex gap-2 text-sm text-gray">
                <InfoIcon />
                {t('exhibitions:custom_date')}
              </div>
            </div>
          </div>
          <WYSIWYGEditor
            label="Úvod (volitelné – maximálně 750 znaků)"
            value={addedIllustration.description}
            onChange={(newValue) => handleDescChange(newValue)}
          />
        </div>
      </div>
    </div>
  )
}

export default AddedIllustration
