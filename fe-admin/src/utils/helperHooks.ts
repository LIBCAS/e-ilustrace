import { useTranslation } from 'react-i18next'
import { TEnrichmentStates } from '../../../fe-shared/@types/illustration'

// eslint-disable-next-line import/prefer-default-export
export const useEnrichmentStates = () => {
  const { t } = useTranslation()

  const states: { value: TEnrichmentStates; label: string }[] = [
    { value: 'DONE', label: t('enrichment.states.done') },
    { value: 'INPROGRESS', label: t('enrichment.states.in_progress') },
    { value: 'UNENRICHED', label: t('enrichment.states.unenriched') },
  ]

  return { states }
}
