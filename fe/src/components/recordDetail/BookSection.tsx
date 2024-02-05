import React, { FC } from 'react'
import { useTranslation } from 'react-i18next'
import LightGallery from 'lightgallery/react'
import 'lightgallery/css/lightgallery.css'
import Button from '../reusableComponents/Button'
import { TBookDetail } from '../../../../fe-shared/@types/book'
import DownArrow from '../../assets/icons/down.svg?react'
import UpArrow from '../../assets/icons/up.svg?react'
// import Loader from '../reusableComponents/Loader's
import ClipBoardCopy from '../reusableComponents/ClipBoardCopy'
import IIIFImage from '../../assets/images/iiif.png'
import generateSearchSearchParams from '../../utils/generateSearchSearchParams'
import constructSearchUrl from '../../utils/constructSearchUrl'

// const MiradorContainer = React.lazy(() => import('../mirador/MiradorContainer'))

type TBookSectionProps = {
  record: TBookDetail
  showMore: boolean
  setShowMore: (value: boolean) => void
}

const BookSection: FC<TBookSectionProps> = ({
  record,
  showMore,
  setShowMore,
}) => {
  const { t } = useTranslation('detail')

  let image

  if (record.frontPageScan) {
    image = record.frontPageScan
  }

  const basicParams = generateSearchSearchParams({
    type: 'BOOK',
    view: 'LIST',
  })

  const authorParams = generateSearchSearchParams({
    filterAuthor: [
      {
        value: record.mainAuthor?.author.id || '',
        label: record.mainAuthor?.author.fullName || '',
      },
    ],
  })

  return (
    <>
      <div className="w-full md:order-2 md:basis-4/12">
        {image ? (
          <LightGallery>
            <a href={`/api/eil/files/${image.id}`}>
              <img
                className="max-h-[250px] max-w-[300px] justify-self-start transition-all duration-300"
                src={`/api/eil/files/${image.id}`}
                alt={record.title}
              />
            </a>
          </LightGallery>
        ) : null}
      </div>
      <div className="mt-4 md:mt-0 md:basis-7/12">
        <div className="flex border-b-[1.5px] border-superlightgray py-2">
          <span className="basis-1/3 font-bold">{t('illustration_name')}</span>
          <span className="basis-2/3">
            {record.title.trim().endsWith('/')
              ? record.title.trim().slice(0, -1)
              : record.title.trim()}
          </span>
        </div>
        <div className="flex border-b-[1.5px] border-superlightgray py-2">
          <span className="basis-1/3 font-bold">{t('source')}</span>
          <a
            href={`https://knihoveda.lib.cas.cz/Record/${record.identifier}`}
            target="_blank"
            rel="noreferrer"
            className="text-red hover:underline"
          >
            {record.identifier}
          </a>
        </div>
        {record.mainAuthor ? (
          <div className="flex border-b-[1.5px] border-superlightgray py-2">
            <span className="basis-1/3 font-bold">{t('main_author')}</span>
            <a
              href={constructSearchUrl(
                `type=${basicParams.type}&view=${basicParams.view}&filterAuthor=${authorParams.filterAuthor}`
              )}
              target="_blank"
              rel="noreferrer"
              className="text-red hover:underline"
            >
              {record.mainAuthor.author.fullName}
            </a>
          </div>
        ) : null}
        {record.publishingEntry &&
        (record.publishingEntry.placesOfPublication.length ||
          record.publishingEntry.originators.length) &&
        record.publishingEntry.date.length ? (
          <div className="flex border-b-[1.5px] border-superlightgray py-2">
            <span className="basis-1/3 font-bold">
              {t('publisher_details')}
            </span>
            <span className="basis-2/3">
              {record.publishingEntry.placesOfPublication.join(' ')}{' '}
              {record.publishingEntry.originators.join(' ')}{' '}
              {record.publishingEntry.date}
            </span>
          </div>
        ) : null}
        {record.physicalDescription || record.technique || record.dimensions ? (
          <div className="flex border-b-[1.5px] border-superlightgray py-2">
            <span className="basis-1/3 font-bold">{t('scope')}</span>
            <span className="basis-2/3">
              {record.physicalDescription} {record.technique}{' '}
              {record.dimensions}
            </span>
          </div>
        ) : null}
        <div className="flex border-b-[1.5px] border-superlightgray py-2">
          <span className="basis-1/3 font-bold">{t('permanent_link')}</span>
          <span className="basis-2/3">
            <ClipBoardCopy text={record.id} copyType="href" />
          </span>
        </div>
        <div className="flex border-b-[1.5px] border-superlightgray py-2">
          <div className="flex basis-1/3 items-center gap-2 font-bold">
            <img src={IIIFImage} className="h-[20px] max-w-full" alt="IIIF" />
            {t('manifest')}
          </div>
          <span className="basis-2/3">
            <ClipBoardCopy text={record.id} copyType="mirador" />
          </span>
        </div>
        {!showMore && (
          <Button
            className="mt-2 bg-footergray"
            variant="text"
            onClick={() => setShowMore(true)}
            startIcon={<DownArrow />}
          >
            {t('show_more')}
          </Button>
        )}
        {showMore && (
          <>
            {record.coauthors.filter((ca) => ca.roles.includes('PRINTER'))
              .length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('printer')}</span>
                <span className="basis-2/3">
                  {record.coauthors
                    .filter((ca) => ca.roles.includes('PRINTER'))
                    .map((ca) => {
                      const printerParams = generateSearchSearchParams({
                        filterAuthor: [
                          {
                            value: ca.author.id || '',
                            label: ca.author.fullName || '',
                          },
                        ],
                      })

                      return (
                        <a
                          key={ca.id}
                          className="block text-red hover:underline"
                          href={constructSearchUrl(
                            `type=${basicParams.type}&view=${basicParams.view}&filterAuthor=${printerParams.filterAuthor}`
                          )}
                          target="_blank"
                          rel="noreferrer"
                        >
                          {ca.author.fullName} [{ca.author.birthYear}-
                          {ca.author.deathYear}]
                        </a>
                      )
                    })}
                </span>
              </div>
            ) : null}
            {record.coauthors.filter((ca) => ca.roles.includes('PUBLISHER'))
              .length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('publisher')}</span>
                <span className="basis-2/3">
                  {record.coauthors
                    .filter((ca) => ca.roles.includes('PUBLISHER'))
                    .map((ca) => (
                      <span key={ca.id} className="block">
                        {ca.author.fullName} [{ca.author.birthYear}-
                        {ca.author.deathYear}]
                      </span>
                    ))}
                </span>
              </div>
            ) : null}
            {record.publishingPlaces.length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">
                  {t('publishing_place')}
                </span>
                <span className="basis-2/3">
                  {record.publishingPlaces.map((pp) => {
                    const publishingPlaceParams = generateSearchSearchParams({
                      filterPublishingPlace: [
                        {
                          value: pp.id || '',
                          label: pp.name || '',
                        },
                      ],
                    })

                    return (
                      <a
                        key={pp.id}
                        className="block text-red hover:underline"
                        href={constructSearchUrl(
                          `type=${basicParams.type}&view=${basicParams.view}&filterPublishingPlace=${publishingPlaceParams.filterPublishingPlace}`
                        )}
                        target="_blank"
                        rel="noreferrer"
                      >
                        {pp.name}
                      </a>
                    )
                  })}
                </span>
              </div>
            ) : null}
            {/* {record.notes.length > 0 ? ( */}
            {/*  <div className="flex border-b-[1.5px] border-superlightgray py-2"> */}
            {/*    <span className="basis-1/3 font-bold">{t('notes')}</span> */}
            {/*    <span className="basis-2/3"> */}
            {/*      {record.notes.map((n) => ( */}
            {/*        <span key={n.id} className="mb-3 block last:mb-0"> */}
            {/*          {n.title ? `${n.title}: ` : null} */}
            {/*          {n.text} */}
            {/*        </span> */}
            {/*      ))} */}
            {/*    </span> */}
            {/*  </div> */}
            {/* ) : null} */}
            {record.subjectPersons.length || record.subjectEntries.length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('topic')}</span>
                <span className="basis-2/3">
                  {record.subjectPersons.map((sp) => {
                    const subjectPersonsParams = generateSearchSearchParams({
                      filterAuthor: [
                        {
                          value: sp.id || '',
                          label: sp.fullName || '',
                        },
                      ],
                    })

                    return (
                      <a
                        key={sp.id}
                        className="block text-red hover:underline"
                        href={constructSearchUrl(
                          `type=${basicParams.type}&view=${basicParams.view}&filterAuthor=${subjectPersonsParams.filterAuthor}`
                        )}
                        target="_blank"
                        rel="noreferrer"
                      >
                        {sp.fullName} [{sp.birthYear}-{sp.deathYear}]
                      </a>
                    )
                  })}
                  {record.subjectEntries
                    .filter((se) => se.label !== 'dřevořezy')
                    .map((se) => {
                      const subjectEntryParams = generateSearchSearchParams({
                        filterObject: [
                          {
                            value: se.id || '',
                            label: se.label || '',
                          },
                        ],
                      })

                      return (
                        <a
                          key={se.id}
                          className="block text-red hover:underline"
                          href={constructSearchUrl(
                            `type=${basicParams.type}&view=${basicParams.view}&filterObject=${subjectEntryParams.filterObject}`
                          )}
                          target="_blank"
                          rel="noreferrer"
                        >
                          {se.label}
                        </a>
                      )
                    })}
                  {record.subjectPlaces.map((sp) => {
                    const subjectPlacesParams = generateSearchSearchParams({
                      filterObject: [
                        {
                          value: sp.id || '',
                          label: sp.name || '',
                        },
                      ],
                    })

                    return (
                      <a
                        key={sp.id}
                        className="block text-red hover:underline"
                        href={constructSearchUrl(
                          `type=${basicParams.type}&view=${basicParams.view}&filterObject=${subjectPlacesParams.filterObject}`
                        )}
                        target="_blank"
                        rel="noreferrer"
                      >
                        {sp.name}
                      </a>
                    )
                  })}
                  {record.subjectInstitutions.map((si) => {
                    const subjectInstitutionsParams =
                      generateSearchSearchParams({
                        filterObject: [
                          {
                            value: si.id || '',
                            label: si.name || '',
                          },
                        ],
                      })

                    return (
                      <a
                        key={si.id}
                        className="block text-red hover:underline"
                        href={constructSearchUrl(
                          `type=${basicParams.type}&view=${basicParams.view}&filterObject=${subjectInstitutionsParams.filterObject}`
                        )}
                        target="_blank"
                        rel="noreferrer"
                      >
                        {si.name}
                      </a>
                    )
                  })}
                  {record.genres.map((g) => {
                    const genresParams = generateSearchSearchParams({
                      filterObject: [
                        {
                          value: g.id || '',
                          label: g.name || '',
                        },
                      ],
                    })

                    return (
                      <a
                        key={g.id}
                        className="block text-red hover:underline"
                        href={constructSearchUrl(
                          `type=${basicParams.type}&view=${basicParams.view}&filterObject=${genresParams.filterObject}`
                        )}
                        target="_blank"
                        rel="noreferrer"
                      >
                        {g.name}
                      </a>
                    )
                  })}
                </span>
              </div>
            ) : null}
            {/* {record.references.length ? ( */}
            {/*  <div className="flex border-b-[1.5px] border-superlightgray py-2"> */}
            {/*    <span className="basis-1/3 font-bold">{t('literature')}</span> */}
            {/*    <span className="basis-2/3"> */}
            {/*      {record.references.map((r) => ( */}
            {/*        <span key={r.id} className="mb-3 block last:mb-0"> */}
            {/*          {`${r.workTitle} ${r.location ? r.location : ''}`} */}
            {/*        </span> */}
            {/*      ))} */}
            {/*    </span> */}
            {/*  </div> */}
            {/* ) : null} */}
            {record.links.length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('links')}</span>
                <span className="basis-2/3">
                  {record.links
                    .filter((l) => l.description !== 'Titulní list')
                    .map((l) => (
                      <a
                        key={l.id}
                        href={l.url}
                        target="_blank"
                        className="block text-red hover:underline"
                        rel="noreferrer"
                      >
                        {l.description}
                      </a>
                    ))}
                </span>
              </div>
            ) : null}
          </>
        )}
        {showMore && (
          <Button
            className="mt-2 bg-footergray"
            variant="text"
            onClick={() => setShowMore(false)}
            endIcon={<UpArrow />}
          >
            {t('show_less')}
          </Button>
        )}
        {/*    <div className="relative mb-5 mt-10">
          {record.illustrations.some((i) => i.book.id) ? (
            <>
              <span className="text-lg font-bold">MIRADOR</span>
              <Suspense fallback={<Loader className="" />}>
                <MiradorContainer
                  config={{
                    id: 'mirador',
                    windows: [
                      {
                        imageToolsEnabled: true,
                        // imageToolsOpen: true,
                        // loadedManifest: `/iiif/2/${record.identifier}.jpg/info.json`,
                        loadedManifest: `/api/eil/record/${record.id}/manifest.json`,
                        thumbnailNavigationPosition: 'far-bottom',
                      },
                    ],
                  }}
                />
              </Suspense>
            </>
          ) : null}
        </div> */}
      </div>
    </>
  )
}

export default BookSection
