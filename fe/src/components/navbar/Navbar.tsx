import { Fragment, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { UserCircleIcon } from '@heroicons/react/24/outline'
import { Transition } from '@headlessui/react'
import Cookies from 'js-cookie'

import MenuIcon from '../../assets/icons/menu.svg?react'
import Logo from '../../assets/icons/logo.svg?react'
import InfoIcon from '../../assets/icons/info.svg?react'
import NavbarItem from './NavbarItem'
import { useSidebarStore } from '../../store/useSidebarStore'
import { useMeQuery } from '../../api/user'

type Props = {
  isDesktop: boolean
}

const CITATION_COOKIE_NAME = 'eil-citation-consent'
const hasCitationConsent = () =>
  Cookies.get(CITATION_COOKIE_NAME) === 'accepted'

const Navbar = ({ isDesktop }: Props) => {
  const { t, i18n } = useTranslation('navigation')
  const { setSidebarOpen, setLoginPhase } = useSidebarStore()
  const { data: me, isLoading: meLoading } = useMeQuery()
  const [citationAccepted, setCitationAccepted] = useState(hasCitationConsent)
  const [citationPopoverOpen, setCitationPopoverOpen] = useState(
    () => !hasCitationConsent()
  )
  const citationPopoverRef = useRef<HTMLDivElement>(null)
  let statusColor = 'bg-gray'
  let statusLabel = t('status_logged_out')

  if (meLoading) {
    statusColor = 'bg-superlightgray'
    statusLabel = t('status_checking')
  } else if (me) {
    statusColor = 'bg-green'
    statusLabel = t('status_logged_in')
  }

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

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        citationPopoverRef.current &&
        !citationPopoverRef.current.contains(event.target as Node)
      ) {
        setCitationPopoverOpen(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
    }
  }, [])

  return (
    <nav
      className={`fixed left-0 top-0 z-20 flex h-20 w-full flex-wrap items-center bg-white px-5 text-sm font-bold text-black ${
        scrolled ? 'border-b-0 shadow-md' : ''
      }`}
    >
      <div className="wrapper flex items-center justify-between">
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
        <div className="ml-auto flex items-center gap-3 md:gap-6">
          {isDesktop ? (
            <div className="hidden uppercase min-[1200px]:flex min-[1200px]:flex-row min-[1200px]:items-center min-[1200px]:justify-end min-[1200px]:gap-5">
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
          ) : null}
          <div ref={citationPopoverRef} className="relative">
            <button
              type="button"
              onClick={() => setCitationPopoverOpen((current) => !current)}
              className={`flex h-9 w-9 items-center justify-center rounded-lg transition-colors ${
                citationAccepted
                  ? 'bg-[#e8f6ee] text-[#1f7a3d] hover:bg-[#ddf1e5]'
                  : 'bg-[#fdecec] text-[#b42318] hover:bg-[#f9dfdf]'
              }`}
              title={t('citations_info')}
              aria-label={t('citations_info')}
            >
              <InfoIcon className="h-4 w-4" />
            </button>
            <Transition
              as={Fragment}
              show={citationPopoverOpen}
              enter="transform transition ease-out duration-200"
              enterFrom="opacity-0 -translate-y-1 scale-95"
              enterTo="opacity-100 translate-y-0 scale-100"
              leave="transform transition ease-in duration-150"
              leaveFrom="opacity-100 translate-y-0 scale-100"
              leaveTo="opacity-0 -translate-y-1 scale-95"
            >
              <div className="fixed left-1/2 top-24 z-50 max-h-[calc(100vh-7rem)] w-[min(94vw,680px)] origin-top -translate-x-1/2 overflow-y-auto rounded-2xl border border-superlightgray bg-white shadow-xl lg:absolute lg:left-auto lg:right-0 lg:top-12 lg:max-h-[80vh] lg:origin-top-right lg:translate-x-0">
                <div className="border-b border-superlightgray px-6 py-4">
                  <h2 className="text-xl font-bold md:text-2xl">
                    {t('citations_title')}
                  </h2>
                </div>
                <div className="space-y-5 break-words px-6 py-6 text-sm font-normal">
                  <p>{t('citations_intro')}</p>
                  <p>{t('citations_recommendation')}</p>
                  <div className="rounded-xl border-l-2 border-red bg-[#fafafa] px-4 py-3 text-gray [overflow-wrap:anywhere]">
                    {t('citations_database_reference')}
                  </div>
                  <p className="font-bold">
                    {t('citations_specific_record_title')}
                  </p>
                  <div className="rounded-xl border-l-2 border-red bg-[#fafafa] px-4 py-3 text-gray [overflow-wrap:anywhere]">
                    {t('citations_specific_reference')}
                  </div>
                  <p className="font-bold text-gray">{t('citations_note')}</p>
                </div>
                <div className="border-t border-superlightgray px-6 py-4">
                  <button
                    type="button"
                    className="ml-auto block rounded-xl bg-red px-6 py-3 font-bold text-white transition-opacity hover:opacity-90"
                    onClick={() => {
                      Cookies.set(CITATION_COOKIE_NAME, 'accepted', {
                        expires: 365,
                        sameSite: 'lax',
                      })
                      setCitationAccepted(true)
                      setCitationPopoverOpen(false)
                    }}
                  >
                    {t('citations_accept')}
                  </button>
                </div>
              </div>
            </Transition>
          </div>
          <button
            type="button"
            onClick={() => {
              setLoginPhase(me ? 'MENU' : 'LOGIN')
              setSidebarOpen(true)
            }}
            className="relative flex h-9 w-9 items-center justify-center rounded-full border border-superlightgray text-gray"
            title={statusLabel}
            aria-label={statusLabel}
          >
            <UserCircleIcon className="h-5 w-5" />
            <span
              className={`absolute -right-0.5 -top-0.5 h-2.5 w-2.5 rounded-full ring-2 ring-white ${statusColor}`}
            />
          </button>
          <MenuIcon
            className="h-7 w-7 shrink-0 cursor-pointer"
            onClick={() => setSidebarOpen(true)}
          />
        </div>
      </div>
    </nav>
  )
}

export default Navbar
