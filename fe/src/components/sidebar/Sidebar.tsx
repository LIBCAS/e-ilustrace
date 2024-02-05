import { FC, useEffect } from 'react'
import { useTranslation } from 'react-i18next'

import { toast } from 'react-toastify'
import { ArrowLeftOnRectangleIcon } from '@heroicons/react/24/outline'
import CloseIcon from '../../assets/icons/close.svg?react'
import RightArrow from '../../assets/icons/right_arrow.svg?react'

import Button from '../reusableComponents/Button'
import Login from './Login'
import Registration from './Registration'
import Recovery from './Recovery'
import Recovered from './Recovered'
import SidebarItem from './SidebarItem'
import useMeQueryWrapper from '../../hooks/useMeQueryWrapper'
import Loader from '../reusableComponents/Loader'
import useLogoutMutation from '../../api/mutation/useLogoutMutation'
import { useSidebarStore } from '../../store/useSidebarStore'

type Props = {
  isDesktop: boolean
}

const Sidebar: FC<Props> = ({ isDesktop }) => {
  const { t } = useTranslation()
  const { me, meLoading, meError } = useMeQueryWrapper()
  const { mutateAsync } = useLogoutMutation()
  const { sidebarOpen, setSidebarOpen, loginPhase, setLoginPhase } =
    useSidebarStore()

  const handleLogout = async () => {
    const response = await mutateAsync()

    if (
      response.redirected &&
      response.ok &&
      !response.url.includes('logout?error')
    ) {
      toast.success(t('common:logged_out_successfully'))
    } else {
      toast.error(t('common:error_occurred_somewhere'))
    }
  }

  // Disable scrolling of page when sidebar is opened
  useEffect(() => {
    if (sidebarOpen && window.innerWidth > 768) {
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = 'unset'
    }
  }, [sidebarOpen])

  return (
    <>
      {/* eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-static-element-interactions */}
      <div
        className={`fixed top-0 h-screen min-h-full w-screen min-w-full overflow-auto bg-black bg-opacity-40 ${
          sidebarOpen ? 'z-40' : 'hidden'
        }`}
        onClick={() => {
          setLoginPhase('MENU')
          setSidebarOpen(false)
        }}
      />

      <div
        className={`fixed top-0 z-50 h-full min-h-full w-screen overflow-auto bg-red text-black md:w-[400px]
    ${
      sidebarOpen ? 'left-0 flex sm:w-full' : '-left-[calc(100%+8px)]'
    } flex-col items-start duration-300 ease-in-out`}
      >
        <div className="flex min-w-full flex-col">
          <Button
            variant="submit"
            className="m-4 self-end font-bold uppercase hover:shadow-none"
            startIcon={<CloseIcon />}
            onClick={() => {
              setLoginPhase('MENU')
              setSidebarOpen(false)
            }}
          >
            {t('navigation:close')}
          </Button>
          {loginPhase === 'MENU' &&
            (isDesktop ? (
              <ul className="flex min-w-full flex-col gap-8 py-10 pl-12 text-xl font-bold uppercase text-white">
                <li className="sidebar-item">
                  <a href="https://e-ilustrace.cz#about">
                    {t('navigation:project')}
                  </a>
                </li>
                <li className="sidebar-item">
                  <a href="https://e-ilustrace.cz#project-team">
                    {t('navigation:team_and_partners')}
                  </a>
                </li>
                <li className="sidebar-item">
                  <a href="https://e-ilustrace.cz#contact">
                    {t('navigation:contact')}
                  </a>
                </li>
                <li className="sidebar-item">
                  <a href="fe/src/components/sidebar#">
                    {t('navigation:help')}
                  </a>
                </li>
              </ul>
            ) : (
              <>
                <ul className="flex w-full flex-col gap-4 py-4 pl-12 text-sm font-bold uppercase text-white">
                  <SidebarItem to="/search" setSidebarOpen={setSidebarOpen}>
                    {t('navigation:search')}
                  </SidebarItem>
                  <SidebarItem to="/iconclass" setSidebarOpen={setSidebarOpen}>
                    {t('navigation:iconclass')}
                  </SidebarItem>
                  <SidebarItem to="/explore" setSidebarOpen={setSidebarOpen}>
                    {t('navigation:explore')}
                  </SidebarItem>
                  <SidebarItem to="/vise" setSidebarOpen={setSidebarOpen}>
                    {t('navigation:vise')}
                  </SidebarItem>
                  <SidebarItem
                    to="/exhibitions"
                    setSidebarOpen={setSidebarOpen}
                  >
                    {t('navigation:exhibitions')}
                  </SidebarItem>
                </ul>
                <hr className="ml-12 w-4/6 text-white" />
                <ul className="flex w-full flex-col gap-4 py-4 pl-12 text-sm font-bold uppercase text-white">
                  <li className="sidebar-item">
                    <a href="https://e-ilustrace.cz#about">
                      {t('navigation:project')}
                    </a>
                  </li>
                  <li className="sidebar-item">
                    <a href="https://e-ilustrace.cz#project-team">
                      {t('navigation:team_and_partners')}
                    </a>
                  </li>
                  <li className="sidebar-item">
                    <a href="https://e-ilustrace.cz#contact">
                      {t('navigation:contact')}
                    </a>
                  </li>
                  <li className="sidebar-item">
                    <a href="fe/src/components/sidebar#">
                      {t('navigation:help')}
                    </a>
                  </li>
                </ul>
              </>
            ))}
        </div>
        {(loginPhase === 'LOGIN' || loginPhase === 'REGISTRATION') && (
          <div className="flex  w-full flex-col items-start justify-between">
            <div className="flex  h-12 w-full flex-row justify-evenly border-b border-white border-opacity-50 text-lg font-bold uppercase text-white">
              <button
                type="button"
                className={`px-4 uppercase  ${
                  loginPhase === 'LOGIN' ? 'border-b-4 pb-3' : 'pb-4'
                }`}
                onClick={() => setLoginPhase('LOGIN')}
              >
                {t('navigation:login_page')}
              </button>
              <button
                type="button"
                className={`px-4 uppercase  ${
                  loginPhase === 'REGISTRATION' ? 'border-b-4 pb-3' : 'pb-4'
                }`}
                onClick={() => setLoginPhase('REGISTRATION')}
              >
                {t('navigation:registration')}
              </button>
            </div>
          </div>
        )}
        {meLoading ? (
          <div className="flex h-full w-full items-center justify-center gap-4 text-white">
            <Loader color="text-white" />
            {t('common:me_loading')}
          </div>
        ) : null}
        {!meLoading && meError ? (
          <div className="flex h-full w-full items-center justify-center gap-4 text-white">
            {t('common:me_loading_error')}
          </div>
        ) : null}
        {me ? (
          <div className="flex h-full w-full items-start justify-start pl-12 text-lg text-white">
            <div className="flex items-center justify-start">
              <ArrowLeftOnRectangleIcon
                className="mr-3 w-[28px] cursor-pointer"
                onClick={() => handleLogout()}
              />
              {me.name}
            </div>
          </div>
        ) : null}
        {!me && !meLoading && !meError ? (
          <>
            <div className="h-full w-full">
              {loginPhase === 'LOGIN' && (
                <Login setLoginPhase={setLoginPhase} />
              )}
              {loginPhase === 'RECOVERY' && (
                <Recovery setLoginPhase={setLoginPhase} />
              )}
              {loginPhase === 'RECOVERED' && <Recovered />}
              {loginPhase === 'REGISTRATION' && <Registration />}
            </div>
            {loginPhase === 'MENU' && (
              <Button
                animate
                variant="submit"
                className="text-md mx-8 mb-10 font-bold uppercase hover:shadow-none"
                endIcon={<RightArrow />}
                onClick={() => setLoginPhase('LOGIN')}
              >
                {t('navigation:login')}
              </Button>
            )}
          </>
        ) : null}
      </div>
    </>
  )
}

export default Sidebar
