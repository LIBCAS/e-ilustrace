import { useState, Dispatch, FC, SetStateAction } from 'react'
import { useTranslation } from 'react-i18next'

import clone from 'lodash/clone'
import MenuClose from '../../assets/icons/menu_close.svg?react'
import MenuOpen from '../../assets/icons/menu_open.svg?react'
import Up from '../../assets/icons/up.svg?react'
import Down from '../../assets/icons/down.svg?react'
import Close from '../../assets/icons/close.svg?react'
import FilterIcon from '../../assets/icons/filter.svg?react'
import WorldIcon from '../../assets/icons/world.svg?react'

import Checkbox from '../reusableComponents/inputs/Checkbox'
import Button from '../reusableComponents/Button'
import { useIconClassListQuery } from '../../api/iconclass'
import Loader from '../reusableComponents/Loader'
import ShowError from '../reusableComponents/ShowError'
import { TIconClassCategoryDefault } from '../../../../fe-shared/@types/iconClass'
import { useIconClassStore } from '../../store/useIconClassStore'

type IconClassFilterItemProps = {
  iccParent: {
    icc: TIconClassCategoryDefault
    children: TIconClassCategoryDefault[]
  }
}

const IconClassFilterItem: FC<IconClassFilterItemProps> = ({ iccParent }) => {
  const [isExpanded, setIsExpanded] = useState(false)
  const { t, i18n } = useTranslation('iconclass_codes')

  const { icc, setIcc } = useIconClassStore()

  const handleChange = (code: string) => {
    const iccClone = clone(icc)
    const index = iccClone.findIndex((i) => i === code)
    if (index >= 0) {
      iccClone.splice(index, 1)
      setIcc(iccClone)
    } else {
      setIcc([...iccClone, code])
    }
  }

  return (
    // eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-noninteractive-element-interactions
    <li
      className={`flex flex-col ${
        isExpanded ? 'border-b-[1.5px] border-superlightgray' : ''
      }`}
      onClick={() => iccParent.children.length && setIsExpanded(!isExpanded)}
    >
      <div className="flex cursor-pointer items-center justify-between py-2">
        <div className="flex items-center">
          <Checkbox
            id={iccParent.icc.id}
            name={
              i18n.resolvedLanguage === 'cs'
                ? `${iccParent.icc.code} - ${t(`${iccParent.icc.code}`)}`
                : `${iccParent.icc.code} - ${iccParent.icc.name}`
            }
            showName
            checked={!!icc.find((i) => i === iccParent.icc.code)}
            onChange={() => handleChange(iccParent.icc.code)}
          />
        </div>
        {/* eslint-disable-next-line no-nested-ternary */}
        {isExpanded ? <Up /> : iccParent.children.length ? <Down /> : null}
      </div>
      {isExpanded && iccParent.children.length && (
        // eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-noninteractive-element-interactions
        <ul className="pb-3 pl-8" onClick={(e) => e.stopPropagation()}>
          {iccParent.children?.map((suboption) => (
            <li key={suboption.id} className="flex flex-col text-left">
              <div className="flex items-center justify-between py-2">
                <div className="flex items-center">
                  <Checkbox
                    id={suboption.id}
                    name={
                      i18n.resolvedLanguage === 'cs'
                        ? `${suboption.code} - ${t(`${suboption.code}`)}`
                        : `${suboption.code} - ${suboption.name}`
                    }
                    showName
                    checked={!!icc.find((i) => i === suboption.code)}
                    onChange={() => handleChange(suboption.code)}
                  />
                </div>
              </div>
            </li>
          ))}
        </ul>
      )}
    </li>
  )
}

type IconClassFilterProps = {
  isMobile: boolean
  filterOpen: boolean
  setFilterOpen: Dispatch<SetStateAction<boolean>>
}

const IconClassFilter: FC<IconClassFilterProps> = ({
  isMobile,
  filterOpen,
  setFilterOpen,
}) => {
  const { t } = useTranslation('iconclass')

  const {
    data: icc,
    isLoading: iccLoading,
    isError: iccError,
  } = useIconClassListQuery()

  const filteredICC: {
    icc: TIconClassCategoryDefault
    children: TIconClassCategoryDefault[]
  }[] = []

  if (icc?.items) {
    for (let i = 0; i <= 9; i += 1) {
      icc.items
        .filter((ic) => ic.code !== '0')
        .forEach((ic) => {
          if (ic.code.startsWith(String(i)) && ic.code.length === 1) {
            filteredICC.push({
              icc: ic,
              children: icc.items.filter(
                (ch) => ch.code.startsWith(String(i)) && ch.code.length === 2
              ),
            })
          }
        })
    }
  }

  return (
    <div
      className={`flex h-full w-screen flex-col overflow-auto ${
        filterOpen
          ? 'w-full md:max-w-[280px] lg:max-w-[370px]'
          : 'w-0 max-md:hidden md:w-16'
      }`}
    >
      <div className="flex items-center justify-between px-6 pt-6 font-bold md:bg-superlightgray md:bg-opacity-30 md:p-4">
        <div className="flex w-full items-center justify-between border-b-[1.5px] border-superlightgray pb-2 md:border-none md:p-0">
          {filterOpen && (
            <h2 className="text-2xl md:text-base">{t('themes')}</h2>
          )}
          {/* eslint-disable-next-line no-nested-ternary */}
          {filterOpen ? (
            !isMobile ? (
              <MenuClose
                className="cursor-pointer"
                onClick={() => setFilterOpen(false)}
              />
            ) : (
              <Close
                className="cursor-pointer"
                onClick={() => setFilterOpen(false)}
              />
            )
          ) : (
            <MenuOpen
              className="cursor-pointer"
              onClick={() => setFilterOpen(true)}
            />
          )}
        </div>
      </div>
      {iccLoading ? (
        <div className="my-10 flex items-center justify-center">
          <Loader />
        </div>
      ) : null}
      {!iccLoading && iccError ? (
        <div className="px-5">
          <ShowError />
        </div>
      ) : null}
      {!iccLoading && !iccError && icc?.items ? (
        <ul
          className={`${
            filterOpen ? '' : 'hidden'
          } h-[calc(100%-250px)] overflow-auto p-6 md:h-fit`}
        >
          {filteredICC.map((i) => (
            <IconClassFilterItem key={i.icc.id} iccParent={i} />
          ))}
        </ul>
      ) : null}
      {isMobile && (
        <div className="flex justify-between gap-4 px-4">
          <Button className="w-full bg-superlightgray" variant="outlined">
            {t('deselect_all')}
          </Button>
          <Button
            className="w-full"
            variant="submit"
            startIcon={<FilterIcon />}
          >
            {t('filter')}
          </Button>
        </div>
      )}
      {filterOpen ? (
        <div className="mx-auto mt-5 w-full max-w-[90%]">
          <Button
            className="w-full"
            href="https://iconclass.org/en/_"
            variant="secondary"
            startIcon={<WorldIcon />}
            target="_blank"
          >
            {t('go_to_iconclass')}
          </Button>
        </div>
      ) : null}
    </div>
  )
}

export default IconClassFilter
