import { ReactNode } from 'react'
import { Link } from 'react-router-dom'

type Props = {
  animate?: boolean
  dense?: boolean
  iconButton?: boolean
  variant?: 'primary' | 'secondary' | 'submit' | 'text' | 'outlined'
  children?: ReactNode
  isBlock?: boolean
  className?: string
  href: string
  width?: string
  startIcon?: ReactNode
  endIcon?: ReactNode
}

const textSize = 'text-base font-bold'

const iconPadding = 'p-2'

const color = {
  primary: 'text-white hover:text-superlightgray',
  secondary: 'text-black',
  outlined: 'text-black',
  submit: 'text-white hover:text-superlightgray',
  text: 'text-black',
}

const backgroundColors = {
  primary: 'bg-black hover:shadow-xl',
  secondary: 'bg-transparent hover:bg-superlightgray',
  outlined: 'bg-transparent hover:bg-superlightgray',
  submit: 'bg-red hover:shadow-xl',
  text: 'bg-transparent hover:bg-superlightgray',
}

const border = {
  primary: 'border-2 border-black',
  secondary: 'border-2 border-black',
  outlined: 'border-2 border-superlightgray',
  submit: 'border-2 border-red',
  text: 'border-none',
}

const LinkLikeButton = ({
  animate = false,
  dense = false,
  iconButton = false,
  variant = 'primary',
  children = undefined,
  className = '',
  href,
  isBlock = true,
  width = undefined,
  startIcon = undefined,
  endIcon = undefined,
}: Props) => {
  const padding = dense ? 'px-3 py-1' : 'px-4 py-2'

  let baseClasses = [
    'rounded-xl',
    'whitespace-nowrap',
    textSize,
    border[variant],
    backgroundColors[variant],
    color[variant],
    iconButton ? iconPadding : padding,
  ]

  if (className) {
    baseClasses = [...baseClasses, ...className.split(' ')]
  }
  if (isBlock) {
    baseClasses = [...baseClasses, 'block']
  }
  if (width) {
    baseClasses = [...baseClasses, width]
  }

  return (
    <Link to={href} className={baseClasses.join(' ')}>
      <div className="button flex items-center gap-4">
        {startIcon}
        <span className="mx-auto">{children}</span>
        {endIcon && animate ? (
          <div className="icon transition duration-300 ease-in-out">
            {endIcon}
          </div>
        ) : (
          endIcon
        )}
      </div>
    </Link>
  )
}

export default LinkLikeButton
