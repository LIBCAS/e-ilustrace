import { FC } from 'react'
import { PhotoIcon } from '@heroicons/react/24/outline'
import { Link } from 'react-router'
import { useTranslation } from 'react-i18next'
import { TExhibitionItemDetail } from '../../../../fe-shared/@types/exhibition'
import constructRecordDetailUrl from '../../utils/constructRecordDetailUrl'
import constructSearchUrl from '../../utils/constructSearchUrl'
import generateSearchSearchParams from '../../utils/generateSearchSearchParams'
import sanitizeWysiwygHtml from '../../utils/sanitizeWysiwygHtml'

const BlankImage = ({ classNames }: { classNames: string }) => {
  return <PhotoIcon className={`text-lightgray ${classNames}`} />
}

type Props = {
  items: TExhibitionItemDetail[]
}

const AlbumView: FC<Props> = ({ items }) => {
  const { t } = useTranslation()

  return (
    <div className="mt-8 flex flex-wrap justify-center gap-6 md:gap-8">
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
            className="w-full max-w-[520px] md:basis-[calc(50%-24px)]"
          >
            <div className="h-full rounded-2xl border border-superlightgray bg-white p-4 sm:p-5">
              <div className="flex h-[420px] items-center justify-center rounded-xl bg-[#fafafa] p-3">
                <Link
                  to={constructRecordDetailUrl(i.illustration.id)}
                  target="_blank"
                  className="flex h-full w-full items-center justify-center"
                >
                  {i.illustration.illustrationScan ? (
                    <img
                      className="max-h-full max-w-full rounded-xl object-contain transition-all duration-300"
                      src={`/api/eil/files/${i.illustration.illustrationScan.id}`}
                      alt={i.illustration.title}
                    />
                  ) : null}
                  {i.illustration.pageScan &&
                  !i.illustration.illustrationScan ? (
                    <img
                      className="max-h-full max-w-full rounded-xl object-contain transition-all duration-300"
                      src={`/api/eil/files/${i.illustration.pageScan.id}`}
                      alt={i.illustration.title}
                    />
                  ) : null}
                  {!i.illustration.illustrationScan &&
                  !i.illustration.pageScan ? (
                    <BlankImage classNames="h-20 w-20 rounded-xl transition-all duration-300" />
                  ) : null}
                </Link>
              </div>
              <h2 className="mb-2 mt-4 text-xl font-bold">
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
              <div
                className="wysiwyg-editor-content mt-2 text-gray"
                dangerouslySetInnerHTML={{
                  __html: sanitizeWysiwygHtml(i.description),
                }}
              />
            </div>
          </div>
        )
      })}
    </div>
  )
}

export default AlbumView
