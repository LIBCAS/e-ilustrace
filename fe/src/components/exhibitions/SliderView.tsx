import { Swiper, SwiperSlide } from 'swiper/react'
import React, { FC, Fragment, useRef, useState } from 'react'
import { FullScreen, useFullScreenHandle } from 'react-full-screen'

import 'swiper/css'

import { Swiper as SwiperType } from 'swiper/types'

import { PhotoIcon } from '@heroicons/react/24/outline'
import { useTranslation } from 'react-i18next'
import { Link } from 'react-router'
import DownArrow from '../../assets/icons/down.svg?react'
import UpArrow from '../../assets/icons/up.svg?react'
import FullScreenIcon from '../../assets/icons/fullscreen.svg?react'
import Close from '../../assets/icons/close.svg?react'
import { TExhibitionDetail } from '../../../../fe-shared/@types/exhibition'
import constructRecordDetailUrl from '../../utils/constructRecordDetailUrl'
import constructSearchUrl from '../../utils/constructSearchUrl'
import generateSearchSearchParams from '../../utils/generateSearchSearchParams'
import sanitizeWysiwygHtml from '../../utils/sanitizeWysiwygHtml'

const BlankImage = ({ classNames }: { classNames: string }) => {
  return <PhotoIcon className={`text-lightgray ${classNames}`} />
}

type Props = {
  exhibition: TExhibitionDetail
}

