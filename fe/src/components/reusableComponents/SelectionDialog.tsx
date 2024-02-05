import { FC, Dispatch, useState, useEffect, SetStateAction } from 'react'
import { useTranslation } from 'react-i18next'

import { PhotoIcon } from '@heroicons/react/24/outline'
import Switch from 'react-switch'
import { Dialog } from '@headlessui/react'
import CloseIcon from '../../assets/icons/close.svg?react'
import Delete from '../../assets/icons/delete.svg?react'

import Button from './Button'
import { RecordType } from '../../@types/types'
import useMobile from '../../hooks/useMobile'
import useMySelectionQuery from '../../api/query/useMySelectionQuery'
import useRemoveFromMySelectionMutationWrapper from '../../hooks/useRemoveFromMySelectionMutationWrapper'
import Loader from './Loader'
import ShowError from './ShowError'
import useMeQueryWrapper from '../../hooks/useMeQueryWrapper'
import { TSelectionItemDetail } from '../../../../fe-shared/@types/selection'
import useChangeViewInMiradorWrapper from '../../hooks/useChangeViewInMiradorWrapper'

const BlankImage = ({ classNames }: { classNames: string }) => {
  return <PhotoIcon className={`text-lightgray ${classNames}`} />
}

type Props = {
  showDialog: boolean
  setShowDialog: Dispatch<SetStateAction<boolean>>
}

