import { FC, useState } from 'react'
import { useSearchParams } from 'react-router-dom'

import KeywordsList from '../../components/explore/KeywordsList'
import useMobile from '../../hooks/useMobile'
import ExploreDetail from './ExploreDetail'
import Themes from './Themes'

const Explore: FC = () => {
  const { isMobile } = useMobile()
  const [filterOpen, setFilterOpen] = useState(!isMobile)
  const [searchParams] = useSearchParams()

  return (
    <div className="mt-20 flex h-[calc(100vh-5rem)] w-full border-collapse border-superlightgray">
      <aside
        className={` ${
          isMobile ? 'fixed left-0 top-0 z-50 h-screen bg-white' : ''
        } border-collapse border-x-[1.5px] border-t-[1.5px] border-superlightgray md:h-[calc(100vh-126px)]`}
      >
        <KeywordsList filterOpen={filterOpen} setFilterOpen={setFilterOpen} />
      </aside>
      <div className="max-h-full w-full border-collapse overflow-y-scroll border-t-[1.5px] border-superlightgray p-6">
        {searchParams.get('themes')?.length ||
        searchParams.get('objects')?.length ||
        searchParams.get('authors')?.length ? (
          <ExploreDetail setFilterOpen={setFilterOpen} />
        ) : (
          <Themes setFilterOpen={setFilterOpen} />
        )}
      </div>
    </div>
  )
}

export default Explore
