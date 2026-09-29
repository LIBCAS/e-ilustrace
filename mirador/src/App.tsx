import React from 'react'
import './App.css'
import { useSearchParams } from 'react-router'
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
            galleryView: {
              height: 120,
              width: 120,
            },
            id: 'mirador',
            theme: {
              components: {
                GalleryView: {
                  styleOverrides: {
                    thumbnail: {
                      alignItems: 'center',
                      boxSizing: 'border-box',
                      display: 'inline-flex',
                      flexDirection: 'column',
                      maxHeight: 'none',
                      minWidth: 136,
                      width: 136,
                      '& > div': {
                        alignItems: 'center',
                        display: 'flex',
                        flexDirection: 'column',
                        width: '100%',
                      },
                      '& > div > span': {
                        alignSelf: 'stretch',
                        width: '100%',
                      },
                    },
                  },
                },
              },
            },
            windows: [...records],
          }}
          plugins={[]}
        />
      </header>
    </div>
  )
}

export default App