const SelectionDialog: FC<Props> = ({ showDialog, setShowDialog }) => {
  const { t } = useTranslation('dialog')
  const [selectionType, setSelectionType] = useState<RecordType>('BOOK')
  const { me } = useMeQueryWrapper()
  const { data: selectionData, isError, isFetching } = useMySelectionQuery(!!me)
  const { isMobile } = useMobile()
  const { doRemove, removingStatus } = useRemoveFromMySelectionMutationWrapper()
  const { doViewChange, viewChangeStatus } = useChangeViewInMiradorWrapper()

  const handleDeletion = (record: TSelectionItemDetail) => {
    doRemove({ items: [record.id] })
  }

  const handleDeletionOfAllItems = () => {
    doRemove({
      items: selectionData?.items?.map((i) => i.id) || [],
    })
  }

  const handleMiradorSwitch = (record: TSelectionItemDetail) => {
    doViewChange({ record })
  }

  useEffect(() => {
    if (showDialog) {
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = 'auto'
    }
  }, [showDialog])

  const selection =
    selectionType === 'BOOK'
      ? selectionData?.items?.filter((i) => i.book?.id)
      : selectionData?.items?.filter((i) => i.illustration?.id)

  const illustrationsForMirador =
    selection
      ?.filter((s) => s.illustration?.id && s.mirador)
      .map((s) => s.illustration?.id) || []

  const booksForMirador =
    selection?.filter((s) => s.book?.id && s.mirador).map((s) => s.book?.id) ||
    []

  return (
    <Dialog
      open={showDialog}
      onClose={() => setShowDialog(false)}
      className="relative z-50"
    >
      <div className="fixed inset-0 bg-black/30" aria-hidden="true" />
      <div className="fixed inset-0 w-screen overflow-y-auto">
        <div className="flex min-h-full items-center justify-center p-4">
          <Dialog.Panel>
            <div className="fixed bottom-1/2 left-1/2 z-10 flex h-full w-full -translate-x-1/2 translate-y-1/2 flex-col bg-white p-6 shadow-xl md:h-[600px] md:w-[700px] md:rounded-2xl">
              <div className="flex items-center justify-between border-b-[1.5px] border-superlightgray pb-2 md:border-none">
                <span className="ml-2 text-2xl font-bold md:text-xl">
                  {t('my_selection')}
                </span>
                <Button
                  iconButton
                  variant="text"
                  className="self-end justify-self-end border-none bg-white font-bold uppercase text-black hover:text-black hover:shadow-none"
                  onClick={() => {
                    setShowDialog(false)
                  }}
                >
                  <CloseIcon />
                </Button>
              </div>
              <div className="mt-4 flex gap-2 border-b-[1.5px] border-superlightgray pb-4">
                {isMobile ? (
                  <div className="flex max-h-11 w-full">
                    <button
                      type="button"
                      className={`w-full rounded-l-xl p-2 px-3 ${
                        selectionType === 'BOOK'
                          ? 'font-bold text-red'
                          : 'text-gray'
                      }  border-collapse border-2 border-superlightgray hover:bg-superlightgray`}
                      onClick={() => setSelectionType('BOOK')}
                    >
                      {t('books')}
                    </button>
                    <button
                      type="button"
                      className={`w-full rounded-r-xl p-2 px-3 ${
                        selectionType === 'ILLUSTRATION'
                          ? 'font-bold text-red'
                          : 'text-gray'
                      } border-collapse border-y-2 border-r-2 border-superlightgray hover:bg-superlightgray`}
                      onClick={() => setSelectionType('ILLUSTRATION')}
                    >
                      {t('illustrations')}
                    </button>
                  </div>
                ) : (
                  <>
                    <Button
                      dense
                      variant={selectionType === 'BOOK' ? 'submit' : 'outlined'}
                      onClick={() => setSelectionType('BOOK')}
                    >
                      {t('books')}
                    </Button>
                    <Button
                      dense
                      variant={
                        selectionType === 'ILLUSTRATION' ? 'submit' : 'outlined'
                      }
                      onClick={() => setSelectionType('ILLUSTRATION')}
                    >
                      {t('illustrations')}
                    </Button>
                    <Button
                      disabled={
                        removingStatus === 'pending' || !selection?.length
                      }
                      dense
                      className="ml-auto text-red"
                      variant="text"
                      onClick={() => handleDeletionOfAllItems()}
                    >
                      {t('delete_all')}
                    </Button>
                  </>
                )}
              </div>

              <div className="mb-4 mt-2 flex h-full w-full flex-col overflow-y-scroll pr-2">
                {selection?.length && !isFetching && !isError
                  ? selection.map((i) => (
                      <div
                        key={i.id}
                        className="my-3 flex items-center justify-start gap-2 border-b-[1.5px] border-superlightgray pb-5"
                      >
                        <div className="ml-4 h-16 w-full max-w-[13%]">
                          {i.illustration?.id &&
                          i.illustration.illustrationScan ? (
                            <img
                              className="h-full max-w-full"
                              src={`/api/eil/files/${i.illustration.illustrationScan.id}`}
                              alt={i.illustration.title}
                            />
                          ) : null}
                          {i.illustration?.id &&
                          i.illustration.pageScan &&
                          !i.illustration.illustrationScan ? (
                            <img
                              className="h-full max-w-full"
                              src={`/api/eil/files/${i.illustration.pageScan.id}`}
                              alt={i.illustration.title}
                            />
                          ) : null}
                          {i.book?.id && i.book.frontPageScan ? (
                            <img
                              className="h-full max-w-full"
                              src={`/api/eil/files/${i.book.frontPageScan.id}`}
                              alt={i.book.title}
                            />
                          ) : null}
                          {(i.book?.id && !i.book.frontPageScan) ||
                          (i.illustration?.id &&
                            !i.illustration.illustrationScan &&
                            !i.illustration.pageScan) ? (
                            <BlankImage classNames="h-full max-w-full" />
                          ) : null}
                        </div>
                        <p className="mr-2 line-clamp-3 w-full max-w-[65%] text-sm font-bold">
                          {i.book?.title || i.illustration?.title}{' '}
                          <span className="font-thin">
                            {i.book?.yearFrom || i.illustration?.yearFrom}
                          </span>
                        </p>
                        <Switch
                          height={22}
                          width={51}
                          checked={i.mirador}
                          onChange={() => handleMiradorSwitch(i)}
                          disabled={viewChangeStatus.status === 'pending'}
                        />
                        <Button
                          iconButton
                          variant="text"
                          className="ml-auto border-none bg-white text-black hover:text-black hover:shadow-none"
                          onClick={() => handleDeletion(i)}
                        >
                          <Delete />
                        </Button>
                      </div>
                    ))
                  : null}
                {isFetching ? (
                  <div className="my-16 flex items-center justify-center">
                    <Loader />
                  </div>
                ) : null}
                {!isFetching && isError ? <ShowError /> : null}
                {!isFetching && !isError && !selection?.length ? (
                  <div className="m-auto flex flex-col items-center">
                    <h2 className="mt-2 text-2xl font-bold">
                      {t(
                        selectionType === 'BOOK'
                          ? 'no_books'
                          : 'no_illustrations'
                      )}
                    </h2>
                    <p>
                      {t(
                        selectionType === 'BOOK'
                          ? 'no_books_text'
                          : 'no_illustrations_text'
                      )}
                    </p>
                  </div>
                ) : null}
              </div>
              <div className="flex flex-row flex-wrap justify-center gap-2 ">
                {isMobile && (
                  <Button
                    className="flex-grow text-red"
                    variant="outlined"
                    disabled={
                      removingStatus === 'pending' || !selection?.length
                    }
                    onClick={() => handleDeletionOfAllItems()}
                  >
                    {t('delete_all')}
                  </Button>
                )}
                {selectionType === 'BOOK' && (
                  <Button
                    disabled={!booksForMirador.length}
                    className={isMobile ? 'flex-grow' : ''}
                    href={`/mirador?r=${booksForMirador.join(',')}`}
                    target="_blank"
                  >
                    {t('open_in_mirador')}
                  </Button>
                )}
                {selectionType === 'ILLUSTRATION' && (
                  <>
                    {/* <Button className={isMobile ? 'flex-grow' : ''}> */}
                    {/*  Otevřít v on-line výstavy */}
                    {/* </Button> */}
                    <Button
                      disabled={!illustrationsForMirador.length}
                      className={isMobile ? 'flex-grow' : ''}
                      href={`/mirador?r=${illustrationsForMirador.join(',')}`}
                      target="_blank"
                    >
                      {t('open_in_mirador')}
                    </Button>
                  </>
                )}
                {!isMobile && (
                  <Button
                    className="ml-auto"
                    variant="secondary"
                    onClick={() => {
                      setShowDialog(false)
                    }}
                  >
                    {t('close')}
                  </Button>
                )}
              </div>
            </div>
          </Dialog.Panel>
        </div>
      </div>
    </Dialog>
  )
}

export default SelectionDialog
