import { useTranslation } from 'react-i18next'
import { toast } from 'react-toastify'
import { useState } from 'react'
import Loader from '../components/reusableComponents/Loader'
import ShowError from '../components/reusableComponents/ShowError'
import useMeQueryWrapper from '../hooks/useMeQueryWrapper'
import Dropdown from '../components/reusableComponents/inputs/Dropdown'
import {
  useChangeUserRoleMutation,
  useDeleteUserMutation,
  useUserListQuery,
} from '../api/user'
import TextInput from '../components/reusableComponents/inputs/TextInput'
import Button from '../components/reusableComponents/Button'

const Users = () => {
  const { me, meLoading, meError } = useMeQueryWrapper()
  const [search, setSearch] = useState('')
  const { t } = useTranslation()
  const {
    data: users,
    isLoading: usersLoading,
    isError: usersError,
  } = useUserListQuery(search)

  const { mutateAsync: changeRole } = useChangeUserRoleMutation()
  const { mutateAsync: deleteUser } = useDeleteUserMutation()

  const allowedChanges = me?.authorities.find(
    (a) => a.authority === 'SUPER_ADMIN'
  )
    ? ['USER', 'ADMIN', 'SUPER_ADMIN']
    : ['USER', 'ADMIN']

  return (
    <div className="min-h-[92vh] border-t border-superlightgray bg-white py-5">
      <div className="container">
        <h2 className="mb-10">{t('menu.users')}</h2>
        <div className="my-5">
          <TextInput
            placeholder={t('users.search')}
            id="user-search"
            value={search}
            onChange={(value) => setSearch(value)}
          />
        </div>
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
                  options={allowedChanges.map((a) => ({
                    value: a,
                    label: a,
                  }))}
                  value={{ value: u.role, label: u.role }}
                  onChange={(values) =>
                    changeRole({ userId: u.id, role: values.value })
                      .then(() => {
                        toast.success(t('users.role_changed'))
                      })
                      .catch(() => {
                        toast.error(t('users.role_error'))
                      })
                  }
                />
              </div>
              <div className="max-w-[250px]">
                {(allowedChanges.some((a) => a === 'SUPER_ADMIN') &&
                  u.role !== 'SUPER_ADMIN') ||
                (allowedChanges.some((a) => a === 'ADMIN') &&
                  u.role === 'USER') ? (
                  <Button
                    variant="secondary"
                    onClick={() =>
                      deleteUser(u.id)
                        .then(() => toast.success(t('users.user_deleted')))
                        .catch(() =>
                          toast.error(t('users.user_deletion_error'))
                        )
                    }
                  >
                    {t('users.delete_user')}
                  </Button>
                ) : null}
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
