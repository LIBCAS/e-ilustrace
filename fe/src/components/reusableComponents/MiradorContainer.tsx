/* eslint-disable react/prop-types */
/* eslint-disable @typescript-eslint/ban-ts-comment */
// @ts-ignore
// import Mirador from 'mirador/dist/es/src/index'
import { FC, memo, ReactElement, useEffect, useState } from 'react'
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
  const [initialized, setInitialized] = useState(false)

  useEffect(() => {
    if (!initialized) {
      // Mirador is imported in index.html (resolves build issues)
      // @ts-ignore
      Mirador.viewer(config, plugins)
      setInitialized(true)
    }
  }, [config, initialized, plugins])

  return <div id={config.id} className="h-[700px] w-full" />
})

export default MiradorContainer
