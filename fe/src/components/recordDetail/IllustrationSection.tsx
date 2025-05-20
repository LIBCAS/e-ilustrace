import React, { FC } from 'react'
import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom'
import LightGallery from 'lightgallery/react'
import 'lightgallery/css/lightgallery.css'
import { ArrowTopRightOnSquareIcon } from '@heroicons/react/24/outline'
import Button from '../reusableComponents/Button'
import DownArrow from '../../assets/icons/down.svg?react'
import UpArrow from '../../assets/icons/up.svg?react'
import { TIllustrationDetail } from '../../../../fe-shared/@types/illustration'
// import Loader from '../reusableComponents/Loader'
import ClipBoardCopy from '../reusableComponents/ClipBoardCopy'
import constructRecordDetailUrl from '../../utils/constructRecordDetailUrl'
import IIIFImage from '../../assets/images/iiif.png'
import generateIconClassSearchParams from '../../utils/generateIconClassSearchParams'
import useIconClassDropdownTranslations from '../../hooks/useIconClassDropdownTranslations'
import { TFilterOperator } from '../../../../fe-shared/@types/common'
import constructSearchUrl from '../../utils/constructSearchUrl'
import generateSearchSearchParams from '../../utils/generateSearchSearchParams'

// const MiradorContainer = React.lazy(() => import('../mirador/MiradorContainer'))

type TIllustrationSectionProps = {
  record: TIllustrationDetail
  showMore: boolean
  setShowMore: (value: boolean) => void
}

