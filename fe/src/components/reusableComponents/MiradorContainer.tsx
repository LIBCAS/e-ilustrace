import { FC, memo, ReactElement, useEffect, useRef, useState } from 'react'
import { ExclamationTriangleIcon } from '@heroicons/react/24/outline'
import mirador from 'mirador'
import { miradorImageToolsPlugin } from 'mirador-image-tools'

type MiradorPlugin = ReactElement | object

type TProps = {
  config: {
    galleryView?: {
      height?: number
      width?: number
    }
    id: string
    theme?: object
    window?: {
      allowClose?: boolean
    }
    windows: {
      defaultView?: string
      imageToolsEnabled?: boolean
      imageToolsOpen?: boolean
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      loadedManifest: any
      thumbnailNavigationPosition?: 'far-bottom'
    }[]
  }
  plugins?: MiradorPlugin[]
}

const MiradorContainer: FC<TProps> = memo(function MiradorContainer({
  config = {
    id: '',
    windows: [],
  },
  plugins = [],
}) {
  const initialized = useRef(false)
  const [initError, setInitError] = useState(false)

  useEffect(() => {
    if (!initialized.current) {
      try {
        mirador.viewer(config, [...plugins, ...miradorImageToolsPlugin])
        initialized.current = true
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
      } catch (e) {
        console.error('Nepodařilo se inicializovat Mirador')
        setInitError(true)
      }
    }
  }, [config, plugins])

  return initError ? (
    <div className="flex h-[100px] w-full items-center gap-2">
      <ExclamationTriangleIcon className="h-4 w-4" /> Nepodařilo se
      inicializovat Mirador
    </div>
  ) : (
    <div id={config.id} className="h-[700px] w-full" />
  )
})

export default MiradorContainer
