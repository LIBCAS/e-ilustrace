import { create } from 'zustand'
import i18next from '../lang/index'
import { TSortTypes } from '../../../fe-shared/@types/common'

type TDropdown = {
  value: string
  label: string
}

export type TParam = TDropdown
export type TFilterParam = TDropdown[]

interface TVariablesState {
  sort: {
    value: TSortTypes
    label: string
  } | null
  year: {
    from: number
    to: number
  }
  itemsPerPage: TDropdown
  categoryActive: boolean
  category: TDropdown
  filterAuthor: TDropdown[]
  filterObject: TDropdown[]
  filterPublishingPlace: TDropdown[]
  filterICCStates: TDropdown[]
  filterThemeStates: TDropdown[]
  currentPage: number
  currentSearch: string
  IIIFFormat: boolean
}

interface TState extends TVariablesState {
  setSort: (newValue: { value: TSortTypes; label: string }) => void
  setYear: (newValues: { from: number; to: number }) => void
  setItemsPerPage: (newValue: TDropdown) => void
  setCategoryActive: (newValue: boolean) => void
  setCategory: (newValue: TParam) => void
  setFilterAuthor: (newValue: TFilterParam) => void
  setFilterObject: (newValue: TFilterParam) => void
  setFilterPublishingPlace: (newValue: TFilterParam) => void
  setFilterICCStates: (newValue: TFilterParam) => void
  setFilterThemeStates: (newValue: TFilterParam) => void
  setCurrentPage: (newValue: number) => void
  setCurrentSearch: (newValue: string) => void
  setIIIFFormat: (newValue: boolean) => void
}

export const useSearchStore = create<TState>()((set) => ({
  sort: null,
  year: { from: 0, to: 1990 },
  itemsPerPage: { value: '10', label: '10' },
  categoryActive: false,
  category: { value: 'identifier', label: i18next.t('search.record_id') },
  filterAuthor: [],
  filterObject: [],
  filterPublishingPlace: [],
  filterICCStates: [],
  filterThemeStates: [],
  currentPage: 0,
  currentSearch: '',
  IIIFFormat: false,
  setSort: (newValue) => set(() => ({ sort: newValue, currentPage: 0 })),
  setYear: (newValue) => set(() => ({ year: newValue, currentPage: 0 })),
  setItemsPerPage: (newValue) => set(() => ({ itemsPerPage: newValue })),
  setCategoryActive: (newValue) => set(() => ({ categoryActive: newValue })),
  setCategory: (newValue) =>
    set(() => ({ category: newValue, currentPage: 0 })),
  setFilterAuthor: (newValue) =>
    set(() => ({ filterAuthor: newValue, currentPage: 0 })),
  setFilterObject: (newValue) =>
    set(() => ({ filterObject: newValue, currentPage: 0 })),
  setFilterPublishingPlace: (newValue) =>
    set(() => ({ filterPublishingPlace: newValue, currentPage: 0 })),
  setFilterICCStates: (newValue) =>
    set(() => ({ filterICCStates: newValue, currentPage: 0 })),
  setFilterThemeStates: (newValue) =>
    set(() => ({ filterThemeStates: newValue, currentPage: 0 })),
  setCurrentPage: (newValue) => set(() => ({ currentPage: newValue })),
  setCurrentSearch: (newValue) =>
    set(() => ({ currentSearch: newValue, currentPage: 0 })),
  setIIIFFormat: (newValue) =>
    set(() => ({ IIIFFormat: newValue, currentPage: 0 })),
}))
