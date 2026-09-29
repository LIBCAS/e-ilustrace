import mirador from 'mirador'
import { useEffect, useRef } from 'react'
import { miradorImageToolsPlugin } from 'mirador-image-tools'

const Mirador = ({ config, plugins }) => {
  const initialized = useRef(false)

  useEffect(() => {
    if (!initialized.current) {
      mirador.viewer(config, [...plugins, ...miradorImageToolsPlugin])
      initialized.current = true
    }
  }, [config, plugins])

  return <div id={config.id} />
}

export default Mirador
