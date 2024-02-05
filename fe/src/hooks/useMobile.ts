import { useEffect, useState } from 'react'

const useMobile: () => { isMobile: boolean; isTablet: boolean } = () => {
  const [state, setState] = useState({
    mobile: window.innerWidth < 768,
    tablet: window.innerWidth < 1280,
  })

  const updateMedia = () => {
    setState({
      mobile: window.innerWidth < 768,
      tablet: window.innerWidth < 1280,
    })
  }

  useEffect(() => {
    window.addEventListener('resize', updateMedia)
    return () => window.removeEventListener('resize', updateMedia)
  })

  return { isMobile: state.mobile, isTablet: state.tablet }
}

export default useMobile
