import { useNavigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import useIllustrationDetailQuery from '../api/query/useIllustrationDetailQuery'
import Loader from '../components/reusableComponents/Loader'
import ShowError from '../components/reusableComponents/ShowError'
import useIconClassListQuery from '../api/query/useIconClassListQuery'
import ShowInfoMessage from '../components/reusableComponents/ShowInfoMessage'
import Dropdown from '../components/reusableComponents/inputs/Dropdown'
import Button from '../components/reusableComponents/Button'
import useSaveIllustrationICCMutation from '../api/query/useSaveIllustrationICCMutation'
import useThemeListQuery from '../api/query/useThemeListQuery'
import TextInput from '../components/reusableComponents/inputs/TextInput'
import useAddNewThemeMutation from '../api/query/useAddNewThemeMutation'
import useAddNewICCMutation from '../api/query/useAddNewICCMutation'
import { useEnrichmentStates } from '../utils/helperHooks'
import useUpdateIconClassStateMutation from '../api/query/useUpdateIconClassStateMutation'
import useUpdateThemeStateMutation from '../api/query/useUpdateThemeStateMutation'
import { TEnrichmentStates } from '../../../fe-shared/@types/illustration'

type TEnrichmentFields = {
  value: string
  label: string
}[]

const Enrichment = () => {
  const { t } = useTranslation()
  const { id } = useParams()
  const navigate = useNavigate()
  const { states: enrichmentStates } = useEnrichmentStates()
  const [selectedICC, setSelectedICC] = useState<TEnrichmentFields>([])
  const [selectedThemes, setSelectedThemes] = useState<TEnrichmentFields>([])
  const [newICCPassword, setNewICCPassword] = useState('')
  const [newTheme, setNewTheme] = useState('')

  const {
    data: illustration,
    isLoading: iLoading,
    isError: iError,
  } = useIllustrationDetailQuery({ id: id || '' })
  const {
    data: iconCC,
    isLoading: iconCCLoading,
    isError: iconCCError,
  } = useIconClassListQuery()
  const {
    data: themes,
    isLoading: themesLoading,
    isError: themesError,
  } = useThemeListQuery()
  const { mutateAsync: saveICC, status: saveICCStatus } =
    useSaveIllustrationICCMutation()
  const { mutateAsync: addNewICC, status: addNewICCStatus } =
    useAddNewICCMutation()
  const { mutateAsync: addNewTheme, status: addNewThemeStatus } =
    useAddNewThemeMutation()
  const { mutateAsync: updateICCState, status: updateICCStateStatus } =
    useUpdateIconClassStateMutation()
  const { mutateAsync: updateThemeState, status: updateThemeStateStatus } =
    useUpdateThemeStateMutation()

  useEffect(() => {
    if (illustration?.iconclass.length) {
      setSelectedICC(
        illustration.iconclass.map((i) => ({ value: i.id, label: i.code }))
      )
    }
    if (illustration?.themes.length) {
      setSelectedThemes(
        illustration.themes.map((i) => ({ value: i.id, label: i.name }))
      )
    }
  }, [illustration])

  if (iLoading || iconCCLoading || themesLoading) {
    return (
      <div className="flex">
        <Loader className="mx-auto my-4 self-center" />
      </div>
    )
  }

  if (iError || iconCCError || themesError) {
    return <ShowError />
  }

  if (!illustration) {
    return <ShowInfoMessage message={t('enrichment.not_found')} />
  }

  const handleSubmit = () => {
    saveICC({
      uuid: illustration.id,
      ICC: selectedICC.map((icc) => ({ id: icc.value })),
      themes: selectedThemes.map((theme) => ({ id: theme.value })),
    })
      .then(() => {
        toast.success(t('enrichment.save_successful'))
      })
      .catch(() => {
        // console.error(e)
      })
  }

  const handleNewICCSubmit = () => {
    addNewICC({ ICC: newICCPassword })
      .then(() => {
        setNewICCPassword('')
        toast.success(t('enrichment.add_successful'))
      })
      .catch(() => {
        // console.error(e)
      })
  }

  const handleNewThemeSubmit = () => {
    addNewTheme({ theme: newTheme })
      .then(() => {
        setNewTheme('')
        toast.success(t('enrichment.add_successful'))
      })
      .catch(() => {
        // console.error(e)
      })
  }

  const handleIconClassStateChange = (state: TEnrichmentStates) => {
    updateICCState({ uuid: illustration.id, state })
      .then(() => {
        toast.success(t('enrichment.state_updated_successfully'))
      })
      .catch(() => {})
  }
  const handleThemeStateChange = (state: TEnrichmentStates) => {
    updateThemeState({ uuid: illustration.id, state })
      .then(() => {
        toast.success(t('enrichment.state_updated_successfully'))
      })
      .catch(() => {})
  }

  return (
    <div className="min-h-[92vh] border-t border-superlightgray bg-white py-5">
      <div className="container">
        <h2>{illustration.title}</h2>
        <Button
          className="mt-2"
          variant="secondary"
          onClick={() => navigate(`/${t('urls.records')}`)}
        >
          {t('enrichment.back')}
        </Button>
        <div className="mt-10 flex gap-10">
          <div className="max-w-[250px]">
            {illustration.illustrationScan ? (
              <img
                className="max-w-full justify-self-start rounded-xl transition-all duration-300"
                src={`/api/eil/files/${illustration.illustrationScan.id}`}
                alt={illustration.title}
              />
            ) : null}
            {illustration.pageScan && !illustration.illustrationScan ? (
              <img
                className="max-w-full justify-self-start rounded-xl transition-all duration-300"
                src={`/api/eil/files/${illustration.pageScan.id}`}
                alt={illustration.title}
              />
            ) : null}
          </div>
          <div className="flex flex-col gap-3">
            <span>
              <span className="text-gray">ID:</span> {illustration.id}
            </span>
            <span>
              <span className="uppercase text-gray">
                {t('enrichment.identifier')}:
              </span>{' '}
              {illustration.identifier}
            </span>
            <span className="uppercase text-gray">
              {t('enrichment.iconclass_state')}:
              <Dropdown
                options={enrichmentStates}
                value={{
                  value: illustration.iconclassState,
                  label:
                    enrichmentStates.find(
                      (s) => s.value === illustration.iconclassState
                    )?.label || '',
                }}
                onChange={(value) => handleIconClassStateChange(value.value)}
                loading={updateICCStateStatus === 'pending'}
              />
            </span>
            <span className="uppercase text-gray">
              {t('enrichment.themes_state')}:
              <Dropdown
                options={enrichmentStates}
                value={{
                  value: illustration.themeState,
                  label:
                    enrichmentStates.find(
                      (s) => s.value === illustration.themeState
                    )?.label || '',
                }}
                onChange={(value) => handleThemeStateChange(value.value)}
                loading={updateThemeStateStatus === 'pending'}
              />
            </span>
          </div>
        </div>
        <span className="mb-2 mt-10 block">
          {t('enrichment.select_iconclass_passwords')}
        </span>
        {!iconCC?.items.length ? (
          <ShowInfoMessage message={t('enrichment.icc_not_found')} />
        ) : (
          <Dropdown
            options={iconCC.items.map((i) => ({
              value: i.id,
              label: `${i.code}: ${i.name}`,
            }))}
            value={selectedICC.map((i) => ({
              value: i.value,
              label: `${i.label}: ${
                iconCC.items.find((ic) => ic.code === i.label)?.name || ''
              }`,
            }))}
            onChange={(values) => setSelectedICC(values)}
            isMulti
            isSearchable
          />
        )}
        <span className="mb-2 mt-10 block">
          {t('enrichment.select_themes')}
        </span>
        {!themes?.items.length ? (
          <ShowInfoMessage message={t('enrichment.themes_not_found')} />
        ) : (
          <Dropdown
            options={themes.items.map((i) => ({
              value: i.id,
              label: i.name,
            }))}
            value={selectedThemes}
            onChange={(values) => setSelectedThemes(values)}
            isMulti
            isSearchable
          />
        )}
        {iconCC?.items.length || themes?.items.length ? (
          <Button
            className="mt-5"
            onClick={() => handleSubmit()}
            isLoading={saveICCStatus === 'pending'}
            disabled={saveICCStatus === 'pending'}
          >
            {t('enrichment.save')}
          </Button>
        ) : null}
        <div className="mt-10 border-t border-lightgray">
          <span className="mb-2 mt-10 block">
            {t('enrichment.add_new_icc')}
          </span>
          <TextInput
            id="new-icc-password"
            onChange={(newValue) => setNewICCPassword(newValue)}
            value={newICCPassword}
          />
          <Button
            className="mt-5"
            onClick={() => handleNewICCSubmit()}
            disabled={
              newICCPassword.length === 0 || addNewICCStatus === 'pending'
            }
            isLoading={addNewICCStatus === 'pending'}
          >
            {t('enrichment.add')}
          </Button>
        </div>
        <span className="mb-2 mt-10 block">
          {t('enrichment.add_new_theme')}
        </span>
        <div className="mt-10">
          <TextInput
            id="new-theme"
            onChange={(newValue) => setNewTheme(newValue)}
            value={newTheme}
          />
          <Button
            className="mt-5"
            onClick={() => handleNewThemeSubmit()}
            disabled={newTheme.length === 0 || addNewThemeStatus === 'pending'}
            isLoading={addNewThemeStatus === 'pending'}
          >
            {t('enrichment.add')}
          </Button>
        </div>
      </div>
    </div>
  )
}

export default Enrichment
