import { FC } from 'react'
import { Route, Routes, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import Menu from './components/Menu'
import Records from './pages/Records'
import Users from './pages/Users'
import Enrichment from './pages/Enrichment'
import Import from './pages/Import'
import Loader from './components/reusableComponents/Loader'
import ShowError from './components/reusableComponents/ShowError'
import ShowInfoMessage from './components/reusableComponents/ShowInfoMessage'
import Login from './pages/Login'
import useMeQueryWrapper from './hooks/useMeQueryWrapper'

const App: FC = () => {
  const { t } = useTranslation()
  const { me, meLoading, meError } = useMeQueryWrapper()

  if (meLoading) {
    return (
      <div className="flex items-center justify-center py-10">
        <Loader />
      </div>
    )
  }

  if (meError) {
    return <ShowError />
  }

  if (!me) {
    return (
      <>
        <Routes>
          <Route path={t('urls.login')} element={<Login />} />
        </Routes>
        <Navigate to={`/${t('urls.login')}`} />
      </>
    )
  }

  if (
    !me.authorities.find(
      (a) => a.authority === 'ADMIN' || a.authority === 'SUPER_ADMIN'
    )
  ) {
    return <ShowInfoMessage message={t('common.admin_rights_needed')} />
  }

  return (
    <>
      <Menu />
      <main role="main">
        <Routes>
          <Route index element={<Navigate to={`/${t('urls.records')}`} />} />
          <Route path={t('urls.records')} element={<Records />} />
          <Route
            path={`${t('urls.enrichment')}/:id`}
            element={<Enrichment />}
          />
          <Route path={t('urls.users')} element={<Users />} />
          <Route path={t('urls.import')} element={<Import />} />
          <Route path={t('urls.login')} element={<Login />} />
          <Route
            path="*"
            element={
              <div className="container mt-10 text-center">
                <h1 className="mb-5">404</h1>
                <span>{t('not_found.text')}</span>
              </div>
            }
          />
        </Routes>
      </main>
    </>
  )
}

export default App
