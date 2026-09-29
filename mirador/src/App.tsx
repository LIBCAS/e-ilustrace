import React from 'react'
import './App.css'
import { useSearchParams } from 'react-router-dom'
import Mirador from './Mirador'

function App() {
  const [searchParams] = useSearchParams()

  const records =
    searchParams
      .get('r')
      ?.split(',')
      .map((r) => ({
        imageToolsEnabled: true,
        imageToolsOpen: true,
        loadedManifest: `/api/eil/record/${r}/manifest.json`,
      })) || []

  return (
    <div className="App">
      <header className="App-header">
        <Mirador
          config={{
            id: 'mirador',
            windows: [...records],
          }}
          plugins={[]}
        />
      </header>
    </div>
  )
}

export default App
