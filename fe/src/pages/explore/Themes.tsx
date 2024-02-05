import { useTranslation } from 'react-i18next'
import { Dispatch, FC, SetStateAction } from 'react'

import KeyIcon from '../../assets/icons/key.svg?react'

import Button from '../../components/reusableComponents/Button'
import useMobile from '../../hooks/useMobile'
import useThemeListQuery from '../../api/query/useThemeListQuery'
import Loader from '../../components/reusableComponents/Loader'
import ShowError from '../../components/reusableComponents/ShowError'

import Architecture from '../../assets/images/architecture.jpg'
import Bible from '../../assets/images/bible.jpg'
import Erby from '../../assets/images/heraldry.jpg'
import People from '../../assets/images/characters.jpg'
import Places from '../../assets/images/locations.jpg'
import Objects from '../../assets/images/objects.jpg'
import Plants from '../../assets/images/plants.jpg'
import Military from '../../assets/images/military.jpg'
import Animals from '../../assets/images/animals.jpg'
import { useExploreStore } from '../../store/useExploreStore'
import LinkLikeButton from '../../components/reusableComponents/LinkLikeButton'
import PaletteIcon from '../../assets/icons/palette.svg?react'

type ThemeProps = {
  name: string
}

type ThemesProps = {
  setFilterOpen: Dispatch<SetStateAction<boolean>>
}

const getImage = (name: string) => {
  let image

  switch (name) {
    case 'Architektura':
      image = Architecture
      break
    case 'Bible':
      image = Bible
      break
    case 'Erby':
      image = Erby
      break
    case 'Lidé a postavy':
      image = People
      break
    case 'Místa':
      image = Places
      break
    case 'Předměty':
      image = Objects
      break
    case 'Rostliny':
      image = Plants
      break
    case 'Vojenství':
      image = Military
      break
    case 'Zvířata':
      image = Animals
      break
    default:
    // image = PhotoIcon
  }

  return image
}

const Theme = ({ name }: ThemeProps) => {
  const { t } = useTranslation()
  const { setThemes } = useExploreStore()

  return (
    <button
      type="button"
      onClick={() => setThemes([name])}
      className="flex-1/3 mx-auto flex cursor-pointer flex-col items-center md:mx-0"
    >
      <img
        className="h-60 w-60  rounded-lg transition-all hover:scale-105"
        src={getImage(name)}
        alt={name}
      />
      <h2 className="mt-2 font-bold uppercase">{t(name)}</h2>
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
        <h1 className="text-xl font-bold uppercase tracking-wider">
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
