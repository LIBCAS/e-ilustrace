import { ReactNode } from 'react'

type Props = {
  animate?: boolean
  dense?: boolean
  iconButton?: boolean
  variant?: 'primary' | 'secondary' | 'submit' | 'text' | 'outlined'
  children?: ReactNode
  onClick?: () => void
  isBlock?: boolean
  disabled?: boolean
  className?: string
  href?: string
  target?: string
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

const Button = ({
  animate = false,
  dense = false,
  iconButton = false,
  variant = 'primary',
  children = undefined,
  onClick = undefined,
  className = '',
  disabled = false,
  href = undefined,
  target = undefined,
  isBlock = true,
  width = undefined,
  startIcon = undefined,
  endIcon = undefined,
}: Props) => {
  const padding = dense ? 'px-3 py-1' : 'px-4 py-2'

  const disabledStyle = disabled
    ? 'opacity-50 cursor-not-allowed'
    : 'transition ease-in-out duration-300 hover:cursor-pointer'

  let baseClasses = [
    'rounded-xl',
    'whitespace-nowrap',
    textSize,
    border[variant],
    backgroundColors[variant],
    color[variant],
    iconButton ? iconPadding : padding,
    disabledStyle,
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
  if (href) {
    const linkClasses = [
      ...baseClasses,
      'flex items-center justify-center whitespace-nowrap',
    ]
    return (
      <a
        href={href}
        target={target}
        onClick={onClick}
        className={linkClasses.join(' ')}
      >
        <div className="flex items-center gap-2">
          {startIcon}
          {children}
          {endIcon}
        </div>
      </a>
    )
  }

  return (
    <button
      type={variant === 'submit' ? 'submit' : 'button'}
      onClick={onClick}
      className={baseClasses.join(' ')}
      disabled={disabled}
    >
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
    </button>
  )
}

export default Button
