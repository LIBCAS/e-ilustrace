import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'

import MenuIcon from '../../assets/icons/menu.svg?react'
import Logo from '../../assets/icons/logo.svg?react'
import NavbarItem from './NavbarItem'
import { useSidebarStore } from '../../store/useSidebarStore'

type Props = {
  isDesktop: boolean
}

const Navbar = ({ isDesktop }: Props) => {
  const { t, i18n } = useTranslation('navigation')
  const { setSidebarOpen } = useSidebarStore()

  const [scrolled, setScrolled] = useState(false)

  useEffect(() => {
    window.onscroll = () => {
      if (window.scrollY > 0) {
        setScrolled(true)
      } else {
        setScrolled(false)
      }
    }
  }, [setScrolled])

  return (
    <nav
      className={`fixed left-0 top-0 z-20 flex h-20 w-full flex-wrap items-center bg-white px-5  text-sm font-bold text-black ${
        scrolled ? 'border-b-0 shadow-md' : ''
      }`}
    >
      <div className="wrapper flex justify-between">
        <div className="flex w-full items-center">
          {isDesktop && (
            <MenuIcon
              className="mr-8 h-7 w-7 cursor-pointer"
              onClick={() => setSidebarOpen(true)}
            />
          )}
          <div className="flex h-full w-full max-w-[127px] items-center justify-between">
            <a
              aria-label="E-Ilustrace logo"
              className="transition-all duration-200 hover:scale-[1.05]"
              href="https://e-ilustrace.cz/"
              target="_blank"
              rel="noreferrer"
            >
              <Logo />
            </a>
          </div>
          {!isDesktop && (
            <MenuIcon
              className="ml-auto h-7 w-7 cursor-pointer"
              onClick={() => setSidebarOpen(true)}
            />
          )}
        </div>
        {isDesktop && (
          <div className="flex flex-row items-center justify-between gap-5 uppercase">
            <NavbarItem to="/search">{t('search')}</NavbarItem>
            <NavbarItem to="/iconclass">{t('iconclass')}</NavbarItem>
            <NavbarItem to="/explore">{t('explore')}</NavbarItem>
            <NavbarItem to="/vise">{t('vise')}</NavbarItem>
            <NavbarItem to="/exhibitions">{t('exhibitions')}</NavbarItem>
            <span className="font-normal">|</span>
            <button
              type="button"
              className="rounded-3xl px-5 py-3 text-sm transition-all hover:bg-superlightgray"
              onClick={() => {
                i18n.changeLanguage(
                  i18n.resolvedLanguage === 'cs' ? 'en' : 'cs'
                )
              }}
            >
              {i18n.resolvedLanguage === 'cs' ? 'ENG' : 'CZ'}
            </button>
          </div>
        )}
      </div>
    </nav>
  )
}

export default Navbar
