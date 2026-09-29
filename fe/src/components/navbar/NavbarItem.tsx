import React, { FC } from 'react'
import { Link, useMatch, useResolvedPath } from 'react-router-dom'

type NavbarItemProps = {
  to: string
  children: React.ReactNode
}

const NavbarItem: FC<NavbarItemProps> = ({ to, children }) => {
  const resolvedPath = useResolvedPath(to)
  const isActive = useMatch({ path: resolvedPath.pathname, end: false })

  return (
    <Link
      className={`rounded-3xl px-5 py-3 text-sm transition-all duration-200 hover:bg-superlightgray ${
        isActive ? 'bg-superlightgray' : ''
      }`}
      to={to}
    >
      {children}
    </Link>
  )
}

export default NavbarItem
