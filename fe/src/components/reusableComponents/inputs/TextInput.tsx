import { FC, ReactNode, useState } from 'react'

import clsx from 'clsx'
import VisibilityIcon from '../../../assets/icons/visibility.svg?react'

type Props = {
  id: string
  placeholder?: string
  value?: string
  label?: string
  error?: string
  startIcon?: ReactNode
  className?: string
  parentClassName?: string
  type?: 'text' | 'password'
  color?: 'red' | 'white'
  onChange: (newValue: string) => void
}

const TextInput: FC<Props> = ({
  id,
  placeholder = undefined,
  value = '',
  label = undefined,
  error = undefined,
  startIcon = undefined,
  className = '',
  parentClassName = '',
  type = 'text',
  color = 'white',
  onChange,
}) => {
  const [passwordType, setPasswordType] = useState(type)
  const togglePassword = () => {
    if (passwordType === 'password') {
      setPasswordType('text')
      return
    }
    setPasswordType('password')
  }

  return (
    <div className={`w-full ${parentClassName}`}>
      {label && (
        <label
          htmlFor={id}
          className="text-md mb-2 block font-medium text-black"
        >
          {label}
        </label>
      )}
      <div className="relative rounded-xl shadow-sm">
        {startIcon && (
          <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
            <div className="ml-1 text-black">{startIcon}</div>
          </div>
        )}
        <input
          type={passwordType}
          id={id}
          value={value}
          placeholder={placeholder}
          className={clsx(
            `block w-full py-3 outline-1 transition ${
              startIcon ? 'pl-12' : 'pl-3'
            } border-gray-300 rounded-xl bg-superlightgray pr-6 sm:text-sm`,
            {
              'font-bold placeholder-white outline-superlightgray':
                color === 'red',
            },
            { 'bg-white': color === 'red' && value.length > 0 },
            {
              'bg-opacity-30 caret-white':
                color === 'red' && value.length === 0,
            },
            // { 'bg-superlightgray': color === 'white' },
            className
          )}
          onChange={(event) => onChange(event.target.value)}
        />
        {type === 'password' ? (
          <button
            aria-label="Toggle visibility"
            type="button"
            className="absolute inset-y-0 right-0 flex cursor-pointer items-center pr-3"
            onClick={() => togglePassword()}
          >
            <div className={`ml-1 ${value ? 'text-black' : 'text-white'}`}>
              <VisibilityIcon />
            </div>
          </button>
        ) : null}
      </div>
      {error ? <span className="pl-2 text-white">{error}</span> : null}
    </div>
  )
}

export default TextInput
