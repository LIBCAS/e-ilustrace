import React, { ChangeEvent, FC, useState } from 'react'
import { toast } from 'react-toastify'
import useUploadMutation from '../api/mutation/useUploadMutation'

const Upload: FC = () => {
  const [file, setFile] = useState<File | undefined>(undefined)
  const { mutateAsync } = useUploadMutation()

  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setFile(e.target.files[0])
    }
  }

  const handleUpload = async () => {
    if (!file) return

    const result = await mutateAsync({ file })

    if (result?.id) {
      toast.success(`Nahráno pod id: ${result.id}`)
    }
  }

  return (
    <div className="p-4">
      <input type="file" accept="text/xml" onChange={handleFileChange} />
      <br />
      <button
        type="button"
        className="mt-2 rounded-xl bg-red p-2 font-bold text-white"
        onClick={() => handleUpload()}
      >
        Upload
      </button>
    </div>
  )
}

export default Upload
