import { useTranslation } from 'react-i18next'
import { NavLink, useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'
import Button from './reusableComponents/Button'
import useLogoutMutation from '../api/query/useLogoutMutation'

const Menu = () => {
  const { t } = useTranslation()
  const { mutateAsync } = useLogoutMutation()
  const navigate = useNavigate()

  const handleLogout = async () => {
    const response = await mutateAsync()

    if (
      response.redirected &&
      response.ok &&
      !response.url.includes('logout?error')
    ) {
      toast.success(t('common.logged_out_successfully'))
      navigate('/')
    } else {
      toast.error(t('common.error_occurred_somewhere'))
    }
  }

  return (
    <header className="bg-white">
      <div className="container flex items-center justify-between py-4 font-semibold">
        <span className="text-xl font-bold">
          <span className="text-red">EIL</span> - {t('menu.header')}
        </span>
        <div className="flex gap-4 uppercase">
          <NavLink to={t('urls.users')} className="px-5 py-2">
            {t('menu.users')}
          </NavLink>
          <NavLink to={t('urls.records')} className="px-5 py-2">
            {t('menu.records')}
          </NavLink>
          <NavLink to={t('urls.import')} className="px-5 py-2">
            {t('menu.import')}
          </NavLink>
          <Button onClick={() => handleLogout()}>{t('menu.logout')}</Button>
        </div>
      </div>
    </header>
  )
}

export default Menu
