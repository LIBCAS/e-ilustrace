import { ReactNode, useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import Navbar from './navbar/Navbar'
import Footer from './Footer'
import Sidebar from './sidebar/Sidebar'
import { useSidebarStore } from '../store/useSidebarStore'

type Props = {
  children: ReactNode
}

const Layout = ({ children }: Props) => {
  const location = useLocation()
  const [isDesktop, setDesktop] = useState(window.innerWidth > 1024)
  const { setSidebarOpen } = useSidebarStore()

  const updateMedia = () => {
    setDesktop(window.innerWidth > 1024)
  }

  useEffect(() => {
    window.addEventListener('resize', updateMedia)
    return () => window.removeEventListener('resize', updateMedia)
  }, [])

  useEffect(() => {
    setSidebarOpen(false)
    // return () => {};
  }, [location, setSidebarOpen])

  return (
    <>
      <Sidebar isDesktop={isDesktop} />
      <Navbar isDesktop={isDesktop} />
      <main className="mt-20 min-h-[calc(100vh-5rem)]">{children}</main>
      {isDesktop && <Footer />}
    </>
  )
}

export default Layout
