import { FC, useCallback, useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router'
import { useTranslation } from 'react-i18next'

import { toast } from 'react-toastify'
import { useIdleTimer } from 'react-idle-timer'
import { Dialog } from '@headlessui/react'
import CloseIcon from '../assets/icons/close.svg?react'
import LeftArrow from '../assets/icons/navigate_back.svg?react'

import Button from '../components/reusableComponents/Button'
import SearchIllustration from '../components/exhibitions/SearchIllustration'
import AddedIllustration from '../components/exhibitions/AddedIllustration'
import WYSIWYGEditor from '../components/reusableComponents/inputs/WYSIWYGEditor'
import SelectionDialog from '../components/reusableComponents/SelectionDialog'
import SelectionDialogButton from '../components/reusableComponents/SelectionDialogButton'

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
  const [showSelectionDialog, setShowSelectionDialog] = useState(false)
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
    setShowSelectionDialog(false)
  }, [setInitialState])

  const handleSelectionToggle = useCallback(
    (illustrationId: string, checked: boolean) => {
      if (checked) {
        if (items.some((item) => item.id === illustrationId)) {
          return
        }

        setItems([
          ...items,
          {
            id: illustrationId,
            description: '',
            name: '',
            year: '',
            preface: false,
          },
        ])
        return
      }

      setItems(items.filter((item) => item.id !== illustrationId))
    },
    [items, setItems]
  )

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
      setInitialState()
    }
  }, [isEditing, setInitialState])

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
      <div className="border-[1.5px] border-superlightgray py-6 md:py-10">
        <div className="px-4 sm:px-6 lg:px-8">
          <div className="mx-auto flex max-w-7xl items-center gap-3">
            <LeftArrow
              className="cursor-pointer text-red"
              onClick={() => {
                navigate('/exhibitions')
              }}
            />
            <h1 className="text-left text-2xl font-bold leading-tight md:text-4xl">
              {isEditing ? editedExhibition?.name : t('new_exhibition')}
            </h1>
          </div>
        </div>
      </div>
      <div className="mx-auto flex max-w-7xl flex-col px-4 sm:px-6 lg:px-8">
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
            <div className="flex w-full flex-col gap-4 border-b border-b-superlightgray py-6 md:flex-row md:items-center md:justify-between md:py-8">
              <a
                href={
                  i18n.resolvedLanguage === 'cs'
                    ? 'https://e-ilustrace.cz/napoveda/'
                    : 'https://e-ilustrace.cz/en/help/'
                }
                target="_blank"
                className="font-bold text-black underline md:text-base"
                rel="noreferrer"
              >
                {t('how_to_use_exhibitions')}
              </a>
              <div className="flex w-full flex-col gap-3 sm:w-auto sm:flex-row sm:flex-wrap sm:justify-end">
                <Button
                  className="w-full sm:w-auto"
                  variant="secondary"
                  onClick={() =>
                    isEditing ? setLoadedData() : discardChanges()
                  }
                  disabled={savingStatus === 'pending'}
                >
                  {t('discard_changes')}
                </Button>
                <Button
                  className="w-full sm:w-auto"
                  variant="secondary"
                  onClick={() => handleSave(true)}
                  disabled={!items.length || savingStatus === 'pending'}
                >
                  {t('save_and_display')}
                </Button>
                <Button
                  className="w-full sm:w-auto"
                  variant="submit"
                  onClick={() => handleSave()}
                  disabled={!items.length || savingStatus === 'pending'}
                >
                  {t('save_exhibtion')}
                </Button>
              </div>
            </div>
            <div className="mt-6 flex flex-col gap-6 lg:flex-row lg:gap-8">
              <div className="w-full lg:basis-2/3">
                <TextInput
                  label={t('name_of_exhibition')}
                  className="bg-superlightgray/50 focus:border-black"
                  id="name"
                  onChange={(value) => setName(value)}
                  value={name}
                />
                <WYSIWYGEditor
                  label={t('introduction')}
                  value={description}
                  onChange={(value) => setDescription(value)}
                />
              </div>
              <div className="w-full lg:basis-1/3">
                <div className="flex flex-col">
                  <span className="block text-sm font-medium text-gray">
                    {t('type_of_exhibition_view')}
                  </span>
                  <div className="mt-3">
                    <fieldset className="flex flex-wrap gap-3">
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
                        className="cursor-pointer font-bold text-black"
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
                        className="cursor-pointer font-bold text-black"
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
            <SearchIllustration />
            <div className="mt-8 flex flex-col-reverse gap-3 py-6 sm:flex-row sm:justify-end sm:py-8">
              <Button
                className="w-full sm:w-auto"
                variant="secondary"
                onClick={() => (isEditing ? setLoadedData() : discardChanges())}
                disabled={savingStatus === 'pending'}
              >
                {t('discard_changes')}
              </Button>
              <Button
                className="w-full sm:w-auto"
                variant="secondary"
                onClick={() => handleSave(true)}
                disabled={!items.length || savingStatus === 'pending'}
              >
                {t('save_and_display')}
              </Button>
              <Button
                className="w-full sm:w-auto"
                variant="submit"
                onClick={() => handleSave()}
                disabled={!items.length || savingStatus === 'pending'}
              >
                {t('save_exhibtion')}
              </Button>
            </div>
            <SelectionDialog
              showDialog={showSelectionDialog}
              setShowDialog={setShowSelectionDialog}
              mode="exhibition"
              selectedIllustrationIds={items.map((item) => item.id)}
              onIllustrationToggle={handleSelectionToggle}
            />
            <SelectionDialogButton setShowDialog={setShowSelectionDialog} />
          </>
        ) : null}
      </div>
    </section>
  )
}

export default NewExhibition
