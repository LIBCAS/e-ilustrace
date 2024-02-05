import { Swiper, SwiperSlide } from 'swiper/react'
import React, { FC, Fragment, useState } from 'react'
import { FullScreen, useFullScreenHandle } from 'react-full-screen'

import 'swiper/css'
import 'swiper/css/navigation'

import { Navigation } from 'swiper/modules'

import { PhotoIcon } from '@heroicons/react/24/outline'
import DOMPurify from 'dompurify'
import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom'
import DownArrow from '../../assets/icons/down.svg?react'
import UpArrow from '../../assets/icons/up.svg?react'
import FullScreenIcon from '../../assets/icons/fullscreen.svg?react'
import Close from '../../assets/icons/close.svg?react'
// import ImageMock from '../../assets/images/1.jpg'
import { TExhibitionDetail } from '../../../../fe-shared/@types/exhibition'
import constructRecordDetailUrl from '../../utils/constructRecordDetailUrl'
import constructSearchUrl from '../../utils/constructSearchUrl'
import generateSearchSearchParams from '../../utils/generateSearchSearchParams'

const BlankImage = ({ classNames }: { classNames: string }) => {
  return <PhotoIcon className={`text-lightgray ${classNames}`} />
}

type Props = {
  exhibition: TExhibitionDetail
}

const SliderView: FC<Props> = ({ exhibition }) => {
  const [currentNumber, setCurrentNumber] = useState(1)
  const handle = useFullScreenHandle()
  const { t } = useTranslation()

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
          // navigation={true}
          modules={[Navigation]}
          navigation={{
            prevEl: '.prev',
            nextEl: '.next',
          }}
          // spaceBetween={handle.active ? 10 : 1080}
          slidesPerView={1}
          direction="vertical"
          onSlideChange={(swiper) => setCurrentNumber(swiper.realIndex + 1)}
          // onSwiper={(swiper) => console.log(swiper)}
          className={`w-full ${
            handle.active ? 'h-screen' : 'h-[700px]'
          } max-w-7xl`}
        >
          <SwiperSlide>
            <div className="flex gap-20 p-28">
              <div className="flex basis-1/2 flex-col py-8">
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
                <p
                  className="mt-2 text-white"
                  dangerouslySetInnerHTML={{
                    __html: DOMPurify.sanitize(exhibition.description),
                  }}
                />
              </div>

              <div className="basis-1/2">
                {prefaceImage.image.length && prefaceImage.iilId.length ? (
                  <Link
                    to={constructRecordDetailUrl(prefaceImage.iilId)}
                    target="_blank"
                  >
                    <img
                      className="justify-self-start rounded-xl transition-all duration-300"
                      src={`/api/eil/files/${prefaceImage.image}`}
                      alt="prefaceImage"
                    />
                  </Link>
                ) : (
                  <BlankImage classNames="justify-self-start rounded-xl transition-all duration-300" />
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
                <div className="flex gap-20 p-28">
                  <div className="flex basis-1/2 flex-col py-8">
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
                    <p
                      className="mt-2 text-white"
                      dangerouslySetInnerHTML={{
                        __html: DOMPurify.sanitize(i.description),
                      }}
                    />
                  </div>

                  <div className="basis-1/2">
                    <Link
                      to={constructRecordDetailUrl(i.illustration.id)}
                      target="_blank"
                    >
                      {i.illustration.illustrationScan ? (
                        <img
                          className="justify-self-start rounded-xl transition-all duration-300"
                          src={`/api/eil/files/${i.illustration.illustrationScan.id}`}
                          alt={i.illustration.title}
                        />
                      ) : null}
                      {i.illustration.pageScan &&
                      !i.illustration.illustrationScan ? (
                        <img
                          className="justify-self-start rounded-xl transition-all duration-300"
                          src={`/api/eil/files/${i.illustration.pageScan.id}`}
                          alt={i.illustration.title}
                        />
                      ) : null}
                      {!i.illustration.illustrationScan &&
                      !i.illustration.pageScan ? (
                        <BlankImage classNames="justify-self-start rounded-xl transition-all duration-300" />
                      ) : null}
                    </Link>
                  </div>
                </div>
              </SwiperSlide>
            )
          })}
        </Swiper>
        <div className="absolute right-11 top-1/2 flex h-full -translate-x-1/2 -translate-y-1/2 flex-col items-center justify-center">
          {handle.active ? (
            <Close
              onClick={() => handle.exit()}
              className="absolute top-28 h-10 w-10 cursor-pointer justify-self-start text-white"
            />
          ) : (
            <FullScreenIcon
              onClick={() => handle.enter()}
              className="absolute top-28 h-10 w-10 cursor-pointer justify-self-start text-white"
            />
          )}
          <UpArrow
            className={`prev h-12 w-12 cursor-pointer ${
              currentNumber === 1 ? 'text-gray' : 'text-white'
            } `}
          />
          <p className="text-white">{currentNumber}</p>
          <span className="text-superlightgray"> - </span>
          <p className="text-white">{exhibition.items.length}</p>
          <DownArrow
            className={`next h-12 w-12 cursor-pointer ${
              currentNumber === exhibition.items.length
                ? 'text-gray'
                : 'text-white'
            }`}
          />
        </div>
      </div>
    </FullScreen>
  )
}

export default SliderView
