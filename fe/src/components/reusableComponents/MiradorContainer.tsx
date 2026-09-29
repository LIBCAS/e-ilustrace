/* eslint-disable react/prop-types */
/* eslint-disable @typescript-eslint/ban-ts-comment */
// @ts-ignore
// import Mirador from 'mirador/dist/es/src/index'
import { FC, memo, ReactElement, useEffect, useRef, useState } from 'react'
import { ExclamationTriangleIcon } from '@heroicons/react/24/outline'
// @ts-ignore
// eslint-disable-next-line import/extensions
// import { miradorImageToolsPlugin } from 'mirador-image-tools'

type TProps = {
  config: {
    id: string
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
  plugins?: ReactElement[]
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
        // Mirador is imported in index.html (resolves build issues)
        // @ts-ignore
        Mirador?.viewer(config, plugins)
        initialized.current = true
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
