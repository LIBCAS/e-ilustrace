import React, { FC, Fragment, useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import { toast } from 'react-toastify'
import DOMPurify from 'dompurify'
import dayjs from 'dayjs'
import LeftArrow from '../assets/icons/navigate_back.svg?react'
import Delete from '../assets/icons/delete.svg?react'
import StorylineIcon from '../assets/icons/storyline.svg?react'
import AlbumIcon from '../assets/icons/album.svg?react'
import SliderIcon from '../assets/icons/slider.svg?react'

import Button from '../components/reusableComponents/Button'
import ShareButtons from '../components/exhibitions/ShareButtons'
import AlbumView from '../components/exhibitions/AlbumView'
import StorylineView from '../components/exhibitions/StorylineView'
import SliderView from '../components/exhibitions/SliderView'
import { ExhibitionView } from '../../../fe-shared/@types/common'
import {
  useExhibitionDetailQuery,
  useDeleteExhibitionMutation,
} from '../api/exhibition'
import Loader from '../components/reusableComponents/Loader'
import ShowError from '../components/reusableComponents/ShowError'
import ShowInfoMessage from '../components/reusableComponents/ShowInfoMessage'
import useMeQueryWrapper from '../hooks/useMeQueryWrapper'
import generateSearchSearchParams from '../utils/generateSearchSearchParams'
import constructSearchUrl from '../utils/constructSearchUrl'

const ExhibitionDetail: FC = () => {
  const { t, i18n } = useTranslation()
  const params = useParams()
  const [exhibitionView, setExhibitionView] =
    useState<ExhibitionView>('STORYLINE')
  const navigate = useNavigate()

  const { me, meLoading, meError } = useMeQueryWrapper()
  const {
    data: exhibition,
    isLoading,
    isError,
  } = useExhibitionDetailQuery(params.id as string)
  const { mutateAsync: doDelete } = useDeleteExhibitionMutation()

  useEffect(() => {
    if (exhibition) {
      setExhibitionView(exhibition.radio)
    }
  }, [exhibition])

  const handleDeletion = (id: string) => {
    toast
      .promise(doDelete({ id }), {
        pending: t('exhibitions:deletion_in_progress'),
        success: t('exhibitions:exhibition_deleted'),
        error: t('exhibitions:deletion_error'),
      })
      .then(() => {
        navigate('/exhibitions')
      })
  }

  const exhibitionCanBeSeen =
    !!exhibition && (!!exhibition.published || exhibition.user.id === me?.id)

  return (
    <section className="pb-10">
      <div className="border-[1.5px] border-superlightgray py-10">
        {exhibitionCanBeSeen ? (
          <div className="mr-8">
            <div className="mx-auto flex max-w-7xl items-center">
              <LeftArrow
                className="cursor-pointer text-red"
                onClick={() => {
                  navigate(-1)
                }}
              />
              <h1 className="text-left text-4xl font-bold">
                {exhibition?.name}
              </h1>
              {exhibition ? (
                <ShareButtons
                  exhibition={exhibition}
                  canEditShare={exhibition.user.id === me?.id}
                />
              ) : null}
            </div>
          </div>
        ) : null}
      </div>
      <div className="mx-auto flex max-w-7xl flex-col px-8">
        {isLoading || meLoading ? (
          <div className="my-10 flex items-center justify-center">
            <Loader />
          </div>
        ) : null}
        {!isLoading && !meLoading && (isError || meError) ? (
          <ShowError />
        ) : null}
        {!isLoading &&
        !meLoading &&
        !isError &&
        !meError &&
        !exhibitionCanBeSeen ? (
          <ShowInfoMessage message={t('exhibitions:exhibition_not_found')} />
        ) : null}
        {!isLoading &&
        !meLoading &&
        !isError &&
        !meError &&
        exhibitionCanBeSeen ? (
          <>
            {exhibition.user.id === me?.id ? (
              <div className="flex w-full flex-row items-center justify-between border-b border-b-superlightgray py-8">
                <a
                  href={
                    i18n.resolvedLanguage === 'cs'
                      ? 'https://e-ilustrace.cz/napoveda/'
                      : 'https://e-ilustrace.cz/en/help/'
                  }
                  target="_blank"
                  className="font-bold text-black underline"
                  rel="noreferrer"
                >
                  {t('exhibitions:how_to_use_exhibitions')}
                </a>
                <div className="flex gap-3">
                  <Button
                    variant="text"
                    startIcon={<Delete />}
                    onClick={() => handleDeletion(exhibition.id)}
                  >
                    {t('exhibitions:delete_exhibition')}
                  </Button>
                  <Button
                    variant="submit"
                    onClick={() =>
                      navigate(`/exhibitions/edit/${exhibition.id}`)
                    }
                  >
                    {t('exhibitions:edit_exhibition')}
                  </Button>
                </div>
              </div>
            ) : null}
            <div className="flex w-full flex-col border-b border-superlightgray py-8">
              <span>
                {t('exhibitions:author')} {exhibition?.user.fullName}
              </span>
              <span>
                {t('exhibitions:created')}{' '}
                {dayjs(exhibition?.created).format('DD. MM. YYYY')}
              </span>
              <p
                dangerouslySetInnerHTML={{
                  __html: DOMPurify.sanitize(exhibition.description),
                }}
              />
              <p className="mt-2">
                <span className="font-bold">
                  {t('exhibitions:featured_artists')}
                </span>
                <span>
                  {exhibition.items
                    .filter((i) => i.illustration.mainAuthor?.author.fullName)
                    .map((i, index, { length }) => {
                      const searchParams = generateSearchSearchParams({
                        filterAuthor: [
                          {
                            value: i.illustration.mainAuthor?.author.id || '',
                            label:
                              i.illustration.mainAuthor?.author.fullName || '',
                          },
                        ],
                        type: 'ILLUSTRATION',
                        view: 'LIST',
                      })
                      return (
                        <Fragment
                          key={`exhibition-detail-${i.illustration.id}`}
                        >
                          <Link
                            target="_blank"
                            className="underline hover:text-red"
                            to={constructSearchUrl(
                              `type=${searchParams.type}&view=${searchParams.view}&filterAuthor=${searchParams.filterAuthor}`
                            )}
                          >
                            {i.illustration.mainAuthor?.author.fullName}
                          </Link>
                          {/* eslint-disable-next-line no-nested-ternary */}
                          {i.illustration.mainAuthor?.author.fullName.lastIndexOf(
                            ','
                          ) ===
                          // eslint-disable-next-line no-unsafe-optional-chaining
                          i.illustration.mainAuthor?.author.fullName.length - 1
                            ? ' '
                            : index !== length - 1
                              ? ', '
                              : null}
                        </Fragment>
                      )
                    })}
                </span>
              </p>
            </div>
            <div
              className={`flex items-center py-8 ${
                exhibitionView !== 'SLIDER'
                  ? 'border-b border-superlightgray'
                  : ''
              } `}
            >
              <span>
                {exhibition.items.length} {t('exhibitions:artworks')}
              </span>
              <div className="ml-auto flex gap-2">
                <Button
                  startIcon={<StorylineIcon />}
                  variant={
                    exhibitionView === 'STORYLINE' ? 'primary' : 'secondary'
                  }
                  onClick={() => {
                    setExhibitionView('STORYLINE')
                  }}
                >
                  Storyline
                </Button>
                <Button
                  startIcon={<AlbumIcon />}
                  variant={exhibitionView === 'ALBUM' ? 'primary' : 'secondary'}
                  onClick={() => {
                    setExhibitionView('ALBUM')
                  }}
                >
                  Album
                </Button>
                <Button
                  startIcon={<SliderIcon />}
                  variant={
                    exhibitionView === 'SLIDER' ? 'primary' : 'secondary'
                  }
                  onClick={() => {
                    setExhibitionView('SLIDER')
                  }}
                >
                  Slider
                </Button>
              </div>
            </div>
          </>
        ) : null}

        {exhibitionCanBeSeen ? (
          <>
            {exhibitionView === 'STORYLINE' && (
              <div className="border-b border-superlightgray py-8">
                <StorylineView items={exhibition?.items || []} />
              </div>
            )}
            {exhibitionView === 'ALBUM' && (
              <div className="border-b border-superlightgray py-8">
                <AlbumView items={exhibition?.items || []} />
              </div>
            )}
          </>
        ) : null}
      </div>
      {exhibitionView === 'SLIDER' && exhibitionCanBeSeen && (
        <SliderView exhibition={exhibition} />
      )}
      {/* <div className="mx-auto mt-5 flex max-w-7xl flex-col px-8"> */}
      {/*  {exhibition ? ( */}
      {/*    <ShareButtons */}
      {/*      exhibition={exhibition} */}
      {/*      canEditShare={exhibition.user.id === me?.id} */}
      {/*    /> */}
      {/*  ) : null} */}
      {/* </div> */}

      {/* {exhibitionCanBeSeen ? ( */}
      {/*  <div className="mx-auto max-w-7xl"> */}
      {/*    <p className="mt-2 py-8"> */}
      {/*      <span className="font-bold"> */}
      {/*        {t('exhibitions:explore_artists')} */}
      {/*      </span> */}
      {/*      <span className="underline"> */}
      {/*        {exhibition.items */}
      {/*          .filter((i) => i.illustration.mainAuthor?.author.fullName) */}
      {/*          .map((i) => i.illustration.mainAuthor?.author.fullName) */}
      {/*          .join(', ')} */}
      {/*      </span> */}
      {/*    </p> */}
      {/*  </div> */}
      {/* ) : null} */}
    </section>
  )
}

export default ExhibitionDetail
