import { create } from 'zustand'
import { v4 as uuidv4 } from 'uuid'
import i18next from '../lang/index'
import {
  TDropdownWithOperator,
  TSortTypes,
  TDropdown,
  RecordType,
} from '../../../fe-shared/@types/common'
import { TIllustrationList } from '../../../fe-shared/@types/illustration'
import { TBookList } from '../../../fe-shared/@types/book'

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
  type: RecordType
  filterAuthor: TDropdown[]
  filterObject: TDropdown[]
  filterPublishingPlace: TDropdown[]
  filterICCStates: TDropdown[]
  filterThemeStates: TDropdown[]
  currentPage: number
  searchWithCategory: {
    search: string
    category: TDropdownWithOperator
    uuid: string
  }[]
  IIIFFormat: boolean
  exportRecords: (TIllustrationList | TBookList)[]
}

interface TState extends TVariablesState {
  setSort: (newValue: { value: TSortTypes; label: string }) => void
  setYear: (newValues: { from: number; to: number }) => void
  setItemsPerPage: (newValue: TDropdown) => void
  setType: (newValue: RecordType) => void
  setFilterAuthor: (newValue: TDropdown[]) => void
  setFilterObject: (newValue: TDropdown[]) => void
  setFilterPublishingPlace: (newValue: TDropdown[]) => void
  setFilterICCStates: (newValue: TDropdown[]) => void
  setFilterThemeStates: (newValue: TDropdown[]) => void
  setCurrentPage: (newValue: number) => void
  setSearchWithCategory: (
    newValue: {
      search: string
      category: TDropdownWithOperator
      uuid: string
    }[]
  ) => void
  setIIIFFormat: (newValue: boolean) => void
  setExportRecords: (newValue: (TIllustrationList | TBookList)[]) => void
}

// eslint-disable-next-line import/prefer-default-export
export const useExportStore = create<TState>()((set) => ({
  sort: null,
  year: { from: 0, to: 1990 },
  itemsPerPage: { value: '10', label: '10' },
  type: 'ILLUSTRATION' as const,
  filterAuthor: [],
  filterObject: [],
  filterPublishingPlace: [],
  filterICCStates: [],
  filterThemeStates: [],
  currentPage: 0,
  searchWithCategory: [
    {
      search: '',
      category: {
        value: 'ALL',
        label: i18next.t('search.all'),
        operation: 'FTXF' as const,
      },
      uuid: uuidv4(),
    },
  ],
  IIIFFormat: false,
  exportRecords: [],
  setSort: (newValue) => set(() => ({ sort: newValue, currentPage: 0 })),
  setYear: (newValue) => set(() => ({ year: newValue, currentPage: 0 })),
  setItemsPerPage: (newValue) => set(() => ({ itemsPerPage: newValue })),
  setType: (newValue) => set(() => ({ type: newValue, currentPage: 0 })),
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
  setSearchWithCategory: (newValue) =>
    set(() => ({ searchWithCategory: newValue, currentPage: 0 })),
  setIIIFFormat: (newValue) =>
    set(() => ({ IIIFFormat: newValue, currentPage: 0 })),
  setExportRecords: (newValue) => set(() => ({ exportRecords: newValue })),
}))
