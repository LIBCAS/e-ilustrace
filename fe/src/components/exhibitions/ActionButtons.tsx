import React from 'react'

import Delete from '../../assets/icons/delete.svg?react'
import DownArrow from '../../assets/icons/down.svg?react'
import UpArrow from '../../assets/icons/up.svg?react'
import Button from '../reusableComponents/Button'

const ActionButtons = () => {
  return (
    <div className="flex gap-1">
      <Button
        startIcon={<Delete />}
        className="flex h-10 w-10 items-center justify-center px-2 py-2"
      />
      <Button
        variant="text"
        startIcon={<UpArrow />}
        className="flex h-10 w-10 items-center justify-center border-lightgray bg-lightgray px-2 py-2 text-gray"
      />
      <Button
        variant="text"
        startIcon={<DownArrow />}
        className="flex h-10 w-10 items-baseline justify-center border-lightgray bg-lightgray px-2 py-2 text-gray"
      />
    </div>
  )
}

export default ActionButtons
