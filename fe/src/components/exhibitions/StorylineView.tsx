import { FC, Fragment } from 'react'
import { PhotoIcon } from '@heroicons/react/24/outline'
import DOMPurify from 'dompurify'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { TExhibitionItemDetail } from '../../../../fe-shared/@types/exhibition'
import constructRecordDetailUrl from '../../utils/constructRecordDetailUrl'
import constructSearchUrl from '../../utils/constructSearchUrl'
import generateSearchSearchParams from '../../utils/generateSearchSearchParams'

const BlankImage = ({ classNames }: { classNames: string }) => {
  return <PhotoIcon className={`text-lightgray ${classNames}`} />
}

type Props = {
  items: TExhibitionItemDetail[]
}

const StorylineView: FC<Props> = ({ items }) => {
  const { t } = useTranslation()

  return (
    <div className="storyline grid">
      {items.map((i) => {
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
          <Fragment key={`storyline-view-${i.illustration.id}`}>
            <Link
              target="_blank"
              to={constructRecordDetailUrl(i.illustration.id)}
              className="ml-auto mr-20 block py-8"
            >
              {i.illustration.illustrationScan ? (
                <img
                  className="justify-self-start rounded-xl transition-all duration-300"
                  src={`/api/eil/files/${i.illustration.illustrationScan.id}`}
                  alt={i.illustration.title}
                />
              ) : null}
              {i.illustration.pageScan && !i.illustration.illustrationScan ? (
                <img
                  className="justify-self-start rounded-xl transition-all duration-300"
                  src={`/api/eil/files/${i.illustration.pageScan.id}`}
                  alt={i.illustration.title}
                />
              ) : null}
              {!i.illustration.illustrationScan && !i.illustration.pageScan ? (
                <BlankImage classNames="justify-self-start rounded-xl transition-all duration-300" />
              ) : null}
              {/* <img */}
              {/*  className="justify-self-start rounded-xl transition-all duration-300" */}
              {/*  // src={require(`assets/images/${i.image}`)} */}
              {/*  src={ImageMock} */}
              {/*  alt={i.illustration.title} */}
              {/* /> */}
            </Link>
            <div className="relative bg-lightgray">
              <div className="absolute left-1/2 h-3 w-3 -translate-x-1/2 rounded-[50%] bg-red" />
            </div>
            <div className="ml-20 flex flex-col py-8">
              <h2 className="mb-2 mt-2 text-xl font-bold">
                <Link
                  target="_blank"
                  to={constructRecordDetailUrl(i.illustration.id)}
                >
                  {i.name.length ? i.name : i.illustration.title}
                </Link>
              </h2>
              {i.illustration.mainAuthor?.author.fullName ? (
                <span>
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
                <span>
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
                className="mt-2 text-gray"
                dangerouslySetInnerHTML={{
                  __html: DOMPurify.sanitize(i.description),
                }}
              />
            </div>
          </Fragment>
        )
      })}
    </div>
  )
}

export default StorylineView
