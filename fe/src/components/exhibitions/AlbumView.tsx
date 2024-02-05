import { FC } from 'react'
import { PhotoIcon } from '@heroicons/react/24/outline'
// import ImageMock from '../../assets/images/1.jpg'
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

const AlbumView: FC<Props> = ({ items }) => {
  const { t } = useTranslation()

  return (
    <div className="mt-8 flex flex-wrap justify-between gap-8">
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
          <div
            key={`album-view-${i.illustration.id}`}
            className="basis-[calc(50%-32px)]"
          >
            <Link
              to={constructRecordDetailUrl(i.illustration.id)}
              target="_blank"
            >
              {i.illustration.illustrationScan ? (
                <img
                  className="max-h-96 justify-self-start rounded-xl transition-all duration-300"
                  src={`/api/eil/files/${i.illustration.illustrationScan.id}`}
                  alt={i.illustration.title}
                />
              ) : null}
              {i.illustration.pageScan && !i.illustration.illustrationScan ? (
                <img
                  className="max-h-96 justify-self-start rounded-xl transition-all duration-300"
                  src={`/api/eil/files/${i.illustration.pageScan.id}`}
                  alt={i.illustration.title}
                />
              ) : null}
              {!i.illustration.illustrationScan && !i.illustration.pageScan ? (
                <BlankImage classNames="max-h-96 justify-self-start rounded-xl transition-all duration-300" />
              ) : null}
            </Link>
            {/* <img */}
            {/*  className="max-h-96 justify-self-start rounded-xl transition-all duration-300" */}
            {/*  // src={require(`assets/images/${i.image}`)} */}
            {/*  src={ImageMock} */}
            {/*  alt="" */}
            {/* /> */}
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
              <span className="block">
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
        )
      })}
    </div>
  )
}

export default AlbumView
