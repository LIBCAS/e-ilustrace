import React, { FC } from 'react'
import { Link } from 'react-router-dom'
import { PhotoIcon } from '@heroicons/react/24/outline'
import BookMark from '../../assets/icons/bookmark.svg?react'

import { TIllustrationList } from '../../../../fe-shared/@types/illustration'
import Loader from './Loader'
import ShowError from './ShowError'
import useMySelectionQuery from '../../api/query/useMySelectionQuery'
import useAddToMySelectionMutationWrapper from '../../hooks/useAddToMySelectionMutationWrapper'
import useRemoveFromMySelectionMutationWrapper from '../../hooks/useRemoveFromMySelectionMutationWrapper'
import useMeQueryWrapper from '../../hooks/useMeQueryWrapper'
import constructRecordDetailUrl from '../../utils/constructRecordDetailUrl'
import { TSelectionItemDetail } from '../../../../fe-shared/@types/selection'

const BlankImage = () => {
  return <PhotoIcon className="h-[150px] w-[150px] text-lightgray" />
}

type Props = {
  items: TIllustrationList[]
  loading: boolean
  error: boolean
  backPath: string
}

const Gallery: FC<Props> = ({ items, loading, error, backPath }) => {
  const { me } = useMeQueryWrapper()
  const { data: selection } = useMySelectionQuery(!!me)
  const { doAdd } = useAddToMySelectionMutationWrapper()
  const { doRemove } = useRemoveFromMySelectionMutationWrapper()

  const handleAddition = (record: TIllustrationList) => {
    doAdd({ illustrations: [record.id], books: [] })
  }

  const handleDeletion = (record: TSelectionItemDetail) => {
    doRemove({ items: [record.id] })
  }

  return (
    <div className="mt-2 flex flex-wrap justify-around gap-8 md:gap-16">
      {loading ? (
        <div className="flex w-full items-center justify-center py-10">
          <Loader />
        </div>
      ) : null}
      {error ? (
        <div className="flex w-full items-center justify-center py-10">
          <ShowError />
        </div>
      ) : null}
      {!loading && !error ? (
        <>
          {items.map((item) => (
            <Link
              to={constructRecordDetailUrl(item.id, backPath)}
              className="explore-item relative mx-auto flex max-w-[180px] flex-col items-center justify-start p-4 md:mx-0"
              key={item.id}
            >
              <BookMark
                className={`${
                  selection?.items?.find(
                    (i) =>
                      i.book?.id === item.id || i.illustration?.id === item.id
                  )
                    ? 'text-red'
                    : 'text-lightgray'
                } absolute right-5 top-5 z-10 ${
                  me ? 'cursor-pointer' : 'cursor-default'
                }`}
                onClick={(e) => {
                  e.preventDefault()

                  const selected = selection?.items?.find(
                    (it) =>
                      it.book?.id === item.id || it.illustration?.id === item.id
                  )
                  if (selected) {
                    handleDeletion(selected)
                  } else {
                    handleAddition(item)
                  }
                }}
              />
              {item.illustrationScan ? (
                <img
                  className="max-h-[170px] justify-self-start transition-all duration-300"
                  src={`/api/eil/files/${item.illustrationScan.id}`}
                  alt={item.title}
                />
              ) : null}
              {item.pageScan && !item.illustrationScan ? (
                <img
                  className="max-h-[170px] justify-self-start transition-all duration-300"
                  src={`/api/eil/files/${item.pageScan.id}`}
                  alt={item.title}
                />
              ) : null}
              {!item.illustrationScan && !item.pageScan ? <BlankImage /> : null}
              <div className="explore-item-text flex flex-col items-center transition-all duration-300">
                <span className="line-clamp-2 text-center font-bold text-black">
                  {item.title}
                </span>
                {(item.printEntry.placesOfPublication.length ||
                  item.printEntry.originators.length) &&
                item.printEntry.date ? (
                  <span className="text-center text-xs text-gray">
                    {item.printEntry.placesOfPublication.join(' ')}{' '}
                    {item.printEntry.originators.join(' ')}{' '}
                    {item.printEntry.date}
                  </span>
                ) : null}
              </div>
            </Link>
          ))}
        </>
      ) : null}
    </div>
  )
}

export default Gallery
