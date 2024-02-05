import { useState, useRef, useEffect, FC } from 'react'
import Switch from 'react-switch'
import { useTranslation } from 'react-i18next'
import { toast } from 'react-toastify'
import Button from '../reusableComponents/Button'

import Link from '../../assets/icons/link.svg?react'
import useChangeExhibitionVisibilityMutation from '../../api/mutation/useChangeExhibitionVisibilityMutation'
import { TExhibitionDetail } from '../../../../fe-shared/@types/exhibition'

type Props = {
  exhibition: TExhibitionDetail
  canEditShare: boolean
}

const ShareButtons: FC<Props> = ({ exhibition, canEditShare }) => {
  const { t } = useTranslation('exhibitions')
  const [copied, setCopied] = useState(false)
  const [copyOpen, setCopyOpen] = useState(false)
  const copyRef = useRef<HTMLInputElement>(null)

  const { mutateAsync: doChangeVisibility } =
    useChangeExhibitionVisibilityMutation()

  const handleVisibilityChange = () => {
    toast.promise(doChangeVisibility({ id: exhibition.id }), {
      pending: t('publicity_changing'),
      success: t('publicity_changed'),
      error: t('publicity_change_error'),
    })
  }

  useEffect(() => {
    const handleClickOutside = (e: Event) => {
      if (
        copyRef.current &&
        !copyRef.current.contains(e.target as HTMLElement)
      ) {
        setCopyOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
    }
  }, [copyRef])

  const delay = (ms: number) =>
    // eslint-disable-next-line no-promise-executor-return
    new Promise((resolve) => setTimeout(resolve, ms))

  return (
    <div className="ml-auto flex items-center gap-1 pr-4">
      {canEditShare ? (
        // eslint-disable-next-line jsx-a11y/label-has-associated-control
        <label className="mr-2 flex items-center">
          <Switch
            className="mr-2"
            onChange={() => handleVisibilityChange()}
            checked={!!exhibition.published}
            height={22}
            width={51}
          />
          <span className="font-bold">
            {exhibition.published ? t('published') : t('private')}
          </span>
        </label>
      ) : null}
      {!!exhibition.published && (
        <div ref={copyRef} className="relative flex flex-col">
          <Button
            iconButton
            variant="secondary"
            onClick={() => setCopyOpen(!copyOpen)}
          >
            {/* eslint-disable-next-line jsx-a11y/anchor-is-valid */}
            <Link />
          </Button>
          {copyOpen && !copied && (
            // eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-static-element-interactions
            <div
              className="absolute top-12 z-20 flex -translate-x-1/2 items-center  gap-4 rounded-sm border-black bg-white px-4 py-2 shadow-xl"
              onClick={async () => {
                // Copy current URL to clipboard and close copy modal
                await navigator.clipboard.writeText(window.location.href)
                setCopied(true)
                await delay(1000)
                setCopyOpen(false)
                setCopied(false)
              }}
            >
              {window.location.href}
              <p className="ml-auto cursor-pointer text-red underline">
                Kopírovat
              </p>
            </div>
          )}
          {copyOpen && copied && (
            // eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-static-element-interactions
            <div
              className="absolute top-12 z-20 flex -translate-x-1/2 items-center  gap-4 border-[1.px] border-black bg-white px-4 py-2 shadow-[0_0_7px_-2px_black]"
              onClick={() => {
                // Copy current URL to clipboard
                navigator.clipboard.writeText(window.location.href)
              }}
            >
              Zkopírováno
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default ShareButtons
