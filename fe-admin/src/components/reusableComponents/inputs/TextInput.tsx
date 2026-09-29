import { FC, ReactNode, useState } from 'react'

import { twMerge } from 'tailwind-merge'
import VisibilityIcon from '../../../assets/icons/visibility.svg?react'

type Props = {
  id: string
  placeholder?: string
  value?: string
  label?: string
  startIcon?: ReactNode
  className?: string
  parentClassName?: string
  type?: 'text' | 'password'
  onChange: (newValue: string) => void
}

const TextInput: FC<Props> = ({
  id,
  placeholder = undefined,
  value = '',
  label = undefined,
  startIcon = undefined,
  className = undefined,
  parentClassName = '',
  type = 'text',
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
          className="mb-2 block text-base font-medium text-black"
        >
          {label}
        </label>
      )}
      <div className="relative rounded-xl">
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
          className={twMerge(
            `block w-full border-2 border-transparent py-3 focus:border-lightgray focus:outline-none ${
              startIcon ? 'pl-12' : 'pl-3'
            } rounded-xl bg-superlightgray pr-6 sm:text-sm ${className}`
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
    </div>
  )
}

export default TextInput
