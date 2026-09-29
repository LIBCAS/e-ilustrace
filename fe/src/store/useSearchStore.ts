import { create } from 'zustand'
import { v4 as uuidv4 } from 'uuid'
import {
  RecordType,
  View,
  TDropdown,
  TDropdownWithOperator,
  TSortTypes,
} from '../../../fe-shared/@types/common'
import i18next from '../lang/index'

export const SortTypes: TSortTypes[] = [
  'title_ASC',
  'title_DESC',
  'yearFrom_ASC',
  'yearFrom_DESC',
]

export const viewTypes: View[] = ['TILES', 'LIST']

export const recordTypes: RecordType[] = ['BOOK', 'ILLUSTRATION']

export interface TSearchVariablesState {
  sort: {
    value: TSortTypes
    label: string
  } | null
  year: {
    from: number
    to: number
  }
  yearRangeSet: { illustrations: boolean; books: boolean }
  itemsPerPage: TDropdown
  view: View
  type: RecordType
  filterAuthor: TDropdown[]
  filterObject: TDropdown[]
  filterPublishingPlace: TDropdown[]
  filterSubjectPlace: TDropdown[]
  currentPage: number
  searchWithCategory: {
    search: string
    category: TDropdownWithOperator
    uuid: string
  }[]
  IIIFFormat: boolean
}

interface TState extends TSearchVariablesState {
  setSort: (newValue: { value: TSortTypes; label: string }) => void
  setYear: (newValues: { from: number; to: number }) => void
  setYearRangeSet: (newValues: {
    illustrations: boolean
    books: boolean
  }) => void
  setYearRange: (newValues: { from: number; to: number }) => void
  setItemsPerPage: (newValue: TDropdown) => void
  setView: (newValue: View) => void
  setType: (newValue: RecordType) => void
  setFilterAuthor: (newValue: TDropdown[]) => void
  setFilterObject: (newValue: TDropdown[]) => void
  setFilterPublishingPlace: (newValue: TDropdown[]) => void
  setFilterSubjectPlace: (newValue: TDropdown[]) => void
  setCurrentPage: (newValue: number) => void
  setSearchWithCategory: (
    newValue: {
      search: string
      category: TDropdownWithOperator
      uuid: string
    }[]
  ) => void
  setIIIFFormat: (newValue: boolean) => void
}

// eslint-disable-next-line import/prefer-default-export
export const useSearchStore = create<TState>()((set) => ({
  sort: null,
  year: { from: 0, to: 1990 },
  yearRangeSet: { illustrations: false, books: false },
  itemsPerPage: { value: '25', label: '25' },
  view: 'LIST',
  type: 'ILLUSTRATION',
  filterAuthor: [],
  filterObject: [],
  filterPublishingPlace: [],
  filterSubjectPlace: [],
  currentPage: 0,
  searchWithCategory: [
    {
      search: '',
      category: {
        value: 'ALL',
        label: i18next.t('search:all'),
        operation: 'FTXF',
      },
      uuid: uuidv4(),
    },
  ],
  IIIFFormat: false,
  setSort: (newValue) => set(() => ({ sort: newValue, currentPage: 0 })),
  setYear: (newValue) => set(() => ({ year: newValue, currentPage: 0 })),
  setYearRange: (newValue) => set(() => ({ year: newValue })),
  setItemsPerPage: (newValue) => set(() => ({ itemsPerPage: newValue })),
  setView: (newValue) => set(() => ({ view: newValue, currentPage: 0 })),
  setType: (newValue) =>
    set(() =>
      newValue === 'BOOK'
        ? { type: newValue, currentPage: 0, view: 'LIST' }
        : { type: newValue, currentPage: 0 }
    ),
  setFilterAuthor: (newValue) =>
    set(() => ({ filterAuthor: newValue, currentPage: 0 })),
  setFilterObject: (newValue) =>
    set(() => ({ filterObject: newValue, currentPage: 0 })),
  setFilterPublishingPlace: (newValue) =>
    set(() => ({ filterPublishingPlace: newValue, currentPage: 0 })),
  setFilterSubjectPlace: (newValue) =>
    set(() => ({ filterSubjectPlace: newValue, currentPage: 0 })),
  setCurrentPage: (newValue) => set(() => ({ currentPage: newValue })),
  setSearchWithCategory: (newValue) =>
    set(() => ({ searchWithCategory: newValue, currentPage: 0 })),
  setIIIFFormat: (newValue) =>
    set(() => ({ IIIFFormat: newValue, currentPage: 0 })),
  setYearRangeSet: (newValue) => set(() => ({ yearRangeSet: newValue })),
}))
