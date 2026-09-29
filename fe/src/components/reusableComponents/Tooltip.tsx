import { FC, ReactNode } from 'react'

type Props = {
  children: ReactNode
  className?: string
  content: ReactNode
  show?: boolean
}

const Tooltip: FC<Props> = ({
  children,
  className = '',
  content,
  show = true,
}) => {
  const rootClassName = className ? `group ${className}` : 'group relative inline-flex'

  return (
    <span className={rootClassName}>
      {show ? (
        <span className="pointer-events-none absolute bottom-full right-0 mb-2 whitespace-nowrap rounded bg-black/85 px-2 py-1 text-xs font-normal text-white opacity-0 transition-opacity duration-100 group-hover:opacity-100">
          {content}
        </span>
      ) : null}
      {children}
    </span>
  )
}

export default Tooltip
