import { FC } from 'react'
import { useTranslation } from 'react-i18next'
import Select, {
  StylesConfig,
  components,
  CSSObjectWithLabel,
} from 'react-select'
import {
  TDropdown,
  TDropdownWithOperator,
} from '../../../../../fe-shared/@types/common'

type DropdownProps = {
  placeholder?: string
  options: TDropdownWithOperator[] | TDropdown[]
  value:
    | TDropdownWithOperator[]
    | TDropdown[]
    | TDropdownWithOperator
    | TDropdown
    | null
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  onChange: (obj: any) => void
  onInputChange?: (value: string) => void
  isMulti?: boolean
  isLoading?: boolean
  useStrictFilter?: boolean
  shortenValues?: boolean
  isSearchable?: boolean
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const NoOptionsMessage = (props: any) => {
  const { t } = useTranslation('search')
  return (
    // eslint-disable-next-line react/jsx-props-no-spreading
    <components.NoOptionsMessage {...props}>
      <span>{t('no_options')}</span>
    </components.NoOptionsMessage>
  )
}

const Dropdown: FC<DropdownProps> = ({
  placeholder = undefined,
  options,
  value,
  onChange,
  onInputChange = undefined,
  isLoading = false,
  isMulti = false,
  useStrictFilter = true,
  shortenValues = false,
  isSearchable = false,
}) => {
  const { t } = useTranslation()

  const customStyles: StylesConfig = {
    multiValue: (styles: CSSObjectWithLabel) => ({
      ...styles,
      // set all chips to a maximum of 70 pixels
      maxWidth: shortenValues ? '70px' : 'none',
    }),
    menu: (styles: CSSObjectWithLabel) => ({
      ...styles,
      borderRadius: '0.75rem',
      padding: '0.25rem 0',
      zIndex: 30,
    }),
    container: (styles: CSSObjectWithLabel) => ({
      ...styles,
      // height: '44px',
      // maxHeight: '44px',
    }),
    control: (styles: CSSObjectWithLabel, { isFocused }) => ({
      ...styles,
      border: isFocused ? '2px solid #E2293F' : '2px solid #E9ECEF',
      '&:hover': {
        borderColor: '#E2293F',
      },
      // outline: '2px solid #E2293F',
      borderRadius: '0.75rem',
      padding: '0.15rem 0.75rem',
      whiteSpace: 'nowrap',
      boxShadow: 'none',
    }),
    option: (styles: CSSObjectWithLabel) => ({
      ...styles,
      cursor: 'pointer',
      borderBottom: '1px solid #EEE',
    }),
  }

  return (
    <Select
      isMulti={isMulti}
      styles={customStyles}
      options={options}
      value={value}
      placeholder={placeholder}
      isSearchable={isSearchable}
      isLoading={isLoading}
      components={{ NoOptionsMessage }}
      loadingMessage={() => t('common:loading')}
      filterOption={(option, searchText) =>
        useStrictFilter
          ? option.label.toLowerCase().startsWith(searchText.toLowerCase())
          : option.label.toLowerCase().includes(searchText.toLowerCase())
      }
      onInputChange={onInputChange}
      onChange={(newValue) => onChange(newValue)}
    />
  )
}

export default Dropdown
