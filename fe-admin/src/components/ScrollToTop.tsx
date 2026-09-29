import { useLocation } from 'react-router-dom'
import { useEffect } from 'react'

const ScrollToTop = () => {
  const { pathname } = useLocation()

  useEffect(() => {
    document.documentElement.scrollTo(0, 0)
  }, [pathname])

  return null
}

export default ScrollToTop