const SliderView: FC<Props> = ({ exhibition }) => {
  const [currentNumber, setCurrentNumber] = useState(1)
  const swiperRef = useRef<SwiperType | null>(null)
  const handle = useFullScreenHandle()
  const { t } = useTranslation()
  const totalSlides = exhibition.items.length + 1
  const imageMaxHeightClass = handle.active
    ? 'max-h-[calc(100vh-12rem)]'
    : 'max-h-[540px]'

  const prefaceImage: { image: string; iilId: string } = {
    image: '',
    iilId: '',
  }

  const prefaceIll = exhibition.items.find(
    (i) =>
      i.preface &&
      (i.illustration.illustrationScan?.id || i.illustration.pageScan?.id)
  )

  if (
    prefaceIll &&
    (prefaceIll.illustration.illustrationScan?.id ||
      prefaceIll.illustration.pageScan?.id)
  ) {
    prefaceImage.image =
      prefaceIll.illustration.illustrationScan?.id ||
      prefaceIll.illustration.pageScan?.id ||
      ''
    prefaceImage.iilId = prefaceIll.illustration.id
  } else {
    const normalIll =
      exhibition.items.find((i) => i.illustration.illustrationScan?.id) ||
      exhibition.items.find((i) => i.illustration.pageScan?.id)
    if (normalIll) {
      prefaceImage.image =
        normalIll.illustration.illustrationScan?.id ||
        normalIll.illustration.pageScan?.id ||
        ''
      prefaceImage.iilId = normalIll.illustration.id
    }
  }

  return (
    <FullScreen handle={handle}>
      <div
        className="relative bg-[#212121]"
        // onClick={(e) => {
        //   // open/close FullScreen on doubleclick
        //   if (e.detail === 2) {
        //     // eslint-disable-next-line @typescript-eslint/no-unused-expressions
        //     handle.active ? handle.exit() : handle.enter()
        //   }
        // }}
      >
        <Swiper
          // spaceBetween={handle.active ? 10 : 1080}
          slidesPerView={1}
          direction="vertical"
          onSlideChange={(swiper) => setCurrentNumber(swiper.realIndex + 1)}
          onSwiper={(swiper) => {
            swiperRef.current = swiper
          }}
          // onSwiper={(swiper) => console.log(swiper)}
          className={`w-full ${
            handle.active ? 'h-screen' : 'h-[700px]'
          } max-w-7xl`}
        >
          <SwiperSlide>
            <div className="flex h-full gap-8 p-8 md:gap-12 md:p-12 lg:gap-20 lg:p-20">
              <div className="flex basis-1/2 flex-col overflow-y-auto py-4">
                <h2 className="mb-2 mt-2 text-xl font-bold text-white">
                  {exhibition.name}
                </h2>
                <span className="font-bold text-white">
                  {t('exhibitions:user_name')}
                  {exhibition.user.fullName}
                </span>
                <span className="text-white">
                  {t('exhibitions:featured_artists')}
                </span>
                <span className="text-white">
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
                        <Fragment key={`slider-view-1-${i.illustration.id}`}>
                          <Link
                            target="_blank"
                            className="underline hover:text-red"
                            to={constructSearchUrl(
                              `type=${searchParams.type}&view=${searchParams.view}&filterAuthor=${searchParams.filterAuthor}`
                            )}
                          >
                            {i.illustration.mainAuthor?.author.fullName}
                          </Link>
                          {i.illustration.mainAuthor?.author.fullName.lastIndexOf(
                            ','
                          ) ===
                          i.illustration.mainAuthor?.author.fullName.length - 1
                            ? ' '
                            : index !== length - 1
                              ? ', '
                              : null}
                        </Fragment>
                      )
                    })}
                </span>
                <span className="text-white">
                  {t('exhibitions:featured_books')}
                </span>
                <span className="text-white">
                  {exhibition.items
                    .filter((i) => i.illustration.book?.id)
                    .map((i, index, { length }) => {
                      return (
                        <Fragment key={`slider-view-2-${i.illustration.id}`}>
                          <Link
                            target="_blank"
                            className="underline hover:text-red"
                            to={constructRecordDetailUrl(
                              i.illustration.book?.id || ''
                            )}
                          >
                            {i.illustration.book?.identifier}
                          </Link>
                          {index !== length - 1 ? ', ' : null}
                        </Fragment>
                      )
                    })}
                </span>
                <div
                  className="wysiwyg-editor-content mt-2 text-white [&_a]:text-red"
                  dangerouslySetInnerHTML={{
                    __html: sanitizeWysiwygHtml(exhibition.description),
                  }}
                />
              </div>

              <div className="flex basis-1/2 items-center justify-center">
                {prefaceImage.image.length && prefaceImage.iilId.length ? (
                  <Link
                    to={constructRecordDetailUrl(prefaceImage.iilId)}
                    target="_blank"
                    className="flex items-center justify-center"
                  >
                    <img
                      className={`max-w-full rounded-xl object-contain transition-all duration-300 ${imageMaxHeightClass}`}
                      src={`/api/eil/files/${prefaceImage.image}`}
                      alt="prefaceImage"
                    />
                  </Link>
                ) : (
                  <BlankImage
                    classNames={`max-w-full rounded-xl object-contain transition-all duration-300 ${imageMaxHeightClass}`}
                  />
                )}
              </div>
            </div>
          </SwiperSlide>
          {exhibition.items.map((i) => {
            const searchParams = generateSearchSearchParams({
              filterAuthor: [
                {
                  value: i.illustration.mainAuthor?.author.id || '',
                  label: i.illustration.mainAuthor?.author.fullName || '',
                },
              ],
              type: 'ILLUSTRATION',
              view: 'LIST',
            })

            return (
              <SwiperSlide key={`slider-view-3-${i.illustration.id}`}>
                <div className="flex h-full gap-8 p-8 md:gap-12 md:p-12 lg:gap-20 lg:p-20">
                  <div className="flex basis-1/2 flex-col overflow-y-auto py-4">
                    <h2 className="mb-2 mt-2 text-xl font-bold text-white">
                      <Link
                        target="_blank"
                        to={constructRecordDetailUrl(i.illustration.id)}
                      >
                        {i.name.length ? i.name : i.illustration.title}
                      </Link>
                    </h2>
                    {i.illustration.mainAuthor?.author.fullName ? (
                      <span className="text-white">
                        {t('exhibitions:author')}
                        <Link
                          to={constructSearchUrl(
                            `type=${searchParams.type}&view=${searchParams.view}&filterAuthor=${searchParams.filterAuthor}`
                          )}
                          target="_blank"
                          className="text-red underline"
                        >
                          {i.illustration.mainAuthor?.author.fullName}
                        </Link>
                      </span>
                    ) : null}
                    {i.illustration.book ? (
                      <span className="text-white">
                        {t('exhibitions:in_book')}
                        <Link
                          target="_blank"
                          className="text-red underline"
                          to={constructRecordDetailUrl(i.illustration.book.id)}
                        >
                          {i.illustration.book.identifier}
                        </Link>
                      </span>
                    ) : null}
                    <div
                      className="wysiwyg-editor-content mt-2 text-white [&_a]:text-red"
                      dangerouslySetInnerHTML={{
                        __html: sanitizeWysiwygHtml(i.description),
                      }}
                    />
                  </div>

                  <div className="flex basis-1/2 items-center justify-center">
                    <Link
                      to={constructRecordDetailUrl(i.illustration.id)}
                      target="_blank"
                      className="flex items-center justify-center"
                    >
                      {i.illustration.illustrationScan ? (
                        <img
                          className={`max-w-full rounded-xl object-contain transition-all duration-300 ${imageMaxHeightClass}`}
                          src={`/api/eil/files/${i.illustration.illustrationScan.id}`}
                          alt={i.illustration.title}
                        />
                      ) : null}
                      {i.illustration.pageScan &&
                      !i.illustration.illustrationScan ? (
                        <img
                          className={`max-w-full rounded-xl object-contain transition-all duration-300 ${imageMaxHeightClass}`}
                          src={`/api/eil/files/${i.illustration.pageScan.id}`}
                          alt={i.illustration.title}
                        />
                      ) : null}
                      {!i.illustration.illustrationScan &&
                      !i.illustration.pageScan ? (
                        <BlankImage
                          classNames={`max-w-full rounded-xl object-contain transition-all duration-300 ${imageMaxHeightClass}`}
                        />
                      ) : null}
                    </Link>
                  </div>
                </div>
              </SwiperSlide>
            )
          })}
        </Swiper>
        <div className="absolute right-11 top-1/2 z-20 flex h-full -translate-x-1/2 -translate-y-1/2 flex-col items-center justify-center">
          {handle.active ? (
            <button
              type="button"
              onClick={() => handle.exit()}
              className="absolute top-28 z-20 text-white"
              aria-label="Close fullscreen"
            >
              <Close className="h-10 w-10 cursor-pointer justify-self-start" />
            </button>
          ) : (
            <button
              type="button"
              onClick={() => handle.enter()}
              className="absolute top-28 z-20 text-white"
              aria-label="Open fullscreen"
            >
              <FullScreenIcon className="h-10 w-10 cursor-pointer justify-self-start" />
            </button>
          )}
          <button
            type="button"
            className="z-20"
            onClick={() => {
              if (currentNumber > 1) {
                swiperRef.current?.slidePrev()
              }
            }}
            aria-label="Previous slide"
          >
            <UpArrow
              className={`h-12 w-12 cursor-pointer ${
                currentNumber === 1 ? 'text-gray' : 'text-white'
              } `}
            />
          </button>
          <p className="text-white">{currentNumber}</p>
          <span className="text-superlightgray"> - </span>
          <p className="text-white">{totalSlides}</p>
          <button
            type="button"
            className="z-20"
            onClick={() => {
              if (currentNumber < totalSlides) {
                swiperRef.current?.slideNext()
              }
            }}
            aria-label="Next slide"
          >
            <DownArrow
              className={`h-12 w-12 cursor-pointer ${currentNumber === totalSlides ? 'text-gray' : 'text-white'}`}
            />
          </button>
        </div>
      </div>
    </FullScreen>
  )
}

export default SliderView
