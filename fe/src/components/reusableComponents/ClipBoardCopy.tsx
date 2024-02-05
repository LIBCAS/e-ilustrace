import React, { FC, useState } from 'react'

import { useTranslation } from 'react-i18next'
import clsx from 'clsx'
import LinkImage from '../../assets/icons/link.svg?react'

type TProps = {
  text: string
  copyType: 'href' | 'mirador'
}

const ClipBoardCopy: FC<TProps> = ({ text, copyType }) => {
  const [copied, setCopied] = useState(false)
  const { t } = useTranslation()

  return (
    <div className="relative">
      <button
        type="button"
        className="cursor-copy text-red hover:underline"
        onClick={async () => {
          if (copyType === 'href') {
            await navigator.clipboard.writeText(window.location.href)
          } else if (copyType === 'mirador') {
            await navigator.clipboard.writeText(
              `https://${window.location.host}/api/eil/record/${text}/manifest.json`
            )
          }
          setCopied(true)
          setTimeout(() => {
            setCopied(false)
          }, 750)
        }}
      >
        <div className="flex items-center gap-2">
          <LinkImage />
          <span>{text}</span>
        </div>
      </button>
      <span
        className={clsx(
          'absolute left-0 top-full z-10 rounded-md border-[1.px] border-black bg-white px-2 py-1 text-red shadow-[0_0_7px_-2px_black] shadow-black',
          {
            block: copied,
            hidden: !copied,
          }
        )}
      >
        {t('common:copied')}
      </span>
    </div>
  )
}

export default ClipBoardCopy
