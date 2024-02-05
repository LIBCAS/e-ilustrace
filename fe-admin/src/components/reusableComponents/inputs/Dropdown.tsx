import { FC } from 'react'
import { useTranslation } from 'react-i18next'
import Select, { StylesConfig, components } from 'react-select'
import Loader from '../Loader'

type OptionType = {
  value: string
  label: string
}

type DropdownProps = {
  placeholder?: string
  options: OptionType[]
  value: OptionType | OptionType[] | null
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  onChange: (obj: any) => void
  isMulti?: boolean
  shortenValues?: boolean
  isSearchable?: boolean
  loading?: boolean
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const NoOptionsMessage = (props: any) => {
  const { t } = useTranslation()
  return (
    // eslint-disable-next-line react/jsx-props-no-spreading
    <components.NoOptionsMessage {...props}>
      <span className="custom-css-class">{t('search.no_options')}</span>
    </components.NoOptionsMessage>
  )
}

const Dropdown: FC<DropdownProps> = ({
  placeholder = undefined,
  options,
  value,
  onChange,
  isMulti = false,
  shortenValues = false,
  isSearchable = false,
  loading = false,
}) => {
  const customStyles: StylesConfig<OptionType> = {
    multiValue: (styles) => ({
      ...styles,
      // set all chips to a maximum of 70 pixels
      maxWidth: shortenValues ? '70px' : 'none',
    }),
    menu: (styles) => ({
      ...styles,
      borderRadius: '0.75rem',
      padding: '0.25rem 0',
      zIndex: 30,
    }),
    container: (styles) => ({
      ...styles,
      // height: '44px',
      // maxHeight: '44px',
    }),
    control: (styles, state) => ({
      ...styles,
      border: state.isFocused ? '2px solid #E2293F' : '2px solid #E9ECEF',
      '&:hover': {
        borderColor: '#E2293F',
      },
      // outline: '2px solid #E2293F',
      borderRadius: '0.75rem',
      padding: '0.15rem 0.75rem',
      whiteSpace: 'nowrap',
      boxShadow: 'none',
    }),
    option: (styles) => ({
      ...styles,
      cursor: 'pointer',
    }),
  }

  return (
    <div className="flex items-center">
      <div className="w-full">
        <Select
          isMulti={isMulti}
          styles={customStyles}
          options={options}
          value={value}
          placeholder={placeholder}
          isSearchable={isSearchable}
          components={{ NoOptionsMessage }}
          onChange={(newValue) => onChange(newValue)}
        />
      </div>
      {loading ? <Loader className="ml-2" /> : null}
    </div>
  )
}

export default Dropdown
