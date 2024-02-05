import { FC } from 'react'
import { ChevronLeftIcon, ChevronRightIcon } from '@heroicons/react/24/outline'
import ReactPaginate from 'react-paginate'

type TPaginatorProps = {
  classNames?: string
  itemsPerPage: number
  contentLength: number
  currentPage: number
  onChange: (newPage: number) => void
}

/**
 * Component for pagination, counting from 0 because of SQL OFFSET, showing numbers from 1.
 */
const Paginator: FC<TPaginatorProps> = ({
  classNames = '',
  itemsPerPage,
  contentLength,
  currentPage,
  onChange,
}) => {
  return (
    <ReactPaginate
      breakLabel="..."
      onPageChange={(event) => onChange(event.selected)}
      pageCount={Math.ceil(contentLength / itemsPerPage)}
      pageRangeDisplayed={2}
      marginPagesDisplayed={1}
      initialPage={currentPage}
      previousLabel={<ChevronLeftIcon className="h-[24px]" />}
      nextLabel={<ChevronRightIcon className="h-[24px]" />}
      className={`flex items-center ${classNames}`}
      pageLinkClassName="border-2 border-superlightgray rounded-sm px-3 py-2 font-semibold mx-2 flex items-center justify-center"
      breakLinkClassName="border-2 border-superlightgray rounded-sm px-3 py-2 font-semibold mx-2 flex items-center justify-center"
      nextLinkClassName="block text-black"
      previousLinkClassName="block text-black"
      activeLinkClassName="bg-red text-white !border-red"
      disabledLinkClassName="!text-gray hover:text-gray cursor-default"
    />
  )
}

export default Paginator
