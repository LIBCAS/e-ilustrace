import { useTranslation } from 'react-i18next'
import { Dispatch, FC, SetStateAction } from 'react'

import KeyIcon from '../../assets/icons/key.svg?react'

import Button from '../../components/reusableComponents/Button'
import useMobile from '../../hooks/useMobile'
import { useThemeListQuery } from '../../api/theme'
import Loader from '../../components/reusableComponents/Loader'
import ShowError from '../../components/reusableComponents/ShowError'

import { useExploreStore } from '../../store/useExploreStore'
import LinkLikeButton from '../../components/reusableComponents/LinkLikeButton'
import PaletteIcon from '../../assets/icons/palette.svg?react'
import getThemeTranslation from '../../utils/getThemeTranslation'
import getThemeImage from '../../utils/getThemeImage'

type ThemeProps = {
  name: string
}

type ThemesProps = {
  setFilterOpen: Dispatch<SetStateAction<boolean>>
}

const Theme = ({ name }: ThemeProps) => {
  const { setThemes } = useExploreStore()
  const { i18n } = useTranslation()

  return (
    <button
      type="button"
      onClick={() => setThemes([name])}
      className="flex-1/3 mx-auto flex cursor-pointer flex-col items-center md:mx-0"
    >
      <img
        className="h-60 w-60 rounded-lg transition-all hover:scale-105"
        src={getThemeImage(name)}
        alt={i18n.resolvedLanguage === 'cs' ? name : getThemeTranslation(name)}
      />
      <h2 className="mt-2 font-bold uppercase">
        {i18n.resolvedLanguage === 'cs' ? name : getThemeTranslation(name)}
      </h2>
    </button>
  )
}

const Themes: FC<ThemesProps> = ({ setFilterOpen }) => {
  const { t } = useTranslation()
  const { isMobile } = useMobile()

  const { data, isLoading, isError } = useThemeListQuery()

  return (
    <section>
      <div className="mr-auto flex w-full items-center justify-between border-b-[1.5px] border-superlightgray pb-6 md:w-auto md:border-none md:pb-0">
        <h1 className="text-lg font-bold uppercase tracking-wider">
          {t('themes')}
        </h1>
        <div className="flex flex-wrap justify-end gap-4">
          <LinkLikeButton
            href="/exhibitions"
            className={isMobile ? 'bg-superlightgray' : ''}
            startIcon={<PaletteIcon />}
            variant={isMobile ? 'outlined' : 'primary'}
          >
            {isMobile ? t('create_exhibition') : t('go_to_create')}
          </LinkLikeButton>
          {isMobile && (
            <Button
              className="bg-superlightgray"
              variant="outlined"
              startIcon={<KeyIcon />}
              onClick={() => setFilterOpen(true)}
            >
              {t('keywords')}
            </Button>
          )}
        </div>
      </div>
      <div className="mt-4 flex flex-wrap content-center items-center gap-16">
        {isLoading ? (
          <div className="my-10 flex w-full items-center justify-center">
            <Loader />
          </div>
        ) : null}
        {!isLoading && isError ? (
          <div className="my-10 flex w-full items-center justify-center">
            <ShowError />
          </div>
        ) : null}
        {!isLoading && !isError && data
          ? data.items.map((i) => {
              return <Theme key={i.id} name={i.name} />
            })
          : null}
      </div>
    </section>
  )
}

export default Themes
