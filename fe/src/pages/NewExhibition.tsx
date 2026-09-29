import { FC, useCallback, useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import { toast } from 'react-toastify'
import { useIdleTimer } from 'react-idle-timer'
import { Dialog } from '@headlessui/react'
import CloseIcon from '../assets/icons/close.svg?react'
import LeftArrow from '../assets/icons/navigate_back.svg?react'
import Search from '../assets/icons/search.svg?react'
import BookMark from '../assets/icons/bookmark.svg?react'

import Button from '../components/reusableComponents/Button'
import SearchIllustration from '../components/exhibitions/SearchIllustration'
import FromMySelection from '../components/exhibitions/FromMySelection'
import AddedIllustration from '../components/exhibitions/AddedIllustration'
import WYSIWYGEditor from '../components/reusableComponents/inputs/WYSIWYGEditor'

import { useNewExhibitionStore } from '../store/useNewExhibitionStore'
import TextInput from '../components/reusableComponents/inputs/TextInput'
import {
  useSaveMyExhibitionMutation,
  useExhibitionDetailQuery,
} from '../api/exhibition'
import Loader from '../components/reusableComponents/Loader'
import ShowError from '../components/reusableComponents/ShowError'
import ShowInfoMessage from '../components/reusableComponents/ShowInfoMessage'

const NewExhibition: FC = () => {
  const [showSearch, setShowSearch] = useState(true)
  const [showSelection, setShowSelection] = useState(false)
  const [activityCheckModalOpen, setActivityCheckModalOpen] = useState(false)
  const { t, i18n } = useTranslation('exhibitions')
  const navigate = useNavigate()
  const { id } = useParams()
  const isEditing = !!id

  const { mutateAsync, status: savingStatus } = useSaveMyExhibitionMutation()
  const {
    data: editedExhibition,
    isLoading: editedExhibitionLoading,
    isError: editedExhibitionError,
  } = useExhibitionDetailQuery(id)

  const {
    name,
    setName,
    description,
    setDescription,
    radio,
    setRadio,
    items,
    setItems,
    setInitialState,
  } = useNewExhibitionStore()

  const discardChanges = useCallback(() => {
    setInitialState()
    setShowSearch(true)
    setShowSelection(false)
  }, [setInitialState])

  const setLoadedData = useCallback(() => {
    if (isEditing && editedExhibition) {
      setName(editedExhibition.name)
      setDescription(editedExhibition.description)
      setRadio(editedExhibition.radio)
      setItems(
        editedExhibition.items.map((i) => ({
          itemId: i.id,
          id: i.illustration.id,
          description: i.description.trim(),
          name: i.name.trim(),
          year: i.year.trim(),
          preface: i.preface,
        }))
      )
    }
  }, [editedExhibition, isEditing, setDescription, setItems, setName, setRadio])

  useEffect(() => {
    setLoadedData()
  }, [setLoadedData])

  useEffect(() => {
    if (!isEditing) {
      discardChanges()
    }
  }, [discardChanges, isEditing])

  const handleSave = (show = false) => {
    if (items.some((i) => i.preface)) {
      toast
        .promise(
          mutateAsync({
            id: editedExhibition?.id,
          }),
          {
            pending: t('exhibitions:saving_exhibition'),
            success: t('exhibitions:exhibition_saved_successfully'),
            error: t('exhibitions:error_when_saving_exhibition'),
          }
        )
        .then((exhibition) => {
          discardChanges()
          if (show) {
            navigate(`/exhibitions/${exhibition.id}`)
          }
        })
    } else {
      toast.error(t('exhibitions:set_default_illustration'))
    }
  }

  const onIdle = () => {
    setActivityCheckModalOpen(true)
  }

  const { reset } = useIdleTimer({
    timeout: 900_000,
    onIdle,
  })

  const handleTimerReset = () => {
    setActivityCheckModalOpen(false)
    reset()
  }

  return (
    <section>
      <Dialog
        open={activityCheckModalOpen}
        onClose={() => handleTimerReset()}
        className="relative z-50"
      >
        <div className="fixed inset-0 bg-black/30" aria-hidden="true" />
        <div className="fixed inset-0 w-screen overflow-y-auto">
          <div className="flex min-h-full items-center justify-center p-4">
            <Dialog.Panel className="bg-white p-6 shadow-xl md:rounded-2xl">
              <Dialog.Title className="mb-4 flex items-center justify-between border-b-[1.5px] border-superlightgray pb-2 md:border-none">
                <span className="text-2xl font-bold md:text-xl">
                  {t('inactivity_modal_header')}
                </span>
                <Button
                  iconButton
                  variant="text"
                  className="self-end justify-self-end border-none bg-white font-bold uppercase text-black hover:text-black hover:shadow-none"
                  onClick={() => {
                    handleTimerReset()
                  }}
                >
                  <CloseIcon />
                </Button>
              </Dialog.Title>
              <Dialog.Description className="mb-8">
                {t('inactivity_modal_text')}
              </Dialog.Description>
              <Button onClick={() => handleTimerReset()} className="ml-auto">
                {t('inactivity_modal_button')}
              </Button>
            </Dialog.Panel>
          </div>
        </div>
      </Dialog>
      <div className="border-[1.5px] border-superlightgray py-10">
        <div className="mr-8">
          <div className="mx-auto flex max-w-7xl items-center">
            <LeftArrow
              className="cursor-pointer text-red"
              onClick={() => {
                navigate('/exhibitions')
              }}
            />
            <h1 className="text-left text-4xl font-bold">
              {isEditing ? editedExhibition?.name : t('new_exhibition')}
            </h1>
          </div>
        </div>
      </div>
      <div className="mx-auto flex max-w-7xl flex-col px-8">
        {isEditing && editedExhibitionLoading ? (
          <div className="my-10 flex items-center justify-center">
            <Loader />
          </div>
        ) : null}
        {isEditing && editedExhibitionError ? <ShowError /> : null}
        {isEditing &&
        !editedExhibitionLoading &&
        !editedExhibitionError &&
        !editedExhibition ? (
          <ShowInfoMessage message={t('exhibition_not_found')} />
        ) : null}
        {(isEditing &&
          !editedExhibitionLoading &&
          !editedExhibitionError &&
          editedExhibition) ||
        !isEditing ? (
          <>
            <div className="flex w-full flex-row items-center justify-between border-b border-b-superlightgray py-8">
              <a
                href={
                  i18n.resolvedLanguage === 'cs'
                    ? 'https://e-ilustrace.cz/napoveda/'
                    : 'https://e-ilustrace.cz/en/help/'
                }
                target="_blank"
                className="font-bold text-black underline"
                rel="noreferrer"
              >
                {t('how_to_use_exhibitions')}
              </a>
              <div className="flex gap-3">
                <Button
                  variant="secondary"
                  onClick={() =>
                    isEditing ? setLoadedData() : discardChanges()
                  }
                  disabled={savingStatus === 'pending'}
                >
                  {t('discard_changes')}
                </Button>
                <Button
                  variant="secondary"
                  onClick={() => handleSave(true)}
                  disabled={!items.length || savingStatus === 'pending'}
                >
                  {t('save_and_display')}
                </Button>
                <Button
                  variant="submit"
                  onClick={() => handleSave()}
                  disabled={!items.length || savingStatus === 'pending'}
                >
                  {t('save_exhibtion')}
                </Button>
              </div>
            </div>
            <div className="mt-4 flex gap-8">
              <div className="basis-2/3">
                <TextInput
                  label={t('name_of_exhibition')}
                  className="bg-opacity-50 outline-black"
                  id="name"
                  onChange={(value) => setName(value)}
                  value={name}
                />
                <WYSIWYGEditor
                  label="Úvod (volitelné – maximálně 750 znaků)"
                  value={description}
                  onChange={(value) => setDescription(value)}
                />
              </div>
              <div className="basis-1/3">
                <div className="flex flex-col">
                  <span className="block text-sm font-medium text-gray">
                    {t('type_of_exhibition_view')}
                  </span>
                  <div className="mt-3 flex items-center gap-4">
                    <fieldset>
                      <label
                        className="cursor-pointer font-bold text-black"
                        htmlFor="album"
                      >
                        <input
                          id="album"
                          type="radio"
                          value="ALBUM"
                          checked={radio === 'ALBUM'}
                          onChange={() => setRadio('ALBUM')}
                          className="mr-2"
                        />
                        {t('album')}
                      </label>
                      <label
                        className="ml-3 cursor-pointer font-bold text-black"
                        htmlFor="storyline"
                      >
                        <input
                          id="storyline"
                          type="radio"
                          value="STORYLINE"
                          checked={radio === 'STORYLINE'}
                          onChange={() => setRadio('STORYLINE')}
                          className="mr-2"
                        />
                        {t('storyline')}
                      </label>
                      <label
                        className="ml-3 cursor-pointer font-bold text-black"
                        htmlFor="slider"
                      >
                        <input
                          id="slider"
                          type="radio"
                          value="SLIDER"
                          checked={radio === 'SLIDER'}
                          onChange={() => setRadio('SLIDER')}
                          className="mr-2"
                        />
                        {t('slider')}
                      </label>
                    </fieldset>
                  </div>
                </div>
              </div>
            </div>
            {items.map((i, index) => (
              <AddedIllustration
                addedIllustration={i}
                index={index}
                key={`added-ill-${i.id}`}
              />
            ))}
            {showSearch && (
              <SearchIllustration close={() => setShowSearch(false)} />
            )}
            {showSelection && (
              <FromMySelection close={() => setShowSelection(false)} />
            )}
            <div className="mt-4 flex justify-between border-y border-superlightgray py-4">
              <h3 className="mt-2 text-lg font-bold">
                {t('add_illustration')}
              </h3>
              <div className="flex flex-wrap items-center gap-4">
                <Button
                  startIcon={<Search />}
                  onClick={() => {
                    setShowSelection(false)
                    setShowSearch((current) => !current)
                  }}
                >
                  {t('search_illustration')}
                </Button>
                <Button
                  startIcon={<BookMark />}
                  onClick={() => {
                    setShowSearch(false)
                    setShowSelection((current) => !current)
                  }}
                >
                  {t('from_selection')}
                </Button>
              </div>
            </div>
            <div className="mt-8 flex items-center justify-end gap-2 py-8">
              <Button
                variant="secondary"
                onClick={() => (isEditing ? setLoadedData() : discardChanges())}
                disabled={savingStatus === 'pending'}
              >
                {t('discard_changes')}
              </Button>
              <Button
                variant="secondary"
                onClick={() => handleSave(true)}
                disabled={!items.length || savingStatus === 'pending'}
              >
                {t('save_and_display')}
              </Button>
              <Button
                variant="submit"
                onClick={() => handleSave()}
                disabled={!items.length || savingStatus === 'pending'}
              >
                {t('save_exhibtion')}
              </Button>
            </div>
          </>
        ) : null}
      </div>
    </section>
  )
}

export default NewExhibition
