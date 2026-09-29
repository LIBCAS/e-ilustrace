import { FC, Fragment } from 'react'
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
              className="ml-auto mr-10 block py-5"
            >
              <div className="flex h-[320px] w-[420px] max-w-full items-center justify-center rounded-xl bg-[#fafafa] p-3">
                {i.illustration.illustrationScan ? (
                  <img
                    className="h-full w-full rounded-xl object-contain transition-all duration-300"
                    src={`/api/eil/files/${i.illustration.illustrationScan.id}`}
                    alt={i.illustration.title}
                  />
                ) : null}
                {i.illustration.pageScan && !i.illustration.illustrationScan ? (
                  <img
                    className="h-full w-full rounded-xl object-contain transition-all duration-300"
                    src={`/api/eil/files/${i.illustration.pageScan.id}`}
                    alt={i.illustration.title}
                  />
                ) : null}
                {!i.illustration.illustrationScan &&
                !i.illustration.pageScan ? (
                  <BlankImage classNames="h-20 w-20 transition-all duration-300" />
                ) : null}
              </div>
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
            <div className="ml-10 flex w-full max-w-[620px] flex-col break-words py-5 pr-4">
              <h2 className="mb-2 mt-1 text-xl font-bold">
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
              <div
                className="wysiwyg-editor-content mt-2 leading-relaxed text-gray"
                dangerouslySetInnerHTML={{
                  __html: sanitizeWysiwygHtml(i.description),
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
