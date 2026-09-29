import { create } from 'zustand'
import i18next from 'i18next'
import { TFilterOperator } from '../../../fe-shared/@types/common'

export interface TIconClassVariablesState {
  icc: string[]
  search: string
  category: { value: string; label: string; operation: TFilterOperator }
  page: number
}

interface TState extends TIconClassVariablesState {
  setIcc: (icc: string[]) => void
  setSearch: (search: string) => void
  setPage: (page: number) => void
  setCategory: (category: {
    value: string
    label: string
    operation: TFilterOperator
  }) => void
  resetParams: () => void
}

export const useIconClassStore = create<TState>()((set) => ({
  icc: [],
  search: '',
  category: {
    value: 'iconclass.code',
    label: i18next.t('iconclass:icc_code'),
    operation: 'START_WITH',
  },
  page: 0,
  setIcc: (icc) => set(() => ({ icc, page: 0 })),
  setSearch: (search) => set(() => ({ search, page: 0 })),
  setCategory: (category) => set(() => ({ category, page: 0 })),
  setPage: (page) => set(() => ({ page })),
  resetParams: () => set(() => ({ themes: [], search: '', page: 0 })),
}))
