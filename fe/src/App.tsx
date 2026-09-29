import { FC } from 'react'
import { Route, Routes, Navigate } from 'react-router-dom'

import Layout from './components/Layout'
import Explore from './pages/explore/Explore'
import Exhibitions from './pages/Exhibitions'
import ExhibitionDetail from './pages/ExhibitionDetail'
import NewExhibition from './pages/NewExhibition'
import Vise from './pages/Vise'
import RecordDetail from './pages/RecordDetail'
import IconClass from './pages/IconClass'
import Search from './pages/Search'
import Upload from './pages/Upload'
import RegistrationConfirmation from './pages/RegistrationConfirmation'
import PasswordReset from './pages/PasswordReset'

const App: FC = () => {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Navigate to="/search" />} />
        <Route path="/search" element={<Search />} />
        <Route path="/iconclass" element={<IconClass />} />
        <Route path="/explore" element={<Explore />} />
        <Route path="/vise" element={<Vise />} />
        <Route path="/exhibitions" element={<Exhibitions />} />
        <Route path="/exhibitions/add" element={<NewExhibition />} />
        <Route path="/exhibitions/edit/:id" element={<NewExhibition />} />
        <Route path="/exhibitions/:id" element={<ExhibitionDetail />} />
        <Route path="/record-detail/:id" element={<RecordDetail />} />
        <Route path="/record-detail/:id/:switch" element={<RecordDetail />} />
        <Route path="/upload" element={<Upload />} />
        <Route
          path="/confirm-registration/"
          element={<RegistrationConfirmation />}
        />
        <Route path="/password-reset/:token" element={<PasswordReset />} />
        <Route
          path="*"
          element={
            <div>
              <h2>404 Page not found</h2>
            </div>
          }
        />
      </Routes>
    </Layout>
  )
}

export default App
