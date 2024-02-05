import { FC } from 'react'
import ReactQuill from 'react-quill'
import 'react-quill/dist/quill.snow.css'

type Props = {
  value: string
  label?: string
  onChange: (value: string) => void
}

const toolbarOptions = [
  ['bold', 'italic', 'underline'], // toggled buttons
  ['blockquote'],

  [{ header: 1 }, { header: 2 }], // custom button values
  [{ list: 'ordered' }, { list: 'bullet' }],
  // [{ script: 'sub' }, { script: 'super' }], // superscript/subscript
  // [{ indent: '-1' }, { indent: '+1' }], // outdent/indent
  // [{ direction: 'rtl' }], // text direction

  [{ size: ['small', false, 'large', 'huge'] }], // custom dropdown
  [{ header: [1, 2, 3, 4, 5, 6, false] }],

  [{ color: [] }], // dropdown with defaults from theme
  // [{ font: [] }],
  // [{ align: [] }],

  ['clean'], // remove formatting button
]

const WYSIWYGEditor: FC<Props> = ({ value, label = undefined, onChange }) => {
  return (
    <div className="mt-8">
      <span>{label}</span>
      <ReactQuill
        className="mt-2"
        value={value}
        modules={{ toolbar: toolbarOptions }}
        theme="snow"
        onChange={(newValue) => onChange(newValue)}
      />
    </div>
  )
}

export default WYSIWYGEditor
