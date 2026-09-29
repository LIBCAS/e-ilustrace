/* eslint-disable react/prop-types */
import mirador from 'mirador'
import { useEffect, useState } from 'react'
// eslint-disable-next-line import/extensions
import miradorImageToolsPlugin from 'mirador-image-tools/es/plugins/miradorImageToolsPlugin.js'

const Mirador = ({ config, plugins }) => {
  const [initialized, setInitialized] = useState(false)

  useEffect(() => {
    if (!initialized) {
      mirador.viewer(config, [...plugins, ...miradorImageToolsPlugin])
      setInitialized(true)
    }
  }, [config, initialized, plugins])

  return <div id={config.id} />
}

export default Mirador
