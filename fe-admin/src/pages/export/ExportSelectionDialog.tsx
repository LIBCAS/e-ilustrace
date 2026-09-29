import { FC, useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { saveAs } from 'file-saver'

import { PhotoIcon } from '@heroicons/react/24/outline'
import { Dialog, DialogPanel } from '@headlessui/react'
import cloneDeep from 'lodash/cloneDeep'
import { toast } from 'react-toastify'
import CloseIcon from '../../assets/icons/close.svg?react'
import Delete from '../../assets/icons/delete.svg?react'

import Button from '../../components/reusableComponents/Button'
import BookMark from '../../assets/icons/bookmark.svg?react'
import { useExportStore } from '../../store/useExportStore'
import { TIllustrationList } from '../../../../fe-shared/@types/illustration'
import { useExportRecordMutation } from '../../api/export'
import { TBookList } from '../../../../fe-shared/@types/book'

const BlankImage = ({ classNames }: { classNames: string }) => {
  return <PhotoIcon className={`text-lightgray ${classNames}`} />
}

type Props = {
  queryRecordsCount: number | undefined
}

const ExportSelectionDialog: FC<Props> = ({ queryRecordsCount }) => {
  const [showDialog, setShowDialog] = useState(false)
  const [downloadType, setDownloadType] = useState<
    'selected' | 'filtered' | null
  >(null)

  const { t } = useTranslation()
  const exportRecords = useExportStore((state) => state.exportRecords)
  const setExportRecords = useExportStore((state) => state.setExportRecords)

  const mutation = useExportRecordMutation()

  const handleDeletion = (record: TIllustrationList | TBookList) => {
    let exportRecordsClone = cloneDeep(exportRecords)
    exportRecordsClone = exportRecordsClone.filter((r) => r.id !== record.id)
    setExportRecords(exportRecordsClone)
  }

  const handleDeletionOfAllItems = () => {
    setExportRecords([])
  }

  const handleExportSelected = () => {
    setDownloadType('selected')

    mutation
      .mutateAsync('selected')
      .then((response) => {
        const date = new Date()

        saveAs(response, `export-${date.toLocaleString()}.json`)
        toast.success(t('export_dialog.exported_successfully'))
      })
      .catch(() => {
        // empty
      })
      .finally(() => {
        setDownloadType(null)
      })
  }

  const handleExportFiltered = () => {
    setDownloadType('filtered')

    mutation
      .mutateAsync('filtered')
      .then((response) => {
        const date = new Date()

        saveAs(response, `export-${date.toLocaleString()}.json`)
        toast.success(t('export_dialog.exported_successfully'))
      })
      .catch(() => {
        // empty
      })
      .finally(() => {
        setDownloadType(null)
      })
  }

  useEffect(() => {
    if (showDialog) {
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = 'auto'
    }
  }, [showDialog])

  return (
    <>
      <Button
        className="fixed bottom-5 right-5 z-20 rounded-3xl shadow-[0px_7px_30px_-5px_rgba(0,0,0,0.75)] hover:shadow-[0px_7px_30px_-5px_rgba(0,0,0,0.75)] md:bottom-10 md:right-10 xl:bottom-16 xl:right-16"
        startIcon={<BookMark />}
        onClick={() => {
          setShowDialog(true)
        }}
      >
        {t('export_dialog.export_selection_button')}
      </Button>
      <Dialog
        open={showDialog}
        onClose={() => setShowDialog(false)}
        className="relative z-50"
      >
        <div className="fixed inset-0 bg-black/30" aria-hidden="true" />
        <div className="fixed inset-0 w-screen overflow-y-auto">
          <div className="flex min-h-full items-center justify-center p-4">
            <DialogPanel>
              <div className="fixed bottom-1/2 left-1/2 z-10 flex h-full w-full -translate-x-1/2 translate-y-1/2 flex-col bg-white p-6 shadow-xl md:h-[600px] md:w-[700px] md:rounded-2xl">
                <div className="flex items-center justify-between border-b-[1.5px] border-superlightgray pb-2 md:border-none">
                  <span className="ml-2 text-2xl font-bold md:text-xl">
                    {t('export_dialog.export_selection')}
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
                  <Button
                    disabled={!queryRecordsCount}
                    dense
                    variant="primary"
                    className="text-sm"
                    onClick={() => handleExportFiltered()}
                    isLoading={
                      mutation.isPending && downloadType === 'filtered'
                    }
                  >
                    {t('export_dialog.export_filtered')} (
                    {queryRecordsCount || 0})
                  </Button>
                  <Button
                    disabled={!exportRecords.length}
                    dense
                    variant="primary"
                    className="text-sm"
                    onClick={() => handleExportSelected()}
                    isLoading={
                      mutation.isPending && downloadType === 'selected'
                    }
                  >
                    {t('export_dialog.export_selected')} (
                    {exportRecords.length || 0})
                  </Button>
                  <Button
                    disabled={!exportRecords.length || mutation.isPending}
                    dense
                    className="ml-auto text-sm text-red"
                    variant="text"
                    onClick={() => handleDeletionOfAllItems()}
                  >
                    {t('export_dialog.delete_all_selected')}
                  </Button>
                </div>

                <div className="mb-4 mt-2 flex h-full w-full flex-col overflow-y-scroll pr-2">
                  {exportRecords.map((i) => (
                    <div
                      key={i.id}
                      className="my-3 flex items-center justify-start gap-2 border-b-[1.5px] border-superlightgray pb-5"
                    >
                      <div className="ml-4 h-16 w-full max-w-[13%]">
                        {i.type === 'ILLUSTRATION' && i.illustrationScan ? (
                          <img
                            className="h-full max-w-full"
                            src={`/api/eil/files/${i.illustrationScan.id}`}
                            alt={i.title}
                          />
                        ) : null}
                        {i.type === 'ILLUSTRATION' &&
                        i.pageScan &&
                        !i.illustrationScan ? (
                          <img
                            className="h-full max-w-full"
                            src={`/api/eil/files/${i.pageScan.id}`}
                            alt={i.title}
                          />
                        ) : null}
                        {i.type === 'BOOK' && i.frontPageScan ? (
                          <img
                            className="max-h-full"
                            src={`/api/eil/files/${i.frontPageScan.id}`}
                            alt={i.title}
                          />
                        ) : null}
                        {(i.type === 'BOOK' && !i.frontPageScan) ||
                        (i.type === 'ILLUSTRATION' &&
                          !i.illustrationScan &&
                          !i.pageScan) ? (
                          <BlankImage classNames="h-full max-w-full" />
                        ) : null}
                      </div>
                      <p className="mr-2 line-clamp-3 w-full max-w-[65%] text-sm font-bold">
                        {i.title.trim().endsWith('/')
                          ? i.title.trim().slice(0, -1)
                          : i.title.trim()}{' '}
                        <span className="font-thin">{i.yearFrom}</span>
                      </p>
                      <Button
                        iconButton
                        variant="text"
                        className="ml-auto border-none bg-white text-black hover:text-black hover:shadow-none"
                        onClick={() => handleDeletion(i)}
                      >
                        <Delete />
                      </Button>
                    </div>
                  ))}
                  {!exportRecords.length ? (
                    <div className="m-auto flex flex-col items-center">
                      <h2 className="mt-2 text-2xl font-bold">
                        {t('export_dialog.no_illustrations')}
                      </h2>
                      <p>{t('export_dialog.no_illustrations_text')}</p>
                    </div>
                  ) : null}
                </div>
                <div className="flex flex-row flex-wrap justify-center gap-2">
                  <Button
                    className="ml-auto"
                    variant="secondary"
                    onClick={() => {
                      setShowDialog(false)
                    }}
                  >
                    {t('export_dialog.close')}
                  </Button>
                </div>
              </div>
            </DialogPanel>
          </div>
        </div>
      </Dialog>
    </>
  )
}

export default ExportSelectionDialog
