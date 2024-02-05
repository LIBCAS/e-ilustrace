import { useTranslation } from 'react-i18next'
import { toast } from 'react-toastify'
import useUserListQuery from '../api/query/useUserListQuery'
import Loader from '../components/reusableComponents/Loader'
import ShowError from '../components/reusableComponents/ShowError'
import useMeQueryWrapper from '../hooks/useMeQueryWrapper'
import Dropdown from '../components/reusableComponents/inputs/Dropdown'
import useChangeUserRoleMutation from '../api/query/useChangeUserRoleMutation'

const Users = () => {
  const { me, meLoading, meError } = useMeQueryWrapper()
  const { t } = useTranslation()
  const {
    data: users,
    isLoading: usersLoading,
    isError: usersError,
  } = useUserListQuery()

  const { mutateAsync } = useChangeUserRoleMutation()

  const allowedRoleChanges = me?.authorities.find(
    (a) => a.authority === 'SUPER_ADMIN'
  )
    ? ['USER', 'ADMIN', 'SUPER_ADMIN']
    : ['USER', 'ADMIN']

  return (
    <div className="min-h-[92vh] border-t border-superlightgray bg-white py-5">
      <div className="container">
        <h2 className="mb-10">{t('menu.users')}</h2>
        {usersLoading || meLoading ? (
          <div className="my-10 flex items-center justify-center">
            <Loader />
          </div>
        ) : null}
        {usersError || meError ? <ShowError /> : null}
        <div className="flex flex-col gap-5">
          {users?.items.map((u) => (
            <div key={u.id} className="flex items-center justify-start gap-2">
              <div className="max-w-[250px]">
                <Dropdown
                  options={allowedRoleChanges.map((a) => ({
                    value: a,
                    label: a,
                  }))}
                  value={{ value: u.role, label: u.role }}
                  onChange={(values) =>
                    mutateAsync({ userId: u.id, role: values.value })
                      .then(() => {
                        toast.success(t('users.role_changed'))
                      })
                      .catch(() => {
                        toast.error(t('users.role_error'))
                      })
                  }
                />
              </div>
              {' - '}
              {u.fullName} - {u.email}
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default Users
