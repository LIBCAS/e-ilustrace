import { FC, ReactElement } from 'react'
import { ChatBubbleOvalLeftEllipsisIcon } from '@heroicons/react/24/outline'

type TShowErrorProps = {
  message: string | ReactElement
}

const ShowInfoMessage: FC<TShowErrorProps> = ({ message }) => {
  return (
    <div className="my-16 text-center">
      <ChatBubbleOvalLeftEllipsisIcon className="mx-auto mb-2 h-10" />
      <span>{message}</span>
    </div>
  )
}

export default ShowInfoMessage
