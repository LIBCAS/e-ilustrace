import React, { FC } from 'react'
import { Link, useMatch, useResolvedPath } from 'react-router-dom'

type SidebarItemProps = {
  to: string
  children: React.ReactNode
  setSidebarOpen: (opened: boolean) => void
}

const SidebarItem: FC<SidebarItemProps> = ({
  to,
  children,
  setSidebarOpen,
}) => {
  const resolvedPath = useResolvedPath(to)
  const isActive = useMatch({ path: resolvedPath.pathname, end: false })

  return (
    <li
      className={`sidebar-item ${
        isActive ? 'border-r-[6px] border-white' : ''
      }`}
    >
      <Link
        className="py-2 transition-all duration-200"
        to={to}
        onClick={() => setSidebarOpen(false)}
      >
        {children}
      </Link>
    </li>
  )
}

export default SidebarItem
