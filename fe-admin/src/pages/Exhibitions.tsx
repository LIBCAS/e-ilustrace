import { useTranslation } from 'react-i18next'
import { toast } from 'react-toastify'
import { useState } from 'react'
import Switch from 'react-switch'
import { version as uuidVersion, validate as uuidValidate } from 'uuid'
import Loader from '../components/reusableComponents/Loader'
import ShowError from '../components/reusableComponents/ShowError'
import TextInput from '../components/reusableComponents/inputs/TextInput'
import Button from '../components/reusableComponents/Button'
import {
  useChangeExhibitionVisibilityMutation,
  useDeleteExhibitionMutation,
  useExhibitionDetailQuery,
} from '../api/exhibition'
import useMeQueryWrapper from '../hooks/useMeQueryWrapper'

const validateUUID = (search: string) => {
  return uuidValidate(search) && uuidVersion(search) === 4
}

const Exhibitions = () => {
  const [search, setSearch] = useState<string>('')
  const { t } = useTranslation()
  const { me, meError } = useMeQueryWrapper()
  const {
    data: exhibition,
    isLoading: exhibitionLoading,
    isError: exhibitionError,
    error,
  } = useExhibitionDetailQuery(validateUUID(search) ? search : undefined)

  const { mutateAsync: changeVisibility, status: visibilityChangeStatus } =
    useChangeExhibitionVisibilityMutation()
  const { mutateAsync: deleteExhibition, status: deletionStatus } =
    useDeleteExhibitionMutation()

  const handleVisibilityChange = (id: string) => {
    toast.promise(changeVisibility({ id }), {
      pending: t('exhibitions.publicity_changing'),
      success: t('exhibitions.publicity_changed'),
      error: t('exhibitions.publicity_change_error'),
    })
  }

  const handleDeletion = (id: string) => {
    toast
      .promise(deleteExhibition({ id }), {
        pending: t('exhibitions.deletion_in_progress'),
        success: t('exhibitions.exhibition_deleted'),
        error: t('exhibitions.deletion_error'),
      })
      .then(() => setSearch(''))
  }

  const pendingChanges = [visibilityChangeStatus, deletionStatus].includes(
    'pending'
  )

  return (
    <div className="min-h-[92vh] border-t border-superlightgray bg-white py-5">
      <div className="container">
        <h2 className="mb-10">{t('menu.exhibitions')}</h2>
        <div className="my-5">
          <TextInput
            placeholder={t('exhibitions.search')}
            id="exhibition-search"
            value={search}
            onChange={(value) => setSearch(value.trim())}
          />
        </div>
        {exhibitionLoading ? (
          <div className="my-10 flex items-center justify-center">
            <Loader />
          </div>
        ) : null}
        {/* eslint-disable-next-line no-nested-ternary */}
        {exhibitionError || meError ? (
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          error?.code === 'ENTITY_NOT_EXIST' ? (
            <div className="text-center">
              {t('exhibitions.exhibition_not_found')}
            </div>
          ) : (
            <ShowError />
          )
        ) : null}
        {/* eslint-disable-next-line no-nested-ternary */}
        {validateUUID(search) ? (
          exhibition ? (
            <div className="flex flex-col gap-4">
              <div className="flex flex-col gap-4">
                <span>{exhibition.name}</span>
                <span>{exhibition.user.fullName}</span>
                <div className="flex gap-2">
                  <Switch
                    disabled={pendingChanges}
                    checked={!!exhibition.published}
                    onChange={() => handleVisibilityChange(exhibition.id)}
                    height={22}
                    width={51}
                  />
                  <span>
                    {exhibition.published
                      ? t('exhibitions.published')
                      : t('exhibitions.private')}
                  </span>
                </div>
              </div>
              <div>
                <Button
                  disabled={
                    pendingChanges ||
                    !me?.authorities.some((a) => a.authority === 'SUPER_ADMIN')
                  }
                  variant="secondary"
                  onClick={() => handleDeletion(exhibition.id)}
                >
                  {t('exhibitions.delete_exhibition')}
                </Button>
              </div>
            </div>
          ) : null
        ) : (
          <div className="text-center">{t('exhibitions.enter_uuid')}</div>
        )}
      </div>
    </div>
  )
}

export default Exhibitions
