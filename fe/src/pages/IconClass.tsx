import { useState } from 'react'

import IconClassFilter from '../components/iconclass/IconClassFilter'
import IconClassSearch from '../components/iconclass/IconClassSearch'
import useMobile from '../hooks/useMobile'

const IconClass = () => {
  const { isMobile } = useMobile()
  const [filterOpen, setFilterOpen] = useState(!isMobile)

  return (
    <div className="mt-20 flex h-[calc(100vh-5rem)] w-full border-collapse border-t-[1.5px] border-superlightgray">
      <aside
        className={` ${
          isMobile ? 'fixed left-0 top-0 z-50 h-screen bg-white' : ''
        } border-collapse border-x-[1.5px] border-superlightgray md:h-[calc(100vh-126px)]`}
      >
        <IconClassFilter
          isMobile={isMobile}
          filterOpen={filterOpen}
          setFilterOpen={setFilterOpen}
        />
      </aside>
      <div className="max-h-full w-full overflow-y-scroll p-6">
        <IconClassSearch isMobile={isMobile} setFilterOpen={setFilterOpen} />
      </div>
    </div>
  )
}

export default IconClass
