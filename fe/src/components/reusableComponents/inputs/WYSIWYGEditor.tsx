import { FC } from 'react'
import { SimpleEditor } from '@/components/tiptap-templates/simple/simple-editor'

type Props = {
  value: string
  label?: string
  onChange: (value: string) => void
}

const maxLength = 750

const WYSIWYGEditor: FC<Props> = ({ value, label = undefined, onChange }) => (
  <div className="mt-8">
    <span>{label}</span>
    <div className="mt-2">
      <SimpleEditor maxLength={maxLength} value={value} onChange={onChange} />
    </div>
  </div>
)

export default WYSIWYGEditor