const IllustrationSection: FC<TIllustrationSectionProps> = ({
  record,
  showMore,
  setShowMore,
}) => {
  const { t } = useTranslation('detail')
  const { filterValuesForDropdown } = useIconClassDropdownTranslations()

  let image

  if (record.illustrationScan) {
    image = record.illustrationScan
  } else if (record.pageScan) {
    image = record.pageScan
  }

  const basicParams = generateSearchSearchParams({
    type: 'ILLUSTRATION',
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
            <span className="basis-1/3 font-bold">{t('artist')}</span>
            <a
              href={constructSearchUrl(
                `type=${basicParams.type}&view=${basicParams.view}&filterAuthor=${authorParams.filterAuthor}`
              )}
              target="_blank"
              rel="noreferrer"
              className="text-red hover:underline"
            >
              {record.mainAuthor.author.fullName} [
              {record.mainAuthor.author.birthYear}-
              {record.mainAuthor.author.deathYear}]
            </a>
          </div>
        ) : null}
        {record.physicalDescription || record.technique || record.dimensions ? (
          <div className="flex border-b-[1.5px] border-superlightgray py-2">
            <span className="basis-1/3 font-bold">
              {t('place_and_dimensions')}
            </span>
            <span className="basis-2/3">
              {record.physicalDescription} {record.technique}{' '}
              {record.dimensions}
            </span>
          </div>
        ) : null}
        {record.subjectEntries.find((se) => se.label === 'dřevořezy') ? (
          <div className="flex border-b-[1.5px] border-superlightgray py-2">
            <span className="basis-1/3 font-bold">{t('graphic_arts')}</span>
            <span className="basis-2/3">
              {
                record.subjectEntries.find((se) => se.label === 'dřevořezy')
                  ?.label
              }
            </span>
          </div>
        ) : null}
        {record.book?.id ? (
          <div className="flex border-b-[1.5px] border-superlightgray py-2">
            <span className="basis-1/3 font-bold">{t('illustration_in')}</span>
            <span className="line-clamp-2 basis-2/3">
              <Link
                to={constructRecordDetailUrl(record.book.id)}
                className="text-red hover:underline"
              >
                {record.book.identifier} ({record.book.title})
              </Link>
            </span>
          </div>
        ) : null}
        {(record.printEntry?.placesOfPublication.length ||
          record.printEntry?.originators.length) &&
        record.printEntry?.date ? (
          <div className="flex border-b-[1.5px] border-superlightgray py-2">
            <span className="basis-1/3 font-bold">
              {t('publisher_details')}
            </span>
            <span className="basis-2/3">
              {record.printEntry.placesOfPublication.join(' ')}{' '}
              {record.printEntry.originators.join(' ')} {record.printEntry.date}
            </span>
          </div>
        ) : null}
        {record.iconclass.length ? (
          <div className="flex border-b-[1.5px] border-superlightgray py-2">
            <span className="basis-1/3 font-bold">ICONCLASS</span>
            <span className="basis-2/3">
              {record.iconclass.map((icc) => {
                const category = filterValuesForDropdown.find(
                  (c) => c.value === 'iconclass.code'
                )
                const searchParamsProps: {
                  search: string
                  category?: {
                    value: string
                    label: string
                    operation: TFilterOperator
                  }
                } = { search: icc.code }
                if (category) {
                  searchParamsProps.category = {
                    value: category.value,
                    label: category.label,
                    operation: category.operation as TFilterOperator,
                  }
                }

                const params = generateIconClassSearchParams(searchParamsProps)

                return (
                  <div key={`iconclass-${icc.code}`}>
                    <Link
                      target="_blank"
                      to={`/iconclass?search=${params.search}&category=${params.category}`}
                      className="inline-flex text-red hover:underline"
                    >
                      {`${icc.code} - ${icc.name}`}
                    </Link>
                    <a
                      href={`https://iconclass.org/${icc.code}`}
                      target="_blank"
                      key={icc.id}
                      rel="noreferrer"
                      aria-hidden
                      className="ml-1 inline-flex align-bottom"
                    >
                      <ArrowTopRightOnSquareIcon className="h-[24px] w-[24px]" />
                    </a>
                  </div>
                )
              })}
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
            {record.printingPlateEntry ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">
                  {t('data_about_the_plate')}
                </span>
                <span className="basis-2/3">
                  {record.printingPlateEntry.placesOfPublication.join(' ')}{' '}
                  {record.printingPlateEntry.originators.join(' ')}{' '}
                  {record.printingPlateEntry.date}
                </span>
              </div>
            ) : null}
            {record.variantTitles.length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">
                  {t('variant_titles')}
                </span>
                <span className="basis-2/3">
                  {record.variantTitles.map((n) => (
                    <span key={n} className="mb-3 block last:mb-0">
                      {n}
                    </span>
                  ))}
                </span>
              </div>
            ) : null}
            {record.notes.length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('notes')}</span>
                <span className="basis-2/3">
                  {record.notes.map((n) => (
                    <span key={n.id} className="mb-3 block last:mb-0">
                      {n.title ? `${n.title}: ` : null}
                      {n.text}
                    </span>
                  ))}
                </span>
              </div>
            ) : null}
            {record.references.length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('literature')}</span>
                <span className="basis-2/3">
                  {record.references.map((r) => (
                    <span key={r.id} className="mb-3 block last:mb-0">
                      {`${r.workTitle} ${r.location ? r.location : ''}`}
                    </span>
                  ))}
                </span>
              </div>
            ) : null}
            {/* {record.themes.length ? ( */}
            {/*  <div className="flex border-b-[1.5px] border-superlightgray py-2"> */}
            {/*    <span className="basis-1/3 font-bold">{t('themes')}</span> */}
            {/*    <span className="basis-2/3"> */}
            {/*      {record.themes */}
            {/*        .map((theme) => tExplore(`${theme.name}`)) */}
            {/*        .join(', ')} */}
            {/*    </span> */}
            {/*  </div> */}
            {/* ) : null} */}
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
                  {t('publishing_place_or_origin')}
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
            {record.coauthors.filter((ca) =>
              ca.roles.includes('BIBLIOGRAPHIC_ANTECEDENT')
            ).length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">
                  {t('bibliographic_antecedent')}
                </span>
                <span className="basis-2/3">
                  {record.coauthors
                    .filter((ca) =>
                      ca.roles.includes('BIBLIOGRAPHIC_ANTECEDENT')
                    )
                    .map((ca) => (
                      <span key={ca.id} className="block">
                        {ca.author.fullName} [{ca.author.birthYear}-
                        {ca.author.deathYear}]
                      </span>
                    ))}
                </span>
              </div>
            ) : null}
            {record.coauthors.filter((ca) => ca.roles.includes('AUTHOR'))
              .length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('author')}</span>
                <span className="basis-2/3">
                  {record.coauthors
                    .filter((ca) => ca.roles.includes('AUTHOR'))
                    .map((ca) => (
                      <span key={ca.id} className="block">
                        {ca.author.fullName} [{ca.author.birthYear}-
                        {ca.author.deathYear}]
                      </span>
                    ))}
                </span>
              </div>
            ) : null}
            {record.coauthors.filter((ca) =>
              ca.roles.includes('PRESUMED_AUTHOR')
            ).length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">
                  {t('presumed_author')}
                </span>
                <span className="basis-2/3">
                  {record.coauthors
                    .filter((ca) => ca.roles.includes('PRESUMED_AUTHOR'))
                    .map((ca) => (
                      <span key={ca.id} className="block">
                        {ca.author.fullName} [{ca.author.birthYear}-
                        {ca.author.deathYear}]
                      </span>
                    ))}
                </span>
              </div>
            ) : null}
            {record.coauthors.filter((ca) => ca.roles.includes('CARTOGRAPHER'))
              .length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('cartographer')}</span>
                <span className="basis-2/3">
                  {record.coauthors
                    .filter((ca) => ca.roles.includes('CARTOGRAPHER'))
                    .map((ca) => (
                      <span key={ca.id} className="block">
                        {ca.author.fullName} [{ca.author.birthYear}-
                        {ca.author.deathYear}]
                      </span>
                    ))}
                </span>
              </div>
            ) : null}
            {record.coauthors.filter((ca) => ca.roles.includes('ENGRAVER'))
              .length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('engraver')}</span>
                <span className="basis-2/3">
                  {record.coauthors
                    .filter((ca) => ca.roles.includes('ENGRAVER'))
                    .map((ca) => (
                      <span key={ca.id} className="block">
                        {ca.author.fullName} [{ca.author.birthYear}-
                        {ca.author.deathYear}]
                      </span>
                    ))}
                </span>
              </div>
            ) : null}
            {record.coauthors.filter((ca) => ca.roles.includes('ETCHER'))
              .length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('etcher')}</span>
                <span className="basis-2/3">
                  {record.coauthors
                    .filter((ca) => ca.roles.includes('ETCHER'))
                    .map((ca) => (
                      <span key={ca.id} className="block">
                        {ca.author.fullName} [{ca.author.birthYear}-
                        {ca.author.deathYear}]
                      </span>
                    ))}
                </span>
              </div>
            ) : null}
            {record.coauthors.filter((ca) => ca.roles.includes('ILLUSTRATOR'))
              .length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('illustrator')}</span>
                <span className="basis-2/3">
                  {record.coauthors
                    .filter((ca) => ca.roles.includes('ILLUSTRATOR'))
                    .map((ca) => (
                      <span key={ca.id} className="block">
                        {ca.author.fullName} [{ca.author.birthYear}-
                        {ca.author.deathYear}]
                      </span>
                    ))}
                </span>
              </div>
            ) : null}
            {record.coauthors.filter((ca) =>
              ca.roles.includes('METAL_ENGRAVER')
            ).length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">
                  {t('metal_engraver')}
                </span>
                <span className="basis-2/3">
                  {record.coauthors
                    .filter((ca) => ca.roles.includes('METAL_ENGRAVER'))
                    .map((ca) => (
                      <span key={ca.id} className="block">
                        {ca.author.fullName} [{ca.author.birthYear}-
                        {ca.author.deathYear}]
                      </span>
                    ))}
                </span>
              </div>
            ) : null}
            {/* {record.coauthors.filter((ca) => ca.roles.includes('PATRON')) */}
            {/*  .length ? ( */}
            {/*  <div className="flex border-b-[1.5px] border-superlightgray py-2"> */}
            {/*    <span className="basis-1/3 font-bold">{t('patron')}</span> */}
            {/*    <span className="basis-2/3"> */}
            {/*      {record.coauthors */}
            {/*        .filter((ca) => ca.roles.includes('PATRON')) */}
            {/*        .map((ca) => ( */}
            {/*          <span key={ca.id} className="block"> */}
            {/*            {ca.author.fullName} [{ca.author.birthYear}-*/}
            {/*            {ca.author.deathYear}] */}
            {/*          </span> */}
            {/*        ))} */}
            {/*    </span> */}
            {/*  </div> */}
            {/* ) : null} */}
            {record.coauthors.filter((ca) => ca.roles.includes('WOODCARVER'))
              .length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('woodcarver')}</span>
                <span className="basis-2/3">
                  {record.coauthors
                    .filter((ca) => ca.roles.includes('WOODCARVER'))
                    .map((ca) => (
                      <span key={ca.id} className="block">
                        {ca.author.fullName} [{ca.author.birthYear}-
                        {ca.author.deathYear}]
                      </span>
                    ))}
                </span>
              </div>
            ) : null}
            {record.coauthors.filter((ca) => ca.roles.includes('OTHER'))
              .length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('other_role')}</span>
                <span className="basis-2/3">
                  {record.coauthors
                    .filter((ca) => ca.roles.includes('OTHER'))
                    .map((ca) => (
                      <span key={ca.id} className="block">
                        {ca.author.fullName} [{ca.author.birthYear}-
                        {ca.author.deathYear}]
                      </span>
                    ))}
                </span>
              </div>
            ) : null}
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
            {record.keywords.length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('keywords')}</span>
                <span className="basis-2/3">
                  {record.keywords.map((keyword) => {
                    const objectParams = generateSearchSearchParams({
                      filterObject: [
                        {
                          value: keyword.id || '',
                          label: keyword.label || '',
                        },
                      ],
                    })

                    return (
                      <a
                        key={keyword.id}
                        className="block text-red hover:underline"
                        href={constructSearchUrl(
                          `type=${basicParams.type}&view=${basicParams.view}&filterObject=${objectParams.filterObject}`
                        )}
                        target="_blank"
                        rel="noreferrer"
                      >
                        {keyword.label}
                      </a>
                    )
                  })}
                </span>
              </div>
            ) : null}
            {/* {record.links.length ? ( */}
            {/*  <div className="flex border-b-[1.5px] border-superlightgray py-2"> */}
            {/*    <span className="basis-1/3 font-bold">{t('links')}</span> */}
            {/*    <span className="basis-2/3"> */}
            {/*      {record.links */}
            {/*        .filter((l) => l.description !== 'Titulní list') */}
            {/*        .map((l) => ( */}
            {/*          <a */}
            {/*            key={l.id} */}
            {/*            href={l.url} */}
            {/*            className="block text-red hover:underline" */}
            {/*          > */}
            {/*            {l.description} */}
            {/*          </a> */}
            {/*        ))} */}
            {/*    </span> */}
            {/*  </div> */}
            {/* ) : null} */}
            {record.owners.length ? (
              <div className="flex border-b-[1.5px] border-superlightgray py-2">
                <span className="basis-1/3 font-bold">{t('exemplars')}</span>
                <span className="basis-2/3">
                  {record.owners
                    .map((o) => `${o.name}, ${o.signature}`)
                    .join('; ')}
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
        {/* <div className="relative mt-10">
          {image ? (
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
                        // loadedManifest: `/api/eil/record/${record.id}/manifest.json`,
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

export default IllustrationSection
