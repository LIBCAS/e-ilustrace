import { FC, ReactNode } from 'react'
import Loader from './Loader'

type Props = {
  children: ReactNode
  onClick?: () => void
  loading?: boolean
}

const LoginButton: FC<Props> = ({
  children,
  onClick = undefined,
  loading = false,
}) => {
  return (
    <button
      type="submit"
      onClick={onClick}
      disabled={loading}
      className="w-full whitespace-nowrap rounded-xl border-none bg-white px-4 py-3 text-base font-bold text-black transition hover:bg-superlightgray"
    >
      <div className="flex items-center justify-center gap-4">
        {children}
        {loading ? <Loader className="ml-2" /> : null}
      </div>
    </button>
  )
}

export default LoginButton
