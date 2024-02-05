import { FC } from 'react'
import clsx from 'clsx'
import ListIcon from '../../assets/icons/list.svg?react'
import Tiles from '../../assets/icons/tiles.svg?react'
import { RecordType, View } from '../../@types/types'

type Props = {
  view: View
  setView: (obj: View) => void
  type?: RecordType
}

const ViewButtons: FC<Props> = ({ view, setView, type = undefined }) => {
  return (
    <div className="flex max-h-11">
      <button
        aria-label="View as list"
        type="button"
        className="flex w-full border-collapse items-center rounded-l-xl border-2 border-superlightgray p-2 text-black hover:bg-superlightgray"
        onClick={() => setView('LIST')}
      >
        <ListIcon
          className={`mx-auto ${
            view === 'LIST' ? 'text-red' : 'text-lightgray'
          }`}
        />
      </button>
      <button
        aria-label="View as tiles"
        type="button"
        className={clsx(
          'flex w-full border-collapse items-center rounded-r-xl border-y-2 border-r-2 border-superlightgray p-2 text-black hover:bg-superlightgray',
          {
            'disabled cursor-not-allowed hover:bg-white': type === 'BOOK',
          }
        )}
        onClick={() => (type !== 'BOOK' ? setView('TILES') : null)}
      >
        <Tiles
          className={`mx-auto ${
            view === 'TILES' ? 'text-red' : 'text-lightgray'
          }`}
        />
      </button>
    </div>
  )
}

export default ViewButtons
