import { FC } from 'react'
import { useTranslation } from 'react-i18next'
import TextInput from './TextInput'

type TProps = {
  fromValue: string
  toValue: string
  fromValueChange: (value: number) => void
  toValueChange: (value: number) => void
}

const NumberRangeInput: FC<TProps> = ({
  fromValue,
  toValue,
  fromValueChange,
  toValueChange,
}) => {
  const { t } = useTranslation()

  const handleFromValueChange = (number: string) => {
    const parsedNumber = Number(number.replace(/\D/g, '').slice(0, 4))
    if (parsedNumber > Number(toValue)) {
      fromValueChange(Number(toValue) - 1)
    } else {
      fromValueChange(parsedNumber)
    }
  }

  const handleToValueChange = (number: string) => {
    const parsedNumber = Number(number.replace(/\D/g, '').slice(0, 4))
    if (parsedNumber < Number(fromValue)) {
      toValueChange(Number(fromValue) + 1)
    } else {
      toValueChange(parsedNumber)
    }
  }

  return (
    <div className="flex items-center gap-4">
      <TextInput
        id="year-from"
        className="border-2 border-[#E9ECEF] bg-white"
        onChange={handleFromValueChange}
        value={fromValue}
        placeholder={t('search.from')}
      />
      -
      <TextInput
        id="year-tp"
        className="border-2 border-[#E9ECEF] bg-white"
        onChange={handleToValueChange}
        value={toValue}
        placeholder={t('search.to')}
      />
    </div>
  )
}

export default NumberRangeInput
