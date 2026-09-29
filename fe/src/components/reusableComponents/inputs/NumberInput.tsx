import { FC, ReactNode } from 'react'

import clsx from 'clsx'

type Props = {
  id: string
  placeholder?: string
  value?: number
  min?: number
  max?: number
  label?: string
  error?: string
  startIcon?: ReactNode
  className?: string
  color?: 'red' | 'white'
  onChange: (newValue: string) => void
}

const TextInput: FC<Props> = ({
  id,
  placeholder = undefined,
  value = '',
  min = undefined,
  max = undefined,
  label = undefined,
  error = undefined,
  startIcon = undefined,
  className = undefined,
  color = 'white',
  onChange,
}) => {
  return (
    <div className="w-full">
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
          type="number"
          id={id}
          value={value}
          min={min}
          max={max}
          step={1}
          pattern="[0-9]+"
          inputMode="numeric"
          placeholder={placeholder}
          className={clsx(
            `block w-full py-3 outline-1 transition ${
              startIcon ? 'pl-12' : 'pl-3'
            } border-gray-300 rounded-xl bg-superlightgray pr-6 sm:text-sm`,
            {
              'font-bold placeholder-white outline-superlightgray':
                color === 'red',
            },
            { 'bg-white': color === 'red' },
            className
          )}
          onChange={(event) => onChange(event.target.value)}
        />
      </div>
      {error ? <span className="pl-2 text-white">{error}</span> : null}
    </div>
  )
}

export default TextInput
