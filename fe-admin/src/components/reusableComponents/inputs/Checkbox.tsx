import React from 'react'
import { Link } from 'react-router-dom'
import Check from '../../../assets/icons/check.svg?react'

type Props = {
  id: string
  name: string
  showName?: boolean
  link?: string
  linkName?: string
  className?: string
  wrapperClassName?: string
  checked: boolean
  onChange: (checked: boolean) => void
}

const Checkbox = ({
  id,
  name,
  showName = false,
  linkName = '',
  link = '',
  wrapperClassName = '',
  className = undefined,
  checked,
  onChange,
}: Props) => {
  return (
    <div className={`my-2 flex items-center ${wrapperClassName}`}>
      <label
        htmlFor={`checkbox-${id}`}
        className="relative flex cursor-pointer items-center"
      >
        <input
          id={`checkbox-${id}`}
          type="checkbox"
          onChange={() => onChange(!checked)}
          checked={checked}
          className="mr-4 h-[18px] min-h-[18px] w-[18px] min-w-[18px] cursor-pointer appearance-none rounded-[5px] border-[1.5px] border-lightgray bg-white"
        />
        <span className={`${className} font-bold`}>
          {showName ? name : ''}{' '}
        </span>
        {checked && (
          <Check className="text-md check-1 absolute left-[3px] top-[5px] h-[13px] w-[13px] fill-black text-opacity-0 transition" />
        )}
      </label>
      {linkName !== '' && link !== '' ? (
        <>
          {'\u00A0'}
          <Link to={link} className="cursor-pointer font-bold underline">
            {linkName}
          </Link>
        </>
      ) : (
        ''
      )}
    </div>
  )
}

export default Checkbox
